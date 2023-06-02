package com.example.wallet.application;

import com.example.wallet.domain.Wallet;

public record WalletResponse(String id, String ownerId, int balance) {
  
  public static WalletResponse from(Wallet wallet) {
    return new WalletResponse(wallet.id(), wallet.ownerId(), wallet.balance());
  }
}
