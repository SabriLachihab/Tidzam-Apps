package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 18/01/2018.
 */

public class TidmarshItemValide {
    private String name;
    private String href;
    private double latitude, longitude;

    public TidmarshItemValide(String name, String href, double latitude, double longitude) {
        this.name = name;
        this.href = href;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
