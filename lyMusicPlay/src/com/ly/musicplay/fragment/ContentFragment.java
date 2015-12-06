package com.ly.musicplay.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ly.musicplay.R;
import com.ly.musicplay.activity.DetilsMusicActivity;
import com.ly.musicplay.pager.ArrtistPager;
import com.ly.musicplay.pager.BasePager;
import com.ly.musicplay.pager.SDPager;
import com.ly.musicplay.service.BackgroundService;
import com.ly.musicplay.service.MusicInterface;
import com.ly.musicplay.utils.MediaUtil;
import com.ly.musicplay.utils.MusicListUtils;
import com.ly.musicplay.view.HorizontalViewPager;

/**
 * 音乐中心
 * 
 * @author Administrator
 * 
 */
public class ContentFragment extends BaseFragment implements OnClickListener {

	/**
	 * 控件
	 */
	private RadioGroup rgGroup;
	private HorizontalViewPager mViewPager;
	public static LinearLayout ly;// 控制音乐的布局，在某些页面隐藏
	public ImageView pre; // 定义上一首歌按钮控件
	public static ImageView play; // 定义播放按钮控件
	public ImageView stop; // 定义停止按钮控件
	public ImageView next; // 定义下一首歌按钮控件
	public static SeekBar seekbar; // 定义进度条控件
	public static TextView tv_progress;// 显示播放时长和正在播放的进度
	public static TextView tv_musicname; // 显示正在播放的歌曲名称，在服务里设置
	public static ImageView content_iv;// 专辑图片
	public static TextView tv_musicartist;// 歌手

