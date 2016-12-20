package com.github.mikephil.charting.sharechart.sar;

import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;

public interface ISarDataSet extends ILineScatterCandleRadarDataSet<SarEntry> {
    int getIncreasingColor();

    int getDecreasingColor();

    float getCircleSize();
}