package com.github.mikephil.charting.sharechart.minutes;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.sharechart.extend.ChartUtils;
import com.github.mikephil.charting.sharechart.extend.MarketChartTouchListener;
import com.github.mikephil.charting.sharechart.extend.MarketXAxis;
import com.github.mikephil.charting.sharechart.extend.MarketXAxisRenderer;
import com.github.mikephil.charting.sharechart.extend.MarketYAxis;
import com.github.mikephil.charting.sharechart.extend.MarketYAxisRenderer;
import com.github.mikephil.charting.sharechart.market.MarketProvider;
import com.github.mikephil.charting.sharechart.market.OnScrollDataListener;
import com.github.mikephil.charting.sharechart.market.OnSyncChartListener;
import com.github.mikephil.charting.sharechart.market.ShinePointView;

/**
 * Created by ljs on 15/11/3.
 */
public class MinutesLineChart extends BarLineChartBase<LineData> implements LineDataProvider, MarketProvider {

    private MarkerView mLeftMarkerView;
    private MarkerView mRightMarkerView;
    private MarketChartTouchListener mMarketTouchListener;
    private MarkerView mXAxisMarkerView;
    private ShinePointView pointView;
    private String[] mScrollClass;
    private int pointDataSet;

    public MinutesLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MinutesLineChart(Context context) {
        super(context);
    }

    public MinutesLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        setDescription("");
        setClipChildren(false);
        setHighlighter(new MinutesLineHighlighter(this));
        mXAxis = new MarketXAxis();
        mAxisLeft = new MarketYAxis(YAxis.AxisDependency.LEFT);
        mAxisRight = new MarketYAxis(YAxis.AxisDependency.RIGHT);


        mXAxisRenderer = new MarketXAxisRenderer(this, mViewPortHandler, (MarketXAxis) mXAxis, mLeftAxisTransformer);
        mAxisRendererLeft = new MarketYAxisRenderer(mViewPortHandler, (MarketYAxis) mAxisLeft, mLeftAxisTransformer);
        mAxisRendererRight = new MarketYAxisRenderer(mViewPortHandler, (MarketYAxis) mAxisRight, mRightAxisTransformer);
        mRenderer = new LineChartRenderer(this, mAnimator, mViewPortHandler);
        mChartTouchListener = new MarketChartTouchListener(this, mViewPortHandler.getMatrixTouch());

