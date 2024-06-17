package com.example.wallet.application;

import com.example.wallet.application.Response.Failure;
import com.example.wallet.application.Response.Success;
import com.example.wallet.domain.TransferState;
import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.client.ComponentClient;
import kalix.javasdk.workflow.AbstractWorkflow.Effect.TransitionalEffect;
import kalix.javasdk.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.example.wallet.domain.TransferStatus.STARTED;

@Id("id")
@TypeId("transfer")
@RequestMapping("/transfer/{id}")
public class TransferWorkflow extends Workflow<TransferState> {

  private final Logger logger = LoggerFactory.getLogger(TransferWorkflow.class);

  private final String withdrawStepName = "withdraw";
  private final String depositStepName = "deposit";
  private final String abortStepName = "abort";

  final private ComponentClient componentClient;

  public TransferWorkflow(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Override
  public WorkflowDef<TransferState> definition() {

    var withdraw =
      step(withdrawStepName)
        .call(() -> {
          TransferState transferState = currentState();
          return componentClient.forEventSourcedEntity(transferState.fromWalletId())
            .call(WalletEntity::withdraw)
            .params(transferState.amount());

        })
        .andThen(Response.class, this::moveToDeposit);

    var deposit =
      step(depositStepName)
        .call(() -> {
          TransferState transferState = currentState();
          return componentClient.forEventSourcedEntity(transferState.toWalletId())
            .call(WalletEntity::deposit)
            .params(transferState.amount());
        })
        .andThen(Response.class, this::completeTransfer);

    var abort =
      step(abortStepName)
        .call(String.class, message -> {
          TransferState transferState = currentState();
          logger.info("compensating withdraw from walletId=" + transferState.fromWalletId());
          return componentClient.forEventSourcedEntity(transferState.fromWalletId())
            .call(WalletEntity::deposit)
            .params(transferState.amount());
        })
        .andThen(Response.class, r -> effects().updateState(currentState().asAborted()).end());

    return workflow()
      .addStep(withdraw)
      .addStep(deposit, maxRetries(3).failoverTo(abortStepName))
      .addStep(abort);
  }

  @PostMapping("/{from}/{to}/{amount}")
  public Effect<Response> create(@PathVariable String from, @PathVariable String to, @PathVariable int amount) {
    if (currentState() != null) {
      return effects().error("transfer is running");
    } else {
      return effects()
        .updateState(new TransferState(from, to, amount, STARTED))
        .transitionTo(withdrawStepName)
        .thenReply(Success.of("transfer started"));
    }
  }

  @GetMapping()
  public Effect<TransferState> get() {
    if (currentState() == null) {
      return effects().error("transfer not exists");
    } else {
      return effects().reply(currentState());
    }
  }

  private TransitionalEffect<Void> moveToDeposit(Response response) {
    TransferState transferState = currentState();

    return switch (response) {
      case Success __ -> {
        TransferState updatedTransfer = transferState.asSuccessfulWithdrawal();
        logger.info("move to deposit: {}", updatedTransfer);
        yield effects()
          .updateState(updatedTransfer)
          .transitionTo(depositStepName);
      }
      case Failure __ -> {
        TransferState updatedTransfer = transferState.asFailed();
        logger.info("finished as failed: {}", updatedTransfer);
        yield effects()
          .updateState(updatedTransfer)
          .end();
      }
    };
  }

  private TransitionalEffect<Void> completeTransfer(Response response) {
    return switch (response) {
      case Success __ -> {
        TransferState updatedTransfer = currentState().asCompleted();
        logger.info("transfer completed: {}", updatedTransfer);
        yield effects()
          .updateState(updatedTransfer)
          .end();
      }
      case Failure __ -> throw new IllegalStateException("unexpected failure for deposit");
    };
  }
}
