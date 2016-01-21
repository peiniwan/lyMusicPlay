package com.ly.musicplay.activity;

import android.os.Bundle;
import android.util.Log;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ly.musicplay.utils.ActivityCollector;
import com.umeng.analytics.MobclickAgent;

/**
 * activity基类
 * 
 * @author Administrator
 * 
 */
public class BaseActivity extends SlidingFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("BaseActivity", getClass().getSimpleName());
		ActivityCollector.addActivity(this);
	}

	// 表明将一个马上要销毁的活动从活动管理器里移除
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
