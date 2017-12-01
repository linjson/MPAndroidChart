package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.HorizontalBarHighlighter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.renderer.YAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.HorizontalViewPortHandler;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.TransformerHorizontalBarChart;
import com.github.mikephil.charting.utils.Utils;

import java.util.Arrays;

/**
 * BarChart with horizontal bar orientation. In this implementation, x- and y-axis are switched, meaning the YAxis class
 * represents the horizontal values and the XAxis class represents the vertical values.
 *
 * @author Philipp Jahoda
 */
public class HorizontalBarChart extends BarChart {

    public HorizontalBarChart(Context context) {
        super(context);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {

        mViewPortHandler = new HorizontalViewPortHandler();

        super.init();

        mLeftAxisTransformer = new TransformerHorizontalBarChart(mViewPortHandler);
        mRightAxisTransformer = new TransformerHorizontalBarChart(mViewPortHandler);

        mRenderer = new HorizontalBarChartRenderer(this, mAnimator, mViewPortHandler);
        setHighlighter(new HorizontalBarHighlighter(this));

        mAxisRendererLeft = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
        mAxisRendererRight = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisRight, mRightAxisTransformer);
        mXAxisRenderer = new XAxisRendererHorizontalBarChart(mViewPortHandler, mXAxis, mLeftAxisTransformer, this);
    }

    private RectF mOffsetsBuffer = new RectF();

