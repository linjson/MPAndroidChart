package com.github.mikephil.charting.sharechart.extend;

import android.graphics.DashPathEffect;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Created by ljs on 16/4/22.
 */
public class MarketLineDataSet extends LineDataSet {
    public MarketLineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public void calcMinMax(int start, int end) {

        if (mYVals == null)
            return;

        final int yValCount = mYVals.size();

        if (yValCount == 0)
            return;

        int endValue;

        if (end == 0 || end >= yValCount)
            endValue = yValCount - 1;
        else
            endValue = end;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        for (int i = start; i <= endValue; i++) {

            Entry e = mYVals.get(i);

            if (e != null && !Float.isNaN(e.getVal())) {

                if (e.getVal() < mYMin)
                    mYMin = e.getVal();

                if (e.getVal() > mYMax)
                    mYMax = e.getVal();
            }
        }

        if (mYMin == Float.MAX_VALUE) {
            mYMin = Float.NaN;// 0.f;
            mYMax = Float.NaN;// 0.f;

        }

        if(Float.isNaN(mYMin)){
            mYMin=0;
            mYMax=1;
        }

    }

    public void enableDashedHighlightLine(float[] intervals, float phase) {
        mHighlightDashPathEffect = new DashPathEffect(intervals, phase);
    }
}
