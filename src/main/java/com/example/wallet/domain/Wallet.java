package com.example.wallet.domain;

import com.example.wallet.domain.WalletEvent.FundsDeposited;
import com.example.wallet.domain.WalletEvent.FundsWithdrawn;
import com.example.wallet.domain.WalletEvent.WalletCreated;
import com.example.wallet.domain.WalletEvent.WalletDeleted;
import io.vavr.control.Either;

import static com.example.wallet.domain.WalletError.INVALID_DEPOSIT_AMOUNT;
import static com.example.wallet.domain.WalletError.NOT_SUFFICIENT_FUNDS;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public record Wallet(String id, String ownerId, int balance) {

  public static final Wallet EMPTY = new Wallet("", "", 0);

  public Either<WalletError, WalletEvent> deposit(int amount) {
    if (amount <= 0) {
      return left(INVALID_DEPOSIT_AMOUNT);
    } else {
      return right(new FundsDeposited(id, ownerId, amount, balance + amount));
    }
  }

  public Either<WalletError, WalletEvent> withdraw(int amount) {
    if (amount <= 0) {
      return left(INVALID_DEPOSIT_AMOUNT);
    } else if (amount > balance) {
      return left(NOT_SUFFICIENT_FUNDS);
    } else {
      return right(new FundsWithdrawn(id, ownerId, amount, balance - amount));
    }
  }

  public Either<WalletError, WalletEvent> delete() {
    //some validation here
    return right(new WalletDeleted(id, ownerId));
  }

  public Wallet apply(WalletEvent walletEvent) {
    return switch (walletEvent) {
      case WalletCreated created -> new Wallet(created.walletId(), created.ownerId(), created.balance());
      case FundsDeposited fundsDeposited -> new Wallet(id, ownerId, fundsDeposited.balanceAfter());
      case FundsWithdrawn fundsWithdrawn -> new Wallet(id, ownerId, fundsWithdrawn.balanceAfter());
      case WalletDeleted __ -> this; //ignore
    };
  }

  public boolean isEmpty() {
    return id.equals("");
  }
}
