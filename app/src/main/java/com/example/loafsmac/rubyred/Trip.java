package com.example.loafsmac.rubyred;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by loafsmac on 2/8/16.
 */
public class Trip {
    ArrayList<TripPoint> tripPointsList = new ArrayList<>();
    TripPoint startPoint;
    TripPoint endPoint;
    String tripID;


    public TripPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(TripPoint startPoint) {
        this.startPoint = startPoint;
    }

    public TripPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(TripPoint endPoint) {
        this.endPoint = endPoint;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }



    public Trip (){

    }


    public void setTripPointsList(ArrayList<TripPoint> tripPointsList) {
        this.tripPointsList = tripPointsList;
    }


    public ArrayList<TripPoint> getTripPointsList() {
        return tripPointsList;
    }

}