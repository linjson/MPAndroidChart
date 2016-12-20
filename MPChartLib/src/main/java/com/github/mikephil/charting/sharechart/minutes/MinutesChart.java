package com.github.mikephil.charting.sharechart.minutes;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.sharechart.extend.MarketLineData;
import com.github.mikephil.charting.sharechart.extend.MarketLineDataSet;
import com.github.mikephil.charting.sharechart.extend.MarketXAxis;
import com.github.mikephil.charting.sharechart.extend.MarketYAxis;
import com.github.mikephil.charting.sharechart.market.OnMarketSelectValueListener;
import com.github.mikephil.charting.sharechart.market.ShinePointView;

import java.util.ArrayList;

/**
 * Created by ljs on 15/11/5.
 */
public class MinutesChart extends LinearLayout implements OnChartValueSelectedListener {

    private MinutesLineChart mLineChart;
    private MinutesBarChart mBarChart;
    private MarketYAxis mlineChartAxisLeft;
    private MarketYAxis mlineChartAxisRight;
    private MarketYAxis mbarChartAxisLeft;
    private int mHighLightColor;
    private MarketXAxis mBarChartXAxis;
    private OnMarketSelectValueListener mOnMarketSelectValueListener;
    private MarketXAxis mlineChartXAxis;
    private float leftOffset;
    private float rightOffset;
    private boolean mDrawLinePathFillEnabled;
    private int mXLabelPosition;
    private int minuteAverageLineColor = 0xFFF9AE57;
    private int minuteLineColor = 0xFF3483B5;
    private int minuteLinePathFillColor = 0x6571A3C8;
    private boolean barChartIsHidden;
    private int minuteLinePathFillColorAlpha;
    private float mLineWidth;

    public MinutesChart(Context context) {
        super(context);
        init(context);
    }

    public MinutesChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MinutesChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        mLineChart = new MinutesLineChart(context);
        mBarChart = new MinutesBarChart(context);


