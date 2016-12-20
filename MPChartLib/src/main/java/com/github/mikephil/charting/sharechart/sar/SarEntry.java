package com.github.mikephil.charting.sharechart.sar;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by ljs on 15/11/17.
 */
public class SarEntry extends Entry {


    private float close;
    private float open;
    private float sar;


    public SarEntry(int xIndex, float open,float close, float sar, Object data) {
        super(sar, xIndex, data);
        this.open = open;
        this.close=close;
        this.sar = sar;
    }

    public SarEntry(int xIndex, float open,float close, float sar) {
        this(xIndex, open,close, sar, null);
    }


    public SarEntry copy() {

        SarEntry c = new SarEntry(getXIndex(),
                open,close, sar, getData());

        return c;

    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float close) {
        this.open = close;
    }

    public float getSar() {
        return sar;
    }

    public void setSar(float sar) {
        this.sar = sar;
    }

}
