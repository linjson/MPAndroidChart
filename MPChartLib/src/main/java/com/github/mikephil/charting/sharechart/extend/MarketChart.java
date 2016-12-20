package com.github.mikephil.charting.sharechart.extend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.sharechart.market.LoadingMarkerView;
import com.github.mikephil.charting.sharechart.market.MarketProvider;
import com.github.mikephil.charting.sharechart.market.OnScrollDataListener;
import com.github.mikephil.charting.sharechart.market.OnSyncChartListener;
import com.github.mikephil.charting.sharechart.market.ValueMarkerView;
import com.github.mikephil.charting.sharechart.sar.SarData;
import com.github.mikephil.charting.sharechart.sar.SarDataProvider;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by ljs on 15/11/17.
 */
public class MarketChart extends BarLineChartBase<MarketData> implements LineDataProvider,
        BarDataProvider, ScatterDataProvider, CandleDataProvider, BubbleDataProvider, SarDataProvider, MarketProvider {


    /**
     * flag that enables or disables the highlighting arrow
     */
    private boolean mDrawHighlightArrow = false;

    /**
     * if set to true, all values are drawn above their bars, instead of below
     * their top
     */
    private boolean mDrawValueAboveBar = true;


    /**
     * if set to true, a grey area is drawn behind each bar that indicates the
     * maximum value
     */
    private boolean mDrawBarShadow = false;

    protected DrawOrder[] mDrawOrder = new DrawOrder[]{
            DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.LINE, DrawOrder.CANDLE, DrawOrder.POINT, DrawOrder.SAR
    };
    private MarketChartTouchListener mKLineTouchListener;
    private MarkerView mHighValueMarkerView;
    private MarkerView mLowValueMarkerView;
    private OnScrollDataListener mOnScrollDataListener;
    private LoadingMarkerView loadingMarkView;
    private float minXRange = -1;
    private float maxXRange = -1;
    private ValueMarkerView valueMarkerView;

    private int valueMarkerViewPosition;


    private final int ValueMarkerViewPosition_Left = 1;
    private final int ValueMarkerViewPosition_Right = 2;
    private float mIndicateLength;
    private Paint mIndicatePaint;
    private boolean mHighlightStyle;
    private MarkerView mXAxisMarkerView;
    private View buttonView;
    private boolean isShowLastValueMarker;
    private String[] mScrollClass;

    @Override
    public SarData getSarData() {
        if (mData == null)
            return null;
        return mData.getSarData();
    }

    /**
     * enum that allows to specify the order in which the different data objects
     * for the combined-chart are drawn
     */
    public enum DrawOrder {
        BAR, BUBBLE, LINE, CANDLE, SCATTER, SAR, POINT
    }

    public MarketChart(Context context) {
        super(context);
    }

    public MarketChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarketChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        setHighlighter(new MarketChartHighlighter(this));
        mIndicateLength = Utils.convertDpToPixel(10);
        mIndicatePaint = new Paint();
        mIndicatePaint.setAntiAlias(true);
        mIndicatePaint.setColor(0xff82B6D1);
        mIndicatePaint.setStyle(Paint.Style.STROKE);
        mIndicatePaint.setStrokeWidth(2f);


        setDescription("");
        mXAxis = new MarketXAxis();
        mChartTouchListener = new MarketChartTouchListener(this, mViewPortHandler.getMatrixTouch());
        mXAxisRenderer = new MarketXAxisRenderer(this, mViewPortHandler, (MarketXAxis) mXAxis, mLeftAxisTransformer);
        mAxisLeft = new MarketYAxis(YAxis.AxisDependency.LEFT);
        mAxisRendererLeft = new MarketYAxisRenderer(mViewPortHandler, (MarketYAxis) mAxisLeft, mLeftAxisTransformer);

        mKLineTouchListener = (MarketChartTouchListener) mChartTouchListener;


    }


    public void restoreSetting() {
        mAxisLeft = new MarketYAxis(YAxis.AxisDependency.LEFT);
        mAxisRendererLeft = new MarketYAxisRenderer(mViewPortHandler, (MarketYAxis) mAxisLeft, mLeftAxisTransformer);
    }


    @Override
    public LineData getLineData() {
        if (mData == null)
            return null;
        return mData.getLineData();
    }

    @Override
    public BarData getBarData() {
        if (mData == null)
            return null;
        return mData.getBarData();
    }

    @Override
    public ScatterData getScatterData() {
        if (mData == null)
            return null;
        return mData.getScatterData();
    }

    @Override
    public CandleData getCandleData() {
        if (mData == null)
            return null;
        return mData.getCandleData();
    }

    @Override
    public BubbleData getBubbleData() {
        if (mData == null)
            return null;
        return mData.getBubbleData();
    }

    @Override
    public boolean isDrawBarShadowEnabled() {
        return mDrawBarShadow;
    }

    @Override
    public boolean isDrawValueAboveBarEnabled() {
        return mDrawValueAboveBar;
    }

    @Override
    public boolean isDrawHighlightArrowEnabled() {
        return mDrawHighlightArrow;
    }


    /**
     * If set to true, a grey area is drawn behind each bar that indicates the
     * maximum value. Enabling his will reduce performance by about 50%.
     *
     * @param enabled
     */
    public void setDrawBarShadow(boolean enabled) {
        mDrawBarShadow = enabled;
    }

    /**
     * Returns the currently set draw order.
     *
     * @return
     */
    public DrawOrder[] getDrawOrder() {
        return mDrawOrder;
    }

    /**
     * Sets the order in which the provided data objects should be drawn. The
     * earlier you place them in the provided array, the further they will be in
     * the background. e.g. if you provide new DrawOrer[] { DrawOrder.BAR,
     * DrawOrder.LINE }, the bars will be drawn behind the lines.
     *
     * @param order
     */
    public void setDrawOrder(DrawOrder[] order) {
        if (order == null || order.length <= 0)
            return;
        mDrawOrder = order;
    }

    @Override
    public MarketXAxis getXAxis() {
        return (MarketXAxis) super.getXAxis();
    }

    @Override
    public MarketYAxis getAxisLeft() {
        return (MarketYAxis) super.getAxisLeft();
    }


    protected void calcModulus() {

        if (mXAxis == null || !mXAxis.isEnabled())
            return;

        if (!mXAxis.isAxisModulusCustom()) {

            float[] values = new float[9];
            mViewPortHandler.getMatrixTouch().getValues(values);

            final int size = getXAxis().getShowLabels().size();
//            float a = size * mViewPortHandler.contentWidth() / (getHighestVisibleXIndex() - getLowestVisibleXIndex() + 1);


            double count = Math
                    .ceil((mData.getXValCount() * mXAxis.mLabelRotatedWidth)
                            / (values[Matrix.MSCALE_X] * mViewPortHandler.contentWidth()));


            mXAxis.mAxisLabelModulus = (int) Math
                    .ceil(size * count / (mData.getXValCount()));


        }

        if (mLogEnabled)
            Log.i(LOG_TAG, "X-Axis modulus: " + mXAxis.mAxisLabelModulus + ", x-axis label width: "
                    + mXAxis.mLabelWidth + ", content width: " + mViewPortHandler.contentWidth());

        if (mXAxis.mAxisLabelModulus < 1)
            mXAxis.mAxisLabelModulus = 1;
    }

    @Override
    public void setData(MarketData data) {
        mRenderer = null;
        super.setData(data);
        mRenderer = new MarketChartRenderer(this, mAnimator, mViewPortHandler);
        mRenderer.initBuffers();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLoadingMarker();
        drawHighLowMarkerView(canvas);
        drawValueMarker(canvas);
        drawButtonView();

    }

    private CandleEntry[] getCandleDataHighLow() {


        CandleEntry[] entries = new CandleEntry[2];

        if (getData() == null) {
            return entries;
        }

        CandleData candleData = getData().getCandleData();

        if (candleData == null || candleData.getDataSets() == null ||
                candleData.getDataSets().size() == 0) {
            return entries;
        }


        final int start = getLowestVisibleXIndex();
        final int end = getHighestVisibleXIndex();
        ICandleDataSet dataset = candleData.getDataSets().get(0);
        int count = dataset.getEntryCount();
        int endValue;
        if (end == 0)
            endValue = count - 1;
        else
            endValue = end;

        float min = Float.MAX_VALUE;
        float max = -Float.MIN_VALUE;


        for (int i = start; i <= endValue && i < count; i++) {


            CandleEntry e = dataset.getEntryForXIndex(i);
            if (e != null) {

                if (e.getLow() < min && !Float.isNaN(e.getLow())) {
                    min = e.getLow();
                    entries[0] = e;
                }

                if (e.getHigh() > max && !Float.isNaN(e.getHigh())) {
                    max = e.getHigh();
                    entries[1] = e;
                }
            }
        }


        return entries;
    }

    private void drawHighLowMarkerView(Canvas canvas) {
        if (mHighValueMarkerView == null || mLowValueMarkerView == null) {
            return;
        }
        CandleEntry[] entries = getCandleDataHighLow();

        CandleEntry maxEntry = entries[1];
        CandleEntry minEntry = entries[0];
        if (mHighValueMarkerView != null && maxEntry != null) {


            measureMarkerView(mHighValueMarkerView, null, maxEntry);
            renderMarkView(canvas, mHighValueMarkerView, maxEntry, true);
        }


        if (mLowValueMarkerView != null && minEntry != null) {
            measureMarkerView(mLowValueMarkerView, null, minEntry);
            renderMarkView(canvas, mLowValueMarkerView, minEntry, false);
        }
    }

    private void renderMarkView(Canvas canvas, MarkerView markerView, CandleEntry entry, boolean high) {
        float[] pos = {entry.getXIndex(), high ? entry.getHigh() : entry.getLow()};
        mLeftAxisTransformer.pointValuesToPixel(pos);
//        if (pos[1] - markerView.getHeight() <= 0) {
//            float y = markerView.getHeight() - pos[1];
//            markerView.draw(canvas, pos[0], pos[1] + y);
//        } else {
//            markerView.draw(canvas, pos[0], pos[1]);
//        }

        float x = 0, y = 0;
        if ((pos[0] - markerView.getWidth() - mIndicateLength) < mViewPortHandler.contentLeft()) {
            canvas.drawLine(pos[0], pos[1], pos[0] + mIndicateLength, pos[1], mIndicatePaint);

            if (pos[1] + markerView.getXOffset(0) < mViewPortHandler.contentTop()) {
                x = pos[0] + mIndicateLength - markerView.getXOffset(0);
                y = mViewPortHandler.contentTop() + markerView.getXOffset(0);
            } else {
                x = pos[0] + mIndicateLength - markerView.getXOffset(0);
                y = pos[1];
            }

        } else {
            canvas.drawLine(pos[0] - mIndicateLength, pos[1], pos[0], pos[1], mIndicatePaint);
//            markerView.draw(canvas, pos[0] + mIndicateLength, pos[1]);
            if (pos[1] + markerView.getXOffset(0) < mViewPortHandler.contentTop()) {
                x = pos[0] - mIndicateLength - markerView.getWidth() - markerView.getXOffset(0);
                y = mViewPortHandler.contentTop() + markerView.getXOffset(0);
            } else {
                x = pos[0] - mIndicateLength - markerView.getWidth() - markerView.getXOffset(0);
                y = pos[1];
            }
        }

        markerView.draw(canvas, x, y);


    }

    private void measureMarkerView(MarkerView markerView, Highlight high, Entry e) {

        markerView.refreshContent(e, high);
        markerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        markerView.layout(0, 0, markerView.getMeasuredWidth(),
                markerView.getMeasuredHeight());
    }

    protected void drawLoadingMarker() {


        if (loadingMarkView == null) {
            return;
        }

        if (getData() == null) {
            loadingMarkView.layout(0, 0, 0, 0);
            return;
        }

        float spaceX = mViewPortHandler.getDragOffsetX();


        Matrix matrix = mViewPortHandler.getMatrixTouch();
        float[] val = getMatrixValues(matrix);

        float x = val[Matrix.MTRANS_X];
        if (spaceX == 0) {
            loadingMarkView.layout(0, 0, 0, 0);
            return;
        }

//        Entry e = mData.getDataSets().get(0).getEntryForXIndex(0);
//        float[] pos = getMarkerPosition(e, 0);


        int offsetY = (int) ((mViewPortHandler.getContentRect().height() - loadingMarkView.getMeasuredHeight()) / 2 + mViewPortHandler.offsetTop());

        int offsetX = (int) (mViewPortHandler.contentLeft() + (spaceX - loadingMarkView.getMeasuredWidth()) / 2);
//        loadingMarkView.draw(canvas, pos[0], 0);

        int width = (int) Math.min(Math.max(x - (spaceX - loadingMarkView.getMeasuredWidth()) / 2, 0), loadingMarkView.getMeasuredWidth());
        int height = loadingMarkView.getMeasuredHeight();

        loadingMarkView.layout(offsetX, offsetY, offsetX + width,
                offsetY + height);

    }

    @Override
    public void computeScroll() {

        mKLineTouchListener.computeScroll();
    }

    public void closeLoadingView() {
//        mMarketTouchListener.complieLoading();
        mKLineTouchListener.closeLoading();

    }

    @Override
    public boolean getDrawbarEnable() {
        return true;
    }

    @Override
    public boolean getHighLightStyle() {
        return mHighlightStyle;
    }

    @Override
    public boolean isStopParentTouch() {
        return ((MarketChartTouchListener) mChartTouchListener).isStopParentTouch();
    }

    public void setStopParentTouch(boolean enable) {
        ((MarketChartTouchListener) mChartTouchListener).setStopParentTouch(enable);
    }

    public void setOnLoadingViewListener(MarketChartTouchListener.OnLoadingViewListener l) {
        mKLineTouchListener.setOnLoadingViewListener(l);
    }

    public void setLoadingViewOpen(boolean open) {
        mKLineTouchListener.setLoadingViewOpen(open);
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

    public void setHighValueMarkerView(MarkerView v) {
        this.mHighValueMarkerView = v;
    }


    public void setLowValueMarkerView(MarkerView v) {
        this.mLowValueMarkerView = v;
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
        mKLineTouchListener.setOnSyncChartListener(l);
    }


    private float[] getMatrixValues(Matrix matrix) {
        float[] val = new float[9];
        matrix.getValues(val);
        return val;
    }

    @Override
    public void syncMatrix(float transx, float scalex, boolean islimit) {
        mKLineTouchListener.performScrollData();
        mKLineTouchListener.prepareSync();
        Matrix matrix = mViewPortHandler.getMatrixTouch();
        float[] val = getMatrixValues(matrix);

        val[Matrix.MTRANS_X] = transx;
        val[Matrix.MSCALE_X] = scalex;

        matrix.setValues(val);
        if (!islimit) {
            invalidate();
        } else {
            mViewPortHandler.refresh(matrix, this, true);
        }
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();


        if (getBarData() != null || getCandleData() != null || getBubbleData() != null || getSarData() != null) {
            mXAxis.mAxisMinimum = -0.5f;
            mXAxis.mAxisMaximum = mData.getXVals().size() - 0.5f;

            if (getBubbleData() != null) {

                for (IBubbleDataSet set : getBubbleData().getDataSets()) {

                    final float xmin = set.getXMin();
                    final float xmax = set.getXMax();

                    if (xmin < mXAxis.mAxisMinimum)
                        mXAxis.mAxisMinimum = xmin;

                    if (xmax > mXAxis.mAxisMaximum)
                        mXAxis.mAxisMaximum = xmax;
                }
            }
        }

        mXAxis.mAxisRange = Math.abs(mXAxis.mAxisMaximum - mXAxis.mAxisMinimum);

        if (mXAxis.mAxisRange == 0.f && getLineData() != null && getLineData().getYValCount() > 0) {
            mXAxis.mAxisRange = 1.f;
        }

    }

    public void setLoadingMarkerView(LoadingMarkerView loadingMarkView) {

        if (loadingMarkView == null) {
            return;
        }

        if (this.loadingMarkView != null) {
            removeView(this.loadingMarkView);
        }

        this.loadingMarkView = loadingMarkView;
        this.loadingMarkView.refreshView();
        addView(this.loadingMarkView);

    }

    @Override
    public void setVisibleXRange(float minXRange, float maxXRange) {
        this.minXRange = minXRange;
        this.maxXRange = maxXRange;
        super.setVisibleXRange(minXRange, maxXRange);
    }

    @Override
    public void setVisibleXRangeMinimum(float minXRange) {
        this.minXRange = minXRange;
        super.setVisibleXRangeMinimum(minXRange);
    }

    @Override
    public void setVisibleXRangeMaximum(float maxXRange) {
        this.maxXRange = maxXRange;
        super.setVisibleXRangeMaximum(maxXRange);
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        if (this.minXRange != -1) {
            super.setVisibleXRangeMinimum(minXRange);
        }

        if (this.maxXRange != -1) {
            super.setVisibleXRangeMaximum(maxXRange);
        }
    }

    @Override
    public void setOnTouchListener(ChartTouchListener l) {
        super.setOnTouchListener(l);
        mKLineTouchListener = (MarketChartTouchListener) l;
    }

    public void setValueMarkerView(ValueMarkerView markerview) {
        this.valueMarkerView = markerview;

        valueMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        int top = valueMarkerView.getMeasuredHeight();


        float srcTop = getExtraTopOffset();
        if (top > srcTop) {
            setExtraTopOffset(Utils.convertPixelsToDp(top));
        }

    }

    public void setButtonView(View markerView) {


        this.buttonView = markerView;

        if (this.buttonView != null) {
            removeView(this.buttonView);
        }

        if (markerView == null) {
            return;
        }


        this.buttonView = markerView;
        this.buttonView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        addView(this.buttonView);
        int top = this.buttonView.getMeasuredHeight();
        float srcTop = getExtraTopOffset();
        if (top > srcTop) {
            setExtraTopOffset(Utils.convertPixelsToDp(top));
        }

    }


    private void drawButtonView() {
        if (this.buttonView == null) {
            return;
        }
        int startY = (int) (getViewPortHandler().contentTop() - this.buttonView.getMeasuredHeight());
        int startX = (int) (getViewPortHandler().contentLeft() - mBorderPaint.getStrokeWidth());
        this.buttonView.layout(
                startX, startY,
                this.buttonView.getMeasuredWidth() + startX,
                startY + this.buttonView.getMeasuredHeight());
    }

    private void drawValueMarker(Canvas canvas) {
        if (valueMarkerView == null || !valuesToHighlight() || mData == null) {

            if (isShowLastValueMarker && mData != null) {
                Highlight highlight = null;
                int last = 1;
                Entry e = null;
                do {
                    highlight = new Highlight(mData.getXValCount() - last, 0);
                    e = mData.getEntryForHighlight(highlight);
                    last++;
                } while (e == null || e.getXIndex() != highlight.getXIndex());

                if (e != null && highlight != null) {
                    showValueMarker(canvas, e, highlight, false);
                }
            }


            return;
        }
        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            int xIndex = highlight.getXIndex();

            float deltaX = mXAxis.mAxisRange;

            if (xIndex <= deltaX && xIndex <= deltaX * mAnimator.getPhaseX()) {

                Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
                showValueMarker(canvas, e, highlight, true);


            }


        }

    }

    private void showValueMarker(Canvas canvas, Entry e, Highlight highlight, boolean checkBound) {


        // make sure entry not null
        if (e == null || e.getXIndex() != highlight.getXIndex())
            return;

        float[] pos = getMarkerPosition(e, highlight);

        // check bounds
        if (checkBound && !mViewPortHandler.isInBounds(pos[0], pos[1]))
            return;

        valueMarkerView.setMarkViewPosition(valueMarkerViewPosition);
        measureMarkerView(valueMarkerView, highlight, e);

//                if (valueMarkerViewPosition == ValueMarkerViewPosition_Left) {
//                    valueMarkerView.draw(canvas, mViewPortHandler.contentLeft(), mViewPortHandler.contentTop() + 1);
//                } else {
//                    valueMarkerView.draw(canvas, mViewPortHandler.contentRight() - valueMarkerView.getWidth() - 2, mViewPortHandler.contentTop() + 1);
//                }

        valueMarkerView.draw(canvas, mViewPortHandler.contentRight() - valueMarkerView.getWidth(), 0);
    }

    @Override
    public Highlight getHighlightByTouchPoint(float x, float y) {

        if (mViewPortHandler.getContentCenter().x < x) {
            this.valueMarkerViewPosition = ValueMarkerViewPosition_Left;
        } else {
            this.valueMarkerViewPosition = ValueMarkerViewPosition_Right;
        }


        Highlight h = super.getHighlightByTouchPoint(x, y);
//        MarketHighlight marketHighlight=null;
//        if (h != null) {
//            marketHighlight=new MarketHighlight(h);
//            marketHighlight.setValuePosition(this.valueMarkerViewPosition);
//        }
        return h;

    }

    public void setValueMarkerViewPosition(int valueMarkerViewPosition) {
        this.valueMarkerViewPosition = valueMarkerViewPosition;
    }


    public void restoreScale() {
        Matrix matrix = mViewPortHandler.getMatrixTouch();

        matrix.setScale(1, 1);


        mViewPortHandler.refresh(matrix, this, false);
    }

    @Override
    protected void drawMarkers(Canvas canvas) {

        // if there is no marker view or drawing marker is disabled

        if (mMarkerView == null && mXAxisMarkerView == null) {
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


                if (mMarkerView != null) {
                    measureMarkerView(mMarkerView, highlight, e);

                    float x = 0;
                    float y = 0;
                    if (pos[0] > mViewPortHandler.getContentRect().centerX()) {
                        x = mViewPortHandler.contentLeft() - mMarkerView.getXOffset(0);
                    } else {
                        x = mViewPortHandler.contentRight() + mMarkerView.getXOffset(0) - mMarkerView.getWidth();
                    }

                    if (pos[1] + mMarkerView.getYOffset(0) < mViewPortHandler.contentTop()) {
                        y = mViewPortHandler.contentTop() - mMarkerView.getYOffset(0);
                    } else if (pos[1] - mMarkerView.getYOffset(0) > mViewPortHandler.contentBottom()) {
                        y = mViewPortHandler.contentBottom() + mMarkerView.getYOffset(0);
                    } else {
                        y = pos[1];
                    }
                    mMarkerView.draw(canvas, x, y);
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


                    mXAxisMarkerView.draw(canvas, x, y);


                }


            }
        }
    }

    public void setHighlightStyle(boolean b) {
        mHighlightStyle = b;
    }

    @Override
    public void moveViewToX(float xIndex) {


        float yValue = 0f;

        float[] pts = new float[]{
                xIndex, yValue
        };

        getTransformer(YAxis.AxisDependency.LEFT).pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, this);

    }

    public void setXAxisMarkerView(MarkerView leftMarkerView) {
        this.mXAxisMarkerView = leftMarkerView;
    }

    public void showLastValueMarkerViewWithNoTouch(boolean value) {
        isShowLastValueMarker = value;
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
