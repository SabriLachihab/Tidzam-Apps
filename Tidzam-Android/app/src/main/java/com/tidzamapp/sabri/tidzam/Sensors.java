package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 08/12/2017.
 */

public class Sensors {
    public String href;
    public String name;
    public String device;
    private double lat, lng;

    public Sensors(String href, String name, String device) {
        this.href = href;
        this.name = name;
        this.device = device;
    }

    public Sensors(String href, String name, String device, double lat, double lng) {
        this.href = href;
        this.name = name;
        this.device = device;
        this.lat = lat;
        this.lng = lng;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
