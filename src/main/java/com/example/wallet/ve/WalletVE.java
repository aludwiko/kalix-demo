package com.example.wallet.ve;

import com.example.wallet.domain.Or;
import com.example.wallet.domain.WalletError;

import static com.example.wallet.domain.Or.left;
import static com.example.wallet.domain.Or.right;
import static com.example.wallet.domain.WalletError.INVALID_AMOUNT;
import static com.example.wallet.domain.WalletError.NOT_SUFFICIENT_FUNDS;

public record WalletVE(String id, String ownerId, int balance) {

  public Or<WalletError, WalletVE> deposit(int amount) {
    if (amount <= 0) {
      return left(INVALID_AMOUNT);
    } else {
      return right(new WalletVE(id, ownerId, balance + amount));
    }
  }

  public Or<WalletError, WalletVE> withdraw(int amount) {
    if (amount <= 0) {
      return left(INVALID_AMOUNT);
    } else if (amount > balance) {
      return left(NOT_SUFFICIENT_FUNDS);
    } else {
      return right(new WalletVE(id, ownerId, balance - amount));
    }
  }
}
