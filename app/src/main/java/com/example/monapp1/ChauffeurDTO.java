package com.example.monapp1;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class ChauffeurDTO {


    private ArrayList<GeoPoint> point;



    public ArrayList<GeoPoint> getPoint() {
        return point;
    }

    public void setPoint(ArrayList<GeoPoint> point) {
        this.point = point;
    }


    public ChauffeurDTO() {

    }
    public ChauffeurDTO(ArrayList<GeoPoint> point) {
        this.point = point;
    }
    @Override
    public String toString() {
        return "ChauffeurDTO{" +
                "point=" + point +
                '}';
    }



}
