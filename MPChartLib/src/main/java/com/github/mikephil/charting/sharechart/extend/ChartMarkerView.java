package com.github.mikephil.charting.sharechart.extend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.sharechart.drawable.ArrowShadowBitmap;
import com.github.mikephil.charting.sharechart.drawable.MarkerViewBitmap;
import com.github.mikephil.charting.sharechart.drawable.RectBitmap;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljs on 16/8/1.
 */
public class ChartMarkerView {


    public final static String BALLOON = "balloon";
    public final static String RECTANGLE = "rectangle";


    protected final Paint mLabelPaint;
    protected final MarkerViewBitmap bitmap;
    protected final DecimalFormat decimalFormat;
    protected final Paint mLinePaint;
    protected final float mSpace;
    protected final float mLineStroke;
    private final String mType;
    private boolean drawAllTipsEnabled;
    protected boolean labelEnabled;
    protected boolean xAxisLabelEnabled;
    protected int negativeColor = ColorTemplate.COLOR_NONE;
    protected int positiveColor = ColorTemplate.COLOR_NONE;
    protected ArrayList<MarkerViewData> showData;
    protected Chart mChart;
    protected boolean percentEnabled;
    private float mPercentPosition;
    private boolean mShowPercent;
    protected boolean mShowSeparationLine;
    protected boolean mValueEnabled;
    protected int mLabelColor;
    protected int mXAxisLabelColor;


    public ChartMarkerView(Context context, String type) {
        mLabelPaint = new Paint();
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(Utils.convertDpToPixel(14));

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLineStroke = Utils.convertDpToPixel(1);
        mLinePaint.setStrokeWidth(mLineStroke);
        mLinePaint.setColor(Color.GRAY);

        mSpace = Utils.convertDpToPixel(5);

        if (BALLOON.equals(type)) {
            mType = BALLOON;
            bitmap = new ArrowShadowBitmap();
        } else {
            mType = RECTANGLE;
            bitmap = new RectBitmap();
        }
        showData = new ArrayList(0);
        decimalFormat = new DecimalFormat("#.00%");

    }

    public void prepare(Highlight highlight) {
        showData.clear();
        final int setIndex = highlight.getDataSetIndex();
        Entry e = null;
        IDataSet highlightSet = null;
        if (mChart instanceof CombinedChart) {
            BarLineScatterCandleBubbleData dataset = ((CombinedChart) mChart).getCombinedData().getDataByIndex(highlight.getDataIndex());
            e = dataset.getEntryForHighlight(highlight);
            highlightSet = dataset.getDataSetByIndex(setIndex);
        } else {
            e = mChart.getData().getEntryForHighlight(highlight);
            highlightSet = mChart.getData().getDataSetByIndex(setIndex);
        }

        if (e == null || e.getX() != highlight.getX()) {
            return;
        }


        int x = highlightSet.getEntryIndex(e);


        if (xAxisLabelEnabled) {

            IAxisValueFormatter formatter = mChart.getXAxis().getValueFormatter();
            String formattedLabel = formatter.getFormattedValue(x, mChart.getXAxis());
            showData.add(new MarkerViewData(formattedLabel));
        }


        if (drawAllTipsEnabled) {
            addTipAxisData(e, x, setIndex, highlight.getDataIndex());
        } else {
            addTipItemData(e, x, setIndex, highlight.getStackIndex(), highlight.getDataIndex());
        }

        calLabelSize();


    }

    private void addTipAxisData(Entry e, int x, int setIndex, int dataIndex) {
        if (mChart instanceof BarChart) {
            addBarData((BarData) mChart.getData(), x);
        } else if (mChart instanceof CombinedChart) {
            List<BarLineScatterCandleBubbleData> allData = ((CombinedChart) mChart).getCombinedData().getAllData();
            BarLineScatterCandleBubbleData a = allData.get(dataIndex);
            IDataSet dataset = a.getDataSetByIndex(setIndex);
            x = dataset.getEntryIndex(e);

            int size = allData.size();
            for (int i = 0; i < size; i++) {
                BarLineScatterCandleBubbleData data = allData.get(i);
                if (data instanceof LineData) {
                    addLineData(data, x);
                } else if (data instanceof BarData) {
                    addBarData((BarData) data, x);
                }
            }
        } else {
            addLineData(mChart.getData(), x);
        }

    }

