package com.example.wallet.api;

import com.example.wallet.application.Response;
import com.example.wallet.application.WalletEntity;
import kalix.javasdk.action.Action;
import kalix.javasdk.client.ComponentClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/public/wallet/{id}")
public class WalletController extends Action {

  private final ComponentClient componentClient;

  public WalletController(ComponentClient componentClient) {this.componentClient = componentClient;}

  @PostMapping("/{ownerId}/{initBalance}")
  public Effect<Response> create(@PathVariable String id, @PathVariable String ownerId, @PathVariable int initBalance) {
    //pre validation here
    return effects().forward(
      componentClient.forEventSourcedEntity(id)
        .call(WalletEntity::create)
        .params(id, ownerId, initBalance)
    );
  }
}
