package com.ly.musicplay.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

public class ReadPager extends BasePager {

	public ReadPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initViews() {
		super.initViews();
		TextView text = new TextView(mActivity);
		text.setText("您没有任何未读消息哦~~");
		text.setTextColor(Color.GRAY);
		text.setTextSize(14);
		text.setGravity(Gravity.CENTER);
		flContent.addView(text);
	}

}
