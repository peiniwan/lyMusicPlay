package com.ly.musicplay.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusedTextView extends TextView {


	// 有style样式的话会走此方法
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 有属性时走此方法
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 用代码new对象时,走此方法
	public FocusedTextView(Context context) {
		super(context);
	}

	/**
	 * 表示有没有获取焦点
	 * 
	 * 跑马灯要运行,首先调用此函数判断是否有焦点,是true的话,跑马灯才会有效果 所以我们不管实际上textview有没有焦点,
	 * 我们都强制返回true, 让跑马灯认为有焦点
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
}
