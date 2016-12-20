package com.github.mikephil.charting.sharechart.sar;

import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljs on 15/11/17.
 */
public class SarData
        extends BarLineScatterCandleBubbleData<ISarDataSet> {

    public SarData() {
        super();
    }

    public SarData(List<String> xVals) {
        super(xVals);
    }

    public SarData(String[] xVals) {
        super(xVals);
    }

    public SarData(List<String> xVals, List<ISarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public SarData(String[] xVals, List<ISarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public SarData(List<String> xVals, ISarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public SarData(String[] xVals, ISarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<ISarDataSet> toList(ISarDataSet dataSet) {
        List<ISarDataSet> sets = new ArrayList<>();
        sets.add(dataSet);
        return sets;
    }
}