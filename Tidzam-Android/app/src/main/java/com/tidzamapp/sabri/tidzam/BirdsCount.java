package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 13/12/2017.
 */

public class BirdsCount {
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

    private int count;
    private String name;

    public BirdsCount(String name,Integer count)
    {
        this.count=count;
        this.name=name;
    }
}
