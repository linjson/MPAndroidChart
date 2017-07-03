
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Renderer for the HorizontalBarChart.
 *
 * @author Philipp Jahoda
 */
public class HorizontalBarChartRenderer extends BarChartRenderer {

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                                      ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

        mValuePaint.setTextAlign(Align.LEFT);
    }

    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new HorizontalBarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new HorizontalBarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    private RectF mBarShadowRectBuffer = new RectF();

    @Override
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

                mBarShadowRectBuffer.top = x - barWidthHalf;
                mBarShadowRectBuffer.bottom = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsTop(mBarShadowRectBuffer.bottom))
                    continue;

                if (!mViewPortHandler.isInBoundsBottom(mBarShadowRectBuffer.top))
                    break;

                mBarShadowRectBuffer.left = mViewPortHandler.contentLeft();
                mBarShadowRectBuffer.right = mViewPortHandler.contentRight();

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
//            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3]))
//                break;
//
//            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
//                continue;
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
        if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3]))
            return j + 4;

        if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
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


    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(5f);
            float posOffset = 0f;
            float negOffset = 0f;
            final boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            if (verticalValueAlignment.equals(V_VALUE_CENTER)) {
                mValuePaint.setTextAlign(Align.CENTER);
            } else {
                mValuePaint.setTextAlign(Align.LEFT);
            }

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);
                final float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
                final float halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f;

                IValueFormatter formatter = dataSet.getValueFormatter();

                // get the buffer
                BarBuffer buffer = mBarBuffers[i];

                final float phaseY = mAnimator.getPhaseY();

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {

                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                        float y = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f;

                        if (horizontalValueAlignment.equals(H_VALUE_RIGHT)) {
                            y = buffer.buffer[j + 3] + valueTextHeight;
                        }

                        BarEntry e = dataSet.getEntryForIndex(j / 4);
                        float val = e.getY();
                        String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);

                        // calculate the correct offset depending on the draw position of the value
                        float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                        posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                        negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                        if (isInverted) {
                            posOffset = -posOffset - valueTextWidth;
                            negOffset = -negOffset - valueTextWidth;
                        }


                        float x = buffer.buffer[j + 2] + (val >= 0 ? posOffset : negOffset);

                        if (verticalValueAlignment.equals(V_VALUE_CENTER)) {
                            x = (buffer.buffer[j + 2] + buffer.buffer[j]) / 2;
                        } else if (verticalValueAlignment.equals(V_VALUE_BOTTOM)) {
                            x = buffer.buffer[j] + valueOffsetPlus;
                        }


                        if (!mViewPortHandler.isInBoundsTop(y))
                            break;

                        if (!mViewPortHandler.isInBoundsX(x))
                            continue;

                        if (!mViewPortHandler.isInBoundsBottom(y))
                            continue;

                        drawValue(c, formattedValue, x,
                                y + halfTextHeight, dataSet.getValueTextColor(j / 2));
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                    int bufferIndex = 0;
                    int index = 0;

                    while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {

                        BarEntry e = dataSet.getEntryForIndex(index);

                        int color = dataSet.getValueTextColor(index);
                        float[] vals = e.getYVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[bufferIndex + 1]))
                                break;

                            if (!mViewPortHandler.isInBoundsX(buffer.buffer[bufferIndex]))
                                continue;

                            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[bufferIndex + 1]))
                                continue;

                            float val = e.getY();
                            String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);

                            // calculate the correct offset depending on the draw position of the value
                            float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                            posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                            negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                            if (isInverted) {
                                posOffset = -posOffset - valueTextWidth;
                                negOffset = -negOffset - valueTextWidth;
                            }

                            drawValue(c, formattedValue, buffer.buffer[bufferIndex + 2]
                                            + (e.getY() >= 0 ? posOffset : negOffset),
                                    buffer.buffer[bufferIndex + 1] + halfTextHeight, color);

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
                            float negY = -e.getNegativeSum();
                            int[] stackColors = dataSet.getStackColors();
                            boolean hasStackColor = stackColors != null && stackColors.length != 0;

                            for (int p = 0, n = 0, idx = 0; idx < vals.length; idx++) {
                                float value = vals[idx];
                                float y;
                                if (value >= 0) {
                                    posY += value;
                                    y = posY;
                                    posTransformed[p] = y * phaseY;
                                    p += 2;
                                } else {
                                    y = negY;
                                    negY -= value;
                                    negTransformed[n] = y * phaseY;
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
                                float y = (buffer.buffer[bufferIndex + 1] + buffer.buffer[bufferIndex + 3]) / 2f;

                                if (horizontalValueAlignment.equals(H_VALUE_RIGHT)) {
                                    y = buffer.buffer[bufferIndex + 3] + valueTextHeight;
                                }

                                float val = vals[idx];
                                float x;
                                boolean pos = val >= 0;
                                float[] values = null;
                                String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);
                                float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                                posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                                negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                                if (isInverted) {
                                    posOffset = -posOffset - valueTextWidth;
                                    negOffset = -negOffset - valueTextWidth;
                                }

                                if (hasStackColor) {
                                    color = stackColors[idx];
                                }

                                if (val >= 0) {
                                    x = posTransformed[p] + posOffset;
                                    if (p == 0) {
                                        values = new float[]{mChart.getYChartMin() > 0 ? mChart.getYChartMin() : 0, 0};
                                        trans.pointValuesToPixel(values);
                                    }
                                } else {
                                    x = negTransformed[n] + negOffset;
                                    if (negTransformed.length == n + 2) {
                                        values = new float[]{0, 0};
                                        trans.pointValuesToPixel(values);
                                    }
                                }


                                if (verticalValueAlignment.equals(V_VALUE_CENTER)) {

                                    if (pos) {
                                        if (values != null) {
                                            x = (posTransformed[p] + values[0]) / 2;

                                        } else {
                                            x = (posTransformed[p] + posTransformed[p - 2]) / 2;
                                        }
                                    } else {
                                        if (values == null) {
                                            x = (negTransformed[n] + negTransformed[n + 2]) / 2;
                                        } else {
                                            x = (negTransformed[n] + values[0]) / 2;
                                        }
                                    }
                                } else if (verticalValueAlignment.equals(V_VALUE_BOTTOM)) {

                                    if (pos) {
                                        mValuePaint.setTextAlign(Align.LEFT);
                                        if (values != null) {
                                            x = values[0];
                                        } else {
                                            x = posTransformed[p - 2];
                                        }
                                        x += valueOffsetPlus;
                                    } else {
                                        mValuePaint.setTextAlign(Align.RIGHT);
                                        if (values == null) {
                                            x = negTransformed[n + 2];
                                        } else {
                                            x = values[0];
                                        }
                                        x -= valueOffsetPlus;
                                    }

                                }

                                if (pos) {
                                    p += 2;
                                } else {
                                    n += 2;
                                }

                                if (!mViewPortHandler.isInBoundsTop(y))
                                    break;

                                if (!mViewPortHandler.isInBoundsX(x))
                                    continue;

                                if (!mViewPortHandler.isInBoundsBottom(y))
                                    continue;

                                drawValue(c, formattedValue, x, y + halfTextHeight, color);

                            }

