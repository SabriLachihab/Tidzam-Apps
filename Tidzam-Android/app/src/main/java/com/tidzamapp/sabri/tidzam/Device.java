package com.tidzamapp.sabri.tidzam;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Georges on 29/11/2017.
 */

public class Device
{
    private String name, href;
    private double longitude, latitude;
    private MarkerOptions marker;
    private String specie;
    private BitmapDescriptor bmp;

    public Device(String name, String href) {
        this.name = name;
        this.href = href;
    }

    public Device(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public BitmapDescriptor getBmp() {
        return bmp;
    }

    public void setBmp(BitmapDescriptor bmp) {
        this.bmp = bmp;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public void setIcon(BitmapDescriptor image)
    {
        this.marker.icon(image);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getHref() {
        return href;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString()
    {
        String msg = "name : "+this.name+",  ("+String.valueOf(this.longitude)+","+String.valueOf(this.latitude)+" / marker : " + this.marker.getTitle();
        return msg;
    }
}
