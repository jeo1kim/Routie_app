package com.example.android.effectivenavigation;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * Created by euiwonkim on 8/27/15.
 */
public class Bing extends AsyncTask<Void, Void, Void> {

    private final String TAG = getClass().getName();

    private String mSearchStr;
    private int mNumOfResults = 0;

    private Callback mCallback;
    private BingSearchResults mBingSearchResults;
    private Error mError;

    public Bing(String searchStr, int numOfResults, Callback callback) {
        mSearchStr = searchStr;
        mNumOfResults = numOfResults;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String searchStr = URLEncoder.encode(mSearchStr);
            String numOfResultsStr = mNumOfResults <= 0 ? "" : "&$top=" + mNumOfResults;
            String bingUrl2 = "https://api.datamarket.azure.com/Bing/Search/v1/Images?Query=%27"+searchStr+"%27&$top=1&$format=json";
            String bingUrl = "https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=%27"+searchStr+"%27&$top=1&$format=json";

            String accountKey = "VJDVFOfS+1xDZuutusWl8ed05bXL4hlsraTZMhVnjaU=";//"AjoSivKLj5k0o6GqweijM1yvqb1j0r7Sai1zKVAVF06nI6O40REArWSduQIy1GpN";
            byte[] accountKeyBytes;
            accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
            String accountKeyEnc = new String(accountKeyBytes);

            URL url = null;
            url = new URL(bingUrl);

            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
            InputStream response = urlConnection.getInputStream();
            String res = readStream(response);

            Gson gson = (new GsonBuilder()).create();
            mBingSearchResults = gson.fromJson(res, BingSearchResults.class);

            Log.d(TAG, res);
            //conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            mError = new Error(e.getMessage(), e);
            //Log.e(TAG, e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (mCallback != null) {
            mCallback.onComplete(mBingSearchResults, mError);
        }

    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();


    }
    public interface Callback {
        void onComplete(Object o, Error error);
    }
}