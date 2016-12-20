package com.github.mikephil.charting.sharechart.extend;

import android.graphics.Canvas;
import android.text.TextUtils;

import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class MarketYAxisRenderer extends YAxisRenderer {

    protected MarketYAxis mYAxis;

    public MarketYAxisRenderer(ViewPortHandler viewPortHandler, MarketYAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
        mYAxis = yAxis;
    }


    protected void computeAxisValues(float min, float max) {


//        if (mYAxis.isShowOnlyMinMaxEnabled()) {
//
//            mYAxis.mEntryCount = 2;
//            mYAxis.mEntries = new float[2];
//            mYAxis.mEntries[0] = min;
//            mYAxis.mEntries[1] = max;
//            return;
//        }
        float base = mYAxis.getBaseValue();

        if (mYAxis.isShowBaseValueAndMaxmin()) {

            mYAxis.mEntryCount = 3;
            mYAxis.mEntries = new float[3];


            float a = Math.max(Math.abs(max), Math.abs(min));

            mYAxis.mEntries[0] = base == 0 ? a : max;
            mYAxis.mEntries[1] = base;
            mYAxis.mEntries[2] = base == 0 ? -a : min;


            return;

        }

        if (Float.isNaN(mYAxis.getBaseValue())) {
            super.computeAxisValues(min, max);

            return;
        }

        float yMin = min;

        int labelCount = mYAxis.getLabelCount();

        float interval = (base - yMin) / labelCount;
        int n = labelCount * 2 + 1;
        mYAxis.mEntryCount = n;
        // Ensure stops contains at least numStops elements.
        mYAxis.mEntries = new float[n];
        int i;
        float f;
        for (f = min, i = 0; i < n; f += interval, i++) {
            mYAxis.mEntries[i] = f;
        }


    }


    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {

        if (mYAxis.isShowOnlyMinMaxEnabled()) {
            // draw
            for (int i = 0; i < mYAxis.mEntryCount; i++) {

                String text = mYAxis.getFormattedLabel(i);

                if (i == 0 && !TextUtils.isEmpty(mYAxis.getMintext())) {
                    text = mYAxis.getMintext();
                }

                if (mYAxis.getTextColors() != null) {
                    mAxisLabelPaint.setColor(mYAxis.getTextColors()[i % mYAxis.getTextColors().length]);
                }

                if (i == 1) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentTop() + offset * 2.5f + 3, mAxisLabelPaint);
                } else if (i == 0) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentBottom() - 3, mAxisLabelPaint);
                }
//                c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
            }
        }
//        else if (mYAxis.isShowBaseValueAndMaxmin()) {
//
//            for (int i = 0; i < mYAxis.mEntryCount; i++) {
//                String text = mYAxis.getFormattedLabel(i);
//                if (i == 0) {
//                    c.drawText(text, fixedPosition, mViewPortHandler.contentTop() + offset * 2.5f + 3, mAxisLabelPaint);
//                } else if (i == 2) {
//                    c.drawText(text, fixedPosition, mViewPortHandler.contentBottom() - 3, mAxisLabelPaint);
//                } else {
//                    c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
//                }
//            }
//
//
//        } else if (mYAxis.isShowOnlyMinMaxEnabled()) {
//            for (int i = 0; i < mYAxis.mEntryCount; i++) {
//
//                String text = mYAxis.getFormattedLabel(i);
//
//                if (i == 1) {
//                    c.drawText(text, fixedPosition, mViewPortHandler.contentTop() + offset * 2.5f + 3, mAxisLabelPaint);
//                } else if (i == 0) {
//                    c.drawText(text, fixedPosition, mViewPortHandler.contentBottom() - 3, mAxisLabelPaint);
//                }
////                c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
//            }
//        }
        else {
//            super.drawYLabels(c, fixedPosition, positions, offset);


            for (int i = 0; i < mYAxis.mEntryCount; i++) {

                String text = mYAxis.getFormattedLabel(i);
                if (!mYAxis.isDrawTopYLabelEntryEnabled() && i >= mYAxis.mEntryCount - 1)
                    return;

                int labelHeight = Utils.calcTextHeight(mAxisLabelPaint, text);
                float pos = positions[i * 2 + 1] + offset;

                if ((pos - labelHeight) < mViewPortHandler.contentTop()) {

                    pos = mViewPortHandler.contentTop() + offset * 2.5f + 3;
                } else if ((pos + labelHeight / 2) > mViewPortHandler.contentBottom()) {
                    pos = mViewPortHandler.contentBottom() - 3;
                }
                if (mYAxis.getTextColors() != null) {
                    mAxisLabelPaint.setColor(mYAxis.getTextColors()[i % mYAxis.getTextColors().length]);
                }

                c.drawText(text, fixedPosition, pos, mAxisLabelPaint);
            }


        }


    }


}