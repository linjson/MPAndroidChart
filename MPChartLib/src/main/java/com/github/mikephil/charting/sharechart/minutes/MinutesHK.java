package com.github.mikephil.charting.sharechart.minutes;

import android.util.SparseArray;

/**
 * Created by ljs on 15/11/19.
 */
public class MinutesHK implements MinutesType {

    @Override
    public SparseArray<String> getShowTimeLabels() {
        SparseArray<String> times = new SparseArray<>();
        times.put(0, "09:30");
//        times.put(30, "10:00");
//        times.put(60, "10:30");
//        times.put(90, "11:00");
//        times.put(120, "11:30");
        times.put(151, "12:00/13:00");
//        times.put(182, "13:30");
//        times.put(212, "14:00");
//        times.put(242, "14:30");
//        times.put(272, "15:00");
//        times.put(302, "15:30");
        times.put(332, "16:00");
        return times;
    }

    @Override
    public String getType() {
        return "hk";
    }

    @Override
    public String[] getMinutesCount() {
        return new String[getOneDayCounts()+1];
    }

    public String[] getDayCount(int day) {
        return new String[(getOneDayCounts()) * day + day];
    }

    public int getOneDayCounts() {
        return 11 * 30 + 2;
    }


    @Override
    public SparseArray<String> getShowDayLabels(String[] days) {

        SparseArray<String> temp = new SparseArray<>();

        for (int i = 0, s = getOneDayCounts(); i < days.length; i++) {
            temp.put(s * (i + 1), days[i]);
        }

        return temp;
    }


}
