package com.github.mikephil.charting.sharechart.extend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by ljs on 16/7/25.
 */
public abstract class ArrowMarkerView extends ChartMarkerView {

    public final static int TOP = 1;
    public final static int BOTTOM = 2;

    private final int mBackgroundColor;
    private final float mTriangleSize;
    private final Paint mTranglePaint;
    private final int mTriangleHeight;
    private int mPosition = TOP;


    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public ArrowMarkerView(Context context, int layoutResource, int backgroundColor) {
        super(context);
        mBackgroundColor = backgroundColor;
        mTriangleSize = Utils.convertDpToPixel(10);
        mTriangleHeight = (int) (Math.sin(Math.PI / 3) * mTriangleSize);

        setupLayoutResource(layoutResource);

        mTranglePaint = new Paint();

        mTranglePaint.setColor(mBackgroundColor);
        mTranglePaint.setStyle(Paint.Style.FILL);
    }

    protected void setupLayoutResource(int layoutResource) {

        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);
        inflated.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        // measure(getWidth(), getHeight());

        int width = (int) Math.max(mTriangleSize, inflated.getMeasuredWidth());

        inflated.layout(0, 0, width, inflated.getMeasuredHeight() + mTriangleHeight);
    }

    public void draw(Canvas canvas, float posx, float posy, float viewOffsetX) {

        canvas.translate(posx, posy);
        drawTrangle(canvas, viewOffsetX);

        posx = viewOffsetX - getWidth() / 2;
        posy = mPosition == TOP ? mTriangleHeight : -mTriangleHeight - getHeight();

        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }

    public void setPosition(int pos) {
        mPosition = pos;
    }

    private void drawTrangle(Canvas canvas, float offsetX) {

        Path path = new Path();
        float height = mTriangleHeight;
        path.moveTo(0, 0);
        if (mPosition == TOP) {

            path.lineTo(-mTriangleSize / 2, height);
            path.lineTo(offsetX - getWidth() / 2, height);
            path.lineTo(offsetX - getWidth() / 2, getHeight() + height);
            path.lineTo(offsetX + getWidth() / 2, getHeight() + height);
            path.lineTo(offsetX + getWidth() / 2, height);
            path.lineTo(mTriangleSize / 2, height);
        } else {
            path.lineTo(-mTriangleSize / 2, -height);
            path.lineTo(offsetX - getWidth() / 2, -height);
            path.lineTo(offsetX - getWidth() / 2, -getHeight() - height);
            path.lineTo(offsetX + getWidth() / 2, -getHeight() - height);
            path.lineTo(offsetX + getWidth() / 2, -height);
            path.lineTo(mTriangleSize / 2, -height);
        }

        path.close();
        canvas.drawPath(path, mTranglePaint);

//        canvas.drawRect(new RectF(-getWidth()/2,height,getWidth()/2,getHeight()+height),mTranglePaint);


    }

    public float getTriangleSize() {
        return mTriangleSize;
    }


    public abstract void refreshContent(Entry e, Highlight highlight);
}
