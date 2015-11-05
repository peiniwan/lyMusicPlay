package com.ly.musicplay.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * 
 * 自定义效果-由下往上动态滑动文字动画(为实现该效果耽误了较多时间) 实现动态刷新效果
 */
public class PushView extends TextSwitcher implements ViewSwitcher.ViewFactory {

	private int index;
	private int size;

	private AttributeSet attrs;
	private ArrayList<String> arrays;// 字符串集

	public PushView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public PushView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.attrs = attrs;
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		index = 0;
		arrays = new ArrayList<String>();
		setFactory(this);
		setInAnimation(animIn());
		setOutAnimation(animOut());
	}

	/**
	 * 进入动画
	 * 
	 * @return TranslateAnimation
	 */
	private Animation animIn() {
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, -0.0f);
		anim.setDuration(1500);
		anim.setInterpolator(new LinearInterpolator());
		return anim;
	}

	/**
	 * 离开动画
	 * 
	 * @return TranslateAnimation
	 */
	private Animation animOut() {
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f);
		anim.setDuration(1500);
		anim.setInterpolator(new LinearInterpolator());
		return anim;
	}

	// 这里返回的TextView，就是我们看到的View
	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		MarqueeTextView t = new MarqueeTextView(getContext(), attrs);
		t.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
		return t;// XML可以直接引用TextView的属性使用，加上LayoutParams才能垂直居中
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		try {
			removeCallbacks(runnable);// 页面销毁时定要移除
			super.onDetachedFromWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置字符串集，并开启动画效果
	 * 
	 * @param texts
	 *            字符串集
	 */
	public void setTextList(ArrayList<String> texts) {
		removeCallbacks(runnable);
		this.index = 0;
		this.size = texts.size();
		this.arrays.clear();
		this.arrays = texts;
		setText(null);
		postDelayed(runnable, 500);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			setText(arrays.get(index));
			if (size > 1) {
				index = (index == size - 1) ? 0 : index + 1;
				postDelayed(this, 5000);
			}
		}
	};

	/**
	 * 目的只有一个，就是永久获得焦点维持跑马灯效果，可惜是马上会执行跑马灯效果，边由下往上动画的时候跑马灯也看到了，
	 * 其API没有提供修改延迟启动的时间的方法，所以不能完美的实现了，而且其效果也不可以随意开关
	 * 
	 * @author CWD
	 * @version 2013.07.18 v1.0
	 */
	private class MarqueeTextView extends TextView {

		public MarqueeTextView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		public MarqueeTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		public boolean isFocused() {
			return true;// 永久获得焦点
		}
	}

}