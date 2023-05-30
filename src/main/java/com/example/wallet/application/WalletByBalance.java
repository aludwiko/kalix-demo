package com.example.wallet.application;

import com.example.wallet.domain.WalletEvent.FundsDeposited;
import com.example.wallet.domain.WalletEvent.FundsWithdrawn;
import com.example.wallet.domain.WalletEvent.WalletCreated;
import com.example.wallet.domain.WalletEvent.WalletDeleted;
import com.example.wallet.domain.WalletWithBalance;
import kalix.javasdk.annotations.Query;
import kalix.javasdk.annotations.Subscribe;
import kalix.javasdk.annotations.Table;
import kalix.javasdk.annotations.ViewId;
import kalix.javasdk.view.View;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;

@ViewId("wallet_by_balance")
@Table("wallet_by_balance")
@Subscribe.EventSourcedEntity(WalletEntity.class)
public class WalletByBalance extends View<WalletWithBalance> {

  @GetMapping("/wallet/by-balance-below/{balance}")
  @Query("SELECT * FROM wallet_by_balance WHERE balance < :balance")
  public Flux<WalletWithBalance> getWalletWithBalanceBelow(@PathVariable int balance) {
    return null;
  }

  public UpdateEffect<WalletWithBalance> handle(WalletCreated walletCreated) {
    WalletWithBalance newState = new WalletWithBalance(walletCreated.walletId(), walletCreated.balance());
    return effects().updateState(newState);
  }

  public UpdateEffect<WalletWithBalance> handle(FundsDeposited fundsDeposited) {
    WalletWithBalance newState = new WalletWithBalance(fundsDeposited.walletId(), fundsDeposited.balanceAfter());
    return effects().updateState(newState);
  }

  public UpdateEffect<WalletWithBalance> handle(FundsWithdrawn fundsWithdrawn) {
    WalletWithBalance newState = new WalletWithBalance(fundsWithdrawn.walletId(), fundsWithdrawn.balanceAfter());
    return effects().updateState(newState);
  }

  public UpdateEffect<WalletWithBalance> handle(WalletDeleted __) {
    return effects().deleteState();
  }
}
