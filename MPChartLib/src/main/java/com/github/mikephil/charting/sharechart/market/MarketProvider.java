package com.github.mikephil.charting.sharechart.market;

import com.github.mikephil.charting.sharechart.extend.MarketChartTouchListener;

/**
 * Created by ljs on 15/11/12.
 */
public interface MarketProvider extends OnSyncChartListener {
    OnScrollDataListener getOnScrollDataListener();

    void setOnScrollDataListener(OnScrollDataListener l);

    void setOnSyncChartListener(OnSyncChartListener l);

    void setOnLoadingViewListener(MarketChartTouchListener.OnLoadingViewListener l);

    boolean getDrawbarEnable();

    /**
     *
     * @return true:十字线,false:竖线
     */
    boolean getHighLightStyle();

    boolean isStopParentTouch();

}
