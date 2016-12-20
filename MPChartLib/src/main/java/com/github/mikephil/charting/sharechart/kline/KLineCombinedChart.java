package com.github.mikephil.charting.sharechart.kline;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.sharechart.extend.MarketChart;
import com.github.mikephil.charting.sharechart.extend.MarketChartTouchListener;
import com.github.mikephil.charting.sharechart.extend.MarketData;
import com.github.mikephil.charting.sharechart.extend.MarketLineData;
import com.github.mikephil.charting.sharechart.extend.MarketLineDataSet;
import com.github.mikephil.charting.sharechart.extend.MarketXAxis;
import com.github.mikephil.charting.sharechart.market.LoadingMarkerView;
import com.github.mikephil.charting.sharechart.market.OnMarketSelectValueListener;
import com.github.mikephil.charting.sharechart.market.OnScrollDataListener;
import com.github.mikephil.charting.sharechart.market.ValueMarkerView;
import com.github.mikephil.charting.sharechart.pai.PaiDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by ljs on 15/11/16.
 */
public class KLineCombinedChart extends LinearLayout implements OnScrollDataListener, OnChartValueSelectedListener, MarketChartTouchListener.OnLoadingViewListener {
    private MarketChart mKLineChart;
    private MarketXAxis mKLineChartXAxis;
    private MarketChart mAffilateChart;
    private int ma5Color;
    private int ma10Color;
    private int ma20Color;
    private int highlightColor;
    private int decreasingColor;
    private Paint.Style decreasingPaintStyle;
    private int increasingColor;
    private Paint.Style increasingPaintStyle;
    private int pointColor;
    private MarketChartTouchListener.OnLoadingViewListener mOnLoadingviewListener;
    private OnMarketSelectValueListener mOnMarketSelectValueListener;
    private float maxRang;
    private float minRang;
    private float pointSize;
    private YAxis.YAxisLabelPosition mYAxisLablePostion;
    private MaterialProgressDrawable progress;
    private ImageView waitingView;
    private float mYLabelOffset;
    private float leftOffset;
    private float rightOffset;
    private OnRefreshAffilcateChart mOnRefreshAffilcateChart;
    private boolean mIsHiddenYLabels;
    private int mXLabelPosition;
    private int gridLineColor;
    private int yAxixLabelColor;

    public KLineCombinedChart(Context context) {
        super(context);
        init(context);
    }

    public KLineCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KLineCombinedChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.ma5Color = 0xffA6577E;
        this.ma10Color = 0xffFFC000;
        this.ma20Color = 0xff00E3F2;
        this.highlightColor = 0xff12A4FF;
        this.decreasingColor = 0xff3AAE62;
        this.increasingColor = Color.RED;
        this.decreasingPaintStyle = Paint.Style.FILL;
        this.increasingPaintStyle = Paint.Style.STROKE;
        this.pointColor = Color.rgb(255, 102, 0);
        this.pointSize = Utils.convertDpToPixel(4);
        setOrientation(VERTICAL);
        mKLineChart = new MarketChart(context);
        mAffilateChart = new MarketChart(context);