	private ArrayList<BasePager> mPagerList;// 存放网络歌曲和本地歌曲页面
	private static Intent intent;
	public static MusicInterface mi;// 访问服务的方法
	public static MyServiceConn conn;// 绑定服务时要实现的

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = msg.getData();
			int duration = bundle.getInt("duration");// 总时长
			int currentPostition = bundle.getInt("currentPosition");// 当前时间
			// 刷新进度条的进度
			seekbar.setMax(duration);
			seekbar.setProgress(currentPostition);
			tv_progress.setText(setPlayInfo(currentPostition / 1000,
					duration / 1000));
		};
	};

	/**
	 * 初始化布局
	 */
	@Override
	public View initViews() {
		View v = View.inflate(mActivity, R.layout.fragment_content, null);// 填充fragment
		mViewPager = (HorizontalViewPager) v.findViewById(R.id.vp_content);
		rgGroup = (RadioGroup) v.findViewById(R.id.rg_group);
		ly = (LinearLayout) v.findViewById(R.id.ly);
		tv_musicname = (TextView) v.findViewById(R.id.tv_musicname);
		tv_musicartist = (TextView) v.findViewById(R.id.tv_musicartist);
		tv_progress = (TextView) v.findViewById(R.id.tv_progress);
		seekbar = (SeekBar) v.findViewById(R.id.sb);

		pre = (ImageView) v.findViewById(R.id.pre); // 实例化上一首歌的按钮控件
		play = (ImageView) v.findViewById(R.id.play); // 实例化播放的按钮控件
		next = (ImageView) v.findViewById(R.id.next); // 实例化下一首歌的按钮控件
		content_iv = (ImageView) v.findViewById(R.id.album_pic);

		pre.setOnClickListener(this); // 设置上一首歌的监听器
		play.setOnClickListener(this); // 设置播放按钮的监听器
		next.setOnClickListener(this); // 设置下一首歌按钮的监听器
		ly.setOnClickListener(this);
		return v;
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void initData() {

		intent = new Intent(mActivity, BackgroundService.class);
		conn = new MyServiceConn();
		mActivity.startService(intent);// 开启后台播放服务
		mActivity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
		BackgroundService.setHandler(handler);

		rgGroup.check(R.id.rb_sd);// 让RedioButton默认勾选第一页
		mPagerList = new ArrayList<BasePager>();// 存放viewpager

		// 将viewpager加入list
		mPagerList.add(new SDPager(mActivity));
		mPagerList.add(new ArrtistPager(mActivity));

		mViewPager.setAdapter(new ContentAdapter());// 设置viewpager适配器

		// 监听RadioGroup的选择事件
		rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sd:
					// mViewPager.setCurrentItem(0);// 设置当前页面
					mViewPager.setCurrentItem(0, false);// 去掉切换页面的动画
					ly.setVisibility(View.VISIBLE);// 显示播放管理
					break;
				case R.id.rb_http:
					mViewPager.setCurrentItem(1, false);// 设置当前页面
					ly.setVisibility(View.GONE);// 隐藏播放管理
					break;
				default:
					break;
				}
			}
		});
		// 界面会预加载三页，浪费流量，所以只在当前页面初始化
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			// 此方法是页面跳转完后得到调用，arg0是当前选中的页面的Position（位置编号）
			@Override
			public void onPageSelected(int arg0) {

				if (mPagerList.get(arg0) == null)// 只是不想执行下面的语句，因为只在页面变化的时候滑动会很卡，不知道为什么
					mPagerList.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据

				switch (arg0) {
				case 0:
					rgGroup.check(R.id.rb_sd);

					break;
				case 1:
					rgGroup.check(R.id.rb_http);
					break;

				default:
					break;
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		// 初始化首页数据,得写这一步要不然一进去不显示页面了
		mPagerList.get(0).initData();
		mPagerList.get(1).initData();
		// 如果完全退出重新进入的时候图片不显示了，在这里补上
		if (BackgroundService.currMp3Path != null) {
			content_iv.setImageBitmap(MediaUtil.getSamllBitmap(
					BackgroundService.currMp3Path, mActivity)); // 设置首页的歌曲专辑
		}

	}

	class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BasePager pager = mPagerList.get(position);
			container.addView(pager.mRootView);
			return pager.mRootView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	/**
	 * 连接服务成功，此方法调用
	 * 
	 * @author Administrator
	 * 
	 */
	class MyServiceConn implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mi = (MusicInterface) service;

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	}

	/**
	 * 控制按钮的监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pre:
			mi.per();

			break;

		case R.id.play:
			mi.startPause();
			break;

		case R.id.next:
			mi.next();
			break;

		case R.id.ly:
			Intent in = new Intent(mActivity, DetilsMusicActivity.class);
			startActivity(in);
			break;
		}

	}

	// 设置当前播放的信息
	private static String setPlayInfo(int position, int max) {

		int pMinutes = 0;
		while (position >= 60) {
			pMinutes++;
			position -= 60;
		}
		String now = (pMinutes < 10 ? "0" + pMinutes : pMinutes) + ":"
				+ (position < 10 ? "0" + position : position);

		int mMinutes = 0;
		while (max >= 60) {
			mMinutes++;
			max -= 60;
		}
		String all = (mMinutes < 10 ? "0" + mMinutes : mMinutes) + ":"
				+ (max < 10 ? "0" + max : max);

		return now + " / " + all;
	}

	// 获取sd页
	public SDPager getSDPager() {
		return (SDPager) mPagerList.get(0);
	}

	/**
	 * 设置再次进来显示的歌名
	 */
	@Override
	public void onStart() {
		super.onStart();
		// 如果使用服务接口在这个类的initdate来调这个方法的时候会空指针，不知道为什么，由于进了歌曲页面再出来进度条就不走了，所以这样写
		BackgroundService.setHandler(handler);
		if (BackgroundService.currMp3Path != null) {
			List<String> songInfo = MusicListUtils.songInfo(mActivity,
					BackgroundService.currMp3Path);
			tv_musicname.setText(songInfo.get(1));// 设置当前播放的歌曲名字
			tv_musicartist.setText(songInfo.get(0));
		}
	}

	/**
	 * 音乐中心 onDestroy时
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("ContentFragment", "onDestroy执行了");
	}

	/**
	 * 退出程序的操作
	 */
	public static void stop() {
		mi.stop();
		seekbar.setProgress(0);
		seekbar.setMax(0);
		mActivity.unregisterReceiver(SDPager.receiver);// 取消注册通知栏，但是还有报很多错误
		SDPager.manager.cancel(1);
		mActivity.unbindService(conn);
		mActivity.stopService(intent);
	}
}
