package com.example.wallet.domain;

public sealed interface WalletEvent {

  record WalletCreated(String walletId, String ownerId, int balance) implements WalletEvent {
  }

  record FundsDeposited(String walletId, String ownerId, int amount, int balanceAfter) implements WalletEvent {
  }

  record FundsWithdrawn(String walletId, String ownerId, int amount, int balanceAfter) implements WalletEvent {
  }

  record WalletDeleted(String walletId, String ownerId) implements WalletEvent {
  }

  record TransferFundsLocked(String walletId, int amount, int balanceAfter, String toWalletId, String lockId) implements WalletEvent {
  }

  record TransferFundsUnlocked(String walletId, int amount, int balanceAfter, String lockId) implements WalletEvent {
  }

  record TransferRejected(String walletId, String fromWalletId, String lockId) implements WalletEvent {
  }

  record TransferFundsWithdrawn(String walletId, int amount, int balanceAfter, String lockId) implements WalletEvent {
  }

  record TransferFundsDeposited(String walletId, int amount, int balanceAfter, String fromWalletId, String lockId) implements WalletEvent {
  }
}
