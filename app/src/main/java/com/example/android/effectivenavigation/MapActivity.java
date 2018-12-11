package com.example.android.effectivenavigation;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MapActivity extends FragmentActivity implements android.location.LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> mMarkerPoints;
    ArrayList<LatLng> mMarkerPoints1;
    double mLatitude=0;
    double mLongitude=0;
    private Location locationLast;
    private LatLng point;
    private LatLng user;
    private LatLng user1;
    private LatLngBounds bounds;

    private int counter = 0;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    public Set tempAdr, tempLoc;


    ArrayList<String> holdAddresses, holdLocations;
    String currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_layout);


        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Initializing
            mMarkerPoints = new ArrayList<LatLng>();
            mMarkerPoints1 = new ArrayList<LatLng>();

            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            mMap = fm.getMap();

            // Enable MyLocation Button in the Map
            mMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            locationLast = locationManager.getLastKnownLocation(provider);

            if (locationLast != null) {
                onLocationChanged(locationLast);
            }

            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

        settings = getSharedPreferences("LOCATIONS", Context.MODE_PRIVATE);
        editor = settings.edit();
        currentLocation = settings.getString("current", new String());
        tempAdr = settings.getStringSet("bucketaddress" + currentLocation, new HashSet<String>());
        tempLoc = settings.getStringSet("bucket" + currentLocation, new HashSet<String>());

        holdAddresses = new ArrayList<String>(tempAdr);
        holdLocations = new ArrayList<String>(tempLoc);

        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(this);
        //Log.d(null, "sizeeeeeeeeeeee " + holdAddresses.size());
        //Log.d(null, "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  " + holdAddresses.get(i));
        try {
            mMarkerPoints1.clear();
            mMap.clear();

            for (int i = 0; i < holdAddresses.size(); i++) {
                Log.d(null, "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  " + holdAddresses.get(i));
                addressList = geocoder.getFromLocationName(holdAddresses.get(i), 1);

                if (addressList.size() > 0) {
                    Double lat = (double) (addressList.get(0).getLatitude());
                    Double lon = (double) (addressList.get(0).getLongitude());
                    user = new LatLng(lat, lon);
                    Log.d(null, "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL  " + lat.toString() + lon.toString());
                }


                //Log.d(null, "mAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + mMarkerPoints1);
                if(holdAddresses.get(i) != null && user != null) {
                    Log.d(null, "lllllllllllllllllllllllllllllll  " + holdAddresses.get(i));
                    mMarkerPoints1.add(user);
                    mMap.addMarker(new MarkerOptions()
                            .position(user)
                            .title(holdLocations.get(i))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.routie_marker))
                            .snippet(holdAddresses.get(i)));
                }
            }

            mMarkerPoints1.add(new LatLng(mLatitude, mLongitude));
            mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("Current Location"));

            for(int z = 0; (z + 1) < mMarkerPoints1.size(); z++) {
                if (mMarkerPoints1.size() >= 2) {
                    LatLng origin = mMarkerPoints1.get(z);
                    LatLng dest = mMarkerPoints1.get(z+1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    //counter++;

                    //LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    //builder.include(mMarkerPoints1.get(z));
                    //builder.include(mMarkerPoints1.get(z+1));

                    //bounds = builder.build();
                }
            }
            //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int z = 0; z < mMarkerPoints1.size(); z++) {
            builder.include(mMarkerPoints1.get(z));
        }
        final LatLngBounds bounds = builder.build();


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mMap.moveCamera(cu);
            }
        });
        /*if(counter == 0) {
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition arg0) {
                    int padding = 0;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                    mMap.moveCamera(cu);
                    counter = 1;
                }
            });
        }*/
    }

    public void onSearch(View view)
    {
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        List<Address> addressList = null;
        String location = location_tf.getText().toString();


        if(location != null || !location.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);
            try {

                if(counter == 1)
                {
                    mMarkerPoints.clear();
                    mMarkerPoints1.clear();
                    mMap.clear();
                    counter--;
                }
                //if(mMarkerPoints.size() > 1)
                //{
                //mMarkerPoints.clear();
                //mMap.clear();

                addressList = geocoder.getFromLocationName(location, 1);

                if (addressList.size() > 0) {
                    Double lat = (double) (addressList.get(0).getLatitude());
                    Double lon = (double) (addressList.get(0).getLongitude());
                    user = new LatLng(lat, lon);
                }
                mMarkerPoints.add(user);

                Marker hamburg = mMap.addMarker(new MarkerOptions()
                        .position(user)
                        .title(location)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.routie_marker)));

                //}

                mMarkerPoints.add(new LatLng(mLatitude, mLongitude));
                mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("FirstMarker"));
                // Move the camera instantly to hamburg with a zoom of 15.
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));

                // Zoom in, animating the camera.
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                if(mMarkerPoints.size() >= 2){
                    LatLng origin = mMarkerPoints.get(0);
                    LatLng dest = mMarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    counter++;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(mMarkerPoints.get(0));
                    builder.include(mMarkerPoints.get(1));

                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
                }



            } catch (IOException e) {
                e.printStackTrace();
            }


            //Address address = addressList.get(0);
            //LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Draw the marker, if destination location is not set
        if(mMarkerPoints.size() < 2){

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            point = new LatLng(mLatitude, mLongitude);

            // mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){

        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */



    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */


    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}