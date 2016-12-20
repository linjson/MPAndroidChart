
package com.github.mikephil.charting.sharechart.extend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.sharechart.market.MarketProvider;
import com.github.mikephil.charting.sharechart.market.OnScrollDataListener;
import com.github.mikephil.charting.sharechart.market.OnSyncChartListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;


/**
 * TouchListener for Bar-, Line-, Scatter- and CandleStickChart with handles all
 * touch interaction. Longpress == Zoom out. Double-Tap == Zoom in.
 *
 * @author Philipp Jahoda
 */
public class MarketChartTouchListener extends ChartTouchListener<BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>>> {

    private final static int DRAGHIGHLIGHT = 7;
    private static final int LONG_PRESS = 1;
    private static final int TOUCHUP = 2;

    private final MarketProvider klineProvider;
    private final Vibrator vibrator;

    private final int FLAG_CLOSELOADING = 2;
    private final int FLAG_OPENLOADING = 1;
    private final int FLAG_NONELOADING = -1;
    private final int longPressTime;
    private final TouchHandler mHandler;
    private final int touchDist;

    /**
     * the original touch-matrix from the chart
     */
    private Matrix mMatrix = new Matrix();

    /**
     * matrix for saving the original matrix state
     */
    private Matrix mSavedMatrix = new Matrix();

    /**
     * point where the touch action started
     */
    private PointF mTouchStartPoint = new PointF();

    /**
     * center between two pointers (fingers on the display)
     */
    private PointF mTouchPointCenter = new PointF();

    private float mSavedXDist = 1f;
    private float mSavedYDist = 1f;
    private float mSavedDist = 1f;

    private IDataSet mClosestDataSetToTouch;

    /**
     * used for tracking velocity of dragging
     */
    private VelocityTracker mVelocityTracker;

    private long mDecelerationLastTime = 0;
    private PointF mDecelerationCurrentPoint = new PointF();
    private PointF mDecelerationVelocity = new PointF();

    /**
     * the distance of movement that will be counted as a drag
     */
    private float mDragTriggerDist;

    /**
     * the minimum distance between the pointers that will trigger a zoom gesture
     */
    private float mMinScalePointerDistance;


    private int showLoading = FLAG_NONELOADING;
    public int loadingHasState = FLAG_CLOSELOADING;
    private PointF mDecelerationDestPoint;
    private boolean isMoveDest;
    private OnLoadingViewListener mListener;
    private boolean isLoading;
    //    private float maxZoom = 50f;
//    protected float minZoom = 10f;
    private boolean isLoadingViewClosing;
    private OnSyncChartListener scyncChartListener;
    private boolean longPressEnable = true;
    private boolean mStopParentTouch = true;
    private float saveDragOffsetX = -1;

    public MarketChartTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix) {
        super(chart);
        this.mMatrix = touchMatrix;

        this.mDragTriggerDist = Utils.convertDpToPixel(3f);

        this.mMinScalePointerDistance = Utils.convertDpToPixel(3.5f);

        klineProvider = (MarketProvider) chart;

        vibrator = (Vibrator) chart.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        longPressTime = ViewConfiguration.get(chart.getContext()).getLongPressTimeout();
        touchDist = ViewConfiguration.get(chart.getContext()).getScaledTouchSlop();
        mHandler = new TouchHandler();


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {


        if (isLoadingViewClosing) {
            return true;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }

        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled()))
            return true;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                startAction(event);
                stopDeceleration();

                saveTouchStart(event);

                if (longPressEnable) {
                    mHandler.removeMessages(LONG_PRESS);

                    Message msg = mHandler.obtainMessage(LONG_PRESS, event);

                    mHandler.sendMessageAtTime(msg, event.getDownTime()
                            + longPressTime);
                }

