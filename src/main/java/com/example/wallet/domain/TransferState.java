package com.example.wallet.domain;

import static com.example.wallet.domain.TransferStatus.COMPLETED;
import static com.example.wallet.domain.TransferStatus.FAILED;
import static com.example.wallet.domain.TransferStatus.SUCCESSFUL_WITHDRAWAL;

public record TransferState(String fromWalletId, String toWalletId, int amount, TransferStatus transferStatus) {
  public TransferState asSuccessfulWithdrawal() {
    return new TransferState(fromWalletId, toWalletId, amount, SUCCESSFUL_WITHDRAWAL);
  }

  public TransferState asCompleted() {
    return new TransferState(fromWalletId, toWalletId, amount, COMPLETED);
  }

  public TransferState asFailed() {
    return new TransferState(fromWalletId, toWalletId, amount, FAILED);
  }
}
