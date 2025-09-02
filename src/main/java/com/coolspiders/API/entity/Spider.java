package com.coolspiders.API.entity;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "spiders")
public class Spider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;
    @Column(name = "type_name")
    private String typeName;
    @Column(name = "species_name")
    private String speciesName;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "size")
    private String size;
    @Column(name = "price")
    private int price;
    @Column(name = "is_cites")
    private boolean isCites;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public Spider() {
    }

    public Spider(String typeName, String speciesName, int quantity, String size, int price, boolean isCites) {
        this.typeName = typeName;
        this.speciesName = speciesName;
        this.quantity = quantity;
        this.size = size;
        this.price = price;
        this.isCites = isCites;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
        String[] typeNames = {"Brachypelma", "Tliltocatl", "Poecilotheria"};
        this.isCites = false;
        for (String type : typeNames) {
            if (typeName.equalsIgnoreCase(type)) {
                this.isCites = true;
                break;
            }
        }
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isCites() {
        return isCites;
    }


}