    protected void calLabelSize() {
        int count = showData.size();
        if (count == 0) {
            return;
        }
        float space = mSpace;
        float height, width = 0;
        final Paint labelPaint = this.mLabelPaint;
        final boolean xAxisLabelEnabled = this.xAxisLabelEnabled;
        height = (Utils.calcTextHeight(labelPaint, "Q") + space) * count - space;
        int i = 0;
        if (xAxisLabelEnabled) {
            if (mShowSeparationLine) {
                height += mLineStroke + space;
            }
            width = Utils.calcTextWidth(labelPaint, showData.get(0).getXAxis());
            i++;
        }

        final boolean labelEnabled = this.labelEnabled;
        final boolean percentEnabled = this.percentEnabled;

        float secondPosition = 0;
        MarkerViewData data = null;
        boolean showPercent = false;
        for (; i < count; i++) {
            data = showData.get(i);
            float w = 0;
            float labelWidth = 0;
            if (labelEnabled && data.showLabel()) {
                w = Utils.calcTextWidth(labelPaint, data.getLabel()) + mSpace;
                labelWidth = w + mSpace;
            }

            if (percentEnabled && data.showPercent()) {
                float pw = Utils.calcTextWidth(labelPaint, data.getPercent()) + mSpace;
                if (labelEnabled) {
                    w += pw;
                } else {
                    w = pw;
                }
                labelWidth += pw;
                showPercent = true;

            }

            secondPosition = Math.max(secondPosition, w);
            if (mValueEnabled) {
                labelWidth += Utils.calcTextWidth(labelPaint, data.getData());
            }

            width = Math.max(width, labelWidth);

        }

        this.mPercentPosition = secondPosition;
        this.mShowPercent = showPercent;

        setBitmapSize((int) width, (int) height);


    }

    protected void setBitmapSize(int width, int height) {

        int minWidth = (int) Utils.convertDpToPixel(20);
//        int minHeight = (int) Utils.convertDpToPixel(20);

        width = Math.max(minWidth, width);
//        height = Math.max(minHeight, height);

        bitmap.initBitmapSize(width, height);


    }


    public void draw(Canvas canvas, float x, float y) {


        int count = showData.size();
        if (count == 0) {
            return;
        }

        bitmap.draw(canvas, x, y);
        final boolean xAxisLabelEnabled = this.xAxisLabelEnabled;
        final boolean labelEnabled = this.labelEnabled;
        final boolean percentEnabled = this.percentEnabled;
        final boolean showPercent = mShowPercent;
        float space = mSpace;
        final Paint labelPaint = this.mLabelPaint;

        int xAxisColor = mXAxisLabelColor == ColorTemplate.COLOR_NONE ? Color.BLACK : mXAxisLabelColor;
        int labelColor = mLabelColor;
        final float textHeight = Utils.calcTextHeight(labelPaint, "Q");
        final float topSpace = bitmap.getTopSpace();
        final float paddingHorizontal = bitmap.getPaddingHorizontal();
        RectF rect = bitmap.getContentRect();

        MarkerViewData data = null;
        float startY = textHeight + y + topSpace;
        float startX = x + rect.left + paddingHorizontal;
        float endX = x + rect.right - paddingHorizontal;
        final float percentPosition = this.mPercentPosition;

        int i = 0;
        if (xAxisLabelEnabled) {
            i++;
            data = showData.get(0);
            labelPaint.setTextAlign(Paint.Align.CENTER);
            labelPaint.setColor(xAxisColor);
            canvas.drawText(data.getXAxis(), x + rect.centerX(), startY, labelPaint);
            if (mShowSeparationLine) {
                startY += space + mLineStroke;
                canvas.drawLine(startX, startY, endX, startY, mLinePaint);
            }
            startY += space + textHeight;
        }

        for (; i < count; i++) {

            data = showData.get(i);
            boolean single = true;
            labelPaint.setTextAlign(Paint.Align.LEFT);
            if (labelColor != ColorTemplate.COLOR_NONE) {
                labelPaint.setColor(labelColor);
            } else {
                labelPaint.setColor(data.getColor());
            }

            if (labelEnabled) {
                canvas.drawText(data.getLabel(), startX, startY, labelPaint);
                single = false;
            }

            if (data.getValue() >= 0 && positiveColor != ColorTemplate.COLOR_NONE) {
                labelPaint.setColor(positiveColor);
            } else if (data.getValue() < 0 && negativeColor != ColorTemplate.COLOR_NONE) {
                labelPaint.setColor(negativeColor);
            } else {
                labelPaint.setColor(data.getColor());
            }
            if (percentEnabled && data.showPercent()) {
                if (labelEnabled) {
                    labelPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(data.getPercent(), startX + percentPosition, startY, labelPaint);
                } else {
                    canvas.drawText(data.getPercent(), startX, startY, labelPaint);
                }

                single = false;
            }

            if (mValueEnabled) {
                if (single && !showPercent) {
                    labelPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(data.getData(), x + rect.centerX(), startY, labelPaint);
                } else {
                    labelPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(data.getData(), endX, startY, labelPaint);
                }
            }
            startY += space + textHeight;

        }


    }


