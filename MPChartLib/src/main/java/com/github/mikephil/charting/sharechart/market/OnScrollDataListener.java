package com.github.mikephil.charting.sharechart.market;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by ljs on 15/11/13.
 */
public interface OnScrollDataListener {
    void onScrollDataSelect(Entry e, Highlight h);
}
