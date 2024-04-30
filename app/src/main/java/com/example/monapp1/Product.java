package com.example.monapp1;

import java.io.Serializable;

public class Product implements Serializable  {
    private String nom;
    private double prix;
    private int quantity;



    private double total;
    private String image;

    public Product() {
        this.quantity = 1; // Par défaut, la quantité est 1
        this.total = prix;
    }

    public Product(String nom, double prix) {
        this.nom = nom;
        this.prix = prix;
        this.quantity = 1;
        this.total = prix;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }

    public void increaseQuantity() {
        quantity++;
        total = quantity * prix;
    }

    public void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            total = quantity * prix;
        }
    }
}
