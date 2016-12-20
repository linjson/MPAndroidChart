package com.github.mikephil.charting.sharechart.extend;

import android.content.Context;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by ljs on 16/8/1.
 */
public abstract class ChartMarkerView extends RelativeLayout {

    public ChartMarkerView(Context context) {
        super(context);

    }

    protected abstract void setupLayoutResource(int layoutResource);




}
