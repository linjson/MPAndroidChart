
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class BarChartRenderer extends BarLineScatterCandleBubbleRenderer {

    public final static String V_VALUE_TOP = "V_TOP";
    public final static String V_VALUE_CENTER = "V_CENTER";
    public final static String V_VALUE_BOTTOM = "V_BOTTOM";
    public final static String H_VALUE_LEFT = "H_LEFT";
    public final static String H_VALUE_CENTER = "H_CENTER";
    public final static String H_VALUE_RIGHT = "H_RIGHT";


    protected BarDataProvider mChart;

    /**
     * the rect object that is used for drawing the bars
     */
    protected RectF mBarRect = new RectF();

    protected BarBuffer[] mBarBuffers;

    protected Paint mShadowPaint;
    protected Paint mBarBorderPaint;
    protected String verticalValueAlignment = V_VALUE_TOP;
    protected String horizontalValueAlignment = H_VALUE_CENTER;

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mBarBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new BarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    @Override
    public void drawData(Canvas c) {

        BarData barData = mChart.getBarData();

        for (int i = 0; i < barData.getDataSetCount(); i++) {

            IBarDataSet set = barData.getDataSetByIndex(i);

            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    protected RectF mBarShadowRectBuffer = new RectF();

    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        final boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }


        final int size = dataSet.getEntryCount();
        for (int i = 0, j = 0; i < size; i++) {

            BarEntry e = dataSet.getEntryForIndex(i);

            float[] vals = e.getYVals();

            if (vals != null) {
                for (int k = 0; k < vals.length; k++) {
                    j = drawBar(c, buffer, j, isSingleColor, drawBorder, dataSet.getColor(k));
                }
            } else {
                j = drawBar(c, buffer, j, isSingleColor, drawBorder, dataSet.getColor(i));
            }


        }


//        for (int j = 0; j < buffer.size(); j += 4) {
//
//            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
//                continue;
//
//            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
//                break;
//
//            if (!isSingleColor) {
//                // Set the color for the currently drawn value. If the index
//                // is out of bounds, reuse colors.
//                mRenderPaint.setColor(dataSet.getColor(j / 4));
//            }
//
//            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
//                    buffer.buffer[j + 3], mRenderPaint);
//
//            if (drawBorder) {
//                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
//                        buffer.buffer[j + 3], mBarBorderPaint);
//            }
//        }
    }

    protected int drawBar(Canvas c, BarBuffer buffer, int j, boolean isSingleColor, boolean drawBorder, int color) {
        if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
            return j + 4;

        if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
            return j + 4;

        if (!isSingleColor) {
            mRenderPaint.setColor(color);
        }

        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                buffer.buffer[j + 3], mRenderPaint);

        if (drawBorder) {
            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mBarBorderPaint);
        }
        return j + 4;
    }


    protected void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {

        float left = x - barWidthHalf;
        float right = x + barWidthHalf;
        float top = y1;
        float bottom = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(4.5f);
            float posOffset = 0f;
            float negOffset = 0f;
            boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();
            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // calculate the correct offset depending on the draw position of
                // the value
                float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
                posOffset = (drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus);
                negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus);


                if (isInverted) {
                    posOffset = -posOffset - valueTextHeight;
                    negOffset = -negOffset - valueTextHeight;
                }

                // get the buffer
                BarBuffer buffer = mBarBuffers[i];

                final float phaseY = mAnimator.getPhaseY();

                MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {

                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                        float x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2f;


                        BarEntry entry = dataSet.getEntryForIndex(j / 4);
                        float val = entry.getY();
                        float y = val >= 0 ? (buffer.buffer[j + 1] + posOffset) : (buffer.buffer[j + 3] + negOffset);

                        if (verticalValueAlignment.equals(V_VALUE_CENTER)) {
                            y = buffer.buffer[j + 1] + buffer.buffer[j + 3];
                            y = (y + valueTextHeight) / 2;
                        }

                        if (!mViewPortHandler.isInBoundsRight(x))
                            break;

                        if (!mViewPortHandler.isInBoundsY(y)
                                || !mViewPortHandler.isInBoundsLeft(x))
                            continue;

                        if (dataSet.isDrawValuesEnabled()) {
                            drawValue(c, dataSet.getValueFormatter(), val, entry, i, x,
                                    y,
                                    dataSet.getValueTextColor(j / 4));
                        }

                        if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                            Drawable icon = entry.getIcon();

                            float px = x;
                            float py = y;

                            px += iconsOffset.x;
                            py += iconsOffset.y;

                            Utils.drawImage(
                                    c,
                                    icon,
                                    (int)px,
                                    (int)py,
                                    icon.getIntrinsicWidth(),
                                    icon.getIntrinsicHeight());
                        }

                    }

                    // if we have stacks
                } else {

                    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                    int bufferIndex = 0;
                    int index = 0;

                    while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {

                        BarEntry entry = dataSet.getEntryForIndex(index);

                        float[] vals = entry.getYVals();
                        float x = (buffer.buffer[bufferIndex] + buffer.buffer[bufferIndex + 2]) / 2f;

                        int color = dataSet.getValueTextColor(index);

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsRight(x))
                                break;

                            if (!mViewPortHandler.isInBoundsY(buffer.buffer[bufferIndex + 1])
                                    || !mViewPortHandler.isInBoundsLeft(x))
                                continue;

                            if (dataSet.isDrawValuesEnabled()) {
                                drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, x,
                                        buffer.buffer[bufferIndex + 1] +
                                                (entry.getY() >= 0 ? posOffset : negOffset),
                                        color);
                            }

                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                Drawable icon = entry.getIcon();

                                float px = x;
                                float py = buffer.buffer[bufferIndex + 1] +
                                        (entry.getY() >= 0 ? posOffset : negOffset);

                                px += iconsOffset.x;
                                py += iconsOffset.y;

                                Utils.drawImage(
                                        c,
                                        icon,
                                        (int)px,
                                        (int)py,
                                        icon.getIntrinsicWidth(),
                                        icon.getIntrinsicHeight());
                            }

                            // draw stack values
                        } else {

                            int posCount = 0;
                            int negCount = 0;
                            for (int j = 0; j < vals.length; j++) {
                                if (vals[j] >= 0) {
                                    posCount++;
                                } else {
                                    negCount++;
                                }
                            }

                            float[] posTransformed = new float[posCount * 2];
                            float[] negTransformed = new float[negCount * 2];
                            float posY = 0f;
                            float negY = -entry.getNegativeSum();

                            for (int p = 0, n = 0, idx = 0; idx < vals.length; idx++) {
                                float value = vals[idx];
                                float y;
                                if (value >= 0) {
                                    posY += value;
                                    y = posY;
                                    posTransformed[p + 1] = y * phaseY;
                                    p += 2;
                                } else {
                                    y = negY;
                                    negY -= value;
                                    negTransformed[n + 1] = y * phaseY;
                                    n += 2;
                                }
                            }
                            if (posCount != 0) {
                                trans.pointValuesToPixel(posTransformed);
                            }
                            if (negCount != 0) {
                                trans.pointValuesToPixel(negTransformed);
                            }

                            for (int idx = 0, p = 0, n = 0; idx < vals.length; idx++) {
                                float y = 0;
                                float[] values = null;
                                boolean pos = true;
                                if (vals[idx] >= 0) {
                                    y = posTransformed[p + 1] + posOffset;
                                    if (p == 0) {
                                        values = new float[]{0, mChart.getYChartMin() > 0 ? mChart.getYChartMin() : 0};
                                        trans.pointValuesToPixel(values);
                                    }

                                    pos = true;

                                } else {
                                    y = negTransformed[n + 1] + negOffset;
                                    pos = false;

                                    if (negTransformed.length == n + 2) {
                                        values = new float[]{0, 0};
                                        trans.pointValuesToPixel(values);
                                    }

                                }


                                if (verticalValueAlignment.equals(V_VALUE_CENTER)) {
                                    if (pos) {
                                        if (values != null) {
                                            y = (posTransformed[p + 1] + values[1] + valueTextHeight) / 2;
                                        } else {
                                            y = (posTransformed[p + 1] + posTransformed[p - 1] + valueTextHeight) / 2;
                                        }
                                    } else {

                                        if (values == null) {
                                            y = (negTransformed[n + 1] + negTransformed[n + 3] + valueTextHeight) / 2;
                                        } else {
                                            y = (negTransformed[n + 1] + values[1] + valueTextHeight) / 2;
                                        }
                                    }
                                }
                                if (pos) {
                                    p += 2;
                                } else {
                                    n += 2;
                                }


                                if (!mViewPortHandler.isInBoundsRight(x)) {
                                    break;
                                }

                                if (!mViewPortHandler.isInBoundsY(y)
                                        || !mViewPortHandler.isInBoundsLeft(x)) {
                                    continue;
                                }
                                if (dataSet.isDrawValuesEnabled()) {
                                    drawValue(c, dataSet.getValueFormatter(), vals[idx], entry, i,idx, x, y, color);
                                }

                                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                    Drawable icon = entry.getIcon();

                                    Utils.drawImage(
                                            c,
                                            icon,
                                            (int)(x + iconsOffset.x),
                                            (int)(y + iconsOffset.y),
                                            icon.getIntrinsicWidth(),
                                            icon.getIntrinsicHeight());
                                }
                            }


