package com.github.mikephil.charting.sharechart.minutes;

/**
 * Created by ljs on 15/11/19.
 */
public class MinutesTypeFactory {

    public static final MinutesType getType(String type) {
        type = type == null ? "" : type;
        switch (type) {
            case MinutesType.HK:
                return new MinutesHK();
            case MinutesType.SH:
                return new MinutesSH();
            case MinutesType.SZ:
                return new MinutesSZ();
            case MinutesType.US:
                return new MinutesUS();
            default:
                return new MinutesSH();
        }

    }

}
