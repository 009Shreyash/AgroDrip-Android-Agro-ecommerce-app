package com.agrodrip.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;


public class UserEditText extends TextInputEditText {

	public UserEditText(Context context) {
		super(context);
		this.setTypeface(Utils.getFont(context, Integer.parseInt(this.getTag().toString())));
	}

	public UserEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setTypeface(Utils.getFont(context, Integer.parseInt(this.getTag().toString())));
	}

	public UserEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(Utils.getFont(context, Integer.parseInt(this.getTag().toString())));
	}

}
