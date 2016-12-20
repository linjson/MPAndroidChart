package com.github.mikephil.charting.dashboard;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ljs on 15/7/3.
 */
public class ArcAxis extends AxisBase {


    private float mMin;
    private float mMax;
    private int mLabelCount = 6;
    public float[] mEntries = new float[0];
    public int mEntryCount;
    public int mDecimals;

    private String mUnit;
    public float mAxisRange;

    public ArcAxis() {

    }

    @Override
    public String getLongestLabel() {
        return null;
    }

    public int getLabelCount() {
        return mLabelCount;
    }

    public void setLabelCount(int labelCount) {
        this.mLabelCount = labelCount;
    }

    public float getMin() {
        return mMin;
    }

    public void setMin(float min) {
        this.mMin = min;
    }

    public float getMax() {
        return mMax;
    }

    public void setMax(float max) {
        this.mMax = max;
    }

    public void sortLimitLine() {
        if (mLimitLines == null) {
            return;
        }
        Collections.sort(mLimitLines, new LimitLineSort());
    }


    public String getUnit() {
        if(mUnit==null){
            return "";
        }
        return mUnit;
    }

    public void setUnit(String unit) {
        this.mUnit = unit;
    }

    private class LimitLineSort implements Comparator<LimitLine> {

        @Override
        public int compare(LimitLine lhs, LimitLine rhs) {
            return lhs.getLimit() > rhs.getLimit() ? 1 : -1;
        }
    }
}
