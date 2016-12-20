package com.github.mikephil.charting.dashboard;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljs on 15/7/3.
 */
public class DashBoradDataSet extends DataSet<Entry> {


    /**
     * Creates a new DataSet object with the given values it represents. Also, a
     * label that describes the DataSet can be specified. The label can also be
     * used to retrieve the DataSet from a ChartData object.
     *
     */

    private static List<Entry> toList(Entry dataSet) {
        List<Entry> sets = new ArrayList<Entry>();
        sets.add(dataSet);
        return sets;
    }

    public DashBoradDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }

    public DashBoradDataSet(Entry value){
        this(toList(value),"");
    }

    @Override
    public DashBoradDataSet copy() {
        return new DashBoradDataSet(getYVals(),"");
    }


//    public DashBoradDataSet(LineDataSet set) {
//        super(new ArrayList<String>(),set);
//    }
}
