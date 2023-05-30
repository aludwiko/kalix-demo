package com.example.wallet.application;

import com.example.wallet.application.Response.Success;
import com.example.wallet.domain.Wallet;
import com.example.wallet.domain.WalletEvent;
import com.example.wallet.domain.WalletEvent.FundsDeposited;
import com.example.wallet.domain.WalletEvent.FundsWithdrawn;
import com.example.wallet.domain.WalletEvent.WalletCreated;
import com.example.wallet.domain.WalletEvent.WalletDeleted;
import kalix.javasdk.annotations.EntityKey;
import kalix.javasdk.annotations.EntityType;
import kalix.javasdk.annotations.EventHandler;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@EntityKey("id")
@EntityType("wallet")
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
      return effects()
        .emitEvent(new WalletCreated(id, ownerId, initBalance))
        .thenReply(__ -> Success.of("wallet created"));
    }
  }

  @PatchMapping("/deposit/{amount}")
  public Effect<Response> deposit(@PathVariable int amount) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return currentState().deposit(amount).fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
  }

  @PatchMapping("/withdraw/{amount}")
  public Effect<Response> withdraw(@PathVariable int amount) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return currentState().withdraw(amount).fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
  }

  @GetMapping
  public Effect<Wallet> get() {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return effects().reply(currentState());
    }
  }

  @DeleteMapping
  public Effect<Response> delete() {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return currentState().delete().fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
  }

  @EventHandler
  public Wallet handle(WalletCreated walletCreated) {
    return currentState().apply(walletCreated);
  }

  @EventHandler
  public Wallet handle(FundsDeposited fundsDeposited) {
    return currentState().apply(fundsDeposited);
  }

  @EventHandler
  public Wallet handle(FundsWithdrawn fundsWithdrawn) {
    return currentState().apply(fundsWithdrawn);
  }

  @EventHandler
  public Wallet handle(WalletDeleted walletDeleted) {
    return currentState().apply(walletDeleted);
  }

}
