package com.github.mikephil.charting.sharechart.minutes;

import com.github.mikephil.charting.highlight.BarHighlighter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

/**
 * Created by ljs on 16/9/5.
 */
public class MinutesBarHighlighter extends BarHighlighter {
    public MinutesBarHighlighter(BarDataProvider chart) {
        super(chart);
    }

    @Override
    protected int getXIndex(float x) {
        int index = super.getXIndex(x);

        int max = 0;
        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            IDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!dataSet.isHighlightEnabled())
                continue;

            max = Math.max(dataSet.getEntryCount(), max);
        }

        if (index > max) {
            index = max - 1;
        }

        return index;

    }
}
