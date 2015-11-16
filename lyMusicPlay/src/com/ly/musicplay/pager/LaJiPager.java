package com.ly.musicplay.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

public class LaJiPager extends BasePager {

	public LaJiPager(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		super.initViews();
		TextView text = new TextView(mActivity);
		text.setText("没有~~");
		text.setTextColor(Color.GRAY);
		text.setTextSize(14);
		text.setGravity(Gravity.CENTER);
		flContent.addView(text);
	}

}
