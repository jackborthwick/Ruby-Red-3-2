package com.example.loafsmac.rubyred;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.DeviceInfo;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
            implements  GridMenuFragment.OnFragmentInteractionListener{

    final ArrayList<Bean> beans = new ArrayList<>();
    Bean beanConnected;
    BeanListener beanListener;
    PendingIntent sentPI;
    SmsManager sms;
//    ParseManager parseManager;
    LocationManager locationManager;
    TextView textView;
    Location currentLocation;
    Boolean tracking = false;
    LocationListener locationListener;
    UUID currentUUID;
    Boolean connectedToParse = false;
    HashMap<String, Trip> tripsMap;
    String message = "generic message";
    ArrayList<String[]> numberList;

    private GridMenuFragment mGridMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadAllNumbersFromParse();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.safeguard);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        loadMessage();
        /**

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = GridMenuFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit();
        }
         */

        mGridMenuFragment = GridMenuFragment.newInstance(R.layout.activity_main);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, mGridMenuFragment);
        tx.addToBackStack(null);
        tx.commit();


        setupGridMenu();


        mGridMenuFragment.setOnClickMenuListener(new GridMenuFragment.OnClickMenuListener() {
            @Override
            public void onClickMenu(GridMenu gridMenu, int position) {
                //Toast.makeText(MainActivity.this, "Title:" + gridMenu.getTitle() + ", Position:" + position, Toast.LENGTH_SHORT).show();

                int id = position;
                if (id == 0){
                    Intent intent = new Intent(MainActivity.this, MessageEditActivity.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
                else if (id == 1) {
                    Intent intent = new Intent(MainActivity.this, SetupCalls.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
                else if(id == 2) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity2.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
//                    fragmentClass = SetupMessagesFragment.class;
//                }

//                Fragment fragment = null;
//                Class fragmentClass = null;
//                if (id == 0) {
//                    fragmentClass = SetupMessagesFragment.class;
//                }
//                else if (id == 1) {
//                    fragmentClass = SetupCallsFragment.class;
//                }
//                else if (id == 2) {
//                    fragmentClass = SetupCallsFragment.class;
//                }
//
//                try {
//                    fragment = (Fragment) fragmentClass.newInstance();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit();

            }
        });

        sms = SmsManager.getDefault();
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        discoverBean();
    }
    @Override
    public void onResume(){
        super.onResume();
        loadAllNumbersFromParse();
        loadMessage();
    }
    private void setupGridMenu() {
        List<GridMenu> menus = new ArrayList<>();
        menus.add(new GridMenu("Send Message", R.drawable.chat21));
        menus.add(new GridMenu("Setup Calls", R.drawable.telephone60));
        menus.add(new GridMenu("View Trips", R.drawable.map32));
        mGridMenuFragment.setupMenu(menus);
    }

    @Override
    public void onBackPressed() {
        if (0 == getSupportFragmentManager().getBackStackEntryCount()) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (beanConnected != null) {
            if (beanConnected.isConnected())
                beanConnected.disconnect();
        }
    }

    public void discoverBean(){
        BeanDiscoveryListener listener = new BeanDiscoveryListener() {
            @Override
            public void onBeanDiscovered(Bean bean, int rssi) {
                Log.i("OBD","size in onBeanDiscovered" + beans.size() + "------------------");
                beans.add(bean);
                beanConnected = bean;
            }
            @Override
            public void onDiscoveryComplete() {
                for (Bean bean : beans) {
                    System.out.println(bean.getDevice().getName());   // "Bean"              (example)
                    System.out.println(bean.getDevice().getAddress());    // "B4:99:4C:1E:BC:75" (example)
                }
                beansDiscovered();
            }
        };
        BeanManager.getInstance().startDiscovery(listener);
    }

    public void beansDiscovered() {
        // Assume we have a reference to the 'beans' ArrayList from above.
        //final Bean bean = beans.get(0);
        //for (int i = 0; i < beans.size(); i++) {
        //final Bean bean = beans.get(i);
        beanListener = new BeanListener() {
            @Override
            public void onConnected() {
                System.out.println("connected to Bean!");
//                programBean();
                beanConnected.readDeviceInfo(new Callback<DeviceInfo>() {
                    @Override
                    public void onResult(DeviceInfo deviceInfo) {
                        System.out.println(deviceInfo.hardwareVersion());
                        System.out.println(deviceInfo.firmwareVersion());
                        System.out.println(deviceInfo.softwareVersion());
                    }
                });
            }

            @Override
            public void onConnectionFailed() {
                System.out.println("connection failed");
            }

            @Override
            public void onDisconnected() {
                System.out.println("disconnected");

            }

            @Override
            public void onSerialMessageReceived(byte[] data) {
                try {
                    String str = new String(data, "UTF-8");
                    Log.d("OnMSgRcvd", str);
                    if (str.equals("HIGHER")) {
                        if (!tracking) {
                            tracking = true;
                            sendMassSMS(numberList);
                            currentUUID = UUID.randomUUID();

                            startLocationTracking();
                        }
                        else {
                            tracking = false;
                            stopLocationTracking();
                        }
                    }
                } catch (Exception e) {
                    Log.d("OnMsgRcvd", e.toString());
                }
            }

            @Override
            public void onScratchValueChanged(ScratchBank bank, byte[] value) {
            }

            @Override
            public void onError(BeanError error) {
                System.out.print(error);
            }
        };
        if (beanConnected != null)
            beanConnected.connect(this, beanListener);
    }


    public void startLocationTracking(){
        if (locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                currentLocation = location;
                System.out.print(location.getLatitude() + "   " + location.getLongitude());
                saveToParse(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), currentUUID);
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        catch (SecurityException e){
            Log.d("Error", e.toString());
        }


    }
    public void stopLocationTracking() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            Log.d("error", e.toString());
        }

    }
    public void saveToParse(String lat, String lon, UUID uuid){
        ParseObject location = new ParseObject("Location");
        location.put("Lat", lat);
        location.put("Lon", lon);
        location.put("tripID", uuid.toString());
        location.saveInBackground();
    }
    public void loadAllNumbersFromParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Number");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> numberPList, ParseException e) {//this line is when request comes back
                if (e == null) {
                    Log.d("Number", "Retrieved " + numberPList.size() + " numbers");
                    numberList = new ArrayList<>();
                    for (int i = 0; i < numberPList.size(); i++) {//parsing pfobjects
                        String[] indivNumberArray = {numberPList.get(i).getString("number"), numberPList.get(i).getString("name")};
                        numberList.add(indivNumberArray);
                    }

                    //this is when you can use numberList (arraylist of string[number,name]
                } else {
                    Log.d("location", "Error: " + e.getMessage());
                }
            }
        });
    }
    public void loadMessage () {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> numberPListToDelete, ParseException e) {  //after this message string is useable
                if (e == null) {
                    for (int i = 0; i < numberPListToDelete.size(); i++){
                        message = numberPListToDelete.get(i).getString("message");
                        Log.d("message", message);
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
//    public void loadFromParse(){
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> locationList, ParseException e) {
//                if (e == null) {
//                    Log.d("location", "Retrieved " + locationList.size() + " locations");
//                    ArrayList<TripPoint> tripPoints = new ArrayList<TripPoint>();
//                    for (int i = 0; i < locationList.size(); i++) {
//                        TripPoint tripPoint = new TripPoint();
//                        tripPoint.setLat(locationList.get(i).getString("Lat"));
//                        tripPoint.setLon(locationList.get(i).getString("Lon"));
//                        tripPoint.setTripID(locationList.get(i).getString("tripID"));
//                        tripPoint.setDate(locationList.get(i).getCreatedAt());
//                        tripPoints.add(tripPoint);
//                    }
//                    tripsMap = tripPointsToTrips(tripPoints);
//                } else {
//                    Log.d("location", "Error: " + e.getMessage());
//                }
//            }
//        });
//    }
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
                if (tripPoints.get(i).date.getTime() < tripToUpdate.startPoint.date.getTime()){
                    tripToUpdate.startPoint.date = tripPoints.get(i).date;
                }
                if (tripPoints.get(i).date.getTime() > tripToUpdate.endPoint.date.getTime()){
                    tripToUpdate.endPoint.date = tripPoints.get(i).date;
                }
                trips.put(tripPoints.get(i).getTripID(), tripToUpdate);
            }
        }
        for (String key : trips.keySet()) {
            Log.d("tripParse",trips.get(key).tripID);
            Trip trip = trips.get(key);
            for (int i = 0; i < trip.tripPointsList.size(); i++){
                Log.d("tripPoint",trip.tripPointsList.get(i).getLat());
            }
        }
        return trips;
        //return new ArrayList<Trip>();
    }

    public boolean sendMassSMS(ArrayList<String[]> numberList) {
        try {
            for (int i = 0; i < numberList.size(); i++) {
                sms.sendTextMessage(numberList.get(i)[0], null, message, sentPI, null);
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Are you sure you have a network connection on this phone?",
                    Toast.LENGTH_LONG).show();
            return false;

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
//    public void programBeam() {
        //                String sketch = ":20000000766F696420736574757028297B0D0A207D200D0A766F6964206C6F6F7028297BCE\n" +
//                        ":200020000D0A4265616E2E7365744C6564283130302C3130302C313030293B200D0A42652F\n" +
//                        ":20004000616E2E736C65657028313030293B200D0A53657269616C2E7772697465282253E0\n" +
//                        ":200060004B455443482055504C4F414445442046524F4D204245414E22293B0D0A7D0D0AE8\n" +
//                        ":00000001FF";
//        sketch = "";
//        Hex programHex = Integer.toHexString(sketch);
//                try {
//                    Callback<UploadProgress> onProgress = new Callback<UploadProgress>() {
//                        @Override
//                        public void onResult(UploadProgress result) {
//                            Log.i("Sketch", "upload done");
//                        }
//                    };
//                    Log.i("Sketch", "before sketchhex made");
//                    SketchHex sketchHex = SketchHex.create("blinking", sketch);
//                    Log.i("Sketch", "before upload");
//                    beanConnected.programWithSketch(sketchHex, onProgress, onSketchUploaded);
//                    Log.i("Sketch", "upload started");
//
//                }
//                catch (Exception e){
//                    Log.d("ERROR", e.toString());
//                }
//    }

//    Runnable onSketchUploaded = new Runnable() {
//        @Override
//        public void run() {
//            Log.d("OnSktchUplded", "SKETCH UPLOADED");
//            //mHandler.postDelayed(mPollAccelerationRunnable, 200);
//        }
//    };



}
//class Application extends android.app.Application {
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Parse.initialize(this, "YOUR_APP_ID", "YOUR_CLIENT_KEY");
//    }
//
//}
