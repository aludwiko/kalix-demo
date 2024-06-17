package com.example.wallet.application;

import com.example.wallet.application.Response.Success;
import com.example.wallet.domain.Or;
import com.example.wallet.domain.Wallet;
import com.example.wallet.domain.WalletCommand;
import com.example.wallet.domain.WalletCommand.AbortTransfer;
import com.example.wallet.domain.WalletCommand.Create;
import com.example.wallet.domain.WalletCommand.Delete;
import com.example.wallet.domain.WalletCommand.Deposit;
import com.example.wallet.domain.WalletCommand.DepositTransferFunds;
import com.example.wallet.domain.WalletCommand.Withdraw;
import com.example.wallet.domain.WalletEvent;
import kalix.javasdk.annotations.EventHandler;
import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Id("id")
@TypeId("wallet")
@RequestMapping("/wallet/{id}")
public class WalletEntity extends EventSourcedEntity<Wallet, WalletEvent> {

  @Override
  public Wallet emptyState() {
    return Wallet.EMPTY;
  }

  @PostMapping("/{ownerId}/{initBalance}")
  public Effect<Response> create(@PathVariable String id, @PathVariable String ownerId, @PathVariable int initBalance) {
    if (!currentState().isEmpty()) {
      return effects().error("wallet already created");
    } else {
      return process(new Create(id, ownerId, initBalance));
    }
  }

  @PatchMapping("/deposit/{amount}")
  public Effect<Response> deposit(@PathVariable int amount) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return process(new Deposit(amount));
    }
  }

  @PatchMapping("/withdraw/{amount}")
  public Effect<Response> withdraw(@PathVariable int amount) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return process(new Withdraw(amount));
    }
  }

  @PatchMapping("/transfer/{toWalletId}/{amount}")
  public Effect<Response> transfer(@PathVariable String toWalletId, @PathVariable int amount) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return process(new WalletCommand.TransferFunds(amount, toWalletId));
    }
  }

  @PatchMapping("/abort/{lockId}")
  public Effect<Response> abort(@PathVariable String lockId) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return process(new AbortTransfer(lockId));
    }
  }

  @PatchMapping("/transfer-deposit/{fromWalletId}/{amount}/{lockId}")
  public Effect<Response> transferDeposit(@PathVariable String fromWalletId, @PathVariable int amount, @PathVariable String lockId) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return process(new DepositTransferFunds(amount, fromWalletId, lockId));
    }
  }

  @PatchMapping("/confirm-deposit/{lockId}")
  public Effect<Response> confirmDeposit(@PathVariable String lockId) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return process(new WalletCommand.ConfirmTransferDeposit(lockId));
    }
  }

  private Effect<Response> process(WalletCommand walletCommand) {
    return switch (currentState().process(walletCommand)) {
      case Or.Left(var error) -> effects().error(error.name());
      case Or.Right(var event) -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"));
    };
  }

  @GetMapping
  public Effect<WalletResponse> get() {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return effects().reply(WalletResponse.from(currentState()));
    }
  }

  @DeleteMapping
  public Effect<Response> delete() {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return process(new Delete());
    }
  }

  @EventHandler
  public Wallet handle(WalletEvent walletEvent) {
    return currentState().apply(walletEvent);
  }
}
