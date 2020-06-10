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

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLangitude(double langitude){
        this.langitude = langitude;
    }
    public void setAccuracy(float accuracy){
        this.accuracy = accuracy;
    }
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
