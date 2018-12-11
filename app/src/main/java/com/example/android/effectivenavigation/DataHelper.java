package com.example.android.effectivenavigation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by euiwonkim on 8/25/15.
 */
public class DataHelper implements Serializable {

    private ArrayList<String> floors;

    public DataHelper() {

    }

    public DataHelper(ArrayList<String> floors) {
        this.floors = floors;
    }

    public ArrayList<String> getList() {
        return this.floors;
    }
}