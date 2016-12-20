package com.github.mikephil.charting.sharechart.market;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by ljs on 15/11/12.
 */
public interface OnMarketSelectValueListener {


    void onValueSelected(int xIndex,Entry klineEntry,Entry affilateEntry);

    void onValueNothing();
}
