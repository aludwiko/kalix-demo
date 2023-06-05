package com.example.wallet.application;

import com.example.wallet.application.Response.Success;
import com.example.wallet.domain.TransferState;
import kalix.spring.testkit.KalixIntegrationTestKitSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.example.wallet.domain.TransferStatus.ABORTED;
import static com.example.wallet.domain.TransferStatus.COMPLETED;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;


@DirtiesContext
@SpringBootTest
class TransferWorkflowTest extends KalixIntegrationTestKitSupport {

  @Autowired
  private WalletRequests walletRequests;

  @Autowired
  private WebClient webClient;

  private Duration timeout = Duration.of(10, SECONDS);

  @Test
  public void shouldTransferFunds() {
    //given
    String walletId1 = "1";
    String walletId2 = "2";
    String ownerId1 = "o1";
    String ownerId2 = "o2";
    String transferId = "t1";

    walletRequests.createWallet(walletId1, ownerId1, 100);
    walletRequests.createWallet(walletId2, ownerId2, 100);

    //when
    runTransfer(transferId, walletId1, walletId2, 50);

    //then
    await()
      .atMost(10, TimeUnit.of(SECONDS))
      .ignoreExceptions()
      .untilAsserted(() -> {
        WalletResponse wallet1 = walletRequests.getWallet(walletId1);
        WalletResponse wallet2 = walletRequests.getWallet(walletId2);
        TransferState transferState = getTransfer(transferId);

        //then
        assertThat(transferState.transferStatus()).isEqualTo(COMPLETED);
        assertThat(wallet1).isEqualTo(new WalletResponse(walletId1, ownerId1, 50));
        assertThat(wallet2).isEqualTo(new WalletResponse(walletId2, ownerId2, 150));
      });
  }

  @Test
  public void shouldAbortFundsTransfer() {
    //given
    String walletId1 = "3";
    String walletId2 = "4";
    String ownerId1 = "o1";
    String ownerId2 = "o2";
    String transferId = "t2";

    walletRequests.createWallet(walletId1, ownerId1, 100);
//    walletRequests.createWallet(walletId2, ownerId2, 100);

    //when
    runTransfer(transferId, walletId1, walletId2, 50);

    //then
    await()
      .atMost(30, TimeUnit.of(SECONDS))
      .ignoreExceptions()
      .untilAsserted(() -> {
        WalletResponse wallet1 = walletRequests.getWallet(walletId1);
        TransferState transferState = getTransfer(transferId);

        //then
        assertThat(transferState.transferStatus()).isEqualTo(ABORTED);
        assertThat(wallet1).isEqualTo(new WalletResponse(walletId1, ownerId1, 100));
      });
  }

  private void runTransfer(String transferId, String from, String to, int amount) {
    Success response = webClient
      .post()
      .uri("/transfer/" + transferId + "/" + from + "/" + to + "/" + amount)
      .retrieve()
      .bodyToMono(Success.class)
      .block(timeout);

    //then
    assertThat(response.message()).isEqualTo("transfer started");
  }

  private TransferState getTransfer(String transferId) {
    return webClient
      .get()
      .uri("/transfer/" + transferId)
      .retrieve()
      .bodyToMono(TransferState.class)
      .block(timeout);
  }

}