package com.example.android.effectivenavigation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchSectionFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private ViewPager viewPager;
    private static int viewNum = 1;
    ViewSwitcher viewSwitcher;

    private ArrayList<String> locList;
    public  SearchSectionFragment (){
    }
    public  SearchSectionFragment (ViewPager viewPager){
        this.viewPager=viewPager;
    }

    private static final String LOG_TAG = "AutoFragment";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private GoogleApiClient mGoogleApiClient;
    private PlacesArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private String locationName;
    private String initialLocation;
    private SharedPreferences prefs;  // shared preferences
    private SharedPreferences prefs1;
    private ArrayList<String> myArrayList = new ArrayList<String>();

    private static int flag2 = 0;
    private  int flag3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_switcher,container,false);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.view_switcher);

/*
        //  GEtting Bing images
        final BingSearchResults[] b = new BingSearchResults[1];
        final ImageView bingImg = (ImageView) rootView.findViewById(R.id.bingImage);
        final TextView desText = (TextView) rootView.findViewById(R.id.desText);

        Bing.Callback[] callback = {new Bing.Callback() {
            @Override
            public void onComplete(Object o, Error error) {

                b[0] = (BingSearchResults) o;
                Log.d(null, "BING: " + b[0].getResults()[0].Url);
                //bingImg.setImageURI(Uri.parse(b[0].getResults()[0].Url));
                Picasso.with(getActivity()).load(b[0].getResults()[0].MediaUrl).into(bingImg);

            }
        }};

        Bing bing = new Bing("San Diego, CA", 1, callback[0]);
        desText.setText("San Diego");
        desText.bringToFront();
        bing.execute();
*/




        rootView.findViewById(R.id.autoCompleteTextView).bringToFront();

        mAutocompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlacesArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                        BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        rootView.findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paris = "Paris, France";
                SharedPreferences pref = getActivity().getSharedPreferences("LOCATIONS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("picture", paris);
                editor.commit();

                Set<String> locationsSet = pref.getStringSet("locations", new HashSet<String>());
                ArrayList<String> locationList = new ArrayList<String>(locationsSet);

                // In location list array, adding location dynamically
                    if(!locationList.contains(paris)) {
                        locationList.add(paris);
                    }

                Set<String> set = new HashSet<String>(locationList);

                editor.putStringSet("locations", set);
                editor.commit();
                viewPager.setCurrentItem(1);
            }
        });


        return rootView;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            locationName = String.valueOf(item.description);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);

            SharedPreferences pref = getActivity().getSharedPreferences("LOCATIONS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
        //////////////////////////////////////////////////////////////////////////////////////////////
            //Stores locations in master list
            Set<String> locationsSet = pref.getStringSet("locations", new HashSet<String>());
            ArrayList<String> locationList = new ArrayList<String>(locationsSet);

            // In location list array, adding location dynamically
            locationList.add(locationName);
            Set<String> set = new HashSet<String>(locationList);

            editor.putStringSet("locations", set);
            editor.commit();
         //////////////////////////////////////////////////////////////////////////////////////////////
            viewPager.setCurrentItem(1);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
}