package com.example.honeyimhome;

public class LocationInfo {
    private double latitude;
    private double langitude;
    private float accuracy;


    LocationInfo(double latitude, double langitude, float accuracy){
        this.latitude = latitude;
        this.langitude = langitude;
        this.accuracy = accuracy;
    }

    LocationInfo(){}

    public double getLatitude(){
        return latitude;
    }
    public double getLangitude(){
        return langitude;
    }
    public float getAccuracy(){
        return accuracy;
    }
}
