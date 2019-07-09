package com.tidzamapp.sabri.tidzam;

import java.util.ArrayList;

/**
 * Created by Sabri Lachihab on 20/11/2017.
 */

public class Stat {

    private String name;
    private ArrayList<ArrayList<SensorsHistory>> histories;

    public Stat(String name, ArrayList<ArrayList<SensorsHistory>> histories) {
        this.name = name;
        this.histories = histories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ArrayList<SensorsHistory>> getHistories() {
        return histories;
    }
}