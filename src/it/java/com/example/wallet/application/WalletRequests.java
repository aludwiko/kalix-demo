package com.example.wallet.application;

import com.example.wallet.domain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

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

  public Wallet getWallet(String walletId) {
    return webClient
      .get()
      .uri("/wallet/" + walletId)
      .retrieve()
      .bodyToMono(Wallet.class)
      .block(timeout);
  }
}
