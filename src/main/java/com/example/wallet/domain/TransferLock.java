package com.example.wallet.domain;

public record TransferLock(String id, String destinationWalletId, int amount) {
}