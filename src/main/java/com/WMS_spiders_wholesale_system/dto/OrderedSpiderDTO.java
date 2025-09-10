package com.WMS_spiders_wholesale_system.dto;

import java.util.UUID;

public class OrderedSpiderDTO {
    private UUID spiderId;
    private int quantity;

    public UUID getSpiderId() {
        return spiderId;
    }

    public void setSpiderId(UUID spiderId) {
        this.spiderId = spiderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}