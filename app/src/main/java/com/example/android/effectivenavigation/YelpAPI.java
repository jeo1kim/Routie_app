package com.example.android.effectivenavigation;


import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Code sample for accessing the Yelp API V2.
 * <p/>
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 * <p/>
 * <p/>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 */
public class YelpAPI {

    private static final String API_HOST = "api.yelp.com";
    private static final int SEARCH_LIMIT = 20;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    /*
     * Update OAuth credentials below from the Yelp Developers API site:
     * http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "nh2Ciw89AGBGojWygrB67Q";
    private static final String CONSUMER_SECRET = "RKmNfr7_ZY8GN-XdqPiHXZnJOM4";
    private static final String TOKEN = "N7U-K1UnDA72s_jbFXuKJvINu0rh7k8h";
    private static final String TOKEN_SECRET = "qFbZd_4Rb-g9h6Nsc_YDgXufg3s";

    OAuthService service;
    Token accessToken;

    public String bizid;
    private String imgurl;
    private Uri uri;
    private Set<String> yelp_set;

    private static String term;
    private String user;
    private String destination;
    private static String location;
    private static ArrayList<String> yelp_result_list;
    private static JSONArray businesses = null;
    private static JSONObject business = null;
    private static int bizSize;

    public void setBizSize(int size){
        this.bizSize = size;
    }
    public int getBizSize(){
        return this.bizSize;
    }
    public String getYelpImage() {
        return yelp_result_list.get(0).replaceAll("ms.jpg", "o.jpg");
    }

    public String getYelpImageLow() {
        return yelp_result_list.get(0);
    }

    public String getYelpName() {
        return yelp_result_list.get(1);
    }

    public String getYelpAddress() {
        return yelp_result_list.get(2);
    }

    public List getYelpCategory() {
        return Arrays.asList(yelp_result_list.get(3));
    }

    public String getYelpRating() {
        return yelp_result_list.get(4);
    }

    public void setCurrentUser(String user) {
        this.user = user;
    }

    public void setTerm(String term) {
        term = term;
    }

    public void setLocation(String location) {
        location = location;
    }

    public void setYelpData(ArrayList<String> aList) {
        yelp_result_list = aList;
    }

    public String getCurrentUser() {
        return user;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setBusinesses(JSONArray businesses) {
        this.businesses = businesses;
    }

    ;

    public void setBusinessesDataByPos(int pos) {
        try {
            business = (JSONObject) businesses.get(pos);
        } catch (NullPointerException e) {
            System.out.println("Error: Null JSON Object: MyList");
            System.exit(1);
        }

        JSONObject address = null;
        address = (JSONObject) business.get("location");
        JSONArray addy = (JSONArray) address.get("display_address");
        String firstBusinessImage;
        String firstBusinessName;
        String firstBusinessAddress;
        String firstBusinessRatingImg;
        String firstBusinessCategory; // should be turned into list

        try {
            firstBusinessImage = business.get("image_url").toString();
        }catch (NullPointerException e){
            firstBusinessImage = "null";
        }
        firstBusinessName = business.get("name").toString();
        firstBusinessAddress = TextUtils.join(" ", addy);
        firstBusinessCategory = business.get("categories").toString();
        firstBusinessRatingImg = business.get("rating_img_url").toString();

        yelp_result_list = new ArrayList<String>();
        // Image,name,address,category,rating
        yelp_result_list.add(firstBusinessImage);
        yelp_result_list.add(firstBusinessName);
        yelp_result_list.add(firstBusinessAddress);
        yelp_result_list.add(firstBusinessCategory);
        yelp_result_list.add(firstBusinessRatingImg);
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p/>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @param term     <tt>String</tt> of the search term to be queried
     * @param location <tt>String</tt> of the location
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation(String term, String location, Double longitude, Double latitude) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        //request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        request.addQuerystringParameter("radius_filter", "40000");
        request.addQuerystringParameter("ll", String.valueOf(latitude)+","+String.valueOf(longitude) );
        //request.addQuerystringParameter("latitude", String.valueOf(latitude));
        //request.addQuerystringParameter("longitude", String.valueOf(longitude));
        //request.addQuerystringParameter("category_filter", "bars,dinner,..");

        Log.d(null, "longitude: " + Double.toString(longitude));
        Log.d(null, "longitude: " + Double.toString(latitude));

        try {
            bizid = new YelpConnect(this).execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            e.getCause();
        }
        //return sendRequestAndGetResponse(request);
        return bizid;
    }

    public String searchForBusinessesByLocation(String term, String location) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        request.addQuerystringParameter("radius_filter", "40000");
        //request.addQuerystringParameter("latitude", latitude);
        //request.addQuerystringParameter("longitude", longitude);
        //request.addQuerystringParameter("category_filter", "bars,dinner,..");

        try {
            bizid = new YelpConnect(this).execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            e.getCause();
        }
        //return sendRequestAndGetResponse(request);
        return bizid;
    }
    /**
     * Creates and sends a request to the Business API by business ID.
     * <p/>
     * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
     * for more info.
     *
     * @param businessID <tt>String</tt> business ID of the requested business
     * @return <tt>String</tt> JSON Response
     */
    public String searchByBusinessId(String businessID) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        try {
            bizid = new YelpConnect(this).execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //return sendRequestAndGetResponse(request);
        return bizid;
    }

    /**
     * Takes in a yelp object.
     *
     * @return String Image_url from yelp database
     */
    public void yelpDataQuery(String term, String location, Double lon, Double lat) {
        String searchResponseJSON =
                searchForBusinessesByLocation(term, location, lon, lat);

        JSONParser parser = new JSONParser();
        JSONObject response = null;
        businesses = new JSONArray();

        try {
            response = (JSONObject) parser.parse(searchResponseJSON);

        } catch (ParseException pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Error: Null JSON Object: Query");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }
        // Images, Name, Location(x,y), Category, Rating
        businesses = (JSONArray) response.get("businesses");
        long total = (long) response.get("total");

        Integer i = (int) (long) total;
        setBizSize(i);

        Log.d(null, "number of business: " + Long.toString(total));
        setBusinesses(businesses);


    }

    public JSONArray getBusinesses(){
        return businesses;
    }


    public void yelpDataQuery( String term, String location) {
        String searchResponseJSON =
                searchForBusinessesByLocation(term, location);

        JSONParser parser = new JSONParser();
        JSONObject response = null;

        try {
            response = (JSONObject) parser.parse(searchResponseJSON);
        } catch (ParseException pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Error: Null JSON Object: yelpDataQuery");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }
        // Images, Name, Location(x,y), Category, Rating
        this.businesses = new JSONArray();
        businesses = (JSONArray) response.get("businesses");
        setBusinesses(businesses);

    }

    /**
     * AsyncTask for Connecting to Yelp data Network on a separate thread.
     */
    private class YelpConnect extends AsyncTask<OAuthRequest, Void, String> {
        YelpAPI yelp;

        protected YelpConnect(YelpAPI servic) {
            yelp = servic;
        }

        @Override
        protected String doInBackground(OAuthRequest... params) {
            yelp.service.signRequest(yelp.accessToken, params[0]);
            Response response = params[0].send();
            return response.getBody().toString();
        }

        @Override
        protected void onPostExecute(String result) {
            bizid = result;
        }
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    /**
     * Setup the Yelp API OAuth credentials.
     */
    public YelpAPI() {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
                        .apiSecret(CONSUMER_SECRET).build();
        this.accessToken = new Token(TOKEN, TOKEN_SECRET);
    }
}