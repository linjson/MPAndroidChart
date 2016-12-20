package com.github.mikephil.charting.sharechart.sar;


import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;

/**
 * Created by ljs on 15/11/17.
 */
public interface SarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    SarData getSarData();

}
