package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 15/12/2017.
 */

public class SensorCount {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private String name;
    private int count;

    public SensorCount(String nom,int count)
    {
        this.name=nom;
        this.count=count;
    }
}
