
package com.github.mikephil.charting.sharechart.pai;

import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.charts.ScatterChart.ScatterShape;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class PaiChartRenderer extends ScatterChartRenderer {


    public PaiChartRenderer(ScatterDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }


    protected void drawDataSet(Canvas c, IScatterDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

//        List<Entry> entries = dataSet.getYVals();

        float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        ScatterShape shape = dataSet.getScatterShape();

        ScatterBuffer buffer = mScatterBuffers[mChart.getScatterData().getIndexOfDataSet(
                dataSet)];
        buffer.setPhases(phaseX, phaseY);
        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        switch (shape) {
            case SQUARE:

                mRenderPaint.setStyle(Style.FILL);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    c.drawRect(buffer.buffer[i] - shapeHalf,
                            mViewPortHandler.contentBottom() - shapeHalf * 2, buffer.buffer[i]
                                    + shapeHalf, mViewPortHandler.contentBottom()
                                    + shapeHalf * 2, mRenderPaint);
                }
                break;
            case CIRCLE:

                mRenderPaint.setStyle(Style.FILL);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    c.drawCircle(buffer.buffer[i], mViewPortHandler.contentBottom() - shapeHalf, shapeHalf,
                            mRenderPaint);
                }
                break;
            case TRIANGLE:

                mRenderPaint.setStyle(Style.FILL);

                // create a triangle path
                Path tri = new Path();

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                            || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));
                    tri.moveTo(buffer.buffer[i], mViewPortHandler.contentBottom() - shapeHalf * 2);
                    tri.lineTo(buffer.buffer[i] + shapeHalf, mViewPortHandler.contentBottom());
                    tri.lineTo(buffer.buffer[i] - shapeHalf, mViewPortHandler.contentBottom());
                    tri.close();

                    c.drawPath(tri, mRenderPaint);
                    tri.reset();
                }
                break;
            case CROSS:

                mRenderPaint.setStyle(Style.STROKE);

                for (int i = 0; i < buffer.size(); i += 2) {

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i]))
                        continue;

                    mRenderPaint.setColor(dataSet.getColor(i / 2));

                    c.drawLine(buffer.buffer[i] - shapeHalf, mViewPortHandler.contentBottom() - shapeHalf,
                            buffer.buffer[i] + shapeHalf,
                            mViewPortHandler.contentBottom() - shapeHalf, mRenderPaint);
                    c.drawLine(buffer.buffer[i], mViewPortHandler.contentBottom() - shapeHalf * 2,
                            buffer.buffer[i], mViewPortHandler.contentBottom()
                                    + shapeHalf * 2, mRenderPaint);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

    }
}
