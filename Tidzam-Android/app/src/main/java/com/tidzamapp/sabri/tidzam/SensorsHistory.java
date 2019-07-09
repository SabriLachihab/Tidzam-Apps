package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 11/01/2018.
 */

public class SensorsHistory {

    private String name;
    private int count;
    private String date;

    public SensorsHistory(int count, String date, String name) {
        this.count = count;
        this.date = date;
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
