package com.example.loafsmac.rubyred;

import java.util.Date;

/**
 * Created by loafsmac on 2/8/16.
 */
public class TripPoint {
    String lat;
    String lon;
    String tripID;
    Date date;


    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
