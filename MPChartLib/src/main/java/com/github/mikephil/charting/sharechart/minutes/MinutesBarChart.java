package com.github.mikephil.charting.sharechart.minutes;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.sharechart.extend.ChartUtils;
import com.github.mikephil.charting.sharechart.extend.MarketBarChartRenderer;
import com.github.mikephil.charting.sharechart.extend.MarketChartTouchListener;
import com.github.mikephil.charting.sharechart.market.MarketProvider;
import com.github.mikephil.charting.sharechart.extend.MarketXAxis;
import com.github.mikephil.charting.sharechart.extend.MarketXAxisRenderer;
import com.github.mikephil.charting.sharechart.extend.MarketYAxis;
import com.github.mikephil.charting.sharechart.extend.MarketYAxisRenderer;
import com.github.mikephil.charting.sharechart.market.OnScrollDataListener;
import com.github.mikephil.charting.sharechart.market.OnSyncChartListener;

public class MinutesBarChart extends BarChart implements MarketProvider {

    protected boolean drawbarEnable;
    protected MarketChartTouchListener mMarketTouchListener;
    private OnScrollDataListener mOnScrollDataListener;
    private MarkerView mXAxisMarkerView;
    private String[] mScrollClass;

    public MinutesBarChart(Context context) {
        super(context);
    }

    public MinutesBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinutesBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        setDescription("");
        setHighlighter(new MinutesBarHighlighter(this));
        mAxisLeft = new MarketYAxis(YAxis.AxisDependency.LEFT);
        mAxisRendererLeft = new MarketYAxisRenderer(mViewPortHandler, (MarketYAxis) mAxisLeft, mLeftAxisTransformer);
        mXAxis = new MarketXAxis();
        mXAxisRenderer = new MarketXAxisRenderer(this, mViewPortHandler, (MarketXAxis) mXAxis, mLeftAxisTransformer);
        mRenderer = new MarketBarChartRenderer(this, mAnimator, mViewPortHandler);
        mChartTouchListener = new MarketChartTouchListener(this, mViewPortHandler.getMatrixTouch());

        mMarketTouchListener = (MarketChartTouchListener) mChartTouchListener;
    }

    public void setLongPressEnable(boolean value) {
        mMarketTouchListener.setLongPressEnable(value);
    }

    @Override
    public MarketYAxis getAxisLeft() {
        return (MarketYAxis) super.getAxisLeft();
    }

    public void setDrawBarEnable(boolean enable) {
        this.drawbarEnable = enable;
    }

    public MarketXAxis getXAxis() {
        return (MarketXAxis) mXAxis;
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        if (!drawbarEnable) {

            mXAxis.mAxisMinimum = 0;
            mXAxis.mAxisMaximum = mData.getXVals().size() - 1;

            mXAxis.mAxisRange = Math.abs(mXAxis.mAxisMaximum - mXAxis.mAxisMinimum);

        }
    }

    @Override
    protected void calcModulus() {

        mXAxis.mAxisLabelModulus = 1;
    }



    public void syncMatrix(float x, float sx, boolean islimit) {

    }

    @Override
    public void setOnLoadingViewListener(MarketChartTouchListener.OnLoadingViewListener l) {
        mMarketTouchListener.setOnLoadingViewListener(l);
    }


    @Override
    public OnScrollDataListener getOnScrollDataListener() {
        return mOnScrollDataListener;
    }

    @Override
    public void setOnScrollDataListener(OnScrollDataListener l) {
        this.mOnScrollDataListener = l;
    }

    public void setOnSyncChartListener(OnSyncChartListener l) {
        mMarketTouchListener.setOnSyncChartListener(l);
    }

    @Override
    public void computeScroll() {

        mMarketTouchListener.computeScroll();
    }


    @Override
    public boolean getDrawbarEnable() {
        return drawbarEnable;
    }

    @Override
    public boolean getHighLightStyle() {
        return false;
    }

    @Override
    public boolean isStopParentTouch() {
        return mMarketTouchListener.isStopParentTouch();
    }

    public Entry highlightTouchWithoutEvent(Highlight high) {
        Entry e = null;

        if (high == null || mData == null)
            mIndicesToHighlight = null;
        else {

            if (mLogEnabled)
                Log.i(LOG_TAG, "Highlighted: " + high.toString());

            e = mData.getEntryForHighlight(high);
            if (e == null || e.getXIndex() != high.getXIndex()) {
                mIndicesToHighlight = null;
            } else {
                // set the indices to highlight
                mIndicesToHighlight = new Highlight[]{
                        high
                };
            }
        }

        // redraw the chart
        invalidate();
        return e;
    }

    protected void drawMarkers(Canvas canvas) {

        // if there is no marker view or drawing marker is disabled

        if (mXAxisMarkerView == null) {
            return;
        }

        if (!mDrawMarkerViews || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];
            int xIndex = highlight.getXIndex();
            int dataSetIndex = highlight.getDataSetIndex();

            float deltaX = mXAxis.mAxisRange;

            if (xIndex <= deltaX && xIndex <= deltaX * mAnimator.getPhaseX()) {

                Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);

                // make sure entry not null
                if (e == null || e.getXIndex() != mIndicesToHighlight[i].getXIndex())
                    continue;

                float[] pos = getMarkerPosition(e, highlight);


                if (mXAxisMarkerView != null) {
                    measureMarkerView(mXAxisMarkerView, highlight, e);
                    float x = pos[0];
                    float y = mViewPortHandler.contentBottom();

                    if (pos[0] + mXAxisMarkerView.getXOffset(0) < mViewPortHandler.contentLeft()) {
                        x = mViewPortHandler.contentLeft() - mXAxisMarkerView.getXOffset(0);
                    } else if (pos[0] - mXAxisMarkerView.getXOffset(0) > mViewPortHandler.contentRight()) {
                        x = mViewPortHandler.contentRight() + mXAxisMarkerView.getXOffset(0);
                    } else {
                        x = pos[0];
                    }



                    mXAxisMarkerView.draw(canvas, x, y);


                }

            }
        }
    }

    private void measureMarkerView(MarkerView markerView, Highlight highlight, Entry e) {
        markerView.refreshContent(e, highlight);
        markerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        markerView.layout(0, 0, markerView.getMeasuredWidth(),
                markerView.getMeasuredHeight());
    }

    public void setXAxisMarkerView(MarkerView leftMarkerView) {
        this.mXAxisMarkerView = leftMarkerView;
    }

    public void setStopParentTouch(boolean enable) {
        mMarketTouchListener.setStopParentTouch(enable);
    }

    public void setScrollClass(String... scrollClass) {

        this.mScrollClass = scrollClass;
    }

    @Override
    public void enableScroll() {
        if (mScrollClass == null) {
            super.enableScroll();
            return;
        }
        for(int i=0;i<mScrollClass.length;i++) {
            ViewParent v = ChartUtils.findParent(getParent(), mScrollClass[i]);
            if (v != null) {
                ((ViewGroup)v).setEnabled(true);
                v.requestDisallowInterceptTouchEvent(false);
            }
        }
    }

    @Override
    public void disableScroll() {
        if (mScrollClass == null) {
            super.disableScroll();
            return;
        }
        for(int i=0;i<mScrollClass.length;i++) {
            ViewParent v = ChartUtils.findParent(getParent(), mScrollClass[i]);
            if (v != null) {
                ((ViewGroup)v).setEnabled(false);
                v.requestDisallowInterceptTouchEvent(true);
            }
        }
    }
}
