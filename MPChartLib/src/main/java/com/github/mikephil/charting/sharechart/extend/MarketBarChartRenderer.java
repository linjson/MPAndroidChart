package com.github.mikephil.charting.sharechart.extend;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.sharechart.market.MarketProvider;
import com.github.mikephil.charting.sharechart.minutes.MinutesData;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by ljs on 15/11/3.
 */
public class MarketBarChartRenderer extends BarChartRenderer {
    private final MarketProvider mBarChar;

    public MarketBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setAlpha(255);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(1f);


        mBarChar = (MarketProvider) chart;
    }


    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        int setCount = mChart.getBarData().getDataSetCount();

        for (int i = 0; i < indices.length; i++) {

            Highlight h = indices[i];
            int index = h.getXIndex();

            int dataSetIndex = h.getDataSetIndex();
            IBarDataSet set = mChart.getBarData().getDataSetByIndex(dataSetIndex);

            if (set == null || !set.isHighlightEnabled())
                continue;

            float barspaceHalf = set.getBarSpace() / 2f;

            Transformer trans = mChart.getTransformer(set.getAxisDependency());

            mHighlightPaint.setColor(set.getHighLightColor());


            // check outofbounds
            if (index >= 0
                    && index < (mChart.getXChartMax() * mAnimator.getPhaseX()) / setCount) {

                BarEntry e = set.getEntryForXIndex(index);

                if (e == null || e.getXIndex() != index)
                    continue;

                float groupspace = mChart.getBarData().getGroupSpace();
                boolean isStack = h.getStackIndex() < 0 ? false : true;

                // calculate the correct x-position
                float x = index * setCount + dataSetIndex + groupspace / 2f
                        + groupspace * index;
                float y = isStack ? e.getVals()[h.getStackIndex()]
                        + e.getBelowSum(h.getStackIndex()) : e.getVal();

                // this is where the bar starts
                float from = isStack ? e.getBelowSum(h.getStackIndex()) : 0f;

                prepareBarHighlight(x, y, barspaceHalf, from, trans);

//                c.drawRect(mBarRect, mHighlightPaint);

                float startX = mBarRect.centerX();
                c.drawLine(startX, mViewPortHandler.getContentRect().bottom, startX, mViewPortHandler.getContentRect().top, mHighlightPaint);


            }
        }
    }

    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();


        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setBarSpace(dataSet.getBarSpace());
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);


        final int size = buffer.size();
        for (int j = 0; j < size; j += 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;


            BarEntry e = dataSet.getEntryForIndex(j / 4);

            MinutesData data = (MinutesData) e.getData();
            if (data != null) {
                mRenderPaint.setColor(data.color);
                mRenderPaint.setStyle(data.barStyle);
            } else {
                mRenderPaint.setColor(dataSet.getColor());
                mRenderPaint.setStyle(Paint.Style.STROKE);
            }


            if (!mBarChar.getDrawbarEnable()) {
                final float startX = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2;
                c.drawLine(startX, buffer.buffer[j + 1], startX, buffer.buffer[j + 3], mRenderPaint);
            } else {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }
        }
    }

    @Override
    protected boolean passesCheck() {
        return false;
    }
}
