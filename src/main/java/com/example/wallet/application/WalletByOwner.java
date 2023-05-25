package com.example.wallet.application;

import com.example.wallet.domain.Wallet;
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
public class WalletByOwner extends View<WalletWithOwner> {

  @GetMapping("/wallet/by-owner/{ownerId}")
  @Query("SELECT * FROM wallet_by_owner WHERE ownerId = :ownerId")
  public Flux<WalletWithOwner> getWalletByOwner(@PathVariable String ownerId) {
    return null;
  }

  @Subscribe.ValueEntity(WalletEntity.class)
  public UpdateEffect<WalletWithOwner> onUpdate(Wallet wallet) {
    return effects().updateState(new WalletWithOwner(wallet.id(), wallet.ownerId()));
  }

  @Subscribe.ValueEntity(value = WalletEntity.class, handleDeletes = true)
  public UpdateEffect<WalletWithOwner> onDelete() {
    return effects().deleteState();
  }
}
