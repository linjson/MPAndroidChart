package com.github.mikephil.charting.sharechart.extend;

import android.view.ViewParent;

/**
 * Created by ljs on 16/7/18.
 */
public class ChartUtils {

    public static ViewParent findParent(ViewParent parent, String parentName) {

        if (parent == null) {
            return null;
        }

        if (parent.getClass().getName().equals(parentName)) {
            return parent;
        } else {

            return findParent(parent.getParent(), parentName);
        }

    }
}
