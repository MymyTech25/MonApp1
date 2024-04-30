package com.example.monapp1;

import android.location.Address;
import android.location.Location;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandeDTO {
    private double prix;
    private Date date;
    private String adresse;
    private List<Product> productsList = new ArrayList<>();

    private GeoPoint point;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public int getAttribué() {
        return attribué;
    }

    public void setAttribué(int attribué) {
        this.attribué = attribué;
    }

    private int attribué;

    public CommandeDTO() {
    }

    public GeoPoint getPoint() {
        return point;
    }

    public void setPoint(double latitude, double longitude) {
        this.point = new GeoPoint(latitude, longitude);
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public List<Product> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Product> productsList) {
        this.productsList = productsList;
    }







}
