package com.example.fevertracker.Classes;

public class PlacesClass {
    private String Placename;
    private String TimeInter;
    private String PlaceDetails;
    private String TimeInRoad;
    private String Distance;
    private String LatLon;

    public String getPlacename() {
        return Placename;
    }

    public String getTimeInter() {
        return TimeInter;
    }

    public String getPlaceDetails() {
        return PlaceDetails;
    }

    public String getTimeInRoad() {
        return TimeInRoad;
    }

    public String getDistance() {
        return Distance;
    }

    public String getLatLon() {
        return LatLon;
    }

    public PlacesClass(String Placename, String TimeInter, String PlaceDetails, String TimeInRoad, String Distance, String LatLon) {
        this.Placename = Placename;
        this.TimeInter = TimeInter;
        this.PlaceDetails = PlaceDetails;
        this.TimeInRoad = TimeInRoad;
        this.Distance = Distance;
        this.LatLon = LatLon;
    }
}
