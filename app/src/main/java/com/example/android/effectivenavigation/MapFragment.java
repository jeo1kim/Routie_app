package com.example.android.effectivenavigation;

/**
 * Created by gnuhc on 8/16/2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.List;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, android.location.LocationListener{

    private GoogleMap mMap;
    private Marker marker;

    ArrayList<LatLng> mMarkerPoints;
    double mLatitude=0;
    double mLongitude=0;
    private Location locationLast;
    private LatLng point;
    private LatLng user;
    private int counter = 0;

    final LatLng mTestLatLng = new LatLng(40.782865,-73.965355);

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setUpMapIfNeeded();
// Getting Google Play availability status

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        View v1 = inflater.inflate(R.layout.map_fragment_layout, null);
        FrameLayout mainChild = (FrameLayout) ((ViewGroup)v).getChildAt(0);
        mainChild.addView(v1);
//////////////////////////////////////////////////////////////////////////////
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());

        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Initializing
            mMarkerPoints = new ArrayList<LatLng>();

            // Getting reference to SupportMapFragment of the activity_main
             SupportMapFragment fm = ((SupportMapFragment) getChildFragmentManager()
                     .findFragmentById(R.id.map));

            // Getting Map for the SupportMapFragment
            mMap = getMap();

            // Enable MyLocation Button in the Map
            mMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
        ///////////////////////////////////////////////////////////////////////////
        EditText location_tf = (EditText)v1.findViewById(R.id.TFaddress);
        List<Address> addressList = null;
        String location = location_tf.getText().toString();

        if(location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {

                if (counter == 1) {
                    mMarkerPoints.clear();
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

                //Marker hamburg = mMap.addMarker(new MarkerOptions()
                //        .position(user)
                //        .title(location)
                //        .icon(BitmapDescriptorFactory
                //                .fromResource(R.drawable.afghanistan)));

                //}
                mMap.addMarker(new MarkerOptions().position(new LatLng(87, 67)).title("SecondMarker"));


                mMarkerPoints.add(new LatLng(mLatitude, mLongitude));
                mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("FirstMarker"));
                // Move the camera instantly to hamburg with a zoom of 15.
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));

                // Zoom in, animating the camera.
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                if (mMarkerPoints.size() >= 2) {
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            ///////////////////////////////////////////////////////////////////////////
        }
            Button btn1 = (Button) v.findViewById(R.id.Bsearch);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "TuTa :)", Toast.LENGTH_LONG).show();
            }
        });
        //initMap();

        return v;
    }


    @Override
    public void onLocationChanged(Location location) {
        // Draw the marker, if destination location is not set
        if(mMarkerPoints.size() < 2){

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            point = new LatLng(mLatitude, mLongitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

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
                lineOptions.width(4);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MyMap", "onMapReady");
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                Log.d("MyMap", "MapClick");

                //remove previously placed Marker
                if (marker != null) {
                    marker.remove();
                }

                //place marker where user just clicked
                marker = mMap.addMarker(new MarkerOptions().position(point).title("Marker")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                Log.d("MyMap", "MapClick After Add Marker");

            }
        });

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
