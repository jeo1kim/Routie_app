package com.example.android.effectivenavigation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InnerMyListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    String[] menutitles;
    TypedArray menuIcons;

    CustomAdapter adapter;
    private List<RowItem> rowItems;
    private List<String> list;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String location;
    private ViewPager view;

    public ArrayList<String> addressList;
    public ArrayList<String> list1, list2;
    public Set innerString, innerAddress;
    int dialogPosition;
    CustomAdapter adapter2;

    public ArrayList<String> holdLocationName;
    public ArrayList<String> holdAddresses;
    public ArrayList<String> emptyList = new ArrayList<String>();
    public Set tempLoc, tempAdr, locSet, rowSet;
    String currentLocation;


    public InnerMyListFragment(){

    }

    public InnerMyListFragment(ViewPager view){
        this.view = view;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v1 = inflater.inflate(R.layout.inner_list_fragment, null, false);
        //v1.findViewById(R.id.normal_plus).bringToFront();

        Button butt = (Button) v1.findViewById(R.id.map_button);
        butt.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        Button back = (Button) v1.findViewById(R.id.back_button);
        back.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {

                //finish();
                Intent intent = new Intent(getActivity().getApplicationContext(), Main2Activity.class );
                startActivity(intent);
            }
        });

        net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton addButton = (net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton) v1.findViewById(R.id.normal_plus);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                view.setCurrentItem(1);
            }
        });

        return v1;
    }

    public void finish(){

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


        menuIcons = getResources().obtainTypedArray(R.array.icons);
        RowItem items;

        //Get the Shared Preference locationString from MyLocationsListFragment
        settings = getActivity().getSharedPreferences("LOCATIONS", Context.MODE_PRIVATE);
        editor = settings.edit();
        currentLocation = settings.getString("current", new String());
        tempLoc = settings.getStringSet("tinderLocations" + currentLocation, new HashSet<String>());
        tempAdr = settings.getStringSet("tinderAddresses" + currentLocation, new HashSet<String>());
        locSet = settings.getStringSet("bucket" + currentLocation, new HashSet<String>());

        holdLocationName = new ArrayList<String>(tempLoc); //locations
        holdAddresses = new ArrayList<String>(tempAdr); //addresses
        rowItems = new ArrayList<RowItem>();
        //Log.d(null, "CURRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR " + currentLocation);
        //Log.d(null, "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  " + holdAddresses.size());

        //innerString = settings.getStringSet("innerString", new HashSet<String>());
        //innerAddress = settings.getStringSet("innerAddress", new HashSet<String>());
        list1 = new ArrayList<String>(locSet);
        list2 = new ArrayList<String>(tempAdr);
        //list2 = new ArrayList<String>(innerAddress);

        for (int j = 0; j < holdLocationName.size(); j++) {

            if (holdLocationName.get(j) != null)
            {
                if(!list1.contains(holdLocationName.get(j)))
                {
                    list1.add(holdLocationName.get(j));
                }
            }
        }

        for (int k = 0; k < list1.size(); k++)
        {
            items = new RowItem(list1.get(k), menuIcons.getResourceId(
                    k, -1));
            rowItems.add(items);
        }

        for (int k = 0; k < holdAddresses.size(); k++) {
            if (holdAddresses.get(k) != null)
            {
                if(!list2.contains(holdAddresses.get(k)))
                {
                    list2.add(holdAddresses.get(k));
                }
            }
        }

        Set<String> list1set = new HashSet<String>(list1);
        Set<String> set2 = new HashSet<String>(list2);

        editor.putStringSet("bucketaddress" + currentLocation, set2);
        editor.putStringSet("bucket" + currentLocation, list1set);
        editor.commit();
        //for (int k = 0; k < holdAddresses.size(); k++) {
        //    if (holdAddresses.get(k) != null) {
                //list2.add(holdAddresses.get(k));
        //    }
        //}
        //Set<String> set1 = new HashSet<String>(list1);
        //Set<String> set2 = new HashSet<String>(list2);

        //editor.putStringSet("innerString", set1);
        //editor.putStringSet("innerAddress", set2);
        //editor.commit();

        adapter = new CustomAdapter(getActivity(), rowItems);
        adapter.notifyDataSetChanged();
        setListAdapter(adapter);

        Set<String> emptySet = new HashSet<String>(emptyList);
        editor.putStringSet("tinderLocations" + currentLocation, emptySet);
        editor.commit();
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
                                        locSet = settings.getStringSet("bucket" + currentLocation, new HashSet<String>());
                                        list1 = new ArrayList<String>(locSet);

                                        for(int i = 0; i < list1.size(); i++) {
                                            Log.d(null, "REMOVVVVEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + list1.get(i));
                                        }

                                        String listLocation = list1.get(dialogPosition);
                                        list1.remove(dialogPosition);

                                        Set<String> set = new HashSet<String>(list1);

                                        rowItems.remove(dialogPosition);

                                        for(int i = 0; i < list1.size(); i++) {
                                            Log.d(null, "AFTTTTTTTTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR" + list1.get(i));
                                        }

                                        editor.putStringSet("bucket" + currentLocation, set);
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

        //registerForContextMenu(getListView().findViewById(android.R.id.list));
        //Intent intent = new Intent(getActivity(), MapListViews.class);
        //startActivity(intent);

        // Toast.makeText(getActivity(), menutitles[position], Toast.LENGTH_SHORT)
        //         .show();

    }

}
