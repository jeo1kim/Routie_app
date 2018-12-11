package com.example.android.effectivenavigation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyLocationsListFragment extends ListFragment implements OnItemClickListener {

    String[] menutitles;
    TypedArray menuIcons;

    ViewPager viewPager;
    private PagerAdapter page;
    CustomAdapter adapter1;
    CustomAdapter adapter2;
    private List<RowItem> rowItems;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Set<String> locations;
    String address;
    String locationAddress;
    YelpAPI yelp;
    long lati;
    long longi;

    List<String> list;
    List<String> deleted;
    int dialogPosition;
    String picture;

    public MyLocationsListFragment(){
    }

    public MyLocationsListFragment(ViewPager view){
        this.viewPager = view;
    }

    public MyLocationsListFragment(ViewPager view, PagerAdapter page){
        this.viewPager = view;
        this.page = page;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_locations_list_fragment, null, false);
        net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton butt = (net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton) v.findViewById(R.id.normal_plus);
        butt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                adapter1.notifyDataSetChanged();
                viewPager.setCurrentItem(0);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
                menutitles = getResources().getStringArray(R.array.titles);
        menuIcons = getResources().obtainTypedArray(R.array.icons);

        rowItems = new ArrayList<RowItem>();

        RowItem items;

        //////////////////////////////////////////////////////////////////////////////////////////////
        //GETTING MASTER LIST OF LOCATIONS
        settings = getActivity().getSharedPreferences("LOCATIONS", Context.MODE_PRIVATE);
        editor = settings.edit();
        locations = settings.getStringSet("locations", new HashSet<String>());
        //picture = settings.getString("picture", new String());
        list = new ArrayList<String>(locations);
        Collections.sort(list);

        //if(picture != null && !list.contains(picture)) {
        //    list.add(picture);
        //}

        //CREATE THE ROWLIST IN THE LAYOUT
        for (int i = 0; i < list.size(); i++) {
            //if(!list.contains(list.get(i))) {
                items = new RowItem(list.get(i), menuIcons.getResourceId(0, -1));
                rowItems.add(items); //LIST OF ROWITEMS TO DISPLAY
            //}
        }

        Set<String> set = new HashSet<String>(list);
        editor.putStringSet("locations", set);
        editor.commit();
        //////////////////////////////////////////////////////////////////////////////////////////////

        adapter1 = new CustomAdapter(getActivity(), rowItems);
        adapter1.notifyDataSetChanged();
        setListAdapter(adapter1);
        getListView().setOnItemClickListener(this);

        AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {

                dialogPosition = position;

                 class AlertDFragment extends DialogFragment {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new AlertDialog.Builder(getActivity())
                                // Set Dialog Icon
                                //.setIcon(R.drawable.androidhappy)
                                        // Set Dialog Title
                                .setTitle("Confirmation")
                                        // Set Dialog Message
                                .setMessage("Are you sure you want to delete?")

                                        // Positive button
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do something else
                                        String listLocation = list.get(dialogPosition);
                                        list.remove(dialogPosition);

                                        Set<String> set = new HashSet<String>(list);

                                        rowItems.remove(dialogPosition);

                                        editor.putStringSet("locations", set);
                                        editor.commit();

                                        adapter2 = new CustomAdapter(getActivity(), rowItems);
                                        adapter2.notifyDataSetChanged();
                                        setListAdapter(adapter2);
                                    }
                                })

                                        // Negative Button
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,	int which) {
                                        // Do something else
                                    }
                                }).create();
                    }
                }
                AlertDFragment alertdFragment = new AlertDFragment();
                // Show Alert DialogFragment
                alertdFragment.show(getFragmentManager().beginTransaction(), "Alert Dialog Fragment");
                return true;
            }
        };
        getListView().setOnItemLongClickListener(listener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        //settings.getString("current", new String());
        String listLocation = list.get(position);
        //Log.d(null, "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL " + listLocation);
        //////////////////////////////////////////////////////////////////////////////////////////////
        // Save where you clicked.
        editor.putString("current", listLocation);
        editor.commit();

        Geocoder geocoder = new Geocoder(getActivity());

        try {
            List addressList = geocoder.getFromLocationName(listLocation, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);

                long lat = (long) address.getLatitude();
                long lon = (long) address.getLongitude();

                //Log.d (null, "Longitutde: " + Long.toString(Long.parseLong(Double.toString( address.getLatitude()))));
                //Log.d(null, "Latitutde: " + Long.toString(Long.parseLong(Double.toString(address.getLongitude()))));

                editor.putLong("latitude", lat);
                editor.putLong("longitude", lon);
                editor.commit();
            }
        } catch (IOException e) {
            Log.e("TAG", "Unable to connect to Geocoder", e);
        }

        Intent intent = new Intent(getActivity(), Tab_List_2.class);
        startActivity(intent);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}