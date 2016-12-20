
package com.github.mikephil.charting.sharechart.extend;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextPaint;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class MarketXAxisRenderer extends XAxisRenderer {

    private final BarLineChartBase mChart;
    protected MarketXAxis mXAxis;

    public MarketXAxisRenderer(BarLineChartBase chart, ViewPortHandler viewPortHandler, MarketXAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
        mXAxis = xAxis;
        mChart = chart;
    }


    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    @Override
    protected void drawLabels(Canvas c, float pos, PointF anchor) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[]{
                0f, 0f
        };

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        if (mXAxis.getShowLabels() == null) {
            throw new RuntimeException("必须调用XAxis.setShowLabels()方法");
        }

        int count = mXAxis.getShowLabels().size();

        float perWidth = 0;// mViewPortHandler.contentWidth() / count;
        TextPaint tp = new TextPaint(mAxisLabelPaint);
        tp.setTextAlign(Paint.Align.LEFT);

        int step = mXAxis.mAxisLabelModulus;

        if (mXAxis.isShowFirstAndLast()) {
            step = count - 1;
        }

        for (int i = 0; i < count; i += step) {

            int ix = mXAxis.getShowLabels().keyAt(i);

            position[0] = ix;

            if (mXAxis.isAvoidFirstLastClippingEnabled()) {
                // avoid clipping of the last
                if ((i + mXAxis.mAxisLabelModulus > mMaxX || i == mXAxis.getValues().size() - 1) && mXAxis.getValues().size() > 1) {
                    position[0] = mMaxX;
                    i=mMaxX;
                    // avoid clipping of the first
                } else if (i == 0) {
                    position[0] = 0;
                }
            }

            mTrans.pointValuesToPixel(position);
            if (mViewPortHandler.isInBoundsX(position[0])) {
                String label = mXAxis.getShowLabels().valueAt(i);
                if(mXAxis.getValueFormatter()!=null){
                    label=mXAxis.getValueFormatter().getXValue(label,i,mViewPortHandler);
                }
                if (mXAxis.getTextPosition() == MarketXAxis.TextPosition_CENTER) {
                    if (perWidth == 0) {
                        perWidth = position[0] - mViewPortHandler.contentLeft();
                    }
                    position[0] -= perWidth / 2;
                } else {
                    int labelWidth = Utils.calcTextWidth(mAxisLabelPaint, label);
                    if ((labelWidth / 2 + position[0]) > mChart.getViewPortHandler().contentRight()) {
                        position[0] = mChart.getViewPortHandler().contentRight() - labelWidth / 2;
                    } else if ((position[0] - labelWidth / 2) < mChart.getViewPortHandler().contentLeft()) {
                        position[0] = mChart.getViewPortHandler().contentLeft() + labelWidth / 2;
                    }

                }
                drawLabel(c, label, i, position[0], pos, anchor, labelRotationAngleDegrees);

            }
        }
    }

    @Override
    protected void drawLabel(Canvas c, String label, int xIndex, float x, float y, PointF anchor, float angleDegrees) {
        Utils.drawXAxisValue(c, label, x, y, mAxisLabelPaint, anchor, angleDegrees);
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        // pre alloc
        float[] position = new float[]{
                0f, 0f
        };

        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());
        mGridPaint.setPathEffect(mXAxis.getGridDashPathEffect());

        Path gridLinePath = new Path();

        if (mXAxis.getShowLabels() == null) {
            throw new RuntimeException("必须调用XAxis.setShowLabels()方法");
        }

        int count = mXAxis.getShowLabels().size();

//        System.out.println("mViewPortHandler:" + mTrans.getValueMatrix());


        if (!mChart.isScaleXEnabled()) {
            count -= 1;
        }

        int step = mXAxis.mAxisLabelModulus;
        if (mXAxis.isShowFirstAndLast() && !mChart.isScaleXEnabled()) {
            step = count - 1;
        }

        for (int i = 0; i < count; i += step) {

            int ix = mXAxis.getShowLabels().keyAt(i);

            position[0] = ix;


            if (mXAxis.isAvoidFirstLastClippingEnabled()) {
                // avoid clipping of the last
                if ((i + mXAxis.mAxisLabelModulus > mMaxX || i == mXAxis.getValues().size() - 1) && mXAxis.getValues().size() > 1) {
                    position[0] = mMaxX;
                    // avoid clipping of the first
                } else if (i == 0) {
                    position[0] = 0;
                }
            }

            mTrans.pointValuesToPixel(position);


            if (position[0] >= mViewPortHandler.offsetLeft()
                    && position[0] <= mViewPortHandler.getChartWidth()) {

                gridLinePath.moveTo(position[0], mViewPortHandler.contentBottom());
                gridLinePath.lineTo(position[0], mViewPortHandler.contentTop());

                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(gridLinePath, mGridPaint);
            }

            gridLinePath.reset();

        }

    }

}