    private void addTipItemData(Entry e, int x, int setIndex, int stackIndex, int dataIndex) {

        if (mChart instanceof BarChart) {
            addBarData(((BarChart) mChart).getData(), setIndex, x, stackIndex);
        } else if (mChart instanceof CombinedChart) {
            BarLineScatterCandleBubbleData data = ((CombinedChart) mChart).getCombinedData().getDataByIndex(dataIndex);
            x = data.getDataSetByIndex(setIndex).getEntryIndex(e);
            if (data instanceof LineData) {
                addLineData(data, setIndex, x);
            } else if (data instanceof BarData) {
                addBarData((BarData) data, setIndex, x, stackIndex);
            }
        } else {
            addLineData(mChart.getData(), setIndex, x);
        }

    }

    private void addBarData(BarData data, int setIndex, int x, int stackIndex) {
        IBarDataSet barset = data.getDataSetByIndex(setIndex);
        IValueFormatter formatter = barset.getValueFormatter();
        BarEntry bar = barset.getEntryForIndex(x);
        float[] vals = bar.getYVals();
        BarDataProvider provider = (BarDataProvider) mChart;
        float sum = bar.getY();
        String[] labels = barset.getStackLabels();
        if (provider.isHighlightAllBarEnabled()) {
            int setCount = data.getDataSetCount();

            for (int i = 0; i < setCount; i++) {
                barset = data.getDataSetByIndex(i);
                formatter = barset.getValueFormatter();
                bar = barset.getEntryForIndex(x);
                labels = barset.getStackLabels();
                if (barset.isStacked()) {
                    vals = bar.getYVals();
                    for (int j = 0; j < vals.length; j++) {
                        String val = formatter.getFormattedValue(vals[j], bar, setIndex, mChart.getViewPortHandler());
                        addDataModel(labels[j], val, barset.getColor(j), vals[j], decimalFormat.format(vals[j] / bar.getY()));
                    }
                } else {
                    String val = formatter.getFormattedValue(bar.getY(), bar, setIndex, mChart.getViewPortHandler());
                    addDataModel(barset.getLabel(), val, barset.getColor(), bar.getY());
                }
            }
        } else if (barset.isStacked()) {
            if (provider.isHighlightFullBarEnabled()) {
                for (int i = 0; i < vals.length; i++) {
                    String val = formatter.getFormattedValue(vals[i], bar, setIndex, mChart.getViewPortHandler());
                    addDataModel(labels[i], val, barset.getColor(i), vals[i], decimalFormat.format(vals[i] / sum));
                }
            } else {
                if (stackIndex >= vals.length) {
                    return;
                }
                String val = formatter.getFormattedValue(vals[stackIndex], bar, setIndex, mChart.getViewPortHandler());
                addDataModel(labels[stackIndex], val, barset.getColor(stackIndex), vals[stackIndex], decimalFormat.format(vals[stackIndex] / sum));
            }

        } else {
            String val = formatter.getFormattedValue(sum, bar, setIndex, mChart.getViewPortHandler());
            addDataModel(barset.getLabel(), val, barset.getColor(), sum);
        }

    }

