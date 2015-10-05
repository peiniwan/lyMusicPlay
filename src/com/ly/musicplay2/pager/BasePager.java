package com.ly.musicplay2.pager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ly.musicplay2.R;
import com.ly.musicplay2.service.MusicInterface;

/**
 * 音乐中心viewpager基类
 * 
 * @author Administrator
 * 
 */
public class BasePager {
	public static Activity mActivity;

	public View mRootView;

	public FrameLayout flContent;

	public BasePager(Activity activity) {
		mActivity = activity;
		initViews();//在创建的时候就初始化布局
	}

	/**
	 * 初始化布局
	 */
	public void initViews() {
		mRootView = View.inflate(mActivity, R.layout.base_pager, null);
		flContent = (FrameLayout) mRootView.findViewById(R.id.fl_content);

	}

	/**
	 * 初始化数据
	 */
	public void initData() {

	}

}
