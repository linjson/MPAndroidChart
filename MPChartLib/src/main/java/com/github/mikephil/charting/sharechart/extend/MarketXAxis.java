
package com.github.mikephil.charting.sharechart.extend;

import android.util.SparseArray;

import com.github.mikephil.charting.components.XAxis;

public class MarketXAxis extends XAxis {


    public static int TextPosition_CENTER = 1;
    public static int TextPosition_LEFT = 2;

    public static int XAXISPOSITION_BOTTOM = 3;
    public static int XAXISPOSITION_MIDDLE = 4;


    private SparseArray<String> labels;

    private int textPosition = TextPosition_LEFT;
    private boolean mShowFirstAndLast;

    public SparseArray<String> getShowLabels() {
        return labels;
    }

    public void setShowLabels(SparseArray<String> labels) {
        this.labels = labels;
    }


    public int getTextPosition() {
        return textPosition;

    }


    /**
     * @param position -->TextPosition_CENTER,TextPosition_LEFT
     */
    public void setTextPosition(int position) {
        textPosition = position;
    }


    public boolean isShowFirstAndLast() {
        return mShowFirstAndLast;
    }

    public void setShowFirstAndLast(boolean showFirstAndLast) {
        mShowFirstAndLast = showFirstAndLast;
    }
}
