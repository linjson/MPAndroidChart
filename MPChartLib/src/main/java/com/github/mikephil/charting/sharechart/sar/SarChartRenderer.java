package com.github.mikephil.charting.sharechart.sar;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.LineRadarRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by ljs on 15/11/17.
 */
public class SarChartRenderer extends LineRadarRenderer {


    protected SarDataProvider mChart;

    private SarPointBuffer[] mPointBuffers;

    public SarChartRenderer(SarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);

        mChart = chart;
        mRenderPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void initBuffers() {
        SarData sarData = mChart.getSarData();
        final int dataSetCount = sarData.getDataSetCount();
        mPointBuffers = new SarPointBuffer[dataSetCount];
        for (int i = 0; i < dataSetCount; i++) {
            ISarDataSet set = sarData.getDataSetByIndex(i);
            mPointBuffers[i] = new SarPointBuffer(set.getEntryCount() * 2);
        }
    }

    @Override
    public void drawData(Canvas c) {
        SarData sarData = mChart.getSarData();

        final int dataSetCount = sarData.getDataSetCount();
        for (int i = 0; i < dataSetCount; i++) {

            ISarDataSet set = sarData.getDataSetByIndex(i);

            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set, i);
            }
        }
    }


    protected void drawDataSet(Canvas c, ISarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        int dataSetIndex = mChart.getSarData().getIndexOfDataSet(dataSet);


        SarPointBuffer pointBuffer = mPointBuffers[dataSetIndex];
        pointBuffer.setPhases(phaseX, phaseY);
        pointBuffer.feed(dataSet);
        int range = pointBuffer.size();
        trans.pointValuesToPixel(pointBuffer.buffer);

        // draw the body
        for (int j = 0; j < range; j += 2) {

            // get the entry
            SarEntry e = dataSet.getEntryForIndex(j / 2);

            if (!fitsBounds(e.getXIndex(), mMinX, mMaxX))
                continue;


            float pointX = pointBuffer.buffer[j];
            float pointY = pointBuffer.buffer[j + 1];

            if (!Float.isNaN(e.getSar())) {
                if (e.getSar() > e.getOpen()||e.getSar() < e.getClose() ) {
                    mRenderPaint.setColor(dataSet.getIncreasingColor());
                    if(e.getSar() > e.getClose()) {
                        mRenderPaint.setColor(dataSet.getDecreasingColor());
                    }
                } else {
                    mRenderPaint.setColor(dataSet.getDecreasingColor());
                }
                c.drawCircle(pointX, pointY, dataSet.getCircleSize(), mRenderPaint);
            }

        }
    }

    @Override
    public void drawValues(Canvas c) {

    }

    @Override
    public void drawExtras(Canvas c) {

    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            int xIndex = indices[i].getXIndex(); // get the
            // x-position

            ISarDataSet set = mChart.getSarData().getDataSetByIndex(
                    indices[i].getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            mHighlightPaint.setColor(set.getHighLightColor());

            SarEntry e = set.getEntryForXIndex(xIndex);

            if (e == null || e.getXIndex() != xIndex)
                continue;

//            float low = e.getLow() * mAnimator.getPhaseY();
//            float high = e.getHigh() * mAnimator.getPhaseY();

            float min = mChart.getYChartMin();
            float max = mChart.getYChartMax();

            float[] vertPts = new float[]{
                    xIndex - 0.5f, max, xIndex - 0.5f, min, xIndex + 0.5f, max, xIndex + 0.5f,
                    min
            };


            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(vertPts);


            c.drawLine((vertPts[0] + vertPts[4]) / 2, vertPts[1], (vertPts[2] + vertPts[6]) / 2, vertPts[3], mHighlightPaint);

        }
    }
}
