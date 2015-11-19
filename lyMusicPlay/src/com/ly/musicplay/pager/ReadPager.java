package com.ly.musicplay.pager;

import com.ly.musicplay.R;
import com.ly.musicplay.view.MyRingWave;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class ReadPager extends BasePager {

	public ReadPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initViews() {
		super.initViews();
		View view = View.inflate(mActivity, R.layout.water_pager, null);
		flContent.addView(view);
	}

}
