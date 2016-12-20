package com.github.mikephil.charting.sharechart.extend;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.highlight.CombinedHighlighter;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import java.util.List;

/**
 * Created by ljs on 16/9/5.
 */
public class MarketChartHighlighter extends CombinedHighlighter {
    public MarketChartHighlighter(BarLineScatterCandleBubbleDataProvider chart) {
        super(chart);
    }

    @Override
    protected int getXIndex(float x) {
        int index = super.getXIndex(x);

        CombinedData data = (CombinedData) mChart.getData();

        List<ChartData> dataObjects = data.getAllData();
        int max = 0;
        for (int i = 0; i < dataObjects.size(); i++) {

            for (int j = 0; j < dataObjects.get(i).getDataSetCount(); j++) {
                IDataSet dataSet = dataObjects.get(i).getDataSetByIndex(j);


                if (!dataSet.isHighlightEnabled())
                    continue;

                max = Math.max(dataSet.getEntryCount(), max);
            }

        }

        if (index > max) {
            index = max - 1;
        }

        return index;

    }
}
