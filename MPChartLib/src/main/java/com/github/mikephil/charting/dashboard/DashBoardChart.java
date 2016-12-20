package com.github.mikephil.charting.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.List;

/**
 * Created by ljs on 15/7/3.
 */
public class DashBoardChart extends Chart<DashBoradData> {


    protected ArcAxis mArcAxis;

    protected ArcRender mArcRenderer;


    public DashBoardChart(Context context) {
        super(context);
    }

    public DashBoardChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DashBoardChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mArcAxis = new ArcAxis();
        mLegendRenderer = new ArcLegendRender(mViewPortHandler, mLegend);
        mArcRenderer = new ArcRender(mViewPortHandler, new Transformer(mViewPortHandler), mArcAxis);

    }

    @Override
    public void notifyDataSetChanged() {


        if (mLegend != null) {
            ((ArcLegendRender) mLegendRenderer).computeLegend(mArcAxis.getLimitLines());
        }

        calculateOffsets();
        // invalidate();
    }

    @Override
    protected void calculateOffsets() {
        float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;


        if (mLegend != null && mLegend.isEnabled()) {

            if (mLegend.getPosition() == Legend.LegendPosition.RIGHT_OF_CHART
                    || mLegend.getPosition() == Legend.LegendPosition.RIGHT_OF_CHART_CENTER) {

                offsetRight += Math.min(mLegend.mNeededWidth, mViewPortHandler.getChartWidth()
                        * mLegend.getMaxSizePercent())
                        + mLegend.getXOffset() * 2f;

            } else if (mLegend.getPosition() == Legend.LegendPosition.LEFT_OF_CHART
                    || mLegend.getPosition() == Legend.LegendPosition.LEFT_OF_CHART_CENTER) {

                offsetLeft += Math.min(mLegend.mNeededWidth, mViewPortHandler.getChartWidth()
                        * mLegend.getMaxSizePercent())
                        + mLegend.getXOffset() * 2f;

            } else if (mLegend.getPosition() == Legend.LegendPosition.BELOW_CHART_LEFT
                    || mLegend.getPosition() == Legend.LegendPosition.BELOW_CHART_RIGHT
                    || mLegend.getPosition() == Legend.LegendPosition.BELOW_CHART_CENTER) {

                float yOffset = mLegend.mTextHeightMax; // It's

                offsetBottom += Math.min(mLegend.mNeededHeight + yOffset,
                        mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());

            }
        }


        offsetTop += getExtraTopOffset();
        offsetRight += getExtraRightOffset();
        offsetBottom += getExtraBottomOffset();
        offsetLeft += getExtraLeftOffset();

        float min = Utils.convertDpToPixel(10f);

        mViewPortHandler.restrainViewPort(Math.max(min, offsetLeft), Math.max(min, offsetTop),
                Math.max(min, offsetRight), Math.max(min, offsetBottom));


    }

    @Override
    protected void calcMinMax() {

    }

    @Override
    public float getYChartMin() {
        return 0;
    }

    @Override
    public float getYChartMax() {
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mData==null)
            return;

        float val = getArcAxis().getMin();

        if (mData != null) {
            List<Entry> list = mData.getDataSets().get(0).getYVals();
            val = list.get(0).getVal();
        }


        mArcRenderer.initParams();
        mArcAxis.sortLimitLine();
        mArcRenderer.computeAxisValues(mArcAxis.getMin(), mArcAxis.getMax());
        mArcRenderer.renderAxisBackgroundColor(canvas);
        mArcRenderer.renderLimitLines(canvas);
        mArcRenderer.renderAxisLine(canvas);
        mArcRenderer.renderGridLines(canvas);

        mArcRenderer.renderArcValuePoint(canvas, val, mAnimator.getPhaseY());
        mArcRenderer.renderAxisLabels(canvas);
        mArcRenderer.renderArcCenter(canvas);

        mArcRenderer.renderValueText(canvas, val);


        mLegendRenderer.renderLegend(canvas);
    }

    @Override
    protected float[] getMarkerPosition(Entry e, Highlight highlight) {
        return new float[0];
    }

    public ArcAxis getArcAxis() {
        return mArcAxis;
    }

    public void setArcLineColor(int color) {
        mArcRenderer.getArcLinePaint().setColor(color);
    }

    public void setArcLineWidth(float width) {
        mArcRenderer.getArcLinePaint().setStrokeWidth(width);
    }

    public void setArcBackGroundColor(int color) {
        mArcRenderer.getArcBackGroundPaint().setColor(color);
    }

    public void setGridLineColor(int color) {
        mArcRenderer.getGridPaint().setColor(color);
    }

    public void setGridLineWidth(float width) {
        mArcRenderer.getGridPaint().setStrokeWidth(width);
    }

    public void setLabelsColor(int color) {
        mArcRenderer.getPaintAxisLabels().setColor(color);
    }

    public void setLabelsSize(float size) {
        mArcRenderer.getPaintAxisLabels().setTextSize(size);
    }

    public void setLablesFormat(ValueFormatter format) {
        mArcRenderer.setLablesFormat(format);
    }

    public void setCenterPointColor(int color) {
        mArcRenderer.getArcPoint().setColor(color);
    }

    public void setCenterPointSize(float size) {
        mArcRenderer.setArcCenterSize(size);
    }

    public void setValueSize(float size) {
        mArcRenderer.getArcValuePaint().setTextSize(size);
    }

    public void setValueColor(int color) {
        mArcRenderer.getArcValuePaint().setColor(color);
    }
}