//                            float[] transformed = new float[vals.length * 2];
//
//                            float posY = 0f;
//                            float negY = -entry.getNegativeSum();
//
//                            for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {
//
//                                float value = vals[idx];
//                                float y;
//
//                                if (value >= 0f) {
//                                    posY += value;
//                                    y = posY;
//                                } else {
//                                    y = negY;
//                                    negY -= value;
//                                }
//
//                                transformed[k + 1] = y * phaseY;
//                            }
//
//                            trans.pointValuesToPixel(transformed);
//
//                            for (int k = 0; k < transformed.length; k += 2) {
//
//                                float y = transformed[k + 1]
//                                        + (vals[k / 2] >= 0 ? posOffset : negOffset);
//
//                                if (!mViewPortHandler.isInBoundsRight(x))
//                                    break;
//
//                                if (!mViewPortHandler.isInBoundsY(y)
//                                        || !mViewPortHandler.isInBoundsLeft(x))
//                                    continue;
//
//
//                                drawValue(c, dataSet.getValueFormatter(), vals[k / 2], entry, i, x, y, color);
//                            }
                        }

                        bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                        index++;
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        BarData barData = mChart.getBarData();
        boolean highlightAll = mChart.isHighlightAllEnabled();

        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null && highlightAll) {
                set = barData.getDataSetByIndex(0);
            }

            if (set == null || !set.isHighlightEnabled())
                continue;

            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            boolean isStack = (high.getStackIndex() >= 0 && e.isStacked()) ? true : false;
            final boolean isAllBar = mChart.isHighlightAllBarEnabled();
            float y1;
            float y2;


            if (isAllBar) {

                final int x = set.getEntryIndex(e);
                int count = barData.getDataSetCount();
                float top = Float.NaN;
                float start = 0, end = 0;

                for (int i = 0; i < count; i++) {
                    IBarDataSet dataSet = mChart.getBarData().getDataSetByIndex(i);
                    BarEntry other = dataSet.getEntryForIndex(x);

                    if (other.isStacked()) {
                        y1 = other.getPositiveSum();
                        y2 = -other.getNegativeSum();
                    } else {
                        y1 = other.getY();
                        y2 = 0.f;
                    }

                    prepareBarHighlight(other.getX(), y1, y2, barData.getBarWidth() / 2f, trans);
                    mHighlightPaint.setColor(dataSet.getHighLightColor());
                    mHighlightPaint.setAlpha(dataSet.getHighLightAlpha());
                    c.drawRect(mBarRect, mHighlightPaint);

                    top = getFullBarTop(mBarRect, top);
                    if (i == 0) {
                        start = getFullBarStart(mBarRect);
                    }

                    if (i == count - 1) {
                        end = getFullBarEnd(mBarRect);
                    }
                }


                if (mChart instanceof CombinedChart) {
                    int dataIndex = ((CombinedChart) mChart).getData().getAllData().indexOf(barData);
                    if (high.getDataIndex() == dataIndex) {
                        high.setDraw(getHightDrawX(start, end), top);
                    }

                } else {
                    high.setDraw(getHightDrawX(start, end), top);
                }


                return;
            }


            if (isStack) {

                if (mChart.isHighlightFullBarEnabled()) {

                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();

                } else {

                    Range range = e.getRanges()[high.getStackIndex()];

                    y1 = range.from;
                    y2 = range.to;
                }

            } else {
                y1 = e.getY();
                y2 = 0.f;
            }

            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);

            setHighlightDrawPos(high, mBarRect);

            c.drawRect(mBarRect, mHighlightPaint);
        }
    }

    protected float getFullBarTop(RectF barRect, float top) {
        if (Float.isNaN(top)) {
            top = Float.MAX_VALUE;
        }
        return Math.min(barRect.top, top);
    }

    protected float getFullBarEnd(RectF barRect) {
        return mBarRect.right;
    }

    protected float getFullBarStart(RectF barRect) {
        return mBarRect.left;
    }

    protected float getHightDrawX(float start, float end) {
        return (start + end) / 2;
    }

    /**
     * Sets the drawing position of the highlight object based on the riven bar-rect.
     *
     * @param high
     */
    protected void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerX(), bar.top);
    }

    @Override
    public void drawExtras(Canvas c) {

//        BarLineChartBase chart = (BarLineChartBase) mChart;
//
//
//        if (!chart.valuesToHighlight()) {
//            return;
//        }
//
//        Highlight[] indices = chart.getHighlighted();
//        BarData barData = mChart.getBarData();
//        final boolean isAllBar = mChart.isHighlightAllBarEnabled();
//
//            IBarDataSet set = barData.getDataSetByIndex(0);
//
//            if (set == null || !set.isHighlightEnabled())
//                return ;
//
//            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
//
//            if (!isInBoundsX(e, set))
//                continue;
//
//            Transformer trans = mChart.getTransformer(set.getAxisDependency());
//
//            mHighlightPaint.setColor(set.getHighLightColor());
//            mHighlightPaint.setAlpha(set.getHighLightAlpha());
//
//            float y1;
//            float y2;
//
//
//            if (isAllBar) {
//
//                final int x = set.getEntryIndex(e);
//                int count = barData.getDataSetCount();
//                float top = Float.NaN;
//                float start = 0, end = 0;
//
//                for (int i = 0; i < count; i++) {
//                    IBarDataSet dataSet = mChart.getBarData().getDataSetByIndex(i);
//                    BarEntry other = dataSet.getEntryForIndex(x);
//
//                    if (other.isStacked()) {
//                        y1 = other.getPositiveSum();
//                        y2 = -other.getNegativeSum();
//                    } else {
//                        y1 = other.getY();
//                        y2 = 0.f;
//                    }
//
//                    prepareBarHighlight(other.getX(), y1, y2, barData.getBarWidth() / 2f, trans);
//
//
//
//                    mHighlightPaint.setColor(dataSet.getHighLightColor());
//                    mHighlightPaint.setAlpha(dataSet.getHighLightAlpha());
//                    c.drawRect(mBarRect, mHighlightPaint);
//
//                    top = getFullBarTop(mBarRect, top);
//                    if (i == 0) {
//                        start = getFullBarStart(mBarRect);
//                    }
//
//                    if (i == count - 1) {
//                        end = getFullBarEnd(mBarRect);
//                    }
//                }
//
//                high.setDraw(getHightDrawX(start, end), top);
//
//
//                return;
//            }
    }

    public void setVerticalValueAlignment(String valuePosition) {
        this.verticalValueAlignment = valuePosition;
    }

    public void setHorizontalValueAlignment(String valuePosition) {
        this.horizontalValueAlignment = valuePosition;
    }


}
