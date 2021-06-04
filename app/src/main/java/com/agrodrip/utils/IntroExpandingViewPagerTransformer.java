package com.agrodrip.utils;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class IntroExpandingViewPagerTransformer implements ViewPager.PageTransformer {

    private static final float MAX_SCALE = 1.0f;
    private static final float MIN_SCALE = 0.8f;

    @Override
    public void transformPage(View page, float position) {

        position = position < -1 ? -1 : position;
        position = position > 1 ? 1 : position;

        float tempScale = position < 0 ? 1 + position : 1 - position;

        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        float scaleValue = MIN_SCALE + tempScale * slope;
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);
    }
}