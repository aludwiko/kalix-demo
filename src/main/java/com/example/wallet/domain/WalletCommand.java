package com.example.wallet.domain;

public sealed interface WalletCommand {

  record Deposit(int amount) implements WalletCommand {
  }

  record Withdraw(int amount) implements WalletCommand {
  }

  record Delete() implements WalletCommand {
  }

  record TransferFunds(int amount, String toWalletId) implements WalletCommand {
  }

  record AbortTransfer(String lockId) implements WalletCommand {
  }

  record ConfirmTransferDeposit(String lockId) implements WalletCommand {
  }

  record DepositTransferFunds(int amount, String fromWalletId, String lockId) implements WalletCommand {
  }
}