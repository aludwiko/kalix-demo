package com.example.wallet.domain;

import com.example.wallet.domain.WalletCommand.AbortTransfer;
import com.example.wallet.domain.WalletCommand.ConfirmTransferDeposit;
import com.example.wallet.domain.WalletCommand.Create;
import com.example.wallet.domain.WalletCommand.Delete;
import com.example.wallet.domain.WalletCommand.Deposit;
import com.example.wallet.domain.WalletCommand.DepositTransferFunds;
import com.example.wallet.domain.WalletCommand.TransferFunds;
import com.example.wallet.domain.WalletCommand.Withdraw;
import com.example.wallet.domain.WalletEvent.FundsDeposited;
import com.example.wallet.domain.WalletEvent.FundsWithdrawn;
import com.example.wallet.domain.WalletEvent.TransferFundsDeposited;
import com.example.wallet.domain.WalletEvent.TransferFundsLocked;
import com.example.wallet.domain.WalletEvent.TransferFundsUnlocked;
import com.example.wallet.domain.WalletEvent.TransferFundsWithdrawn;
import com.example.wallet.domain.WalletEvent.TransferRejected;
import com.example.wallet.domain.WalletEvent.WalletCreated;
import com.example.wallet.domain.WalletEvent.WalletDeleted;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.wallet.domain.Or.left;
import static com.example.wallet.domain.Or.right;
import static com.example.wallet.domain.WalletError.INVALID_AMOUNT;
import static com.example.wallet.domain.WalletError.INVALID_TRANSFER_AMOUNT;
import static com.example.wallet.domain.WalletError.LOCK_NOT_FOUND;
import static com.example.wallet.domain.WalletError.NOT_SUFFICIENT_FUNDS;

public record Wallet(String id, String ownerId, int balance, Map<String, TransferLock> locks) {

  public static final Wallet EMPTY = new Wallet("", "", 0, new HashMap<>());

  public Or<WalletError, WalletEvent> process(WalletCommand walletCommand) {
    return switch (walletCommand) {
      case Create create -> handle(create);
      case Deposit deposit -> handle(deposit);
      case Withdraw withdraw -> handle(withdraw);
      case TransferFunds transferFunds -> handle(transferFunds);
      case DepositTransferFunds depositTransferFunds -> handle(depositTransferFunds);
      case ConfirmTransferDeposit confirmTransferDeposit -> handle(confirmTransferDeposit);
      case AbortTransfer abortTransfer -> handle(abortTransfer);
      case Delete delete -> handle(delete);
    };
  }

  private Or<WalletError, WalletEvent> handle(Create create) {
    if (create.initBalance() <= 0) {
      return left(INVALID_AMOUNT);
    } else {
      return right(new WalletCreated(create.id(), create.ownerId(), create.initBalance()));
    }
  }

  private Or<WalletError, WalletEvent> handle(Deposit deposit) {
    var amount = deposit.amount();
    if (amount <= 0) {
      return left(INVALID_AMOUNT);
    } else {
      return right(new FundsDeposited(id, ownerId, amount, balance + amount));
    }
  }

  private Or<WalletError, WalletEvent> handle(TransferFunds transferFunds) {
    var amount = transferFunds.amount();
    if (amount <= 0 || amount > balance) {
      return left(INVALID_TRANSFER_AMOUNT);
    } else {
      return right(new TransferFundsLocked(id, amount, balance - amount, transferFunds.toWalletId(), newLockId()));
    }
  }

  private Or<WalletError, WalletEvent> handle(ConfirmTransferDeposit confirmTransferDeposit) {
    String lockId = confirmTransferDeposit.lockId();
    var lock = locks.get(lockId);
    if (lock == null) {
      return left(LOCK_NOT_FOUND);
    } else {
      return right(new TransferFundsWithdrawn(id, lock.amount(), balance, lockId));
    }
  }

  private Or<WalletError, WalletEvent> handle(AbortTransfer abortTransfer) {
    String lockId = abortTransfer.lockId();
    var lock = locks.get(lockId);
    if (lock == null) {
      return left(LOCK_NOT_FOUND);
    } else {
      return right(new TransferFundsUnlocked(id, lock.amount(), balance + lock.amount(), lockId));
    }
  }

  private Or<WalletError, WalletEvent> handle(DepositTransferFunds depositTransferFunds) {
    if (depositTransferFunds.fromWalletId().equals("terroristOrganisation")) {
      return right(new TransferRejected(id, depositTransferFunds.fromWalletId(), depositTransferFunds.lockId()));
    } else {
      return right(new TransferFundsDeposited(id, depositTransferFunds.amount(), balance + depositTransferFunds.amount(), depositTransferFunds.fromWalletId(), depositTransferFunds.lockId()));
    }
  }

  private Or<WalletError, WalletEvent> handle(Withdraw withdraw) {
    var amount = withdraw.amount();
    if (amount <= 0) {
      return left(INVALID_AMOUNT);
    } else if (amount > balance) {
      return left(NOT_SUFFICIENT_FUNDS);
    } else {
      return right(new FundsWithdrawn(id, ownerId, amount, balance - amount));
    }
  }

  private Or<WalletError, WalletEvent> handle(Delete __) {
    //some validation here
    return right(new WalletDeleted(id, ownerId));
  }

  public Wallet apply(WalletEvent walletEvent) {
    return switch (walletEvent) {
      case WalletCreated created -> new Wallet(created.walletId(), created.ownerId(), created.balance(), locks);
      case FundsDeposited fundsDeposited -> new Wallet(id, ownerId, fundsDeposited.balanceAfter(), locks);
      case FundsWithdrawn fundsWithdrawn -> new Wallet(id, ownerId, fundsWithdrawn.balanceAfter(), locks);
      case TransferFundsLocked transferFundsLocked -> {
        TransferLock transferLock = new TransferLock(transferFundsLocked.lockId(), transferFundsLocked.toWalletId(), transferFundsLocked.amount());
        locks.put(transferFundsLocked.lockId(), transferLock);
        yield new Wallet(id, ownerId, transferFundsLocked.balanceAfter(), locks);
      }
      case TransferRejected transferRejected -> this;
      case TransferFundsWithdrawn transferFundsWithdrawn -> {
        locks.remove(transferFundsWithdrawn.lockId());
        yield new Wallet(id, ownerId, balance, locks);
      }
      case TransferFundsDeposited transferFundsDeposited -> new Wallet(id, ownerId, transferFundsDeposited.balanceAfter(), locks);
      case TransferFundsUnlocked transferFundsUnlocked -> {
        locks.remove(transferFundsUnlocked.lockId());
        yield new Wallet(id, ownerId, transferFundsUnlocked.balanceAfter(), locks);
      }
      case WalletDeleted __ -> this; //ignore
    };
  }

  public boolean isEmpty() {
    return id.equals("");
  }

  private String newLockId() {
    return UUID.randomUUID().toString();
  }
}