//                longPressStartTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:


                if (event.getPointerCount() >= 2) {
                    mHandler.removeMessages(LONG_PRESS);
                    mChart.disableScroll();

                    saveTouchStart(event);

                    // get the distance between the pointers on the x-axis
                    mSavedXDist = getXDist(event);

                    // get the distance between the pointers on the y-axis
                    mSavedYDist = getYDist(event);

                    // get the total distance between the pointers
                    mSavedDist = spacing(event);

                    if (mSavedDist > 10f) {
                        if (mChart.isPinchZoomEnabled()) {
                            mTouchMode = PINCH_ZOOM;
                        } else if (mChart.isScaleXEnabled() && mChart.isScaleYEnabled()) {
                            if (mSavedXDist >= mSavedYDist) {
                                mTouchMode = X_ZOOM;
                            } else {
                                mTouchMode = Y_ZOOM;
                            }
                        } else if (mChart.isScaleXEnabled()) {
                            mTouchMode = X_ZOOM;
                        } else if (mChart.isScaleYEnabled()) {
                            mTouchMode = Y_ZOOM;
                        }
                    }

                    // determine the touch-pointer center
                    midPoint(mTouchPointCenter, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (mTouchMode == DRAG) {


                    mChart.disableScroll();
                    performDrag(event);


                } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {

                    mChart.disableScroll();
                    if (mChart.isScaleXEnabled() || mChart.isScaleYEnabled())
                        performZoom(event);

                } else if (mTouchMode == DRAGHIGHLIGHT && mChart.isHighlightPerDragEnabled()) {
                    performHighlightDrag(event);
                } else if (mTouchMode == NONE) {


                    if (!longPressEnable) {
                        mTouchMode = DRAGHIGHLIGHT;
                        mHandler.removeMessages(LONG_PRESS);
                    } else if (mChart.isDragEnabled() && !allowLongPress(event)) {
                        mTouchMode = DRAG;
                        mHandler.removeMessages(LONG_PRESS);
                    }


                }

                break;
            case MotionEvent.ACTION_UP:

                final VelocityTracker velocityTracker = mVelocityTracker;
                final int pointerId = event.getPointerId(0);
                velocityTracker.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity());
                final float velocityY = velocityTracker.getYVelocity(pointerId);
                final float velocityX = velocityTracker.getXVelocity(pointerId);
                if (Math.abs(velocityX) > Utils.getMinimumFlingVelocity() ||
                        Math.abs(velocityY) > Utils.getMinimumFlingVelocity()) {

                    if (mTouchMode == DRAG && mChart.isDragDecelerationEnabled()) {
                        stopDeceleration();

                        mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();
                        mDecelerationCurrentPoint = new PointF(event.getX(), event.getY());
                        mDecelerationVelocity = new PointF(velocityX, velocityY);

                        Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google


                    }
                } else {
//                    System.out.println("mTouchMode:" + mTouchMode);
                    if (mTouchMode == DRAG || mTouchMode == POST_ZOOM || mTouchMode == X_ZOOM) {
                        performLoading();
                    }
                }

                if (mTouchMode == X_ZOOM ||
                        mTouchMode == Y_ZOOM ||
                        mTouchMode == PINCH_ZOOM ||
                        mTouchMode == POST_ZOOM) {

                    // Range might have changed, which means that Y-axis labels
                    // could have changed in size, affecting Y-axis size.
                    // So we need to recalculate offsets.
                    mChart.calculateOffsets();
                    mChart.postInvalidate();
                }


//                if (!isLoading && (mTouchMode == X_ZOOM || mTouchMode == POST_ZOOM)) {
//                    mChart.getViewPortHandler().setDragOffsetX(srcDragOffsetX);
//                }
                mHandler.removeMessages(LONG_PRESS);
                mTouchMode = NONE;
                clearHighlight();
                mChart.enableScroll();

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                restoreDragOffset();
                endAction(event);

                break;
            case MotionEvent.ACTION_POINTER_UP:
                Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, mVelocityTracker);
                clearHighlight();
                mTouchMode = POST_ZOOM;
                restoreDragOffset();
                break;

            case MotionEvent.ACTION_CANCEL:
                mTouchMode = NONE;
                mHandler.removeMessages(LONG_PRESS);
                clearHighlight();
                endAction(event);
                restoreDragOffset();
                break;
        }

        // perform the transformation, update the chart
        mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, true);
        dispatchSyncChart();

        return true; // indicate event was handled
    }

    private void restoreDragOffset() {
        if (saveDragOffsetX != -1) {
            mChart.getViewPortHandler().setDragOffsetX(saveDragOffsetX);
        }
    }

    private boolean allowTouchEvent(float x, float y) {

        RectF rect = mChart.getViewPortHandler().getContentRect();
        return rect.contains(x, y);

    }

    private boolean allowLongPress(MotionEvent event) {
        return Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
                mTouchStartPoint.y)) < touchDist;
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW CODE PERFORMS THE ACTUAL TOUCH ACTIONS */

    /**
     * Saves the current Matrix state and the touch-start point.
     *
     * @param event
     */
    private void saveTouchStart(MotionEvent event) {

        mSavedMatrix.set(mMatrix);
        mTouchStartPoint.set(event.getX(), event.getY());

        mClosestDataSetToTouch = mChart.getDataSetByTouchPoint(event.getX(), event.getY());
    }

    /**
     * Performs all necessary operations needed for dragging.
     *
     * @param event
     */
    private void performDrag(MotionEvent event) {

        mMatrix.set(mSavedMatrix);

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        float dX, dY;

        // check if axis is inverted
        if (mChart.isAnyAxisInverted() && mClosestDataSetToTouch != null
                && mChart.getAxis(mClosestDataSetToTouch.getAxisDependency()).isInverted()) {

            // if there is an inverted horizontalbarchart
            if (mChart instanceof HorizontalBarChart) {
                dX = -(event.getX() - mTouchStartPoint.x);
                dY = event.getY() - mTouchStartPoint.y;
            } else {
                dX = event.getX() - mTouchStartPoint.x;
                dY = -(event.getY() - mTouchStartPoint.y);
            }
        } else {
            dX = event.getX() - mTouchStartPoint.x;
            dY = event.getY() - mTouchStartPoint.y;
        }

        mMatrix.postTranslate(dX, dY);
        performScrollData();


        if (l != null)
            l.onChartTranslate(event, dX, dY);
    }


    /**
     * Performs the all operations necessary for pinch and axis syncZoom.
     *
     * @param event
     */
    private void performZoom(MotionEvent event) {

        if (event.getPointerCount() >= 2) {

            OnChartGestureListener l = mChart.getOnChartGestureListener();

            // get the distance between the pointers of the touch
            // event
            float totalDist = spacing(event);
            if (totalDist > mMinScalePointerDistance) {

                // get the translation
                PointF t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y);
                ViewPortHandler h = mChart.getViewPortHandler();
                if (saveDragOffsetX == -1) {
                    saveDragOffsetX = h.getDragOffsetX_DP();
                }
                h.setDragOffsetX(0);
                // take actions depending on the activated touch
                // mode
                if (mTouchMode == PINCH_ZOOM) {

                    float scale = totalDist / mSavedDist; // total scale

                    boolean isZoomingOut = (scale < 1);
                    boolean canZoomMoreX = isZoomingOut ?
                            h.canZoomOutMoreX() :
                            h.canZoomInMoreX();

                    boolean canZoomMoreY = isZoomingOut ?
                            h.canZoomOutMoreY() :
                            h.canZoomInMoreY();

                    float scaleX = (mChart.isScaleXEnabled()) ? scale : 1f;
                    float scaleY = (mChart.isScaleYEnabled()) ? scale : 1f;

                    if (canZoomMoreY || canZoomMoreX) {

                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(scaleX, scaleY, t.x, t.y);
                        if (l != null)
                            l.onChartScale(event, scaleX, scaleY);
                    }

                } else if (mTouchMode == X_ZOOM && mChart.isScaleXEnabled()) {

                    float xDist = getXDist(event);

                    if (xDist < mMinScalePointerDistance) {
                        return;
                    }

                    float scaleX = xDist / mSavedXDist; // x-axis scale
//                    System.out.printf("==>%s,%s,%s,%s \n", scaleX, xDist, mSavedXDist,h.getScaleX());
                    //true:缩小,false:放大
                    boolean isZoomingOut = (scaleX < 1);
                    boolean canZoomMoreX = isZoomingOut ?
                            h.canZoomOutMoreX() :
                            h.canZoomInMoreX();


                    if (canZoomMoreX) {

                        mMatrix.set(mSavedMatrix);
                        mMatrix.postScale(scaleX, 1f, t.x, t.y);
                        performScrollData();
                        if (l != null)
                            l.onChartScale(event, scaleX, 1f);
                    } else {
                        mSavedXDist = xDist;
                    }

                } else if (mTouchMode == Y_ZOOM && mChart.isScaleYEnabled()) {

                    float yDist = getYDist(event);
                    float scaleY = yDist / mSavedYDist; // y-axis scale
                    boolean isZoomingOut = (scaleY < 1);
                    boolean canZoomMoreY = isZoomingOut ?
                            h.canZoomOutMoreY() :
                            h.canZoomInMoreY();

                    if (canZoomMoreY) {
                        mMatrix.set(mSavedMatrix);

                        // y-axis comes from top to bottom, revert y
                        mMatrix.postScale(1f, scaleY, t.x, t.y);
                        performScrollData();
                        if (l != null)
                            l.onChartScale(event, 1f, scaleY);
                    }
                }

            }
        }
    }

    /**
     * Perform a highlight operation.
     *
     * @param e
     */
    private void performHighlight(MotionEvent e) {


        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h != null && !h.equalTo(mLastHighlighted)) {
            mLastHighlighted = h;
            mChart.highlightValue(h, true);
        } else {
            mLastHighlighted = null;
            mChart.highlightValue(null, false);
        }

    }

    private void clearHighlight() {
        mChart.highlightTouch(null);
    }

    /**
     * Highlights upon dragging, generates callbacks for the selection-listener.
     *
     * @param e
     */
    private void performHighlightDrag(MotionEvent e) {

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h != null) {
            mLastHighlighted = h;
            mChart.highlightValue(h,true);
        }
    }

    /**
     * ################ ################ ################ ################
     */
    /** DOING THE MATH BELOW ;-) */


    /**
     * Determines the center point between two pointer touch points.
     *
     * @param point
     * @param event
     */
    private static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2f, y / 2f);
    }

    /**
     * returns the distance between two pointer touch points
     *
     * @param event
     * @return
     */
    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * calculates the distance on the x-axis between two pointers (fingers on
     * the display)
     *
     * @param e
     * @return
     */
    private static float getXDist(MotionEvent e) {
        float x = Math.abs(e.getX(0) - e.getX(1));
        return x;
    }

    /**
     * calculates the distance on the y-axis between two pointers (fingers on
     * the display)
     *
     * @param e
     * @return
     */
    private static float getYDist(MotionEvent e) {
        float y = Math.abs(e.getY(0) - e.getY(1));
        return y;
    }

    /**
     * returns the correct translation depending on the provided x and y touch
     * points
     *
     * @param x
     * @param y
     * @return
     */
    public PointF getTrans(float x, float y) {

        ViewPortHandler vph = mChart.getViewPortHandler();

        float xTrans = x - vph.offsetLeft();
        float yTrans = 0f;

        // check if axis is inverted
        if (mChart.isAnyAxisInverted() && mClosestDataSetToTouch != null
                && mChart.isInverted(mClosestDataSetToTouch.getAxisDependency())) {
            yTrans = -(y - vph.offsetTop());
        } else {
            yTrans = -(mChart.getMeasuredHeight() - y - vph.offsetBottom());
        }

        return new PointF(xTrans, yTrans);
    }

    /**
     * ################ ################ ################ ################
     */
    /** GETTERS AND GESTURE RECOGNITION BELOW */

    /**
     * returns the matrix object the listener holds
     *
     * @return
     */
    public Matrix getMatrix() {
        return mMatrix;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
            return super.onDoubleTap(e);
        }

        // check if double-tap zooming is enabled
        if (mChart.isDoubleTapToZoomEnabled()) {

            PointF trans = getTrans(e.getX(), e.getY());

            mChart.zoom(mChart.isScaleXEnabled() ? 1.4f : 1f, mChart.isScaleYEnabled() ? 1.4f : 1f, trans.x, trans.y);

            if (mChart.isLogEnabled())
                Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.x + ", y: "
                        + trans.y);
        }

        return super.onDoubleTap(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
//        if (!longPressEnable) {
//            return;
//        }
//        if (!isOnLongPress && !prepareLoading(-1)) {
//            isOnLongPress = true;
//            mTouchMode = NONE;
//            //震动提示可拖动
////            vibrator.vibrate(100);
//
//            if (mChart.isHighlightPerDragEnabled()) {
//                performHighlightDrag(e);
//            }
//        }
//        OnChartGestureListener l = mChart.getOnChartGestureListener();
//
//        if (l != null) {
//            l.onChartLongPressed(e);
//        }
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (!this.allowTouchEvent(e.getX(), e.getY())) {
            return false;
        }

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null)
            l.onChartFling(e1, e2, velocityX, velocityY);

        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public void stopDeceleration() {
        isMoveDest = false;
        mDecelerationVelocity = new PointF(0.f, 0.f);
    }

    public void computeScroll() {


        if (!isMoveDest) {
            if (mDecelerationVelocity.x == 0.f && mDecelerationVelocity.y == 0.f) {
                return; // There's no deceleration in progress
            }

            final long currentTime = AnimationUtils.currentAnimationTimeMillis();

            mDecelerationVelocity.x *= mChart.getDragDecelerationFrictionCoef();
            mDecelerationVelocity.y *= mChart.getDragDecelerationFrictionCoef();

            final float timeInterval = (float) (currentTime - mDecelerationLastTime) / 1000.f;

            float distanceX = mDecelerationVelocity.x * timeInterval;
            float distanceY = mDecelerationVelocity.y * timeInterval;

            mDecelerationCurrentPoint.x += distanceX;
            mDecelerationCurrentPoint.y += distanceY;

            MotionEvent event = MotionEvent.obtain(currentTime, currentTime, MotionEvent.ACTION_MOVE, mDecelerationCurrentPoint.x, mDecelerationCurrentPoint.y, 0);
            performDrag(event);
            event.recycle();
            mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, false);
            mDecelerationLastTime = currentTime;

            dispatchSyncChart();
            if (Math.abs(mDecelerationVelocity.x) >= 0.01 || Math.abs(mDecelerationVelocity.y) >= 0.01) {

                if (prepareLoading(0)) {
                    performLoading();
                    return;
                }

                Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google
            } else {
                stopDeceleration();
            }
        } else {

            if (mDecelerationDestPoint == null) {
                return;
            }

            float distanceX = (mDecelerationDestPoint.x - mDecelerationCurrentPoint.x) / 3;

            mDecelerationCurrentPoint.x += distanceX;
//            System.out.println("mDecelerationCurrentPoint.x:" + mDecelerationCurrentPoint.x);


            float[] val = getMatrixValues();
            val[Matrix.MTRANS_X] = mDecelerationCurrentPoint.x;
            mMatrix.setValues(val);


            mMatrix = mChart.getViewPortHandler().refresh(mMatrix, mChart, false);


            dispatchSyncChart();
            if (Math.abs(mDecelerationDestPoint.x - mDecelerationCurrentPoint.x) > 0.9) {

                Utils.postInvalidateOnAnimation(mChart);
            } else {

                if (Math.abs(mDecelerationDestPoint.x - mDecelerationCurrentPoint.x) != 0) {
                    mDecelerationCurrentPoint.x = mDecelerationDestPoint.x;
//                    dispatchSyncChart();
                    Utils.postInvalidateOnAnimation(mChart);
                } else {
                    stopDeceleration();
                    handleLoadingViewStateChange();
                }
            }


        }


    }

    private void handleLoadingViewStateChange() {


        if (isLoading) {
            return;
        }
//        System.out.println(mChart.getClass().getName() + "-->");
//        System.out.println("loadingHasState:" + loadingHasState + " ,showLoading:" + showLoading);

        if (loadingHasState != showLoading) {
            loadingHasState = showLoading;


            if (showLoading == FLAG_OPENLOADING) {
//                System.out.println("开");
                isLoading = true;
                if (mListener != null) {
                    mListener.onLoadingViewStateOpen(mChart);
                }
            } else if (showLoading == FLAG_CLOSELOADING) {
                isLoadingViewClosing = false;
//                System.out.println("关");
                if (mListener != null) {
                    mListener.onLoadingViewStateClose(mChart);
                }
            }
        } else {
            isLoadingViewClosing = false;
        }
    }


    private void performLoading() {

        float[] val = getMatrixValues();
//        Matrix matrix = mChart.getViewPortHandler().getMatrixTouch();
        float offsetX = mChart.getViewPortHandler().getDragOffsetX();
//        matrix.getValues(val);


        float transX = val[Matrix.MTRANS_X];

        if (transX < 0) {
            return;
        }

        if (transX > offsetX / 2) {
            showLoading = FLAG_OPENLOADING;
            mDecelerationDestPoint = new PointF(offsetX, 0);

        } else {
            showLoading = FLAG_CLOSELOADING;
            mDecelerationDestPoint = new PointF(0, 0);
        }


        mDecelerationCurrentPoint = new PointF(transX, 0);

        isMoveDest = true;
        Utils.postInvalidateOnAnimation(mChart);


    }


    private boolean prepareLoading(int offset) {
        float[] val = getMatrixValues();
        float transX = val[Matrix.MTRANS_X];
        float offsetX = mChart.getViewPortHandler().getDragOffsetX();

        return transX <= offsetX + offset && transX > 0;
    }

    public void closeLoading() {

        if (!isLoading) {
            return;
        }
        System.out.println(mChart.getClass().getName() + ":closeLoading()");
        isLoading = false;
        isLoadingViewClosing = true;
        mTouchMode = NONE;
        isMoveDest = true;
        showLoading = FLAG_CLOSELOADING;
        float[] val = getMatrixValues();

        mDecelerationCurrentPoint = new PointF(val[Matrix.MTRANS_X], 0);
        mDecelerationDestPoint = new PointF(0, 0);
        Utils.postInvalidateOnAnimation(mChart);
    }

    public void setOnSyncChartListener(OnSyncChartListener l) {
        this.scyncChartListener = l;
    }


    public void setOnLoadingViewListener(OnLoadingViewListener l) {
        this.mListener = l;
    }


    /**
     * 在最右边的点显示数据
     */
    public void performScrollData() {
        Highlight h = mChart.getHighlightByTouchPoint(mChart.getViewPortHandler().contentRight(), 0);
        OnScrollDataListener l = klineProvider.getOnScrollDataListener();
        if (h != null && l != null && mChart.getData() != null) {
            Entry e = mChart.getData().getEntryForHighlight(h);
            l.onScrollDataSelect(e, h);
        }
    }


    private void dispatchSyncChart() {


        if (scyncChartListener != null) {
            float[] val = getMatrixValues();
            scyncChartListener.syncMatrix(val[Matrix.MTRANS_X], val[Matrix.MSCALE_X], true);
        }


    }

    private float[] getMatrixValues() {
        float[] val = new float[9];
        mChart.getViewPortHandler().getMatrixTouch().getValues(val);
//        mMatrix.getValues(val);
        return val;
    }

    public void prepareSync() {
        stopDeceleration();

    }


    public interface OnLoadingViewListener {

        void onLoadingViewStateOpen(Chart chart);

        void onLoadingViewStateClose(Chart chart);

    }

    public void setLoadingViewOpen(boolean open) {

        if (open) {
            loadingHasState = FLAG_OPENLOADING;
            isLoadingViewClosing = false;
            isLoading = true;
        } else {
            loadingHasState = FLAG_CLOSELOADING;
            isLoadingViewClosing = false;
            isLoading = false;
        }

    }

    public void setLongPressEnable(boolean value) {
        longPressEnable = value;
    }


    private class TouchHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LONG_PRESS:
//                    mChart.getParent().requestDisallowInterceptTouchEvent(true);
                    dispatchLongPress((MotionEvent) msg.obj);
                    break;
                case TOUCHUP:
//                    mChart.getParent().requestDisallowInterceptTouchEvent(false);
                    dispatchTapUp();
                    break;
                default:
                    throw new RuntimeException("Unknown message " + msg); //never
            }
        }
    }

    private void dispatchTapUp() {
        System.out.println("dispatchTapUp");
    }

    private void dispatchLongPress(MotionEvent event) {
        mTouchMode = DRAGHIGHLIGHT;
        // System.out.printf("dispatchLongPress \n");
        mChart.disableScroll();
        if (mChart.isHighlightPerDragEnabled()) {
            performHighlightDrag(event);
        }
    }

    public void setStopParentTouch(boolean stop) {
        this.mStopParentTouch = stop;
    }

    public boolean isStopParentTouch() {
        // System.out.printf("==>touch mode:%s ---1,7 \n",mTouchMode);
        return mStopParentTouch || mTouchMode == DRAGHIGHLIGHT;
    }

}
