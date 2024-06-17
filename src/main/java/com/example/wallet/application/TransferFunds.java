package com.example.wallet.application;

import com.example.wallet.domain.WalletEvent.TransferFundsDeposited;
import com.example.wallet.domain.WalletEvent.TransferFundsLocked;
import com.example.wallet.domain.WalletEvent.TransferRejected;
import com.google.protobuf.any.Any;
import kalix.javasdk.DeferredCall;
import kalix.javasdk.action.Action;
import kalix.javasdk.annotations.Subscribe;
import kalix.javasdk.client.ComponentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Subscribe.EventSourcedEntity(value = WalletEntity.class, ignoreUnknown = true)
public class TransferFunds extends Action {

  private final Logger logger = LoggerFactory.getLogger(TransferFunds.class);

  private final ComponentClient componentClient;

  public TransferFunds(ComponentClient componentClient) {this.componentClient = componentClient;}

  public Effect<Response> handle(TransferFundsLocked locked) {
    logger.info("Depositing transfer funds");
    DeferredCall<Any, Response> transferDeposit = componentClient
      .forEventSourcedEntity(locked.toWalletId())
      .call(WalletEntity::transferDeposit)
      .params(locked.walletId(), locked.amount(), locked.lockId());
    return effects().forward(transferDeposit);
  }

  public Effect<Response> handle(TransferRejected rejected) {
    logger.info("Aborting transfer");
    DeferredCall<Any, Response> abortTransfer = componentClient
      .forEventSourcedEntity(rejected.fromWalletId())
      .call(WalletEntity::abort)
      .params(rejected.lockId());
    return effects().forward(abortTransfer);
  }

  public Effect<Response> handle(TransferFundsDeposited transferFundsDeposited) {
    logger.info("Confirming transfer");
    DeferredCall<Any, Response> confirmTransfer = componentClient
      .forEventSourcedEntity(transferFundsDeposited.fromWalletId())
      .call(WalletEntity::confirmDeposit)
      .params(transferFundsDeposited.lockId());
    return effects().forward(confirmTransfer);
  }
}
