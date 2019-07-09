package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 18/12/2017.
 */

public class StatsCount {
    private int annual;
    private int week;
    private int day;
    private int month;
    private String name;

    public StatsCount(String name, int annual, int month, int week, int day) {
        this.annual = annual;
        this.week = week;
        this.day = day;
        this.month = month;
        this.name = name;
    }

    public int getAnnual() {
        return annual;
    }

    public void setAnnual(int annual) {
        this.annual = annual;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getName() {
        return name;
    }

}
