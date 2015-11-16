package com.ly.musicplay.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 用代码写状态选择器和shape
 * 
 * @author Administrator
 * 
 */
public class DrawableUtils {
	public static GradientDrawable createShape(int color) {
		GradientDrawable drawable = new GradientDrawable();// 相当于shape
		drawable.setCornerRadius(UiUtils.dip2px(5));// 设置4个角的弧度
		drawable.setColor(color);// 设置颜色
		return drawable;
	}

	public static StateListDrawable createSelectorDrawable(
			Drawable pressedDrawable, Drawable normalDrawable) {

		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[] { android.R.attr.state_pressed },
				pressedDrawable);// 按下显示的图片
		stateListDrawable.addState(new int[] {}, normalDrawable);// 抬起显示的图片
		return stateListDrawable;

	}
}
