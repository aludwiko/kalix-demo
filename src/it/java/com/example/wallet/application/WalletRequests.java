package com.example.wallet.application;

import com.example.wallet.domain.WalletWithBalance;
import com.example.wallet.domain.WalletWithOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Component
public class WalletRequests {

  @Autowired
  private WebClient webClient;

  private Duration timeout = Duration.of(10, SECONDS);

  public void createWallet(String walletId, String ownerId, int initialBalance) {
    Response.Success response = webClient
      .post()
      .uri("/wallet/" + walletId + "/" + ownerId + "/" + initialBalance)
      .retrieve()
      .bodyToMono(Response.Success.class)
      .block(timeout);

    assertThat(response.message()).isEqualTo("wallet created");
  }

  public void depositFunds(String walletId, int amount) {
    Response.Success response = webClient
      .patch()
      .uri("/wallet/" + walletId + "/deposit/" + amount)
      .retrieve()
      .bodyToMono(Response.Success.class)
      .block(timeout);

    assertThat(response.message()).isEqualTo("ok");
  }

  public void withdrawFunds(String walletId, int amount) {
    Response.Success response = webClient
      .patch()
      .uri("/wallet/" + walletId + "/withdraw/" + amount)
      .retrieve()
      .bodyToMono(Response.Success.class)
      .block(timeout);

    assertThat(response.message()).isEqualTo("ok");
  }

  public void transferFunds(String fromWalletId, String toWalletId, int amount) {
    Response.Success response = webClient
      .patch()
      .uri("/wallet/" + fromWalletId + "/transfer/" + toWalletId + "/" + amount)
      .retrieve()
      .bodyToMono(Response.Success.class)
      .block(timeout);

    assertThat(response.message()).isEqualTo("ok");
  }

  public WalletResponse getWallet(String walletId) {
    return webClient
      .get()
      .uri("/wallet/" + walletId)
      .retrieve()
      .bodyToMono(WalletResponse.class)
      .block(timeout);
  }

  public static String randomWalletId() {
    return randomString();
  }

  public static String randomOwnerId() {
    return randomString();
  }

  private static String randomString() {
    return UUID.randomUUID().toString().substring(8);
  }

  public List<WalletWithBalance> findByBalanceBelow(int balance) {
    return webClient
      .get()
      .uri("/wallet/by-balance-below/" + balance)
      .retrieve()
      .bodyToFlux(WalletWithBalance.class)
      .toStream()
      .toList();
  }

  public List<WalletWithOwner> findByOwnerId(String ownerId) {
    return webClient
      .get()
      .uri("/wallet/by-owner/" + ownerId)
      .retrieve()
      .bodyToFlux(WalletWithOwner.class)
      .toStream()
      .toList();
  }
}
