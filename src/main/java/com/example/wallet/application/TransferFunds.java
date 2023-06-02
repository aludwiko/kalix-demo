package com.example.wallet.application;

import com.example.wallet.domain.WalletEvent.TransferFundsDeposited;
import com.example.wallet.domain.WalletEvent.TransferFundsLocked;
import com.example.wallet.domain.WalletEvent.TransferRejected;
import com.google.protobuf.any.Any;
import kalix.javasdk.DeferredCall;
import kalix.javasdk.action.Action;
import kalix.javasdk.annotations.Subscribe;
import kalix.spring.KalixClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Subscribe.EventSourcedEntity(value = WalletEntity.class, ignoreUnknown = true)
public class TransferFunds extends Action {

  private final Logger logger = LoggerFactory.getLogger(TransferFunds.class);

  private final KalixClient kalixClient;

  public TransferFunds(KalixClient kalixClient) {this.kalixClient = kalixClient;}

  public Effect<Response> handle(TransferFundsLocked locked) {
    DeferredCall<Any, Response> transferDeposit = kalixClient.patch("/wallet/" + locked.toWalletId() + "/transfer-deposit/" + locked.walletId() + "/" + locked.amount() + "/" + locked.lockId(), Response.class);
    logger.info("Depositing transfer funds");
    return effects().forward(transferDeposit);
  }

  public Effect<Response> handle(TransferRejected rejected) {
    DeferredCall<Any, Response> abortTransfer = kalixClient.patch("/wallet/" + rejected.fromWalletId() + "/abort/" + rejected.lockId(), Response.class);
    logger.info("Aborting transfer");
    return effects().forward(abortTransfer);
  }

  public Effect<Response> handle(TransferFundsDeposited transferFundsDeposited) {
    DeferredCall<Any, Response> confirmTransfer = kalixClient.patch("/wallet/" + transferFundsDeposited.fromWalletId() + "/confirm-deposit/" + transferFundsDeposited.lockId(), Response.class);
    logger.info("Confirming transfer");
    return effects().forward(confirmTransfer);
  }
}
