package com.github.mikephil.charting.sharechart.kline;

/**
 * Created by ljs on 15/11/5.
 */
public class KLineData {

    public KLineData preKLineData;

    public String date;
    public float open;
    public float close;
    public float high;
    public float low;
    public PaiYuan paiyuan;
    public float ma5 = Float.NaN;
    public float ma10 = Float.NaN;
    public float ma20 = Float.NaN;
    public float vol;
    public float MACD_12EMA;
    public float MACD_26EMA;


    public static class PaiYuan {
        public String cqr;
        public String FHcontent;
    }
}
