package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri Lachihab on 14/11/2017.
 */

public class Species {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


    public Species(String name)
    {
        this.name=name;
    }
}
