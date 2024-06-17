package com.example.wallet.ve;

import com.example.wallet.application.Response;
import com.example.wallet.application.Response.Success;
import com.example.wallet.domain.Or.Left;
import com.example.wallet.domain.Or.Right;
import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.valueentity.ValueEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Id("id")
@TypeId("wallet-ve")
@RequestMapping("/wallet-ve/{id}")
public class WalletEntity extends ValueEntity<WalletVE> {

  @PostMapping("/{ownerId}/{initBalance}")
  public Effect<Response> create(@PathVariable String id, @PathVariable String ownerId, @PathVariable int initBalance) {
    if (currentState() != null) {
      return effects().error("wallet already created");
    } else {
      return effects()
        .updateState(new WalletVE(id, ownerId, initBalance))
        .thenReply(Success.of("wallet created"));
    }
  }

  @PatchMapping("/deposit/{amount}")
  public Effect<Response> deposit(@PathVariable int amount) {
    if (currentState() == null) {
      return effects().error("wallet not created");
    } else {
      WalletVE walletVE = currentState();
      return switch (walletVE.deposit(amount)) {
        case Left(var error) -> effects().error(error.name());
        case Right(var updatedWallet) -> effects().updateState(updatedWallet).thenReply(Success.of("ok"));
      };
    }
  }

  @PatchMapping("/withdraw/{amount}")
  public Effect<Response> withdraw(@PathVariable int amount) {
    if (currentState() == null) {
      return effects().error("wallet not created");
    } else {
      return switch (currentState().withdraw(amount)) {
        case Left(var error) -> effects().error(error.name());
        case Right(var updatedWallet) -> effects().updateState(updatedWallet).thenReply(Success.of("ok"));
      };
    }
  }

  @GetMapping
  public Effect<WalletVE> get() {
    if (currentState() == null) {
      return effects().error("wallet not created");
    } else {
      return effects().reply(currentState());
    }
  }

  @DeleteMapping
  public Effect<Response> delete() {
    if (currentState() != null) {
      return effects().error("wallet not created");
    } else {
      return effects()
        .deleteEntity()
        .thenReply(Success.of("wallet deleted"));
    }
  }

}
