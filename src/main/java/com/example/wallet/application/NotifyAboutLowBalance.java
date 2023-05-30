package com.example.wallet.application;

import akka.Done;
import com.example.wallet.domain.WalletEvent.FundsWithdrawn;
import kalix.javasdk.action.Action;
import kalix.javasdk.annotations.Subscribe;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Subscribe.EventSourcedEntity(value = WalletEntity.class, ignoreUnknown = true)
public class NotifyAboutLowBalance extends Action {

  public Effect<Done> handle(FundsWithdrawn fundsWithdrawn) {
    if (fundsWithdrawn.balanceAfter() < 50) {
      return effects().asyncReply(sendEmailTo(fundsWithdrawn.ownerId(), fundsWithdrawn.balanceAfter()));
    } else {
      return effects().ignore();
    }
  }

  private CompletionStage<Done> sendEmailTo(String ownerId, int currentBalance) {
    return CompletableFuture.supplyAsync(() -> {
      System.out.println("Sending email to: " + ownerId + ". Balance: " + currentBalance);
      return Done.getInstance();
    });
  }
}
