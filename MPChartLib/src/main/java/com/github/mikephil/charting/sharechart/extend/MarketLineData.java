package com.github.mikephil.charting.sharechart.extend;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljs on 16/4/22.
 */
public class MarketLineData extends LineData {

    public MarketLineData() {
        super();
    }

    public MarketLineData(List<String> xVals) {
        super(xVals);
    }

    public MarketLineData(String[] xVals) {
        super(xVals);
    }

    public MarketLineData(List<String> xVals, List<ILineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public MarketLineData(String[] xVals, List<ILineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public MarketLineData(List<String> xVals, ILineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public MarketLineData(String[] xVals, ILineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<ILineDataSet> toList(ILineDataSet dataSet) {
        List<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(dataSet);
        return sets;
    }


    @Override
    public void calcMinMax(int start, int end) {

        if (mDataSets == null || mDataSets.size() < 1) {

            mYMax = 0f;
            mYMin = 0f;
        } else {

            mYMin = Float.MAX_VALUE;
            mYMax = -Float.MAX_VALUE;

            for (int i = 0; i < mDataSets.size(); i++) {

                IDataSet set = mDataSets.get(i);
                set.calcMinMax(start, end);

                if (!Float.isNaN(set.getYMin())&&set.getYMin() < mYMin)
                    mYMin = set.getYMin();

                if (!Float.isNaN(set.getYMax())&&set.getYMax() > mYMax)
                    mYMax = set.getYMax();
            }

            if (mYMin == Float.MAX_VALUE) {
                mYMin = 0.f;
                mYMax = 0.f;
            }

            // left axis
            ILineDataSet firstLeft = getFirstLeft();

            if (firstLeft != null) {

                mLeftAxisMax = firstLeft.getYMax();
                mLeftAxisMin = firstLeft.getYMin();

                for (IDataSet dataSet : mDataSets) {
                    if (dataSet.getAxisDependency() == YAxis.AxisDependency.LEFT) {
                        if (!Float.isNaN(dataSet.getYMin())&&dataSet.getYMin() < mLeftAxisMin)
                            mLeftAxisMin = dataSet.getYMin();

                        if (!Float.isNaN(dataSet.getYMax())&&dataSet.getYMax() > mLeftAxisMax)
                            mLeftAxisMax = dataSet.getYMax();
                    }
                }

                if(Float.isNaN(mLeftAxisMax)){
                    mLeftAxisMax=0;
                }

                if(Float.isNaN(mLeftAxisMin)){
                    mLeftAxisMin=0;
                }


            }

            // right axis
            ILineDataSet firstRight = getFirstRight();

            if (firstRight != null) {

                mRightAxisMax = firstRight.getYMax();
                mRightAxisMin = firstRight.getYMin();

                for (IDataSet dataSet : mDataSets) {
                    if (dataSet.getAxisDependency() == YAxis.AxisDependency.RIGHT) {
                        if (dataSet.getYMin() < mRightAxisMin)
                            mRightAxisMin = dataSet.getYMin();

                        if (dataSet.getYMax() > mRightAxisMax)
                            mRightAxisMax = dataSet.getYMax();
                    }
                }
            }

            // in case there is only one axis, adjust the second axis
            handleEmptyAxis(firstLeft, firstRight);
        }
    }
}
