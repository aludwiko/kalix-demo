package com.example.wallet.application;

import com.example.wallet.domain.WalletEvent.WalletCreated;
import com.example.wallet.domain.WalletEvent.WalletDeleted;
import com.example.wallet.domain.WalletWithOwner;
import kalix.javasdk.annotations.Query;
import kalix.javasdk.annotations.Subscribe;
import kalix.javasdk.annotations.Table;
import kalix.javasdk.annotations.ViewId;
import kalix.javasdk.view.View;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;

@ViewId("wallet_by_owner")
@Table("wallet_by_owner")
@Subscribe.EventSourcedEntity(value = WalletEntity.class, ignoreUnknown = true)
public class WalletByOwner extends View<WalletWithOwner> {

  @GetMapping("/wallet/by-owner/{ownerId}")
  @Query("SELECT * FROM wallet_by_owner WHERE ownerId = :ownerId")
  public Flux<WalletWithOwner> getWalletByOwner(@PathVariable String ownerId) {
    return null;
  }

  public UpdateEffect<WalletWithOwner> handle(WalletCreated walletCreated) {
    return effects().updateState(new WalletWithOwner(walletCreated.walletId(), walletCreated.ownerId()));
  }

  public UpdateEffect<WalletWithOwner> handle(WalletDeleted __) {
    return effects().deleteState();
  }
}
