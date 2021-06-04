package com.agrodrip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.agrodrip.R;

import org.jetbrains.annotations.NotNull;

public class IntroViewPagerAdapter extends PagerAdapter {

    private int[] img1;
    private String[] introTitle;
    private String[] introDesc;
    private Context context;

    public IntroViewPagerAdapter(Context context, int[] img, String[] introTitle, String[] introDesc) {
        this.context = context;
        this.introTitle = introTitle;
        this.img1 = img;
        this.introDesc = introDesc;
    }

    @Override
    public int getCount() {
        return img1.length;
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == object;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View itemview = inflater.inflate(R.layout.raw_intro_item, container, false);
        ImageView img = itemview.findViewById(R.id.ima1);
        AppCompatTextView tv_order = itemview.findViewById(R.id.tv_order);
        AppCompatTextView tvTitleContent = itemview.findViewById(R.id.tvTitleContent);
        img.setImageResource(img1[position]);
        tv_order.setText(introTitle[position]);
        tvTitleContent.setText(introDesc[position]);
        container.addView(itemview);
        return itemview;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        container.removeView((CardView) object);
    }
}