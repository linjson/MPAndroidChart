package com.github.mikephil.charting.dashboard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.renderer.AxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Created by ljs on 15/7/3.
 */
public class ArcRender extends AxisRenderer {


    private ArcAxis mArcAxis;

    private float mStartAngle;
    private float mSweepAngle;

    private float midleAngle = 90f;
    private float changeAngle = 50f;

    private float mPointSize = 20;

    private Paint mArcLinePaint;
    private Paint mArcBackGroundPaint;
    private TextPaint mArcValuePaint;
    private Paint mArcPointPaint;
    private PointF mCenterPoint;

    private float mRadiusX;
    private float mRadiusY;
    private ValueFormatter mLabelsFormat;

    public ArcRender(ViewPortHandler viewPortHandler, Transformer trans, ArcAxis arcAxix) {
        super(viewPortHandler, trans);
        this.mArcAxis = arcAxix;

        mStartAngle = midleAngle + changeAngle;
        mSweepAngle = 360f - changeAngle * 2;

        mArcLinePaint = new Paint(mAxisLinePaint);
        mArcLinePaint.setStyle(Paint.Style.STROKE);
        mArcLinePaint.setAntiAlias(true);
        mArcLinePaint.setColor(Color.RED);

        mArcBackGroundPaint = new Paint(mArcLinePaint);
        mArcBackGroundPaint.setStyle(Paint.Style.FILL);
        mArcBackGroundPaint.setColor(Color.YELLOW);

        mGridPaint.setAntiAlias(true);

        mArcPointPaint = new Paint(mArcLinePaint);
        mArcPointPaint.setColor(Color.BLACK);
        mArcPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mArcValuePaint = new TextPaint(mAxisLabelPaint);
        mArcValuePaint.setTextSize(25f);
        mArcValuePaint.setColor(0xff18b1ea);

    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mArcAxis.isDrawLabelsEnabled()) {
            return;
        }

        float angle = mStartAngle;
        float offset = -5;

        float length = Math.max(0, mArcAxis.mEntries.length - 1);
        float step = mSweepAngle / length;
        Paint.FontMetrics font = mAxisLabelPaint.getFontMetrics();

