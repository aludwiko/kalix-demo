package com.example.wallet.ve;

import com.example.wallet.domain.WalletWithOwner;
import kalix.javasdk.annotations.Query;
import kalix.javasdk.annotations.Subscribe;
import kalix.javasdk.annotations.Table;
import kalix.javasdk.annotations.ViewId;
import kalix.javasdk.view.View;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;

@ViewId("wallet_by_owner-ve")
@Table("wallet_by_owner_ve")
public class WalletByOwner extends View<WalletWithOwner> {

  @GetMapping("/wallet-ve/by-owner/{ownerId}")
  @Query("SELECT * FROM wallet_by_owner_ve WHERE ownerId = :ownerId")
  public Flux<WalletWithOwner> getWalletByOwner(@PathVariable String ownerId) {
    return null;
  }

  @Subscribe.ValueEntity(value = WalletEntity.class)
  public UpdateEffect<WalletWithOwner> onChange(WalletVE wallet) {
    return effects().updateState(new WalletWithOwner(wallet.id(), wallet.ownerId()));
  }
}
