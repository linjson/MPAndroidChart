package com.github.mikephil.charting.dashboard;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;

/**
 * Created by ljs on 15/7/3.
 */
public class DashBoradData extends ChartData<DashBoradDataSet> {


    public DashBoradData() {
    }

    public DashBoradData(float value) {
        buildValue(value);
    }

    public void setValue(float value){
        mDataSets.clear();
        buildValue(value);
    }

    private void buildValue(float value){
        Entry entry=new Entry(value,0);
        DashBoradDataSet dashBoradDataSet = new DashBoradDataSet(entry);
        mDataSets.add(dashBoradDataSet);
    }

}
