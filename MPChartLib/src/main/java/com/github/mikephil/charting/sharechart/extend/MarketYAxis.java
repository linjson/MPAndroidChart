
package com.github.mikephil.charting.sharechart.extend;

import com.github.mikephil.charting.components.YAxis;

/**
 * Class representing the y-axis labels settings and its entries. Only use the
 * setter methods to modify it. Do not access public variables directly. Be
 * aware that not all features the YLabels class provides are suitable for the
 * RadarChart. Customizations that affect the value range of the axis need to be
 * applied before setting data for the chart.
 *
 * @author Philipp Jahoda
 */
public class MarketYAxis extends YAxis {

    private float baseValue = Float.NaN;
    private String minText;
    private boolean showBaseValueAndMaxmin;
    private int[] colors;

    public MarketYAxis() {
        super();
    }

    public MarketYAxis(AxisDependency axis) {
        super(axis);
    }

    public void setShowOnlyMax(String minText) {
        setShowOnlyMinMax(true);
        this.minText = minText;
    }

    public String getMintext() {
        return this.minText;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
    }

    public void setShowBaseValueAndMaxmin(boolean show) {
        this.showBaseValueAndMaxmin = show;
    }

    public boolean isShowBaseValueAndMaxmin() {
        return showBaseValueAndMaxmin;
    }

    public void setTextColors(int... color) {
        colors = color;
    }

    public int[] getTextColors() {
        return colors;
    }

    @Override
    public void calculate(float dataMin, float dataMax) {
        super.calculate(dataMin, dataMax);

        if (getBaseValue() == 0 && showBaseValueAndMaxmin) {

            float a = Math.max(Math.abs(mAxisMaximum), Math.abs(mAxisMinimum));
            this.mAxisMaximum = a;
            this.mAxisMinimum = -a;
            this.mAxisRange = Math.abs(this.mAxisMaximum - this.mAxisMinimum);

        }
    }
}