        addView(mKLineChart, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 2));


        addAfficalteChart(context);


        mAffilateChart.setOnSyncChartListener(mKLineChart);
        mKLineChart.setOnSyncChartListener(mAffilateChart);

        initChartConfig();

        setLeftRightOffset(10, 10);
        setDragOffsetX(Utils.convertDpToPixel(50));


    }

    private void addAfficalteChart(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        waitingView = new ImageView(context);
        progress = new MaterialProgressDrawable(context, waitingView);
        progress.setColorSchemeColors(Color.rgb(88, 168, 208));
        progress.setAlpha(255);
        waitingView.setImageDrawable(progress);
        frameLayout.addView(mAffilateChart, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        int size = (int) Utils.convertDpToPixel(30);
        FrameLayout.LayoutParams ivLayout = new FrameLayout.LayoutParams(size, size);
        ivLayout.gravity = Gravity.CENTER;
        frameLayout.addView(waitingView, ivLayout);
        addView(frameLayout, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        closeAffilateWaiting();
    }


    public void showAffilateWaiting() {
        progress.start();
        waitingView.setVisibility(View.VISIBLE);
    }

    public void closeAffilateWaiting() {
        progress.stop();
        waitingView.setVisibility(View.INVISIBLE);
    }


    public void setWaitingColor(int... colors) {
        progress.setColorSchemeColors(colors);
    }


    public void setExtraTopOffset(float offset) {
        mKLineChart.setExtraTopOffset(offset);
    }

    private void initChartConfig() {

//        float offset = Utils.convertDpToPixel(5);
//
//        mKLineChart.setExtraOffsets(offset, offset, offset, 0);
//        mAffilateChart.setExtraOffsets(offset, offset, offset, offset);
        mKLineChart.showLastValueMarkerViewWithNoTouch(true);
        mKLineChart.setDescription("");
        mKLineChart.setNoDataText("");
        mKLineChart.setAutoScaleMinMaxEnabled(true);
        mKLineChart.setDoubleTapToZoomEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mKLineChart.setPinchZoom(false);
        mKLineChart.setScaleYEnabled(false);
        mKLineChart.setScaleXEnabled(true);
        mKLineChart.setDrawGridBackground(false);
        mKLineChart.setDrawBorders(true);
        mKLineChart.setMinOffset(0);
//        mKLineChart.setBorderColor(gridLineColor);

        mKLineChartXAxis = mKLineChart.getXAxis();


        mKLineChartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mKLineChartXAxis.setDrawGridLines(true);

        YAxis leftAxis = mKLineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
//        leftAxis.setStartAtZero(false);

        YAxis rightAxis = mKLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        mKLineChart.getLegend().setEnabled(false);
        mKLineChart.setDragDecelerationEnabled(true);

        mKLineChart.setOnLoadingViewListener(this);
        mKLineChart.setOnScrollDataListener(this);
        mKLineChart.setOnChartValueSelectedListener(this);

        setXAxisLabelPosition(MarketXAxis.XAXISPOSITION_BOTTOM);

    }

    public void setTouchEnable(boolean enable) {
        mKLineChart.setTouchEnabled(enable);
        mAffilateChart.setTouchEnabled(enable);
    }

    public void setDragOffsetX(float offsetX) {
        mKLineChart.setDragOffsetX(offsetX);
        mAffilateChart.setDragOffsetX(offsetX);

    }

    public void setLeftRightOffset(float left, float right) {

        this.leftOffset = left;
        this.rightOffset = right;
    }

    public void setXAxisValueFormat(XAxisValueFormatter format) {
        mKLineChartXAxis.setValueFormatter(format);
        mAffilateChart.getXAxis().setValueFormatter(format);
    }

    public void setLoadingMarkerView(LoadingMarkerView loadingMarkerView) {
        mKLineChart.setLoadingMarkerView(loadingMarkerView);
    }

    public void setHighLowValueMarkerView(MarkerView highValueMarkerView, MarkerView lowValueMarkerView) {
        mKLineChart.setHighValueMarkerView(highValueMarkerView);
        mKLineChart.setLowValueMarkerView(lowValueMarkerView);

    }

    public void setDrawBorder(int borderColor, float borderWidth) {


        mKLineChart.setDrawBorders(true);
        mKLineChart.setBorderWidth(borderWidth);
        mKLineChart.setBorderColor(borderColor);

        mAffilateChart.setDrawBorders(true);
        mAffilateChart.setBorderWidth(borderWidth);
        mAffilateChart.setBorderColor(borderColor);

    }

    public void setAffilateChartData(MarketData marketData) {

        if (marketData == null) {
            mAffilateChart.setVisibility(View.GONE);
            return;
        }

//        marketData.setShowLabels(mAffilateChart.getXAxis().getShowLabels());

        if (mAffilateChart.getData() == marketData) {
            return;
        }
        setFirstDataSetHighlight(marketData);
        mAffilateChart.setVisibility(View.VISIBLE);
        mAffilateChart.setData(marketData);

        if (this.minRang != 0 && this.maxRang != 0) {
            mAffilateChart.setVisibleXRange(this.minRang, this.maxRang);
        } else if (this.minRang != 0) {
            mAffilateChart.setVisibleXRangeMinimum(minRang);
        }
        mAffilateChart.invalidate();

    }

    private void setFirstDataSetHighlight(MarketData marketData) {

        int count = marketData.getDataSetCount();

        for (int i = 0; i < count; i++) {

            final BarLineScatterCandleBubbleDataSet<?> dataSetByIndex = (BarLineScatterCandleBubbleDataSet<?>) marketData.getDataSetByIndex(i);
            dataSetByIndex.setHighlightEnabled(i == 0);
            if (i == 0) {
                dataSetByIndex.setHighLightColor(this.highlightColor);
            }
            dataSetByIndex.setDrawValues(false);

        }


    }

    public void restoreAffilateChart() {
        mAffilateChart.restoreSetting();
        initAffilateChartConfig();
    }


    private void initAffilateChartConfig() {
        mAffilateChart.showLastValueMarkerViewWithNoTouch(true);
        mAffilateChart.setDescription("");
        mAffilateChart.setNoDataText("");
        mAffilateChart.getAxisLeft().setXOffset(mYLabelOffset);
        mAffilateChart.setScaleYEnabled(false);
        mAffilateChart.setScaleXEnabled(true);
        mAffilateChart.setAutoScaleMinMaxEnabled(true);

        mAffilateChart.setDrawGridBackground(false);
        MarketXAxis mBarChartXAxis = mAffilateChart.getXAxis();
        mBarChartXAxis.setDrawGridLines(true);
        mBarChartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mAffilateChart.getLegend().setEnabled(false);
        mAffilateChart.getAxisRight().setEnabled(false);
        mAffilateChart.getAxisLeft().setDrawGridLines(true);
        mAffilateChart.getAxisLeft().setGridColor(this.gridLineColor);
        mAffilateChart.getAxisLeft().setAxisLineColor(this.gridLineColor);
        mAffilateChart.getAxisLeft().setTextColor(this.yAxixLabelColor);
        mAffilateChart.getXAxis().setGridColor(this.gridLineColor);
        mAffilateChart.getXAxis().setAxisLineColor(this.gridLineColor);
//        mAffilateChart.getAxisLeft().setLabelCount(6);
//        mAffilateChart.getAxisLeft().setStartAtZero(false);
        mAffilateChart.getAxisLeft().setDrawLabels(mIsHiddenYLabels);
//        mAffilateChart.getAxisRight().setDrawGridLines(false);
        mAffilateChart.setDoubleTapToZoomEnabled(false);
        mAffilateChart.setPinchZoom(false);
        mAffilateChart.setMinOffset(0);


        mAffilateChart.setOnChartValueSelectedListener(this);
        mAffilateChart.setOnLoadingViewListener(this);

        if (mYAxisLablePostion != null) {
            mAffilateChart.getAxisLeft().setPosition(mYAxisLablePostion);
        }


    }

    public void syncAffilateChart() {
        setOffset();
        Matrix matrix = mKLineChart.getViewPortHandler().getMatrixTouch();
        float[] val = new float[9];
        matrix.getValues(val);

        mAffilateChart.syncMatrix(val[Matrix.MTRANS_X], val[Matrix.MSCALE_X], false);
        mKLineChart.invalidate();


    }


    @Override
    public void onScrollDataSelect(Entry e, Highlight h) {

        refreshAffilate();


//        if (mOnMarketSelectValueListener != null) {
//            mOnMarketSelectValueListener.onValueSelected(h.getXIndex(), e, null);
//        }

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        mKLineChart.setValueMarkerViewPosition(h.getXIndex());
        mAffilateChart.setValueMarkerViewPosition(h.getXIndex());


        Entry entry = mKLineChart.highlightTouchWithoutEvent(new Highlight(h.getXIndex(), 0));
        Entry affilateEntry =
                mAffilateChart.highlightTouchWithoutEvent(h);
        if (mOnMarketSelectValueListener != null) {
            mOnMarketSelectValueListener.onValueSelected(h.getXIndex(), entry, affilateEntry);
        }


    }

    @Override
    public void onNothingSelected() {
        mKLineChart.highlightTouchWithoutEvent(null);
        mAffilateChart.highlightTouchWithoutEvent(null);

        if (mOnMarketSelectValueListener != null) {
            mOnMarketSelectValueListener.onValueNothing();
        }

    }

    @Override
    public void onLoadingViewStateOpen(Chart chart) {

        if (mOnLoadingviewListener != null) {
            mOnLoadingviewListener.onLoadingViewStateOpen(mKLineChart);
        }

        if (chart == mKLineChart) {
            mKLineChart.setLoadingViewOpen(true);
        } else {
            mKLineChart.setLoadingViewOpen(true);
        }
    }

    @Override
    public void onLoadingViewStateClose(Chart chart) {
        if (mOnLoadingviewListener != null) {
            mOnLoadingviewListener.onLoadingViewStateClose(mKLineChart);
        }
        if (chart == mKLineChart) {
            mAffilateChart.setLoadingViewOpen(false);
        } else {
            mKLineChart.setLoadingViewOpen(false);
        }
    }


    public void closeLoadingView() {
        mKLineChart.closeLoadingView();
        mAffilateChart.closeLoadingView();
    }

    public void setOnMarketSelectValueListener(OnMarketSelectValueListener l) {
        this.mOnMarketSelectValueListener = l;
    }

    public void setData(ArrayList<KLineData> yValues) {

        if (yValues == null) {
            return;
        }

        int count = yValues.size();

        if (count == 0) {
            return;
        }

        List<String> xVals = Arrays.asList(new String[count]);
        ArrayList<CandleEntry> yVals = new ArrayList<>();
        ArrayList<Entry> ma5List = new ArrayList<>();
        ArrayList<Entry> ma10List = new ArrayList<>();
        ArrayList<Entry> ma20List = new ArrayList<>();
        ArrayList<Entry> paiList = new ArrayList<>();


        for (int i = 0; i < count; i++) {

            KLineData d = yValues.get(i);
            float high = d.high;
            float low = d.low;

            float open = d.open;
            float close = d.close;

            if (i - 1 != -1) {
                d.preKLineData = yValues.get(i - 1);
            }


            yVals.add(new CandleEntry(i, high, low, open, close, d));

            ma5List.add(new Entry(d.ma5, i));

            ma10List.add(new Entry(d.ma10, i));

            ma20List.add(new Entry(d.ma20, i));

            if (d.paiyuan != null) {

                paiList.add(new Entry(Float.NaN, i));
            }
        }


        CandleDataSet set1 = new CandleDataSet(yVals, "Kline");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColorSameAsCandle(true);
        set1.setHighLightColor(highlightColor);
        set1.setDrawVerticalHighlightIndicator(true);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setShadowWidth(0.5f);
        set1.setDecreasingColor(this.decreasingColor);
        set1.setDecreasingPaintStyle(this.decreasingPaintStyle);
        set1.setIncreasingColor(this.increasingColor);
        set1.setIncreasingPaintStyle(this.increasingPaintStyle);
        set1.setNeutralColor(this.decreasingColor);
        set1.setDrawValues(false);

        PaiDataSet scatterDataSet = new PaiDataSet(paiList, "");
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setColor(this.pointColor);
        scatterDataSet.setDrawValues(false);
        scatterDataSet.setScatterShapeSize(this.pointSize);


        CandleData data = new CandleData(xVals, set1);


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();


        MarketLineDataSet ma5 = new MarketLineDataSet(ma5List, "MA5");
        MarketLineDataSet ma10 = new MarketLineDataSet(ma10List, "MA10");
        MarketLineDataSet ma20 = new MarketLineDataSet(ma20List, "MA20");

        setLineDateSetStyle(ma5);
        setLineDateSetStyle(ma10);
        setLineDateSetStyle(ma20);

        ma5.setColor(this.ma5Color);
        ma10.setColor(this.ma10Color);
        ma20.setColor(this.ma20Color);

        lineDataSets.add(ma5);
        lineDataSets.add(ma10);
        lineDataSets.add(ma20);


        MarketLineData lineDate = new MarketLineData(xVals, lineDataSets);
        lineDate.setHighlightEnabled(false);
        ScatterData scatterData = new ScatterData(xVals, scatterDataSet);
        scatterData.setHighlightEnabled(false);

        MarketData combinedData = new MarketData(xVals, mKLineChartXAxis.getShowLabels());
        combinedData.setData(data);
        combinedData.setData(lineDate);
        combinedData.setData(scatterData);


        mKLineChart.setData(combinedData);

        mKLineChart.notifyDataSetChanged();
        mKLineChart.restoreScale();
        mKLineChart.invalidate();
    }


//    private KLineData convertToKLineData(ReadableMap map) {
//
//        KLineData data = new KLineData();
//
//        data.close = (float) map.getDouble("close");
//        data.high = (float) map.getDouble("high");
//        data.open = (float) map.getDouble("open");
//        data.low = (float) map.getDouble("low");
//        if (map.hasKey("ma5")) {
//            data.ma5 = (float) map.getDouble("high");
//        }
//
//        if (map.hasKey("ma10")) {
//            data.ma10 = (float) map.getDouble("ma10");
//        }
//        if (map.hasKey("ma20")) {
//            data.ma20 = (float) map.getDouble("ma20");
//        }
//
//        data.date = map.getString("date");
//
//
//        if (map.hasKey("paiyuan")) {
//
//            ReadableMap pai = map.getMap("paiyuan");
//
//            data.paiyuan = new KLineData.PaiYuan();
//
//
//            data.paiyuan.cqr = pai.getString("cqr");
//            data.paiyuan.FHcontent = pai.getString("FHcontent");
//
//
//        }
//
//
//        return data;
//
//    }

    private void setLineDateSetStyle(MarketLineDataSet dataSet) {
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(1f);
        dataSet.setHighlightEnabled(false);
    }

    public void setShowXLabels(SparseArray<String> labels) {

        SparseArray<String> l = labels.clone();
        mKLineChartXAxis.setShowLabels(l);
        mAffilateChart.getXAxis().setShowLabels(l);
    }

    public void setMAColor(int ma5Color, int ma10Color, int ma20Color) {
        this.ma5Color = ma5Color;
        this.ma10Color = ma10Color;
        this.ma20Color = ma20Color;
    }

    public void setHighlightColor(int color) {
        this.highlightColor = color;
    }

    //
    public void setDecreasingColor(int color) {
        this.decreasingColor = color;
    }

    public void setDecreasingPaintStyle(Paint.Style style) {
        this.decreasingPaintStyle = style;
    }

    public void setIncreasingColor(int color) {
        this.increasingColor = color;
    }

    public void setIncreasingPaintStyle(Paint.Style style) {
        this.increasingPaintStyle = style;
    }

    public void setPointColor(int color) {
        this.pointColor = color;
    }

    public void setPointSize(float size) {
        this.pointSize = size;
    }

    public void setOnLoadingViewListener(MarketChartTouchListener.OnLoadingViewListener l) {

        this.mOnLoadingviewListener = l;
    }

    public void setYAxixPosition(YAxis.YAxisLabelPosition pos) {
        mYAxisLablePostion = pos;
        mKLineChart.getAxisLeft().setPosition(pos);
    }


    public void moveToLast(float max) {
        int prog = mKLineChart.getData().getXValCount();
        mKLineChart.zoom(max, 1, 0, 0);
        mAffilateChart.zoom(max, 1, 0, 0);
        mAffilateChart.moveViewToX(prog - 1);
        mKLineChart.moveViewToX(prog - 1);
        mKLineChart.notifyDataSetChanged();
    }

    public MarketChart getAffilateChart() {
        return this.mAffilateChart;
    }

    public void setVisibleXRange(float minRang, float maxRang) {
        this.minRang = minRang;
        this.maxRang = maxRang;
        mKLineChart.setVisibleXRange(minRang, maxRang);

    }

    public void setVisibleXRangeMin(float minRang) {
        this.minRang = minRang;
        mKLineChart.setVisibleXRangeMinimum(minRang);

    }


    @Override
    public void invalidate() {
        super.invalidate();
        mKLineChart.invalidate();
        mAffilateChart.invalidate();
    }

    public void clearKLineData() {
        mKLineChart.clear();
    }

    public void clearAffilateData() {
        mAffilateChart.clear();
    }


    public void setValueMarkerView(ValueMarkerView markerview) {
        mKLineChart.setValueMarkerView(markerview);
    }

    public void restoreScale() {
        mKLineChart.restoreScale();
        mAffilateChart.restoreScale();
    }

    public void setYValueFormatter(YAxisValueFormatter f) {
        mKLineChart.getAxisLeft().setValueFormatter(f);
    }

    public void setXAxisLabelPosition(int position) {

        float offset = 5;
        mXLabelPosition = position;
        if (position == MarketXAxis.XAXISPOSITION_MIDDLE) {
            mKLineChartXAxis.setDrawLabels(true);
            mAffilateChart.getXAxis().setDrawLabels(false);
        } else if (position == MarketXAxis.XAXISPOSITION_BOTTOM) {
            mKLineChartXAxis.setDrawLabels(false);
            mAffilateChart.getXAxis().setDrawLabels(true);
        }

        mKLineChart.setExtraOffsets(offset, offset, offset, 0);
        mAffilateChart.setExtraOffsets(offset, offset, offset, offset);


    }

    public void setYAxisLabelCount(int count) {
        mKLineChart.getAxisLeft().setLabelCount(count, true);
    }

    public void setShowOnlyMinMax(boolean enabled) {
        mKLineChart.getAxisLeft().setShowOnlyMinMax(enabled);
    }

    public void setOnChartGestureListener(OnChartGestureListener l) {
        mKLineChart.setOnChartGestureListener(l);
    }

    public void setYAxisOffset(float offset) {
        this.mYLabelOffset = offset;
        mKLineChart.getAxisLeft().setXOffset(mYLabelOffset);
        mAffilateChart.getAxisLeft().setXOffset(mYLabelOffset);
    }

    private void setOffset() {

        float offsetLeft = 0;
        float offsetRight = 0;


        float lMax1 = this.leftOffset;
        if (mKLineChart.getAxisLeft().needsOffset()) {
            lMax1 = mKLineChart.getAxisLeft().getRequiredWidthSpace(mKLineChart.getRendererLeftYAxis().getPaintAxisLabels());
        }

        float lMax2 = 0;
        if (mAffilateChart.getAxisLeft().needsOffset()) {
            lMax2 = mAffilateChart.getAxisLeft().getRequiredWidthSpace(mAffilateChart.getRendererLeftYAxis().getPaintAxisLabels());
        }

        float rMax = this.rightOffset;
        if (mKLineChart.getAxisRight().needsOffset()) {
            rMax = mKLineChart.getAxisRight().getRequiredWidthSpace(mKLineChart.getRendererRightYAxis().getPaintAxisLabels());
        }

        if (lMax1 < lMax2) {
            lMax1 = lMax2;
        }


        offsetLeft = Utils.convertPixelsToDp(lMax1);
        offsetRight = Utils.convertPixelsToDp(rMax);

        mAffilateChart.setExtraLeftOffset(offsetLeft);
        mAffilateChart.setExtraRightOffset(offsetRight);
        mKLineChart.setExtraLeftOffset(offsetLeft);
        mKLineChart.setExtraRightOffset(offsetRight);


    }

    public void setNoDataText(String txt) {
        mKLineChart.setNoDataText(txt);
    }

    public void setAffilateChartNoDataText(String txt) {
        mAffilateChart.setNoDataText(txt);
        mAffilateChart.invalidate();
    }

    public void refreshAffilate() {

        if (mAffilateChart != null && mAffilateChart.getData() != null) {
            float max = mAffilateChart.getYMax();

            if (mOnRefreshAffilcateChart != null) {
                mOnRefreshAffilcateChart.onChangeLeftAxis(max);
            }
        }
    }

    public void setOnRefreshAffilcateChart(OnRefreshAffilcateChart l) {
        this.mOnRefreshAffilcateChart = l;
    }

    public void setXMarkerView(MarkerView markerView) {
        mAffilateChart.setXAxisMarkerView(markerView);
    }

    public void setDrawYLabels(boolean enable) {
        mKLineChart.getAxisLeft().setDrawLabels(enable);
        mAffilateChart.getAxisLeft().setDrawLabels(enable);
        mIsHiddenYLabels = enable;

    }

    public void setDrawXLabels(boolean enable) {


        if (mXLabelPosition == MarketXAxis.XAXISPOSITION_MIDDLE) {
            mKLineChart.getXAxis().setDrawLabels(enable);
        } else if (mXLabelPosition == MarketXAxis.XAXISPOSITION_BOTTOM) {
            mAffilateChart.getXAxis().setDrawLabels(enable);
        }


    }

    public void setGridLineColor(int color) {
        mKLineChart.getAxisLeft().setGridColor(color);
        mKLineChart.getXAxis().setGridColor(color);
        mKLineChart.getAxisLeft().setAxisLineColor(color);
        mKLineChart.getXAxis().setAxisLineColor(color);
        this.gridLineColor = color;
    }

    public void setXAxisLabelColor(int color) {

        if (mXLabelPosition == MarketXAxis.XAXISPOSITION_MIDDLE) {
            mKLineChart.getXAxis().setTextColor(color);
        } else if (mXLabelPosition == MarketXAxis.XAXISPOSITION_BOTTOM) {
            mAffilateChart.getXAxis().setTextColor(color);
        }


    }

    public void setYAxixLabelColor(int color) {
        mKLineChart.getAxisLeft().setTextColor(color);
        this.yAxixLabelColor = color;
    }

    public boolean isStopParentTouch() {
        return mKLineChart.isStopParentTouch() || mAffilateChart.isStopParentTouch();
    }

    public void setStopParentTouch(boolean enable) {
        mKLineChart.setStopParentTouch(enable);
        mAffilateChart.setStopParentTouch(enable);
    }

    public void setScrollClass(String... name) {
        mKLineChart.setScrollClass(name);
        mAffilateChart.setScrollClass(name);
    }

    public interface OnRefreshAffilcateChart {
        void onChangeLeftAxis(float max);
    }
}