//                            float[] transformed = new float[vals.length * 2];
//
//                            float posY = 0f;
//                            float negY = -e.getNegativeSum();
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
//                                transformed[k] = y * phaseY;
//                            }
//
//                            trans.pointValuesToPixel(transformed);
//
//                            for (int k = 0; k < transformed.length; k += 2) {
//
//                                float val = vals[k / 2];
//                                String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);
//
//                                // calculate the correct offset depending on the draw position of the value
//                                float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
//                                posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
//                                negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);
//
//                                if (isInverted) {
//                                    posOffset = -posOffset - valueTextWidth;
//                                    negOffset = -negOffset - valueTextWidth;
//                                }
//
//                                float x = transformed[k]
//                                        + (val >= 0 ? posOffset : negOffset);
//                                float y = (buffer.buffer[bufferIndex + 1] + buffer.buffer[bufferIndex + 3]) / 2f;
//
//                                if (!mViewPortHandler.isInBoundsTop(y))
//                                    break;
//
//                                if (!mViewPortHandler.isInBoundsX(x))
//                                    continue;
//
//                                if (!mViewPortHandler.isInBoundsBottom(y))
//                                    continue;
//
//                                drawValue(c, formattedValue, x, y + halfTextHeight, color);
//                            }
                        }

                        bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                        index++;
                    }
                }
            }
        }
    }

    protected void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(valueText, x, y, mValuePaint);
    }

    @Override
    protected void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {

        float top = x - barWidthHalf;
        float bottom = x + barWidthHalf;
        float left = y1;
        float right = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectToPixelPhaseHorizontal(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    protected void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.top, bar.right);
    }

    @Override
    protected boolean isDrawingValuesAllowed(ChartInterface chart) {
        return chart.getData().getEntryCount() < chart.getMaxVisibleCount()
                * mViewPortHandler.getScaleY();
    }

    protected float getHightDrawX(float start, float end) {
        return end;
    }

    protected float getFullBarEnd(RectF barRect) {
        return mBarRect.top;
    }

    protected float getFullBarStart(RectF barRect) {
        return mBarRect.bottom;
    }

    protected float getFullBarTop(RectF barRect, float top) {
        if (Float.isNaN(top)) {
            top = Float.MIN_VALUE;
        }
        return Math.max(barRect.right, top);
    }
}