        mMarketTouchListener = (MarketChartTouchListener) mChartTouchListener;
    }

    public void setLongPressEnable(boolean value) {
        mMarketTouchListener.setLongPressEnable(value);
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();


        if (mXAxis.mAxisRange == 0 && mData.getYValCount() > 0)
            mXAxis.mAxisRange = 1;


    }


    @Override
    public LineData getLineData() {
        return mData;
    }


    @Override
    public MarketXAxis getXAxis() {
        return (MarketXAxis) super.getXAxis();
    }

    @Override
    public MarketYAxis getAxisLeft() {
        return (MarketYAxis) super.getAxisLeft();
    }

    @Override
    public MarketYAxis getAxisRight() {
        return (MarketYAxis) super.getAxisRight();
    }

    public void setLeftMarkerView(MarkerView leftMarkerView) {
        this.mLeftMarkerView = leftMarkerView;
    }

    public void setXAxisMarkerView(MarkerView leftMarkerView) {
        this.mXAxisMarkerView = leftMarkerView;
    }

    public void setRightMarkerView(MarkerView rightMarkerView) {
        this.mRightMarkerView = rightMarkerView;
    }

    protected void drawMarkers(Canvas canvas) {

        // if there is no marker view or drawing marker is disabled

        if (mLeftMarkerView == null && mRightMarkerView == null && mXAxisMarkerView == null) {
            return;
        }

        if (!mDrawMarkerViews || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            int xIndex = mIndicesToHighlight[i].getXIndex();
            int dataSetIndex = mIndicesToHighlight[i].getDataSetIndex();
            float deltaX = mXAxis.mAxisRange;
            if (xIndex <= deltaX && xIndex <= deltaX * mAnimator.getPhaseX()) {

                Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);

                // make sure entry not null
                if (e == null || e.getXIndex() != mIndicesToHighlight[i].getXIndex())
                    continue;

                float[] pos = getMarkerPosition(e, highlight);

                // check bounds
//                if (!mViewPortHandler.isInBounds(pos[0], pos[1])) {
//                    System.out.println("isInBounds");
//                    continue;
//                }

                // callbacks to update the content

                if (mLeftMarkerView != null && mRightMarkerView == null) {
                    measureMarkerView(mLeftMarkerView, highlight, e);
                    float x = 0;
                    float y = 0;
                    if (pos[0] > mViewPortHandler.getContentRect().centerX()) {
                        x = mViewPortHandler.contentLeft() - mLeftMarkerView.getXOffset(0);
                    } else {
                        x = mViewPortHandler.contentRight() + mLeftMarkerView.getXOffset(0) - mLeftMarkerView.getWidth();
                    }

                    if (pos[1] + mLeftMarkerView.getYOffset(0) < mViewPortHandler.contentTop()) {
                        y = mViewPortHandler.contentTop() - mLeftMarkerView.getYOffset(0);
                    } else if (pos[1] - mLeftMarkerView.getYOffset(0) > mViewPortHandler.contentBottom()) {
                        y = mViewPortHandler.contentBottom() + mLeftMarkerView.getYOffset(0);
                    } else {
                        y = pos[1];
                    }


                    mLeftMarkerView.draw(canvas, x, y);

                } else if (mLeftMarkerView != null) {
                    measureMarkerView(mLeftMarkerView, highlight, e);
                    if (pos[1] + mLeftMarkerView.getYOffset(0) < mViewPortHandler.contentTop()) {
                        mLeftMarkerView.draw(canvas, mViewPortHandler.offsetLeft(), mViewPortHandler.contentTop() - mLeftMarkerView.getYOffset(0));
                    } else if (pos[1] - mLeftMarkerView.getYOffset(0) > mViewPortHandler.contentBottom()) {
                        mLeftMarkerView.draw(canvas, mViewPortHandler.offsetLeft(), mViewPortHandler.contentBottom() + mLeftMarkerView.getYOffset(0));
                    } else {
                        mLeftMarkerView.draw(canvas, mViewPortHandler.offsetLeft(), pos[1]);
                    }

                }

                if (mRightMarkerView != null) {
                    measureMarkerView(mRightMarkerView, highlight, e);

                    if (pos[1] + mRightMarkerView.getYOffset(0) < mViewPortHandler.contentTop()) {
                        mRightMarkerView.draw(canvas, mViewPortHandler.contentRight(), mViewPortHandler.contentTop() - mRightMarkerView.getYOffset(0));
                    } else if (pos[1] - mLeftMarkerView.getYOffset(0) > mViewPortHandler.contentBottom()) {
                        mRightMarkerView.draw(canvas, mViewPortHandler.contentRight(), mViewPortHandler.contentBottom() + mRightMarkerView.getYOffset(0));
                    } else {
                        mRightMarkerView.draw(canvas, mViewPortHandler.contentRight(), pos[1]);
                    }
                }

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


//                    if (pos[1] - mXAxisMarkerView.getYOffset(0) + mXAxisMarkerView.getHeight() > mViewPortHandler.contentBottom()) {
//                        y = mViewPortHandler.contentTop() - mXAxisMarkerView.getYOffset(0);
//                    } else {
//                        y = mViewPortHandler.contentBottom() - mXAxisMarkerView.getHeight() - mXAxisMarkerView.getYOffset(0);
//                    }


                    mXAxisMarkerView.draw(canvas, x, y);


                }

//                if (pos[1] - mMarkerView.getHeight() <= 0) {
//                    float y = mMarkerView.getHeight() - pos[1];
//                    mMarkerView.draw(canvas, pos[0], pos[1] + y);
//                } else {
//                    mMarkerView.draw(canvas, pos[0], pos[1]);
//                }
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

    protected void calcModulus() {
        mXAxis.mAxisLabelModulus = 1;
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
                high = null;
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

    public void setShinePointView(ShinePointView pointView,int pointDataSet,boolean anim) {

        if (pointView == null) {
            return;
        }

        if (this.pointView != null) {
            removeView(this.pointView);
        }
        this.pointView = pointView;
        this.pointDataSet=pointDataSet;
        this.pointView.refreshView();
        addView(this.pointView);
        if(anim) {
            this.pointView.startAnimation();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawShinePointView();

    }

    private void drawShinePointView() {

        if (pointView == null) {
            return;
        }

        if (mData == null) {
            pointView.layout(-999, -999, -999, -999);
            return;
        }

        ILineDataSet dataSet = getData().getDataSetByIndex(pointDataSet);
        int size = dataSet.getEntryCount();

        if (size > 0) {
            Entry entry = dataSet.getEntryForIndex(size - 1);

            float[] pos = {entry.getXIndex(), entry.getVal()};
            mLeftAxisTransformer.pointValuesToPixel(pos);


            pointView.layout((int) (pos[0] - pointView.getMeasuredWidth() / 2),
                    (int) (pos[1] - pointView.getMeasuredHeight() / 2),
                    (int) (pos[0] + pointView.getMeasuredWidth() / 2),
                    (int) (pos[1] + pointView.getMeasuredHeight() / 2));
        }


    }

    @Override
    public OnScrollDataListener getOnScrollDataListener() {
        return null;
    }

    @Override
    public void setOnScrollDataListener(OnScrollDataListener l) {

    }

    @Override
    public void setOnSyncChartListener(OnSyncChartListener l) {

    }

    @Override
    public void setOnLoadingViewListener(MarketChartTouchListener.OnLoadingViewListener l) {

    }

    @Override
    public boolean getDrawbarEnable() {
        return false;
    }

    @Override
    public boolean getHighLightStyle() {
        return false;
    }

    @Override
    public boolean isStopParentTouch() {
        return mMarketTouchListener.isStopParentTouch();
    }

    @Override
    public void syncMatrix(float transx, float scalex, boolean islimit) {

    }

    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if (mRenderer != null && mRenderer instanceof LineChartRenderer) {
            ((LineChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }

    public void setStopParentTouch(boolean enable) {
        mMarketTouchListener.setStopParentTouch(enable);
    }

    @Override
    public float getYChartMin() {
        return mAxisLeft.getAxisMinimum();
    }

    @Override
    public float getYChartMax() {
        return mAxisLeft.getAxisMaximum();
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
//                System.out.printf("==>enableScroll,%s \n",mScrollClass[i]);
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
//                System.out.printf("==>disableScroll,%s \n",mScrollClass[i]);
            }
        }
    }
}
