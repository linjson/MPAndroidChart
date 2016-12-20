package com.github.mikephil.charting.sharechart.pai;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.List;

/**
 * Created by ljs on 16/3/31.
 */
public class PaiDataSet extends ScatterDataSet {
    /**
     * Custom path object the user can provide that is drawn where the values
     * are at. This is used when ScatterShape.CUSTOM is set for a DataSet.
     *
     * @param yVals
     * @param label
     */
    public PaiDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }



    @Override
    public void calcMinMax(int start, int end) {
        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;
    }
}
