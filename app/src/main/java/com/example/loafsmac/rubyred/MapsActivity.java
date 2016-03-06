package com.example.loafsmac.rubyred;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    HashMap<String, Trip> tripsMap;
    ArrayList<String> keysList = new ArrayList<>();
    PolylineOptions polylineOptions;
    int currentIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loadAllPointsFromParse();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    upPressed();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    downPressed();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    public void downPressed(){
        if (currentIndex != 0){
            currentIndex--;
            loadPinsFromTrip(tripsMap.get(keysList.get(currentIndex)));
        }
    }
    public void upPressed(){
        if (currentIndex != keysList.size() - 1){
            currentIndex++;
            loadPinsFromTrip(tripsMap.get(keysList.get(currentIndex)));
        }
    }
    public void loadAllPointsFromParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locationList, ParseException e) {
                if (e == null) {
                    Log.d("location", "Retrieved " + locationList.size() + " locations");
                    ArrayList<TripPoint> tripPoints = new ArrayList<TripPoint>();
                    for (int i = 0; i < locationList.size(); i++) {
                        TripPoint tripPoint = new TripPoint();
                        tripPoint.setLat(locationList.get(i).getString("Lat"));
                        tripPoint.setLon(locationList.get(i).getString("Lon"));
                        tripPoint.setTripID(locationList.get(i).getString("tripID"));
                        tripPoint.setDate(locationList.get(i).getCreatedAt());
                        tripPoints.add(tripPoint);
                    }
                    tripsMap = tripPointsToTrips(tripPoints);
                    Set keys = tripsMap.keySet();
                    keysList.addAll(keys);
                    loadPinsFromTrip(tripsMap.get(keysList.get(currentIndex)));
                } else {
                    Log.d("location", "Error: " + e.getMessage());
                }
            }
        });
    }
    public void loadPinsFromTrip(Trip trip){
        mMap.clear();
        polylineOptions = new PolylineOptions();
        for (TripPoint tripPoint : trip.tripPointsList){
            LatLng latLng = new LatLng(new Double(tripPoint.getLat()),new Double(tripPoint.getLon()));
            polylineOptions.add(latLng);
            Marker pin = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(tripPoint.date.toString()));
        }
        centerZoomToPins();
    }
    private void centerZoomToPins() {
        List<LatLng> tripPointPins = polylineOptions.getPoints(); // route is instance of PolylineOptions
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng pin : tripPointPins) {
            builder.include(pin);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
    }
    public HashMap<String, Trip> tripPointsToTrips(ArrayList<TripPoint> tripPoints){
        HashMap<String, Trip> trips = new HashMap<String, Trip>();
        TripPoint startPoint = tripPoints.get(0);
        TripPoint endPoint = tripPoints.get(0);
        for (int i = 0; i < tripPoints.size(); i++){
            Trip trip = trips.get(tripPoints.get(i).getTripID());
            if (trip == null){
                Trip tripToAdd = new Trip();
                tripToAdd.tripID = tripPoints.get(i).getTripID();
                tripToAdd.tripPointsList.add(tripPoints.get(i));
                tripToAdd.endPoint = tripPoints.get(i);
                tripToAdd.startPoint = tripPoints.get(i);
                trips.put(tripPoints.get(i).getTripID(), tripToAdd);
            }
            else {
                Trip tripToUpdate = trips.get(tripPoints.get(i).getTripID());
                tripToUpdate.tripPointsList.add(tripPoints.get(i));
                if (tripPoints.get(i).date.getTime() <= tripToUpdate.startPoint.date.getTime()){
                    tripToUpdate.startPoint.date = tripPoints.get(i).date;
                }
                if (tripPoints.get(i).date.getTime() >= tripToUpdate.endPoint.date.getTime()){
                    tripToUpdate.endPoint.date = tripPoints.get(i).date;
                }
                trips.put(tripPoints.get(i).getTripID(), tripToUpdate);
            }
        }
        for (String key : trips.keySet()) {
            Log.d("tripParse", trips.get(key).tripID);
            Trip trip = trips.get(key);
            for (int i = 0; i < trip.tripPointsList.size(); i++){
                Log.d("tripPoint",trip.tripPointsList.get(i).getLat());
            }
        }
        return trips;
        //return new ArrayList<Trip>();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        MapsActivity.this.startActivity(intent);
        MapsActivity.this.finish();
    }

}
