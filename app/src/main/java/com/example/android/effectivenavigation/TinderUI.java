package com.example.android.effectivenavigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by euiwonkim on 8/12/15.
 */
public class TinderUI extends AppCompatActivity{

    private CardContainer mCardContainer;
    static ArrayList<CardModel> likes = new ArrayList<CardModel>();
    static ArrayList<CardModel> dislikes = new ArrayList<CardModel>();
    private Button button;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Set<String> temp;
    private ArrayList<String> temp1;
    private ArrayList<String> temp2;
    String currentLocation;

    //ImageView imgV = new ImageView(TinderUI.this);
    //SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);

    int count = 0;
    int flag = 0;
    int flag2 = 0;
    int likeCount = 0;
    static Map mMap = new HashMap();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tinder_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.routie_icon);
        toolbar.setTitle("Routie");
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("LOCATIONS", Context.MODE_PRIVATE);
        editor = prefs.edit();
        currentLocation = prefs.getString("current", new String());
        Log.d(null, "CURRRRRRRRRRRRRRRRRR " + currentLocation);

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);

        button = (Button) findViewById(R.id.okayButton1);

        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view) {
                                          if(likes.size() > 0) {
                                              for(int i = 0; i < likes.size(); i++) {
                                                  Log.d(null, "INSSSSSSSSSSSSSSSSSIDDEEE " + likes.get(i));
                                              }
                                              likes.clear();
                                          }
                                          likes.addAll(mCardContainer.getLikes());
                                          dislikes.addAll(mCardContainer.getDislikes());

                                          for(int i = 0; i < likes.size(); i++) {
                                              Log.d(null, "AAAAAAAAAADDDDDDDDDDDDDDDDD " + likes.get(i));
                                          }

                                          //GET CURRENT LOCATION


                                          //GET AND STORE CURRENT LOCATION'S TINDER LIKES (TITLE and ADDRESS)
                                          Set<String> tinderLocationSet = prefs.getStringSet("tinderLocations" + currentLocation, new HashSet<String>());
                                          Set<String> tinderAddressSet = prefs.getStringSet("tinderAddresses" + currentLocation, new HashSet<String>());
                                          ArrayList<String> temploc = new ArrayList<String>(tinderLocationSet);
                                          ArrayList<String> tempaddr = new ArrayList<String>(tinderAddressSet);

                                          for(int i = 0; i < likes.size(); i++) {
                                              temploc.add(likes.get(i).getTitle());
                                              tempaddr.add(likes.get(i).getDescription());
                                          }

                                          Set<String> locSet = new HashSet<String>(temploc);
                                          Set<String> addrSet = new HashSet<String>(tempaddr);
                                          editor.putStringSet("tinderLocations" + currentLocation, locSet);
                                          editor.putStringSet("tinderAddresses" + currentLocation, addrSet);
                                          editor.commit();
                                    ///////////////////////////////////////////////////////////////////////////////////////////////
                                          Intent intent = new Intent(getApplicationContext(), Tab_List_2.class);
                                          startActivity(intent);
                                          finish();
                                      }
                                  });
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);
        //SimpleCardStackAdapter adapter2 = new SimpleCardStackAdapter(this);


        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        String location = currentLocation;//"San Diego, CA";

        String key = category + location;

        /* ORIGNAL CODE*/
        CardModel card;

        YelpAPI yelp = new YelpAPI();
        String imgurl;


        if(mMap.isEmpty() == false) {

            if (mMap.containsKey(key) == true) {
                yelp.setBusinesses((JSONArray) mMap.get(key));
                Log.d(null, "true condition");
            } else {
                yelp.yelpDataQuery(category, location);
                mMap.put(key, yelp.getBusinesses());
            }
        }
        else {
            yelp.yelpDataQuery(category, location);
            mMap.put(key, yelp.getBusinesses());
        }


        ImageView imgV = new ImageView(TinderUI.this);

//count < 20 && count < yelp.getBizSize()
        while(count < 5){

            yelp.setBusinessesDataByPos(count);
            imgurl = yelp.getYelpImage();

            Picasso.with(TinderUI.this).load(imgurl).placeholder(R.drawable.picture1).into(imgV, new Callback() {
                @Override
                public void onSuccess() {
                    count++;
                    flag++;
                }

                @Override
                public void onError() {

                }
            });

            if(flag == 1 ) {
                Drawable draw = imgV.getDrawable();

                card = new CardModel(yelp.getYelpName(), yelp.getYelpAddress(), draw);
                card.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
                    @Override
                    public void onLike() {
                        Log.d(null,"Like");
                    }

                    @Override
                    public void onDislike() {
                        Log.d(null,"Dislike");
                    }
                });
                adapter.add(card);
                flag = 0;
            }
        }

        Log.d(null, "loop out");

        mCardContainer.setOrientation(Orientations.Orientation.Ordered);

        mCardContainer.setAdapter(adapter);
/*
        prefs = getSharedPreferences("YourActivityPreferences", Context.MODE_PRIVATE);
        editor = prefs.edit();
        temp = prefs.getStringSet("tinderlist", new HashSet<String>());

        temp1 = new ArrayList<String>(temp);
        temp2 = new ArrayList<String>(temp);

        for(int i = 0; i < likes.size(); i++) {
            temp1.add(likes.get(i).getTitle());
            temp2.add(likes.get(i).getDescription());
        }
        Set<String> set = new HashSet<String>(temp1);
        Set<String> set1 = new HashSet<String>(temp2);
        editor.putStringSet("tinderLocations", set);
        editor.putStringSet("tinderAddresses", set1);

        editor.commit();*/
    }



    public CardModel createCard(String title, String description, int id) {

        CardModel card = new CardModel();

        Resources r = getResources();

        card.setTitle(title);
        card.setDescription(description);
        card.setCardImageDrawable(r.getDrawable(id));

        card.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
            @Override
            public void onLike() {
                Log.d(null,"Like");
                likeCount++;
            }

            @Override
            public void onDislike() {
                Log.d(null,"Dislike");
            }
        });

        return card;
    }


    @Override
    public void onBackPressed(){
        if(likes.size() > 0) {
            for(int i = 0; i < likes.size(); i++) {
                Log.d(null, "LIKESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS " + likes.get(i));
            }
            likes.clear();
        }
        likes.addAll(mCardContainer.getLikes());
        dislikes.addAll(mCardContainer.getDislikes());
        finish();
    }

    public ArrayList<CardModel> getLikes(){
        return likes;
    }

    public ArrayList<CardModel> getDislikes(){
        return dislikes;
    }

    public void clear(){
        likes.clear();
        dislikes.clear();
    }
}
