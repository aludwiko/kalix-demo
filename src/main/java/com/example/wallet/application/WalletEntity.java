package com.example.wallet.application;

import com.example.wallet.application.Response.Success;
import com.example.wallet.domain.Wallet;
import com.example.wallet.domain.WalletCommand;
import com.example.wallet.domain.WalletCommand.AbortTransfer;
import com.example.wallet.domain.WalletCommand.Delete;
import com.example.wallet.domain.WalletCommand.Deposit;
import com.example.wallet.domain.WalletCommand.DepositTransferFunds;
import com.example.wallet.domain.WalletCommand.Withdraw;
import com.example.wallet.domain.WalletEvent;
import com.example.wallet.domain.WalletEvent.FundsDeposited;
import com.example.wallet.domain.WalletEvent.FundsWithdrawn;
import com.example.wallet.domain.WalletEvent.TransferFundsDeposited;
import com.example.wallet.domain.WalletEvent.TransferFundsLocked;
import com.example.wallet.domain.WalletEvent.TransferFundsUnlocked;
import com.example.wallet.domain.WalletEvent.TransferFundsWithdrawn;
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
      return currentState().process(new Deposit(amount)).fold(
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
      return currentState().process(new Withdraw(amount)).fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
  }

  @PatchMapping("/transfer/{toWalletId}/{amount}")
  public Effect<Response> transfer(@PathVariable String toWalletId, @PathVariable int amount) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return currentState().process(new WalletCommand.TransferFunds(amount, toWalletId)).fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
  }

  @PatchMapping("/abort/{lockId}")
  public Effect<Response> transfer(@PathVariable String lockId) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return currentState().process(new AbortTransfer(lockId)).fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
  }

  @PatchMapping("/transfer-deposit/{fromWalletId}/{amount}/{lockId}")
  public Effect<Response> transferDeposit(@PathVariable String fromWalletId, @PathVariable int amount, @PathVariable String lockId) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return currentState().process(new DepositTransferFunds(amount, fromWalletId, lockId)).fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
  }

  @PatchMapping("/confirm-deposit/{lockId}")
  public Effect<Response> confirmDeposit(@PathVariable String lockId) {
    if (currentState().isEmpty()) {
      return effects().error("wallet not created");
    } else {
      return currentState().process(new WalletCommand.ConfirmTransferDeposit(lockId)).fold(
        error -> effects().error(error.name()),
        event -> effects().emitEvent(event).thenReply(__ -> Success.of("ok"))
      );
    }
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
      return currentState().process(new Delete()).fold(
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

  @EventHandler
  public Wallet handle(TransferFundsLocked transferFundsLocked) {
    return currentState().apply(transferFundsLocked);
  }

  @EventHandler
  public Wallet handle(TransferFundsDeposited transferFundsDeposited) {
    return currentState().apply(transferFundsDeposited);
  }

  @EventHandler
  public Wallet handle(TransferFundsWithdrawn transferFundsWithdrawn) {
    return currentState().apply(transferFundsWithdrawn);
  }

  @EventHandler
  public Wallet handle(TransferFundsUnlocked transferFundsUnlocked) {
    return currentState().apply(transferFundsUnlocked);
  }

  @EventHandler
  public Wallet handle(WalletEvent.TransferRejected transferRejected) {
    return currentState().apply(transferRejected);
  }
}
