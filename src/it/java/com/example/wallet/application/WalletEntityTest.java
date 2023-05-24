package com.example.wallet.application;

import com.example.wallet.domain.Wallet;
import kalix.spring.testkit.KalixIntegrationTestKitSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WalletEntityTest extends KalixIntegrationTestKitSupport {

  @Autowired
  private WalletRequests walletRequests;

  private Duration timeout = Duration.of(10, SECONDS);

  @Test
  public void shouldCreateWallet() {
    //given
    String walletId = "1";
    String ownerId = "o1";

    //when
    walletRequests.createWallet(walletId, ownerId, 100);

    //then
    Wallet wallet = walletRequests.getWallet(walletId);
    assertThat(wallet).isEqualTo(new Wallet(walletId, ownerId, 100));
  }

  @Test
  public void shouldDepositFunds() {
    //given
    String walletId = "2";
    String ownerId = "o1";
    walletRequests.createWallet(walletId, ownerId, 100);

    //when
    walletRequests.depositFunds(walletId, 50);

    //then
    Wallet wallet = walletRequests.getWallet(walletId);
    assertThat(wallet).isEqualTo(new Wallet(walletId, ownerId, 150));
  }

  @Test
  public void shouldWithdrawFunds() {
    //given
    String walletId = "3";
    String ownerId = "o1";
    walletRequests.createWallet(walletId, ownerId, 100);

    //when
    walletRequests.withdrawFunds(walletId, 50);

    //then
    Wallet wallet = walletRequests.getWallet(walletId);
    assertThat(wallet).isEqualTo(new Wallet(walletId, ownerId, 50));
  }
}