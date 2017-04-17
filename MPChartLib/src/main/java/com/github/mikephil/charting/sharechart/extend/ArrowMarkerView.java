package com.github.mikephil.charting.sharechart.extend;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.sharechart.drawable.ArrowShadowBitmap;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by ljs on 16/7/25.
 */
public abstract class ArrowMarkerView extends ChartMarkerView {

    public final static int TOP = 1;
    public final static int BOTTOM = 2;


    private int mPosition = TOP;
    private ArrowShadowBitmap arrowShaowBitmap;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public ArrowMarkerView(Context context, int layoutResource, int backgroundColor) {
        super(context);

        arrowShaowBitmap = new ArrowShadowBitmap();
        arrowShaowBitmap.setBackgroundColor(backgroundColor);
        arrowShaowBitmap.setTriangleSize(Utils.convertDpToPixel(10));


        setupLayoutResource(layoutResource);


    }

    protected void setupLayoutResource(int layoutResource) {

        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);
        inflated.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));


        int width = (int) Math.max(arrowShaowBitmap.getTriangleSize(), inflated.getMeasuredWidth());

        inflated.layout(0, 0, width, inflated.getMeasuredHeight() + arrowShaowBitmap.getTriangleHeight());


    }

    public void draw(Canvas canvas, float posx, float posy, float viewOffsetX) {

        canvas.translate(posx, posy);
        if (arrowShaowBitmap.getWidth() != getWidth()) {
            arrowShaowBitmap.initBitmapSize(getWidth(), getHeight());
        }

        arrowShaowBitmap.drawTrangle(canvas, viewOffsetX);
        int triangleHeight = arrowShaowBitmap.getTriangleHeight();

        posx = viewOffsetX - getWidth() / 2;
        posy = mPosition == TOP ? triangleHeight : -triangleHeight - getHeight();

        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }


    public void setPosition(int pos) {
        arrowShaowBitmap.setPosition(pos);
        mPosition = pos;
    }

    public float getTriangleSize() {
        return arrowShaowBitmap.getTriangleSize();
    }

    public void setBorderColor(int color) {
        arrowShaowBitmap.setBorderColor(color);
    }

    public abstract void refreshContent(Entry e, Highlight highlight, IValueFormatter formatter);
}
