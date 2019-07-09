package com.tidzamapp.sabri.tidzam;

/**
 * Created by Sabri on 20/12/2017.
 */

public class StatsByMonth {
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public StatsByMonth(int month, int count) {
        this.month = month;
        this.count = count;
    }

    private int month;
    private int count;
}
