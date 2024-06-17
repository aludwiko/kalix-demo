package com.example.wallet.ve;

import akka.Done;
import kalix.javasdk.action.Action;
import kalix.javasdk.annotations.Subscribe;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Subscribe.ValueEntity(WalletEntity.class)
public class NotifyAboutLowBalanceVE extends Action {

  public Effect<Done> onChange(WalletVE wallet) {
    if (wallet.balance() < 50) {
      return effects().asyncReply(sendEmailTo(wallet.ownerId(), wallet.balance()));
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