    /**
     * 对应位置转换
     * bottom->left
     * top->right
     * left->top
     * right->bottom
     */
    @Override
    public void calculateOffsets() {


        float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;

        calculateLegendOffsets(mOffsetsBuffer);

        offsetLeft += mOffsetsBuffer.left;
        offsetTop += mOffsetsBuffer.top;
        offsetRight += mOffsetsBuffer.right;
        offsetBottom += mOffsetsBuffer.bottom;

        //x->LT[0],LB[1],RB[2],RT[3],y->LT[4],LB[5],RB[6],RT[7]
        //计算所有x,y轴title四个方位的大小
        //最后上下左右边距取最大
        FSize[] titleRect = new FSize[8];
        for (int i = 0; i < titleRect.length; i++) {
            titleRect[i] = FSize.getInstance(0, 0);
        }
        FSize size = null;
        float leftNeedWidth = 0, rightNeedWidth = 0, topNeedHeight = 0, bottomNeedHeight = 0;

        // offsets for y-labels

        if (mAxisLeft.needsDraw()) {
            final YAxis.YAxisLabelPosition pos = mAxisLeft.getLabelPosition();
            size = mAxisLeft.getTitleSize(mAxisRendererLeft
                    .getTitlePaint());
            int titlePosition = mAxisLeft.getTitlePosition();
            boolean drawTitleEnabled = mAxisLeft.drawTitleEnabled();
            if (pos == YAxis.YAxisLabelPosition.INSIDE_CHART) {
                if (drawTitleEnabled) {
                    if (titlePosition == AxisBase.Y_TITLEPOSITION_BOTTOM) {
                        titleRect[4].width = size.width;
                    } else {
                        titleRect[7].width = size.width;
                    }
                }
            } else {
                topNeedHeight = mAxisLeft.getRequiredHeightSpace(mAxisRendererLeft.getPaintAxisLabels());
                if (drawTitleEnabled) {
                    if (titlePosition == AxisBase.Y_TITLEPOSITION_BOTTOM) {
                        titleRect[4].set(size);
                    } else {
                        titleRect[7].set(size);
                    }
                }
            }


        }

        if (mAxisRight.needsDraw()) {

            final YAxis.YAxisLabelPosition pos = mAxisRight.getLabelPosition();
            size = mAxisRight.getTitleSize(mAxisRendererRight
                    .getTitlePaint());
            int titlePosition = mAxisRight.getTitlePosition();
            boolean drawTitleEnabled = mAxisRight.drawTitleEnabled();
            if (pos == YAxis.YAxisLabelPosition.INSIDE_CHART) {
                if (drawTitleEnabled) {
                    if (titlePosition == AxisBase.Y_TITLEPOSITION_BOTTOM) {
                        titleRect[5].width = size.width;
                    } else {
                        titleRect[6].width = size.width;
                    }
                }
            } else {
                bottomNeedHeight = mAxisRight.getRequiredHeightSpace(mAxisRendererRight.getPaintAxisLabels());
                if (drawTitleEnabled) {
                    if (titlePosition == AxisBase.Y_TITLEPOSITION_BOTTOM) {
                        titleRect[5].set(size);
                    } else {
                        titleRect[6].set(size);
                    }
                }
            }
        }

        float xlabelwidth = mXAxis.mLabelRotatedWidth;

        if (mXAxis.isEnabled()) {
            final XAxis.XAxisPosition position = mXAxis.getPosition();
            final boolean drawTitleEnabled = mXAxis.drawTitleEnabled();
            // offsets for x-labels
            if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

                leftNeedWidth = xlabelwidth;

                if (drawTitleEnabled) {

                    size = mXAxis.getTitleSize(mXAxisRenderer.getTitlePaint());
                    if (mXAxis.getTitlePosition() == AxisBase.X_TITLEPOSITION_LEFT) {
                        titleRect[0].set(size);
                    } else {
                        titleRect[1].set(size);
                    }
                }


            } else if (mXAxis.getPosition() == XAxisPosition.TOP) {

                rightNeedWidth = xlabelwidth;
                if (drawTitleEnabled) {

                    size = mXAxis.getTitleSize(mXAxisRenderer.getTitlePaint());
                    if (mXAxis.getTitlePosition() == AxisBase.X_TITLEPOSITION_LEFT) {
                        titleRect[3].set(size);
                    } else {
                        titleRect[2].set(size);
                    }
                }
            } else if (mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {

                leftNeedWidth = xlabelwidth;
                rightNeedWidth = xlabelwidth;
                if (drawTitleEnabled) {

                    size = mXAxis.getTitleSize(mXAxisRenderer.getTitlePaint());
                    if (mXAxis.getTitlePosition() == AxisBase.X_TITLEPOSITION_LEFT) {
                        titleRect[0].set(size);
                        titleRect[3].set(size);
                    } else {
                        titleRect[2].set(size);
                        titleRect[1].set(size);
                    }
                }
            } else if (position == XAxis.XAxisPosition.TOP_INSIDE) {
                if (drawTitleEnabled) {

                    size = mXAxis.getTitleSize(mXAxisRenderer.getTitlePaint());
                    if (mXAxis.getTitlePosition() == AxisBase.X_TITLEPOSITION_LEFT) {
                        titleRect[0].height = size.height;
                    } else {
                        titleRect[1].height = size.height;
                    }
                }
            } else if (position == XAxis.XAxisPosition.BOTTOM_INSIDE) {
                if (drawTitleEnabled) {

                    size = mXAxis.getTitleSize(mXAxisRenderer.getTitlePaint());
                    if (mXAxis.getTitlePosition() == AxisBase.X_TITLEPOSITION_LEFT) {
                        titleRect[3].height = size.height;
                    } else {
                        titleRect[2].height = size.height;
                    }
                }
            }
        }

        final int max = 4;
        final float[] allLeft = {leftNeedWidth, titleRect[4].width, titleRect[5].width, titleRect[1].width, titleRect[0].width};
        final float[] allright = {rightNeedWidth, titleRect[6].width, titleRect[7].width, titleRect[2].width, titleRect[3].width};
        final float[] alltop = {topNeedHeight, titleRect[0].height, titleRect[3].height, titleRect[4].height, titleRect[7].height};
        final float[] allbottom = {bottomNeedHeight, titleRect[1].height, titleRect[2].height, titleRect[5].height, titleRect[6].height};

        Arrays.sort(allLeft);
        Arrays.sort(allright);
        Arrays.sort(alltop);
        Arrays.sort(allbottom);

        offsetLeft += allLeft[max];
        offsetRight += allright[max];
        offsetTop += alltop[max];
        offsetBottom += allbottom[max];


        offsetTop += getExtraTopOffset();
        offsetRight += getExtraRightOffset();
        offsetBottom += getExtraBottomOffset();
        offsetLeft += getExtraLeftOffset();

        float minOffset = Utils.convertDpToPixel(mMinOffset);

        mViewPortHandler.restrainViewPort(
                Math.max(minOffset, offsetLeft),
                Math.max(minOffset, offsetTop),
                Math.max(minOffset, offsetRight),
                Math.max(minOffset, offsetBottom));

        if (mLogEnabled) {
            Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop + ", offsetRight: " +
                    offsetRight + ", offsetBottom: "
                    + offsetBottom);
            Log.i(LOG_TAG, "Content: " + mViewPortHandler.getContentRect().toString());
        }

