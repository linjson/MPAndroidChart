package com.github.mikephil.charting.sharechart.extend;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by ljs on 16/8/1.
 */
public abstract class FixedMarkerView extends ChartMarkerView {


    private final int mBackgroundColor;


    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public FixedMarkerView(Context context, int layoutResource, int backgroundColor) {
        super(context);
        mBackgroundColor = backgroundColor;
        setBackgroundColor(mBackgroundColor);
        setupLayoutResource(layoutResource);

    }

    protected void setupLayoutResource(int layoutResource) {

        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);
        inflated.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
    }

    public void draw(Canvas canvas, float posx, float posy) {


        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }


    public abstract void refreshContent(String xVal, String[] labels, Entry[] entries, Highlight highlight);

}
