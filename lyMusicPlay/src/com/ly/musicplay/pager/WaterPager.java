package com.ly.musicplay.pager;

import android.app.Activity;
import android.view.View;

import com.ly.musicplay.R;

public class WaterPager extends BasePager {

	public WaterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initViews() {
		super.initViews();
		View view = View.inflate(mActivity, R.layout.water_pager, null);
		flContent.addView(view);
	}

}
