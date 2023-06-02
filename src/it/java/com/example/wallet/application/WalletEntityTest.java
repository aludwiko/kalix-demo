package com.example.wallet.application;

import kalix.spring.testkit.KalixIntegrationTestKitSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@DirtiesContext
@SpringBootTest
class WalletEntityTest extends KalixIntegrationTestKitSupport {

  @Autowired
  private WalletRequests walletRequests;

  @Test
  public void shouldCreateWallet() {
    //given
    String walletId = "1";
    String ownerId = "o1";

    //when
    walletRequests.createWallet(walletId, ownerId, 100);

    //then
    WalletResponse wallet = walletRequests.getWallet(walletId);
    assertThat(wallet).isEqualTo(new WalletResponse(walletId, ownerId, 100));
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
    WalletResponse wallet = walletRequests.getWallet(walletId);
    assertThat(wallet).isEqualTo(new WalletResponse(walletId, ownerId, 150));
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
    WalletResponse wallet = walletRequests.getWallet(walletId);
    assertThat(wallet).isEqualTo(new WalletResponse(walletId, ownerId, 50));
  }

  @Test
  public void shouldTransferFunds() {
    //given
    String walletId1 = "4";
    String walletId2 = "5";
    String ownerId1 = "o1";
    String ownerId2 = "o2";
    walletRequests.createWallet(walletId1, ownerId1, 100);
    walletRequests.createWallet(walletId2, ownerId2, 100);

    //when
    walletRequests.transferFunds(walletId1, walletId2, 50);

    //then
    await()
      .atMost(10, TimeUnit.of(SECONDS))
      .untilAsserted(() -> {
        WalletResponse wallet1 = walletRequests.getWallet(walletId1);
        WalletResponse wallet2 = walletRequests.getWallet(walletId2);

        //then
        assertThat(wallet1).isEqualTo(new WalletResponse(walletId1, ownerId1, 50));
        assertThat(wallet2).isEqualTo(new WalletResponse(walletId2, ownerId2, 150));
      });
  }

  @Test
  public void shouldAbortTransferFunds() {
    //given
    String walletId1 = "terroristOrganisation";
    String walletId2 = "6";
    String ownerId1 = "o1";
    String ownerId2 = "o2";
    walletRequests.createWallet(walletId1, ownerId1, 100);
    walletRequests.createWallet(walletId2, ownerId2, 100);

    //when
    walletRequests.transferFunds(walletId1, walletId2, 50);

    //then
    WalletResponse walletLocked = walletRequests.getWallet(walletId1);
    assertThat(walletLocked).isEqualTo(new WalletResponse(walletId1, ownerId1, 50));

    await()
      .atMost(10, TimeUnit.of(SECONDS))
      .untilAsserted(() -> {
        WalletResponse wallet1 = walletRequests.getWallet(walletId1);
        WalletResponse wallet2 = walletRequests.getWallet(walletId2);

        //then
        assertThat(wallet1).isEqualTo(new WalletResponse(walletId1, ownerId1, 100));
        assertThat(wallet2).isEqualTo(new WalletResponse(walletId2, ownerId2, 100));
      });
  }
}