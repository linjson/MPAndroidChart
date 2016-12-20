package com.github.mikephil.charting.dashboard;

import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.renderer.LegendRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljs on 15/7/3.
 */
public class ArcLegendRender extends LegendRenderer {


    public ArcLegendRender(ViewPortHandler viewPortHandler, Legend legend) {
        super(viewPortHandler, legend);
    }


    public void computeLegend(List<LimitLine> data) {


        if (data == null || data.size() == 0) {
            return;
        }

        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();


        int count = data.size();

        for (int i = 0; i < count; i++) {
            LimitLine limitLine = data.get(i);
            labels.add(limitLine.getLabel());
            colors.add(limitLine.getLineColor());
        }


        mLegend.setComputedColors(colors);
        mLegend.setComputedLabels(labels);


        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        // calculate all dimensions of the mLegend
        mLegend.calculateDimensions(mLegendLabelPaint, mViewPortHandler);
    }
}
