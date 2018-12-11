package com.example.android.effectivenavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends android.support.v4.app.Fragment {

    private ImageView imgView, imgView2;
    private TextView userNameView, userIDView, userNameView2, userIDView2;
    private TextView userTitleView, userTitleView2;
    private TextView userPointsView, userPointsView2;
    private ListView listView, listView2;

    private TextView countriesStat, citiesStat, placesStat;

    String userid, username;
    int[] feedpics =
            {
                    R.drawable.jkimpic,
                    R.drawable.jchungpic,
                    R.drawable.hyunpic
            };
    String[] feednames =
            {
                    "John Kim",
                    "John Chung",
                    "Hyun Jong Han"
            };
    String[] feedText =
            {
                    " just visited the Eiffel Tower",
                    " just created a new 'San Diego' list",
                    " has been added to your friends list"
            };
    String[] friendnames =
            {
                    "John Kim",
                    "John Chung",
                    "Hyun Jong Han",
                    "Kihwan Lee",
                    "Cindy Chou"
            };
    int[] friendpics =
            {
                    R.drawable.jkimpic,
                    R.drawable.jchungpic,
                    R.drawable.hyunpic,
                    R.drawable.kihawnpic,
                    R.drawable.cchoupic
            };

    private static int viewNum = 1;

    ViewSwitcher viewSwitcher;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
        final List<HashMap<String, String>> bList = new ArrayList<HashMap<String, String>>();

        final View view = inflater.inflate(R.layout.profile_view_switcher, container, false);

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher2);
        userid = getUserId();
        getName(userid);
        imgView = (ImageView) view.findViewById(R.id.profile_picture);
        userNameView = (TextView) view.findViewById(R.id.username);

        Picasso.with(getActivity()).load("https://graph.facebook.com/" + userid + "/picture?type=large").into(imgView);

        for (int i = 0; i < feednames.length; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("pics", Integer.toString(feedpics[i]));
            hm.put("names", feednames[i]);
            hm.put("feedText", feedText[i]);
            aList.add(hm);
        }

        for (int i = 0; i < friendnames.length; i++) {
            HashMap<String, String> hm2 = new HashMap<String, String>();
            hm2.put("pics", Integer.toString(friendpics[i]));
            hm2.put("names", friendnames[i]);
            bList.add(hm2);
        }


        listView = (ListView) view.findViewById(R.id.feedList);
        String[] from = {"pics", "names", "feedText", "feedEmote"};
        int[] to = {R.id.imglist, R.id.namelist, R.id.feedText};
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.feedlistview_layout, from, to);
        listView.setAdapter(adapter);


        listView2 = (ListView) view.findViewById(R.id.friendList);
        String[] from2 = {"pics", "names"};
        int[] to2 = {R.id.imglist2, R.id.namelist2};
        SimpleAdapter adapter2 = new SimpleAdapter(getActivity().getBaseContext(), bList, R.layout.friendlistview_layout, from2, to2);
        listView2.setAdapter(adapter2);

        if (viewNum == 2)
            viewSwitcher.showNext();


        view.findViewById(R.id.mylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewSwitcher.showNext();
                viewNum = 2;

            }
        });

        view.findViewById(R.id.routiestats2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showPrevious();
                viewNum = 1;
            }
        });


        //countriesStat.setText("5 Countries Visited!");
        //citiesStat.setText("12 Cities Visited!");
        //placesStat.setText("53 Places Visited!");


//=======================SECOND TAB VIEWS====================================================


        imgView2 = (ImageView) view.findViewById(R.id.profile_picture2);
        userNameView2 = (TextView) view.findViewById(R.id.username2);

        //userIDView2 = (TextView) view.findViewById(R.id.userid2);
        Picasso.with(getActivity()).load("https://graph.facebook.com/" + userid + "/picture?type=large").into(imgView2);


        return view;
    }


    public static String getUserId() {
        String userid = AccessToken.getCurrentAccessToken().getUserId();
        return userid;
    }

    public void getName(String userid) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userid,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        try {
                            JSONObject jsonObject = response.getJSONObject();
                            username = jsonObject.getString("name");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        userNameView.setText(username);
                        userNameView2.setText(username);
                    }
                });
        request.executeAsync();
    }

/*
    public List<HashMap<String,String>> getFriendList(String userid)
    {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userid + "/friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.d("Facebook-Example-Friends Request", "Response: " + response);

                            final JSONObject json = response.getJSONObject();
                            JSONArray data = json.getJSONArray("data");
                            int l = (data != null ? data.length() : 0);
                            Log.d("Facebook-Example-Friends Request", "data.length(): " + l);


                            for (int i=0; i<l; i++) {
                                HashMap<String, String> hMap = new HashMap<String, String>();
                                JSONObject user = data.getJSONObject(i);
                                String imgurl = user.getString("url");
                                String name = user.getString("name");
                                hMap.put("imgurl", imgurl);
                                hMap.put("name", name);
                                aList.add(hMap);
                            }


                        } catch (JSONException e) {
                            Log.w("Facebook-Example", "JSON Error in response");
                        }

                    }
                });

        request.executeAsync();


        return aList;
    }
*/

}