        addView(mLineChart, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 2));
        addView(mBarChart, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));

        mHighLightColor = Color.argb(255, 244, 117, 117);

        initChartSetting();

    }

    private void initChartSetting() {
//      lineChart设置
        mLineChart.setNoDataText("");
        mBarChart.setNoDataText("");
//        mLineChart.setHighlightEnabled(true);
        mLineChart.setTouchEnabled(true);

        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setHighlightPerDragEnabled(true);
        mLineChart.setMinOffset(0);


        Legend l = mLineChart.getLegend();
        l.setEnabled(false);

        mlineChartAxisRight = mLineChart.getAxisRight();
        mlineChartAxisLeft = mLineChart.getAxisLeft();
        mlineChartXAxis = mLineChart.getXAxis();

//        mlineChartAxisLeft.setStartAtZero(false);
//
//        mlineChartAxisRight.setStartAtZero(false);
        mlineChartAxisRight.setDrawGridLines(false);
        mlineChartAxisRight.setDrawAxisLine(false);

        mlineChartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mLineChart.setOnChartValueSelectedListener(this);


//        barChart设置
        mBarChart.setMinOffset(0);
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawGridBackground(false);
        mBarChartXAxis = mBarChart.getXAxis();
        mBarChartXAxis.setDrawGridLines(true);
        mBarChartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        mBarChart.getLegend().setEnabled(false);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getAxisLeft().setDrawGridLines(false);

        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setPinchZoom(false);

        mBarChart.setTouchEnabled(true);
        mbarChartAxisLeft = mBarChart.getAxisLeft();
        mBarChart.setOnChartValueSelectedListener(this);

        setXLabelPosition(MarketXAxis.XAXISPOSITION_BOTTOM);
        setStopParentTouch(false);

    }

    public void setDrawYLabels(boolean enable) {
        mlineChartAxisLeft.setDrawLabels(enable);
        mlineChartAxisRight.setDrawLabels(enable);
        mbarChartAxisLeft.setDrawLabels(enable);
    }

    public void setDrawXLabels(boolean enable) {

        if (mXLabelPosition == MarketXAxis.XAXISPOSITION_MIDDLE) {
            mlineChartXAxis.setDrawLabels(enable);
        } else if (mXLabelPosition == MarketXAxis.XAXISPOSITION_BOTTOM) {
            mBarChartXAxis.setDrawLabels(enable);
        }

    }

    public void setTouchEnable(boolean enable) {
        mLineChart.setTouchEnabled(enable);
        mBarChart.setTouchEnabled(enable);

    }

    public void setBarMinText(String txt) {
        mBarChart.getAxisLeft().setShowOnlyMax(txt);
    }


    /**
     * @param position:MarketXAxis.XAXISPOSITION_BOTTOM,XAXISPOSITION_MIDDLE
     */
    public void setXLabelPosition(int position) {
        float offset = 5;
        this.mXLabelPosition = position;
        if (position == MarketXAxis.XAXISPOSITION_MIDDLE || barChartIsHidden) {
            mlineChartXAxis.setDrawLabels(true);
            mBarChartXAxis.setDrawLabels(false);

        } else if (position == MarketXAxis.XAXISPOSITION_BOTTOM) {
            mlineChartXAxis.setDrawLabels(false);
            mBarChartXAxis.setDrawLabels(true);
        }

        mLineChart.setExtraOffsets(offset, offset, offset, 0);
        mBarChart.setExtraOffsets(offset, 0, offset, offset);
    }

    public void setExtraOffsets(float left, float top, float right, float bottom) {
        mLineChart.setExtraOffsets(left, top, right, 0);
        mBarChart.setExtraOffsets(left, 0, right, bottom);
    }

    public void setDrawBorder(int color, float width) {
        mLineChart.setDrawBorders(true);
        mBarChart.setDrawBorders(true);

        mLineChart.setBorderColor(color);
        mBarChart.setBorderColor(color);

        mLineChart.setBorderWidth(width);
        mBarChart.setBorderWidth(width);
    }

    public void setYAxixPosition(YAxis.YAxisLabelPosition pos) {
        mlineChartAxisLeft.setPosition(pos);
        mlineChartAxisRight.setPosition(pos);
        mbarChartAxisLeft.setPosition(pos);
    }

    public void setAxisLeftValueFormatter(YAxisValueFormatter format) {
        mlineChartAxisLeft.setValueFormatter(format);
    }

    public void setAxisRightValueFormatter(YAxisValueFormatter format) {
        mlineChartAxisRight.setValueFormatter(format);
    }

    public void setBarValueFormatter(YAxisValueFormatter format) {

        mbarChartAxisLeft.setValueFormatter(format);
    }

    public void showDrawYLabels(boolean enable) {
        showLineDrawYLabels(enable);
        showBarDrawYLabels(enable);
    }


    public void showLineDrawYLabels(boolean enable) {
        mlineChartAxisRight.setDrawLabels(enable);
        mlineChartAxisLeft.setDrawLabels(enable);
    }

    public void showBarDrawYLabels(boolean enable) {
        mbarChartAxisLeft.setDrawLabels(enable);
    }

    public void showLineMaxMinLabels(boolean show) {
        mlineChartAxisLeft.setShowBaseValueAndMaxmin(show);
        mlineChartAxisRight.setShowBaseValueAndMaxmin(show);
    }


    public void setShowLabels(SparseArray<String> labels) {
        mlineChartXAxis.setShowLabels(labels);
        mBarChartXAxis.setShowLabels(labels);
    }

    public void setBaseLine(float value, int color) {
        mlineChartAxisLeft.setBaseValue(value);
        mlineChartAxisRight.setBaseValue(0);
        mlineChartAxisLeft.getLimitLines().clear();
        LimitLine ll = new LimitLine(value);
        ll.setLineWidth(1f);
        ll.setLineColor(color);
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setLineWidth(1);

        mlineChartAxisLeft.addLimitLine(ll);
    }

    public void setBaseLine(float value) {
        setBaseLine(value, Color.RED);
    }


    public void setMarkerView(MarkerView leftView, MarkerView xaxisView) {
        mLineChart.setLeftMarkerView(leftView);
        if (barChartIsHidden) {
            mLineChart.setXAxisMarkerView(xaxisView);
        } else {
            mBarChart.setXAxisMarkerView(xaxisView);
        }
    }

    public void setLeftMarkerView(MarkerView leftView) {
        mLineChart.setLeftMarkerView(leftView);
    }


    public void setRightMarkerView(MarkerView rightView) {
        mLineChart.setRightMarkerView(rightView);
    }


    public void setLeftRightOffset(float left, float right) {
//        mBarChart.setExtraLeftOffset(left);
//        mBarChart.setExtraRightOffset(right);
//        mLineChart.setExtraLeftOffset(left);
//        mLineChart.setExtraRightOffset(right);

        this.leftOffset = left;
        this.rightOffset = right;


    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//        mBarChart.highlightValue(h.getXIndex(), dataSetIndex);

        Entry entry = mLineChart.highlightTouchWithoutEvent(new Highlight(h.getXIndex(), 1));

        Entry barEntry =
                mBarChart.highlightTouchWithoutEvent(new Highlight(h.getXIndex(), 0));
        if (mOnMarketSelectValueListener != null) {
            mOnMarketSelectValueListener.onValueSelected(h.getXIndex(), entry, barEntry);
        }

    }

    @Override
    public void onNothingSelected() {
        mLineChart.highlightTouchWithoutEvent(null);
        mBarChart.highlightTouchWithoutEvent(null);

        if (mOnMarketSelectValueListener != null) {

            mOnMarketSelectValueListener.onValueNothing();
        }
    }

    public void setOnMarketSelectValueListener(OnMarketSelectValueListener l) {
        this.mOnMarketSelectValueListener = l;
    }

    public void setMaxMin(float lmax, float lmin, float rmax, float rmin, float bmax) {
        mlineChartAxisLeft.setAxisMaxValue(lmax);
        mlineChartAxisLeft.setAxisMinValue(lmin);
//
        mlineChartAxisRight.setAxisMaxValue(rmax);
        mlineChartAxisRight.setAxisMinValue(rmin);

        mbarChartAxisLeft.setAxisMaxValue(bmax);
        mbarChartAxisLeft.setAxisMinValue(0);


    }

    /**
     * 以baseline为准,上下各显示多少Y轴坐标
     *
     * @param lableCount
     */
    public void setLabelCount(int lableCount) {
        mlineChartAxisLeft.setLabelCount(lableCount, true);
        mlineChartAxisRight.setLabelCount(lableCount, true);
    }


    private void setOffset() {

        float offsetLeft = 0;
        float offsetRight = 0;


        float lMax1 = this.leftOffset;
        if (mlineChartAxisLeft.needsOffset()) {
            lMax1 = mlineChartAxisLeft.getRequiredWidthSpace(mLineChart.getRendererLeftYAxis().getPaintAxisLabels());
        }

        float lMax2 = 0;
        if (mbarChartAxisLeft.needsOffset()) {
            lMax2 = mbarChartAxisLeft.getRequiredWidthSpace(mBarChart.getRendererLeftYAxis().getPaintAxisLabels());
        }

        float rMax = this.rightOffset;
        if (mlineChartAxisRight.needsOffset()) {
            rMax = mlineChartAxisRight.getRequiredWidthSpace(mLineChart.getRendererRightYAxis().getPaintAxisLabels());
        }

        if (lMax1 < lMax2) {
            lMax1 = lMax2;
        }


        offsetLeft = lMax1;
        offsetRight = rMax;


        mBarChart.setExtraLeftOffset(offsetLeft);
        mBarChart.setExtraRightOffset(offsetRight);
        mLineChart.setExtraLeftOffset(offsetLeft);
        mLineChart.setExtraRightOffset(offsetRight);


    }

    public void setData(String[] xValue, ArrayList<MinutesData> yValue) {

        String[] xValueTemp = xValue;
        ArrayList<Entry> junjiaList = new ArrayList<>();
        ArrayList<Entry> chengjiaojiaList = new ArrayList<>();
        ArrayList<BarEntry> changjiaoliangList = new ArrayList<>();


        int valueCount = yValue.size();

        SparseArray<String> showLabels = mlineChartXAxis.getShowLabels();

        for (int i = 0, x = 0; x < valueCount; i++, x++) {

            MinutesData t = yValue.get(x);

            if (t == null) {

                chengjiaojiaList.add(new Entry(Float.NaN, i, t));
                junjiaList.add(new Entry(Float.NaN, i, t));
                changjiaoliangList.add(new BarEntry(Float.NaN, i, t));
                continue;
            }

            if (!TextUtils.isEmpty(showLabels.get(i)) &&
                    showLabels.get(i).contains("/")) {
                i++;
            }

            chengjiaojiaList.add(new Entry(t.chengjiaojia, i, t));
            junjiaList.add(new Entry(t.junjia, i, t));
            changjiaoliangList.add(new BarEntry(t.chengjiaoliang, i, t));
        }

        // create a dataset and give it a type
        MarketLineDataSet set1 = new MarketLineDataSet(chengjiaojiaList, "成交价");
        setLineDateSetStyle(set1);
        set1.setColor(this.minuteLineColor);
        set1.setDrawFilled(mDrawLinePathFillEnabled);
        set1.setFillAlpha(this.minuteLinePathFillColorAlpha);
        set1.setFillColor(this.minuteLinePathFillColor);


        // create a dataset and give it a type
        MarketLineDataSet set2 = new MarketLineDataSet(junjiaList, "均价");
        setLineDateSetStyle(set2);
        set2.setColor(this.minuteAverageLineColor);
//        set2.setHighlightEnabled(false);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set2);
        dataSets.add(set1);


        mLineChart.setData(new MarketLineData(xValueTemp, dataSets));

        BarDataSet barDataSet = new BarDataSet(changjiaoliangList, "成交量");
        barDataSet.setBarSpacePercent(0);
        barDataSet.setHighLightColor(mHighLightColor);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setDrawValues(false);

        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);

        // data.setValueFormatter(new MyValueFormatter());
        mBarChart.setData(new BarData(xValueTemp, barDataSets));