        for (int i = 0; i <= length; i++) {

            PointF p = computePoint(angle, offset);

//            c.drawPoint(p.x, p.y, mArcPointPaint);
            String label = mArcAxis.mEntries[i] + "";
            if (mLabelsFormat != null) {
                label = mLabelsFormat.getFormattedValue(mArcAxis.mEntries[i]);
            }


            mAxisLabelPaint.setTextAlign(getLablesAlign(angle));

            PointF textPoint = computeTextPoint(label, p, angle);

//            c.drawPoint(textPoint.x, textPoint.y, mArcPointPaint);

            c.drawText(label, textPoint.x, textPoint.y, mAxisLabelPaint);
            angle += step;
            if (angle >= 360) {
                angle %= 360;
            }
        }
    }


    public void setLablesFormat(ValueFormatter labelFormat) {
        mLabelsFormat = labelFormat;
    }

    private Paint.Align getLablesAlign(float angle) {

        if (angle >= 90 && angle < 270) {
            return Paint.Align.LEFT;
        } else if (angle == 270) {
            return Paint.Align.CENTER;
        } else {
            return Paint.Align.RIGHT;
        }

//        return Paint.Align.CENTER;
    }

    private PointF computeTextPoint(String text, PointF startPoint, float angle) {
        float labelHeight = Utils.calcTextHeight(mAxisLabelPaint, text);
        float labelWidth = Utils.calcTextWidth(mAxisLabelPaint, text);

//        if (angle >= 90 && angle < 180) {
//            return startPoint;
//        } else if ((angle >= 180 && angle < 270) || angle > 270 && angle <= 360) {
//            return new PointF(startPoint.x, startPoint.y - labelHeight / 2);
//        } else if (angle == 270) {
//            return new PointF(startPoint.x - labelWidth / 2, startPoint.y - labelHeight);
//        } else {
//            return startPoint;
//        }

        float y = (float) (labelHeight * Math.sin(Math.toRadians(angle)));

        if (angle > 0 && angle < 180) {
            y = 0;
        }

        float x = (float) (labelWidth * Math.cos(Math.toRadians(angle)));

//        System.out.println(String.format("angle:%s,y:%s,sin:%s", angle, y, Math.sin(Math.toRadians(angle))));
        PointF point = new PointF(startPoint.x, startPoint.y - y);


        return point;

    }


    @Override
    public void renderGridLines(Canvas c) {

        if (!mArcAxis.isDrawGridLinesEnabled()) {
            return;
        }
        float angle = mStartAngle;
        float offset = -5;

        float length = Math.max(0, mArcAxis.mEntries.length - 1);
        float step = mSweepAngle / length;

        for (int i = 0; i <= length; i++) {


            PointF p1 = computePoint(angle, 0);
            PointF p2 = computePoint(angle, offset);

            c.drawLine(p1.x, p1.y, p2.x, p2.y, mGridPaint);
//            c.drawPoint(p1.x, p1.y, mArcPointPaint);
            angle += step;
        }
    }


    private PointF computePoint(float angle, float offset) {

        float offset_px = Utils.convertDpToPixel(offset);
        double degree = Math.toRadians(angle);
        float x = (float) (Math.cos(degree) * (mRadiusX + offset_px) + mCenterPoint.x);
        float y = (float) (Math.sin(degree) * (mRadiusY + offset_px) + mCenterPoint.y);

        return new PointF(x, y);
    }

    @Override
    public void renderAxisLine(Canvas c) {

        if (!mArcAxis.isDrawAxisLineEnabled()) {
            return;
        }

        c.drawArc(mViewPortHandler.getContentRect(), mStartAngle, mSweepAngle, false, mArcLinePaint);
    }

    public void renderAxisBackgroundColor(Canvas c) {
        c.drawArc(mViewPortHandler.getContentRect(), mStartAngle, mSweepAngle, true, mArcBackGroundPaint);
    }

    @Override
    public void renderLimitLines(Canvas c) {

        float min = mArcAxis.getMin();
        float max = mArcAxis.getMax();

        List<LimitLine> limitLines = mArcAxis.getLimitLines();
        int limitLineCount = limitLines.size();
        float axisRange = mArcAxis.mAxisRange;
        for (int i = 0; i < limitLineCount; i++) {
            float startValue = min;

            if (i != 0) {
                startValue = limitLines.get(i - 1).getLimit();
            }

            float startAngle = (startValue - min) / axisRange * mSweepAngle + mStartAngle;
            float endValue = limitLines.get(i).getLimit();

            if (i == limitLineCount - 1) {
                endValue = max;
            }


            float sweepAngle = (endValue - startValue) / axisRange * mSweepAngle;
            Paint colorPaint = new Paint(mArcBackGroundPaint);
            colorPaint.setColor(limitLines.get(i).getLineColor());

            c.drawArc(mViewPortHandler.getContentRect(), startAngle, sweepAngle, true, colorPaint);

            if (i != limitLineCount - 1) {
                float angle = startAngle + sweepAngle;
                PointF p1 = computePoint(angle, 5);
                PointF p2 = computePoint(angle, -5);

                c.drawLine(p1.x, p1.y, p2.x, p2.y, mGridPaint);
            }
        }


    }

    public void renderArcCenter(Canvas c) {

        c.drawCircle(mCenterPoint.x, mCenterPoint.y, mPointSize, mArcPointPaint);
    }

    public void renderArcValuePoint(Canvas c, float value, float phase) {
        float axisRange = mArcAxis.mAxisRange;
        float targetAngle = (value - mArcAxis.getMin()) / axisRange * mSweepAngle * phase + mStartAngle;
        float a1 = targetAngle + 90;
        float a2 = targetAngle - 90;

        PointF p = computePoint(targetAngle, Utils.convertDpToPixel(10f));
        PointF p1 = Utils.getPosition(mCenterPoint, mPointSize / 2, a1);
        PointF p2 = Utils.getPosition(mCenterPoint, mPointSize / 2, a2);

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p.x, p.y);
        path.lineTo(p1.x, p1.y);

        c.drawPath(path, mArcPointPaint);


    }

    public void renderValueText(Canvas canvas, float value) {
        canvas.save();
        String valString = "";
        if (value != mArcAxis.getMin()) {
            if (mLabelsFormat != null) {
                valString = mLabelsFormat.getFormattedValue(value);
            } else {
                valString = value + "";
            }
        }
        valString += mArcAxis.getUnit();

        Spanned html = Html.fromHtml(valString);
        int textWidth = Utils.calcTextWidth(mArcValuePaint, html.toString());
        int textHeight = Utils.calcTextHeight(mArcValuePaint, html.toString());

        canvas.translate(mCenterPoint.x - textWidth / 2, mCenterPoint.y + textHeight);

        StaticLayout myStaticLayout = new StaticLayout
                (html, mArcValuePaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        myStaticLayout.draw(canvas);

        canvas.restore();

    }


    protected void initParams() {
        mCenterPoint = mViewPortHandler.getContentCenter();
        mRadiusX = (mViewPortHandler.getContentRect().width()) / 2;
        mRadiusY = (mViewPortHandler.getContentRect().height()) / 2;
        mPointSize = Utils.convertDpToPixel(5);
        mArcAxis.mAxisRange = mArcAxis.getMax() - mArcAxis.getMin();

    }


    protected void computeAxisValues(float min, float max) {

        float yMin = min;
        float yMax = max;

        int labelCount = mArcAxis.getLabelCount();
        double range = Math.abs(yMax - yMin);

        if (labelCount == 0 || range <= 0) {
            mArcAxis.mEntries = new float[]{};
            mArcAxis.mEntryCount = 0;
            return;
        }

        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }


        double first = Math.ceil(yMin / interval) * interval;
        double last = Utils.nextUp(Math.floor(yMax / interval) * interval);

        double f;
        int i;
        int n = 0;
        for (f = first; f <= last; f += interval) {
            ++n;
        }

        mArcAxis.mEntryCount = n;

        if (mArcAxis.mEntries.length < n) {
            // Ensure stops contains at least numStops elements.
            mArcAxis.mEntries = new float[n];
        }

        for (f = first, i = 0; i < n; f += interval, ++i) {
            mArcAxis.mEntries[i] = (float) f;
        }

        if (interval < 1) {
            mArcAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mArcAxis.mDecimals = 0;
        }
    }


    public Paint getArcLinePaint() {
        return mArcLinePaint;
    }

    public Paint getArcBackGroundPaint() {
        return mArcBackGroundPaint;
    }

    public Paint getGridPaint() {
        return mGridPaint;
    }

    public Paint getArcPoint() {
        return mArcPointPaint;
    }

    public void setArcCenterSize(float size) {
        mPointSize = size;
    }

    public Paint getArcValuePaint() {
        return mArcValuePaint;
    }

}