        FSize.recycleInstances(Arrays.asList(titleRect));
        if (size != null) {
            FSize.recycleInstance(size);
        }

        prepareOffsetMatrix();
        prepareValuePxMatrix();
    }

    @Override
    protected void prepareValuePxMatrix() {
        mRightAxisTransformer.prepareMatrixValuePx(mAxisRight.mAxisMinimum, mAxisRight.mAxisRange, mXAxis.mAxisRange,
                mXAxis.mAxisMinimum);
        mLeftAxisTransformer.prepareMatrixValuePx(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisRange, mXAxis.mAxisRange,
                mXAxis.mAxisMinimum);
    }

    @Override
    protected float[] getMarkerPosition(Highlight high) {
        return new float[]{high.getDrawY(), high.getDrawX()};
    }

    @Override
    public void getBarBounds(BarEntry e, RectF outputRect) {

        RectF bounds = outputRect;
        IBarDataSet set = mData.getDataSetForEntry(e);

        if (set == null) {
            outputRect.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            return;
        }

        float y = e.getY();
        float x = e.getX();

        float barWidth = mData.getBarWidth();

        float top = x - barWidth / 2f;
        float bottom = x + barWidth / 2f;
        float left = y >= 0 ? y : 0;
        float right = y <= 0 ? y : 0;

        bounds.set(left, top, right, bottom);

        getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);

    }

    protected float[] mGetPositionBuffer = new float[2];

    /**
     * Returns a recyclable MPPointF instance.
     *
     * @param e
     * @param axis
     * @return
     */
    @Override
    public MPPointF getPosition(Entry e, AxisDependency axis) {

        if (e == null)
            return null;

        float[] vals = mGetPositionBuffer;
        vals[0] = e.getY();
        vals[1] = e.getX();

        getTransformer(axis).pointValuesToPixel(vals);

        return MPPointF.getInstance(vals[0], vals[1]);
    }

    /**
     * Returns the Highlight object (contains x-index and DataSet index) of the selected value at the given touch point
     * inside the BarChart.
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public Highlight getHighlightByTouchPoint(float x, float y) {
        if (!getViewPortHandler().getContentRect().contains(x, y)) {
            return null;
        }
        if (mData == null) {
            if (mLogEnabled)
                Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        } else
            return getHighlighter().getHighlight(y, x); // switch x and y
    }

    @Override
    public float getLowestVisibleX() {
        getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                mViewPortHandler.contentBottom(), posForGetLowestVisibleX);
        float result = (float) Math.max(mXAxis.mAxisMinimum, posForGetLowestVisibleX.y);
        return result;
    }

    @Override
    public float getHighestVisibleX() {
        getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                mViewPortHandler.contentTop(), posForGetHighestVisibleX);
        float result = (float) Math.min(mXAxis.mAxisMaximum, posForGetHighestVisibleX.y);
        return result;
    }

    /**
     * ###### VIEWPORT METHODS BELOW THIS ######
     */

    @Override
    public void setVisibleXRangeMaximum(float maxXRange) {
        float xScale = mXAxis.mAxisRange / (maxXRange);
        mViewPortHandler.setMinimumScaleY(xScale);
    }

    @Override
    public void setVisibleXRangeMinimum(float minXRange) {
        float xScale = mXAxis.mAxisRange / (minXRange);
        mViewPortHandler.setMaximumScaleY(xScale);
    }

    @Override
    public void setVisibleXRange(float minXRange, float maxXRange) {
        float minScale = mXAxis.mAxisRange / minXRange;
        float maxScale = mXAxis.mAxisRange / maxXRange;
        mViewPortHandler.setMinMaxScaleY(minScale, maxScale);
    }

    @Override
    public void setVisibleYRangeMaximum(float maxYRange, AxisDependency axis) {
        float yScale = getAxisRange(axis) / maxYRange;
        mViewPortHandler.setMinimumScaleX(yScale);
    }

    @Override
    public void setVisibleYRangeMinimum(float minYRange, AxisDependency axis) {
        float yScale = getAxisRange(axis) / minYRange;
        mViewPortHandler.setMaximumScaleX(yScale);
    }

    @Override
    public void setVisibleYRange(float minYRange, float maxYRange, AxisDependency axis) {
        float minScale = getAxisRange(axis) / minYRange;
        float maxScale = getAxisRange(axis) / maxYRange;
        mViewPortHandler.setMinMaxScaleX(minScale, maxScale);
    }
}
