package com.example.wallet.application;

import com.example.wallet.domain.WalletWithBalance;
import com.example.wallet.domain.WalletWithOwner;
import kalix.spring.testkit.KalixIntegrationTestKitSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.wallet.application.WalletRequests.randomOwnerId;
import static com.example.wallet.application.WalletRequests.randomWalletId;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@DirtiesContext
@SpringBootTest
class WalletViewsTest extends KalixIntegrationTestKitSupport {

  @Autowired
  private WalletRequests walletRequests;

  @Test
  public void shouldFindByBalanceBelow() {
    //given
    String walletId1 = randomWalletId();
    String walletId2 = randomWalletId();
    String ownerId = randomOwnerId();
    walletRequests.createWallet(walletId1, ownerId, 100);
    walletRequests.createWallet(walletId2, ownerId, 200);

    await()
      .atMost(10, TimeUnit.of(SECONDS))
      .untilAsserted(() -> {
        //when
        List<WalletWithBalance> result = walletRequests.findByBalanceBelow(150);

        //then
        assertThat(result).containsOnly(new WalletWithBalance(walletId1, 100));
      });
  }

  @Test
  public void shouldFindWithBalanceBelow() {
    //given
    String walletId1 = randomWalletId();
    String walletId2 = randomWalletId();
    String walletId3 = randomWalletId();
    String ownerId1 = randomOwnerId();
    String ownerId2 = randomOwnerId();
    walletRequests.createWallet(walletId1, ownerId1, 100);
    walletRequests.createWallet(walletId2, ownerId1, 200);
    walletRequests.createWallet(walletId3, ownerId2, 200);


    await()
      .atMost(10, TimeUnit.of(SECONDS))
      .untilAsserted(() -> {
        //when
        List<WalletWithOwner> result = walletRequests.findByOwnerId(ownerId1);

        //then
        assertThat(result).containsOnly(new WalletWithOwner(walletId1, ownerId1), new WalletWithOwner(walletId2, ownerId1));
      });
  }
}