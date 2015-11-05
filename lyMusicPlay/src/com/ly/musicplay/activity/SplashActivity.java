package com.ly.musicplay.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ly.musicplay.R;

/**
 * 闪屏页
 * 
 * @author Administrator
 * 
 */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aplash);
		Handler handler = new Handler();
		// 重写进程里面的run方法，当启动进程是就会主动条用run方法
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				SplashActivity.this.startActivity(intent);
				// 实现跳转之后结束MainActivity控件,否则返回键会返回这个界面
				SplashActivity.this.finish();
			}

		};
		handler.postDelayed(runnable, 2000);// 调用进程延迟2秒
	}

}