    private void addBarData(BarData data, int x) {
        int setCount = data.getDataSetCount();
        for (int i = 0; i < setCount; i++) {
            IBarDataSet barset = data.getDataSetByIndex(i);
            BarEntry bar = barset.getEntryForIndex(x);
            String[] labels = barset.getStackLabels();
            IValueFormatter formatter = barset.getValueFormatter();
            if (barset.isStacked()) {
                float[] vals = bar.getYVals();
                for (int j = 0; j < vals.length; j++) {
                    String val = formatter.getFormattedValue(vals[j], bar, i, mChart.getViewPortHandler());
                    addDataModel(labels[j], val, barset.getColor(j), vals[j], decimalFormat.format(vals[j] / bar.getY()));
                }
            } else {
                String val = formatter.getFormattedValue(bar.getY(), bar, i, mChart.getViewPortHandler());
                addDataModel(barset.getLabel(), val, barset.getColor(), bar.getY());
            }
        }
    }

    private void addLineData(ChartData data, int datasetIndex, int x) {
        IDataSet dataset = data.getDataSetByIndex(datasetIndex);

        Entry eline = dataset.getEntryForIndex(x);

        IValueFormatter formatter = dataset.getValueFormatter();
        String value = formatter.getFormattedValue(eline.getY(), eline, datasetIndex, mChart.getViewPortHandler());

        addDataModel(dataset.getLabel(), value, dataset.getColor(), eline.getY());
    }


    private void addLineData(ChartData data, int x) {
        int dataSetCount = data.getDataSetCount();
        for (int j = 0; j < dataSetCount; j++) {
            IDataSet dataset = data.getDataSetByIndex(j);
            Entry eline = dataset.getEntryForIndex(x);

            IValueFormatter formatter = dataset.getValueFormatter();
            String value = formatter.getFormattedValue(eline.getY(), eline, j, mChart.getViewPortHandler());

            addDataModel(dataset.getLabel(), value, dataset.getColor(), eline.getY());
        }
    }


    protected void addDataModel(String label, String data, int color, float value) {
        showData.add(new MarkerViewData(label, data, color, value, null));
    }

    protected void addDataModel(String label, String data, int color, float value, String percent) {
        showData.add(new MarkerViewData(label, data, color, value, percent));
    }


    public void setDrawAllTipsEnabled(boolean drawAllTipsEnabled) {
        this.drawAllTipsEnabled = drawAllTipsEnabled;
    }

    public void setLabelEnabled(boolean labelEnabled) {
        this.labelEnabled = labelEnabled;
    }

    public void setXAxisLabelEnabled(boolean xAxisLabelEnabled) {
        this.xAxisLabelEnabled = xAxisLabelEnabled;
    }

    public void setBorderColor(int borderColor) {
        bitmap.setBorderColor(borderColor);
    }

    public void setTextSize(float textSize) {
        mLabelPaint.setTextSize(Utils.convertDpToPixel(textSize));
    }

    public void setTypeFace(Typeface face) {
        mLabelPaint.setTypeface(face);
    }


    public void setColor(int color) {
        bitmap.setBackgroundColor(color);
    }

    public void setNegativeColor(int negativeColor) {
        this.negativeColor = negativeColor;
    }

    public void setPositiveColor(int positiveColor) {
        this.positiveColor = positiveColor;
    }

    public void setPercentEnabled(boolean percentEnabled) {
        this.percentEnabled = percentEnabled;
    }

    public void setChart(Chart chart) {
        this.mChart = chart;
    }

    public float getWidth() {
        return bitmap.getBitmapWidth();
    }

    public String getType() {
        return mType;
    }

    public float getHeight() {
        return bitmap.getBitmapHeight();
    }

    public void setArrowPostion(int pos) {
        bitmap.setArrowPosition(pos);
    }

    public float getTriangleSize() {
        return bitmap.getTriangleSize();
    }

    public void setViewOffset(float x) {
        bitmap.setViewOffset(x);
    }

    public RectF getContectRect() {
        return bitmap.getContentRect();
    }

    public void setShowSeparationLine(boolean showSeparationLine) {
        mShowSeparationLine = showSeparationLine;
    }

    public void setValueEnabled(boolean valueEnabled) {
        mValueEnabled = valueEnabled;
    }

    public void setLabelColor(int labelColor) {
        mLabelColor = labelColor;
    }

    public void setXAxisLabelColor(int XAxisLabelColor) {
        mXAxisLabelColor = XAxisLabelColor;
    }

    public void setBorderRadius(float radius) {
        bitmap.setRadius(radius);
    }
}
