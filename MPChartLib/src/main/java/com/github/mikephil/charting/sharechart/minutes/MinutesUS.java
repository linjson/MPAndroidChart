package com.github.mikephil.charting.sharechart.minutes;

import android.util.SparseArray;

/**
 * Created by ljs on 15/11/19.
 */
public class MinutesUS implements MinutesType {

    @Override
    public SparseArray<String> getShowTimeLabels() {
        SparseArray<String> times = new SparseArray<>();
        times.put(0, "09:30");
        times.put(30, "10:00");
        times.put(60, "10:30");
        times.put(90, "11:00");
        times.put(120, "11:30");
        times.put(150, "12:00");
        times.put(180, "12:30");
        times.put(210, "13:00");
        times.put(240, "13:30");
        times.put(270, "14:00");
        times.put(300, "14:30");
        times.put(330, "15:00");
        times.put(360, "15:30");
        times.put(390, "16:00");
        return times;
    }

    @Override
    public String getType() {
        return "us";
    }

    @Override
    public String[] getMinutesCount() {
        return new String[getOneDayCounts()];
    }

    @Override
    public SparseArray<String> getShowDayLabels(String[] days) {
        SparseArray<String> temp = new SparseArray<>();

        for (int i = 0, s = getOneDayCounts(); i < days.length; i++) {
            temp.put(s * (i + 1), days[i]);
        }

        return temp;
    }

    @Override
    public String[] getDayCount(int day) {
        return new String[getOneDayCounts() * day + day];
    }

    @Override
    public int getOneDayCounts() {
        return 30 * 13 + 1;
    }
}