//        setOffset();

    }

//    private MinutesData convertToMinutesData(ReadableMap map) {
//
//        MinutesData minutesData = new MinutesData();
//
//
//        minutesData.color = map.getInt("color");
//        minutesData.chengjiaojia = (float) map.getDouble("chengjiaojia");
//        minutesData.chengjiaoliang = (float) map.getDouble("chengjiaoliang");
//        minutesData.percentage = (float) map.getDouble("percentage");
//        minutesData.time = map.getString("date");
//        minutesData.junjia = (float) map.getDouble("junjia");
//
//        return minutesData;
//    }
//
//    private void setData(String[] xValue, ReadableArray yValue) {
//
//        String[] xValueTemp = xValue;
//        ArrayList<Entry> junjiaList = new ArrayList<>();
//        ArrayList<Entry> chengjiaojiaList = new ArrayList<>();
//        ArrayList<BarEntry> changjiaoliangList = new ArrayList<>();
//
//
//        int valueCount = yValue.size();
//        SparseArray<String> showLabels = mlineChartXAxis.getShowLabels();
//        for (int i = 0, x = 0; x < valueCount; i++, x++) {
//
//            MinutesData t = convertToMinutesData(yValue.getMap(x));
//
//            if (t == null) {
//
//                chengjiaojiaList.add(new Entry(Float.NaN, i, t));
//                junjiaList.add(new Entry(Float.NaN, i));
//                changjiaoliangList.add(new BarEntry(Float.NaN, i, t));
//                continue;
//            }
//
//            if (!TextUtils.isEmpty(showLabels.get(i)) &&
//                    showLabels.get(i).contains("/")) {
//                i++;
//            }
//
//            chengjiaojiaList.add(new Entry(t.chengjiaojia, i, t));
//            junjiaList.add(new Entry(t.junjia, i));
//            changjiaoliangList.add(new BarEntry(t.chengjiaoliang, i, t));
//        }
//        // create a dataset and give it a type
//        MarketLineDataSet set1 = new MarketLineDataSet(chengjiaojiaList, "成交价");
//        setLineDateSetStyle(set1);
//        set1.setColor(this.minuteLineColor);
//        set1.setDrawFilled(mDrawLinePathFillEnabled);
//        set1.setFillAlpha(65);
//        set1.setFillColor(0x71A3C8);
//
//        // create a dataset and give it a type
//        MarketLineDataSet set2 = new MarketLineDataSet(junjiaList, "均价");
//        setLineDateSetStyle(set2);
//        set2.setColor(minuteAverageLineColor);
//        set2.setHighlightEnabled(false);
//
//        ArrayList<MarketLineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//        dataSets.add(set2);
//
//
//        mLineChart.setData(new LineData(xValueTemp, dataSets));
//
//        BarDataSet barDataSet = new BarDataSet(changjiaoliangList, "成交量");
//        barDataSet.setBarSpacePercent(0);
//        barDataSet.setHighLightColor(mHighLightColor);
//        barDataSet.setHighLightAlpha(255);
//        barDataSet.setDrawValues(false);
//
//        ArrayList<BarDataSet> barDataSets = new ArrayList<>();
//        barDataSets.add(barDataSet);
//
//        // data.setValueFormatter(new MyValueFormatter());
//        mBarChart.setData(new BarData(xValueTemp, barDataSets));
//        setOffset();
//
//    }

    private void setLineDateSetStyle(MarketLineDataSet dataSet) {
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(mLineWidth);
        dataSet.setHighLightColor(mHighLightColor);
    }

    public void setHighLightColor(int color) {
        this.mHighLightColor = color;
    }


    public void setXAxisTextPosition(int position) {
        mlineChartXAxis.setTextPosition(position);
        mBarChartXAxis.setTextPosition(position);

    }

    public void setXAxisValueFormatter(XAxisValueFormatter formatter) {
        mlineChartXAxis.setValueFormatter(formatter);
        mBarChartXAxis.setValueFormatter(formatter);
    }

    public void showXAxisLabels(boolean enable) {
        mlineChartXAxis.setDrawLabels(enable);
        mBarChartXAxis.setDrawLabels(enable);
    }

    public void setYAxisOffset(float offset) {
        mlineChartAxisLeft.setXOffset(offset);
        mlineChartAxisRight.setXOffset(offset);
        mbarChartAxisLeft.setXOffset(offset);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mLineChart.invalidate();
        mBarChart.invalidate();
    }

    public void hideBarChart() {
        barChartIsHidden = true;
        mBarChart.setVisibility(View.GONE);
    }


    public void setNoDataText(String txt) {
        mLineChart.setNoDataText(txt);
        mLineChart.invalidate();
    }

    public void setOnChartGestureListener(OnChartGestureListener l) {
        mLineChart.setOnChartGestureListener(l);
        mBarChart.setOnChartGestureListener(l);
    }

    public void setYLabelColors(int... colors) {
        mlineChartAxisLeft.setTextColors(colors);
        mlineChartAxisRight.setTextColors(colors);

    }

    public void setShinePointView(ShinePointView view, boolean anim) {
        mLineChart.setShinePointView(view, 1,anim);
    }

    public void setExtraTopOffset(float v) {
        mLineChart.setExtraTopOffset(v);
    }

    public void setDrawLinePathFillEnabled(boolean drawLinePathFillEnabled) {
        mDrawLinePathFillEnabled = drawLinePathFillEnabled;
    }

    public void setMinuteAverageLineColor(int color) {
        this.minuteAverageLineColor = color;
    }

    public void setMinuteLineColor(int color) {
        this.minuteLineColor = color;
    }

    public void setMinuteLinePathFillColor(int color) {
        this.minuteLinePathFillColor = color;
    }

    public void setGridLineColor(int color) {
//        this.gridLineColor=color;
        mlineChartAxisLeft.setGridColor(color);
        mlineChartAxisRight.setGridColor(color);
        mlineChartXAxis.setGridColor(color);
        mlineChartXAxis.setAxisLineColor(color);
        mbarChartAxisLeft.setGridColor(color);
        mBarChartXAxis.setGridColor(color);
        mBarChartXAxis.setAxisLineColor(color);
    }

    public void setXAxisLabelColor(int color) {
        if (mXLabelPosition == MarketXAxis.XAXISPOSITION_MIDDLE) {
            mlineChartXAxis.setTextColor(color);
        } else if (mXLabelPosition == MarketXAxis.XAXISPOSITION_BOTTOM) {
            mBarChartXAxis.setTextColor(color);
        }

    }

    public void setMinuteLinePathFillColorAlpha(int color) {
        this.minuteLinePathFillColorAlpha = color;
    }


    public boolean isStopParentTouch() {
        return mLineChart.isStopParentTouch() || mBarChart.isStopParentTouch();
    }

    public void setStopParentTouch(boolean enable) {
        mLineChart.setStopParentTouch(enable);
        mBarChart.setStopParentTouch(enable);
    }

    public void setScrollClass(String... name) {
        mLineChart.setScrollClass(name);
        mBarChart.setScrollClass(name);
    }


    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }
}
