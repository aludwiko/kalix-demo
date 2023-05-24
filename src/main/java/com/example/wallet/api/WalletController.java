package com.example.wallet.api;

import com.example.wallet.application.Response;
import kalix.javasdk.action.Action;
import kalix.spring.KalixClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/public/wallet/{id}")
public class WalletController extends Action {

  private final KalixClient kalixClient;

  public WalletController(KalixClient kalixClient) {this.kalixClient = kalixClient;}

  @PostMapping("/{ownerId}/{initBalance}")
  public Effect<Response> create(@PathVariable String id, @PathVariable String ownerId, @PathVariable int initBalance) {
    //pre validation here
    return effects().forward(kalixClient.post("/wallet/" + id + "/" + ownerId + "/" + initBalance, Response.class));
  }
}
