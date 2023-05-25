package com.example.wallet.application;

import com.example.wallet.domain.Wallet;
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
@Subscribe.ValueEntity(WalletEntity.class)
public class WalletByBalance extends View<Wallet> {

  @GetMapping("/wallet/by-balance-below/{balance}")
  @Query("SELECT * FROM wallet_by_balance WHERE balance < :balance")
  public Flux<Wallet> getWalletWithBalanceBelow(@PathVariable int balance) {
    return null;
  }
}
