package com.example.wallet.ve;

import com.example.wallet.domain.WalletError;
import io.vavr.control.Either;

import static com.example.wallet.domain.WalletError.INVALID_DEPOSIT_AMOUNT;
import static com.example.wallet.domain.WalletError.NOT_SUFFICIENT_FUNDS;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public record WalletVE(String id, String ownerId, int balance) {

  public Either<WalletError, WalletVE> deposit(int amount) {
    if (amount <= 0) {
      return left(INVALID_DEPOSIT_AMOUNT);
    } else {
      return right(new WalletVE(id, ownerId, balance + amount));
    }
  }

  public Either<WalletError, WalletVE> withdraw(int amount) {
    if (amount <= 0) {
      return left(INVALID_DEPOSIT_AMOUNT);
    } else if (amount > balance) {
      return left(NOT_SUFFICIENT_FUNDS);
    } else {
      return right(new WalletVE(id, ownerId, balance - amount));
    }
  }
}
