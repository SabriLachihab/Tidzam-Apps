package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 19/01/2018.
 */

public class Data {
    private String date;
    private double donnees;
    private String label;
    private int index;

    public Data(String date, double donnees) {
        this.date = date;
        this.donnees = donnees;
    }

    public Data(String date, double donnees, int index, String label) {
        this.date = date;
        this.donnees = donnees;
        this.index = index;
        this.label = label;
    }

    public String getDate() {
        return date;
    }

    public double getDonnees() {
        return donnees;
    }

    public String getLabel() {
        return label;
    }

    public int getIndex() {
        return index;
    }


}
