package com.example.wallet.domain;

import static com.example.wallet.domain.TransferStatus.ABORTED;
import static com.example.wallet.domain.TransferStatus.COMPLETED;
import static com.example.wallet.domain.TransferStatus.DEPOSIT_FAILED;
import static com.example.wallet.domain.TransferStatus.FAILED;
import static com.example.wallet.domain.TransferStatus.SUCCESSFUL_WITHDRAWAL;

public record TransferState(String fromWalletId, String toWalletId, int amount, TransferStatus transferStatus) {
  public TransferState asSuccessfulWithdrawal() {
    return new TransferState(fromWalletId, toWalletId, amount, SUCCESSFUL_WITHDRAWAL);
  }

  public TransferState asCompleted() {
    return new TransferState(fromWalletId, toWalletId, amount, COMPLETED);
  }

  public TransferState asAborted() {
    return new TransferState(fromWalletId, toWalletId, amount, ABORTED);
  }

  public TransferState asFailed() {
    return new TransferState(fromWalletId, toWalletId, amount, FAILED);
  }

  public TransferState asDepositFailed() {
    return new TransferState(fromWalletId, toWalletId, amount, DEPOSIT_FAILED);
  }
}
