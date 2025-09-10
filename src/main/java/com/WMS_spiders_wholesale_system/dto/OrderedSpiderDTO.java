package com.WMS_spiders_wholesale_system.dto;

import java.util.UUID;

public class OrderedSpiderDTO {
    private UUID spiderId;
    private String typeName;
    private String speciesName;
    private int quantity;
    private double price;
    private String size;

    public UUID getSpiderId() {
        return spiderId;
    }

    public void setSpiderId(UUID spiderId) {
        this.spiderId = spiderId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}