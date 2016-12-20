package com.github.mikephil.charting.sharechart.market;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.Animation;

import com.github.mikephil.charting.utils.Utils;


/**
 * Created by ljs on 15/12/23.
 */
public class ShinePointView extends View implements ValueAnimator.AnimatorUpdateListener {
    private int size;
    private Paint pan;
    private float radius;
    private ObjectAnimator animator;
    private float alpha = 1;
    private int color = 0x71A3C8;
    private Animation anim;

    public ShinePointView(Context context, int color) {
        super(context);
        this.color = color;
        init();
    }


    private void init() {
        size = (int) Utils.convertDpToPixel(6);
        radius = size / 2;

        pan = new Paint();
        pan.setAntiAlias(false);
        pan.setColor(color);
        pan.setStyle(Paint.Style.FILL);

        animator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f);

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(this);
        animator.setDuration(800);


//        anim = new ScaleAnimation(1, 0.5f, 1, 0.5f, radius, radius);
//        anim.setDuration(800);
//        anim.setRepeatCount(ValueAnimator.INFINITE);
//        anim.setRepeatMode(ValueAnimator.REVERSE);


//        setAnimation(anim);

//        post(new Runnable() {
//            @Override
//            public void run() {
//                animator.start();
//            }
//        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        pan.setAlpha((int) (255 * getAlpha()));
        int value = (int) (radius * getAlpha());
        canvas.drawCircle(radius, radius, value, pan);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        postInvalidate();
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }

    public void refreshView() {
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }


    public void startAnimation() {
        animator.start();
    }
}
