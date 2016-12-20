package com.github.mikephil.charting.sharechart.extend;

import android.util.SparseArray;

import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.sharechart.sar.SarData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljs on 15/11/17.
 */
public class MarketData extends CombinedData {

    private SarData mSarData;

    private SparseArray<String> labels;

//    public MarketData() {
//        super();
//    }
//
//    public MarketData(List<String> xVals) {
//        super(xVals);
//    }
//
//    public MarketData(String[] xVals) {
//        super(xVals);
//    }

    public MarketData(List<String> xVals,SparseArray<String> labels){
        this.mXVals = xVals;
        this.mDataSets = new ArrayList<>();
        this.labels=labels;
        init();

    }


    public void setData(SarData data) {
        mSarData = data;
        mDataSets.addAll(data.getDataSets());
        init();
    }


    public SarData getSarData() {
        return mSarData;
    }


    @Override
    public void notifyDataChanged() {
        if (getLineData() != null)
            getLineData().notifyDataChanged();
        if (getBarData() != null)
            getBarData().notifyDataChanged();
        if (getCandleData() != null)
            getCandleData().notifyDataChanged();
        if (getScatterData() != null)
            getScatterData().notifyDataChanged();
        if (getBubbleData() != null)
            getBubbleData().notifyDataChanged();
        if (mSarData != null)
            mSarData.notifyDataChanged();

        init();
    }


    @Override
    protected void calcXValMaximumLength() {

        if (labels == null || labels.size() <= 0) {
            mXValMaximumLength = 1;
            return;
        }


        int max = 1;

        int size = labels.size();
        for (int i = 0; i < size; i++) {

            if (labels.valueAt(i) != null) {
                int length = labels.valueAt(i).length();

                if (length > max)
                    max = length;
            }
        }

        mXValMaximumLength = max;

    }
}