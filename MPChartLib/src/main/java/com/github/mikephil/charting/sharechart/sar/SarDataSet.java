package com.github.mikephil.charting.sharechart.sar;

import android.graphics.Color;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineScatterCandleRadarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljs on 15/11/17.
 */
public class SarDataSet extends LineScatterCandleRadarDataSet<SarEntry> implements  ISarDataSet{



    private int mIncreasingColor = Color.argb(255, 222, 50, 109);
    private int mDecreasingColor = Color.argb(255, 58, 174, 98);
    private float mCircleSize = 5f;

    public SarDataSet(List<SarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<SarEntry> copy() {
        List<SarEntry> yVals = new ArrayList<>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add((mYVals.get(i)).copy());
        }

        SarDataSet copied = new SarDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mHighLightColor = mHighLightColor;
        copied.mIncreasingColor = mIncreasingColor;
        copied.mDecreasingColor = mDecreasingColor;
        return copied;
    }


    public int getIncreasingColor() {
        return mIncreasingColor;
    }

    public void setIncreasingColor(int color) {
        this.mIncreasingColor = color;
    }

    public int getDecreasingColor() {
        return mDecreasingColor;
    }

    public void setDecreasingColor(int color) {
        this.mDecreasingColor = color;
    }



    public void setCircleSize(float size) {
        mCircleSize = size;
    }

    public float getCircleSize() {
        return mCircleSize;
    }

}
