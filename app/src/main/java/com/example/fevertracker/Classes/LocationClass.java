package com.example.fevertracker.Classes;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.fevertracker.Activities.LocationHistory.circleRange;
import static com.example.fevertracker.Activities.LocationHistory.maxInSec;

public class LocationClass {
    ArrayList<Long> unixTime = new ArrayList<>();     //from database
    ArrayList<LatLng> locations = new ArrayList<>();

    public ArrayList<String> timeTitle = new ArrayList<>();   //for all  //compute later
    public ArrayList<LatLng> LocationsCompressed = new ArrayList<>();   //for all
    public ArrayList<Boolean> isStop = new ArrayList<>();     //for all

    public ArrayList<String> distance = new ArrayList<>();    //for stops
    public ArrayList<String> timeTaken = new ArrayList<>();     //for stops

    public void compute() {
        if (unixTime.size() > 0) {
//            Collections.sort(unixTime);

            LatLng currLoc = new LatLng(locations.get(0).latitude, locations.get(0).longitude);
            double totalLat = 0, totalLong = 0, counter = 0;
            long currTime = unixTime.get(0);
            long currTimeStop = unixTime.get(0), temp;
            double TotalDistance = 0;
            for (int i = 0; i < unixTime.size(); i++) {
                if (distance(currLoc, locations.get(i)) > circleRange || i == (unixTime.size() - 1)) {

                    i--;
                    timeTitle.add(ConvertTime(currTime) + " - " + ConvertTime(unixTime.get(i)));
                    LocationsCompressed.add(currLoc);
                    int size = LocationsCompressed.size();
                    if (size > 1) {
                        TotalDistance += distance(LocationsCompressed.get(size - 2), LocationsCompressed.get(size - 1));
                    }
                    if ((unixTime.get(i) - currTime) >= maxInSec) {
                        temp = currTime;
                        isStop.add(true);
                        timeTaken.add(getTimeTaken(temp - currTimeStop));
                        distance.add((round(TotalDistance / 1000d, 1)) + " km");
                        currTimeStop = unixTime.get(i);
                        TotalDistance = 0;
                    } else {
                        isStop.add(false);
                        timeTaken.add("");
                        distance.add("");
                    }

                    i++;
                    totalLat = 0;
                    totalLong = 0;
                    counter = 0;
                    currTime = unixTime.get(i);
                }

                totalLat += locations.get(i).latitude;
                totalLong += locations.get(i).longitude;
                counter++;

                currLoc = new LatLng(totalLat / counter, totalLong / counter);
            }
        }
    }

    public void add(String unixString, String LocationString) {
        unixTime.add(getLong(unixString));
        locations.add(getLocation(LocationString));
    }

    public void clear() {
        timeTitle.clear();
        unixTime.clear();
        locations.clear();
        distance.clear();
        timeTaken.clear();
        LocationsCompressed.clear();
        isStop.clear();
    }

    public Long getLong(String unixString) {
        try {
            return Long.parseLong(unixString);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public LatLng getLocation(String LocationString) {
        try {
            String[] arr = LocationString.split(",", -1);
            return new LatLng(getDouble(arr[0]), getDouble(arr[1]));
        } catch (Exception ignored) {
            return new LatLng(0, 0);
        }
    }

    public double getDouble(String latlng) {
        try {
            return Double.parseDouble(latlng);
        } catch (Exception ignored) {
            return 0D;
        }
    }

    public String ConvertTime(Long seconds) {
        Calendar cal1 = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");
        cal1.setTimeInMillis(seconds * 1000);
        return dateFormat.format(cal1.getTime());
    }

    public double distance(LatLng latLng1, LatLng latLng2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.

        double lat1 = latLng1.latitude, lat2 = latLng2.latitude, lon1 = latLng1.longitude, lon2 = latLng2.longitude;


        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r * 1000);
    }

    public String getTimeTaken(long value) {
        String timeString = "";
        long hours = 0, minutes = 0;
        while (value >= 60) {
            if (value >= 3600) {
                value -= 3600;
                hours += 1;
            } else {
                value -= 60;
                minutes += 1;
            }
        }
        if (hours > 0) {
            timeString += hours + " h";
            if (minutes > 0) {
                timeString += ", ";
            }
        }
        if (minutes > 0) {
            timeString += minutes + " min";
            if (value > 0 && hours == 0) {
                timeString += ", ";
            }
        }
        if (hours == 0 && value > 0) {
            timeString += value + " sec";
        }
        if (timeString.isEmpty()) {
            return "0 sec";
        }
        return timeString;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
