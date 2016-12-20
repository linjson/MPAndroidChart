
package com.github.mikephil.charting.sharechart.sar;

import com.github.mikephil.charting.buffer.AbstractBuffer;

public class SarPointBuffer extends AbstractBuffer<ISarDataSet> {

    public SarPointBuffer(int size) {
        super(size);
    }

    @Override
    public void feed(ISarDataSet data) {

        float size = data.getEntryCount() * phaseX;

        for (int i = 0; i < size; i++) {

            SarEntry entry = data.getEntryForIndex(i);
            addForm(entry.getXIndex(), entry.getSar() * phaseY);
        }

        reset();


    }

    protected void addForm(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

}
