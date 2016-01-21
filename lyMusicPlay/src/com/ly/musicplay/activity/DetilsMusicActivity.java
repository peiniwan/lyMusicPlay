package com.ly.musicplay.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ly.musicplay.R;
import com.ly.musicplay.service.BackgroundService;
import com.ly.musicplay.service.MusicInterface;
import com.ly.musicplay.utils.MediaUtil;
import com.ly.musicplay.utils.MusicListUtils;

/**
 * 播放详情界面
 * 
 * @author Administrator
 * 
 */
public class DetilsMusicActivity extends BaseActivity implements
		OnClickListener, OnSeekBarChangeListener {

	private ImageButton returnMain;// 回到上个界面
	public static TextView detil_name;// 歌曲名
	private TextView detil_current;// 当前进度
	private TextView detil_total;// 总共进度
	private SeekBar detil_seek;// 进度条
	private ImageButton detil_mode;// 播放模式
	private ImageButton detil_pre;// 上一首
	private ImageButton detil_play;// 暂停播放
	private ImageButton detil_next;// 下一首
	private ImageButton detil_star;// 红心
	public static ImageView detil_pic;// 专辑图片

	public MusicInterface mi;// 访问服务的方法
	public MyServiceConn conn;// 绑定服务时要实现的

	// 播放模式的各种状态选择器
	private final int[] modeImage = { R.drawable.player_btn_mode_normal_style,
			R.drawable.player_btn_mode_repeat_one_style,
			R.drawable.player_btn_mode_repeat_all_style,
			R.drawable.player_btn_mode_random_style };

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = msg.getData();
			int duration = bundle.getInt("duration");// 总时长
			int currentPostition = bundle.getInt("currentPosition");// 当前时间
			// 刷新进度条的进度
			detil_seek.setMax(duration);
			detil_seek.setProgress(currentPostition);
			ArrayList<String> list = setPlayInfo(currentPostition / 1000,
					duration / 1000);
			detil_current.setText(list.get(0));
			detil_total.setText(list.get(1));
		};
	};
	private SharedPreferences preferences;
	private int mode;

	// 音量相关
	private SeekBar seekVolumeBar;// 音量进度条
	private PopupWindow popupVolume;
	private AudioManager audioManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detil_music);
		setBehindContentView(R.layout.left_menu);// 这个得写
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);// 取消侧边栏

		Intent intent = new Intent(this, BackgroundService.class);
		MyServiceConn conn = new MyServiceConn();
		startService(intent);// 开启后台播放服务
		bindService(intent, conn, Context.BIND_AUTO_CREATE);

		initView();
		initPopupVolume();// 初始化音量窗口
		preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		mode = preferences.getInt(MainActivity.PREFERENCES_MODE, 0);
		detil_mode.setImageResource(modeImage[mode]);// 一进来设置模式

		// 设置歌曲名和专辑图片
		if (BackgroundService.currMp3Path != null) {
			List<String> songInfo = MusicListUtils.songInfo(this,
					BackgroundService.currMp3Path);
			detil_name.setText(songInfo.get(0) + "-" + songInfo.get(1));
			Bitmap bitmap = MediaUtil.getLargeBitmap(
					BackgroundService.currMp3Path, this);
			detil_pic.setImageBitmap(bitmap);
		}
		// 给服务设置handler
		BackgroundService.setHandler(handler);
		// 设置暂停播放的图片
		if (BackgroundService.mediaPlayer != null) {
			if (BackgroundService.mediaPlayer.isPlaying()) {
				detil_play.setImageResource(R.drawable.player_btn_pause_style);
			} else {
				detil_play.setImageResource(R.drawable.player_btn_play_style);
			}
		}

		SeekBarChange();

		returnMain.setOnClickListener(this);
		detil_pre.setOnClickListener(this);
		detil_play.setOnClickListener(this);
		detil_next.setOnClickListener(this);
		detil_mode.setOnClickListener(this);
		detil_pic.setOnClickListener(this);
	}

	/**
	 * 初始化音量窗口
	 */
	private void initPopupVolume() {
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		View view = LayoutInflater.from(this).inflate(R.layout.volume, null);// 引入窗口配置文件
		popupVolume = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, false);// 创建PopupWindow对象
		seekVolumeBar = (SeekBar) view.findViewById(R.id.pupup_volume_seek);
		seekVolumeBar.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		seekVolumeBar.setOnSeekBarChangeListener(this);
		popupVolume.setBackgroundDrawable(new BitmapDrawable());// 点击外边可消失
		popupVolume.setOutsideTouchable(true);// 设置点击窗口外边窗口消失
	}

	/**
	 * 再次进来设置对应的play按钮的图片
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if (BackgroundService.mediaPlayer != null) {
			if (BackgroundService.mediaPlayer.isPlaying()) {
				detil_play.setImageResource(R.drawable.player_btn_pause_style);
			} else {
				detil_play.setImageResource(R.drawable.player_btn_play_style);
			}
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		returnMain = (ImageButton) findViewById(R.id.detil_return);
		detil_name = (TextView) findViewById(R.id.detil_name);
		detil_current = (TextView) findViewById(R.id.detil_current);
		detil_total = (TextView) findViewById(R.id.detil_total);
		detil_seek = (SeekBar) findViewById(R.id.detil_seek);
		detil_mode = (ImageButton) findViewById(R.id.detil_mode);
		detil_pre = (ImageButton) findViewById(R.id.detil_pre);
		detil_play = (ImageButton) findViewById(R.id.detil_play);
		detil_next = (ImageButton) findViewById(R.id.detil_next);
		detil_star = (ImageButton) findViewById(R.id.detil_star);
		detil_pic = (ImageView) findViewById(R.id.detil_pic);
	}

	/**
	 * 控制条变化歌曲进度跟着变
	 */
	private void SeekBarChange() {
		detil_seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				// 改变播放进度
				mi.seekTo(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
	}

	// 设置当前播放的信息
	private static ArrayList<String> setPlayInfo(int position, int max) {

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
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(now);
		arrayList.add(all);
		return arrayList;
	}

	/**
	 * 点击侦听
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.detil_return:
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.detil_pre:
			mi.per();
			break;
		case R.id.detil_play:
			mi.startPause();
			if (BackgroundService.mediaPlayer.isPlaying()) {
				detil_play.setImageResource(R.drawable.player_btn_pause_style);
			} else {
				detil_play.setImageResource(R.drawable.player_btn_play_style);
			}
			break;
		case R.id.detil_next:
			mi.next();
			break;
		case R.id.detil_mode:
			int modeChange = mi.modeChange(mode);// 调用服务接口的改变模式方法，并返回一个模式
			detil_mode.setImageResource(modeImage[modeChange]);
			preferences.edit()
					.putInt(MainActivity.PREFERENCES_MODE, modeChange).commit();// 把模式保存起来
			break;
		case R.id.detil_pic:
			seekVolumeBar.setProgress(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			popupVolume.showAsDropDown(detil_name);
			break;

		default:
			break;
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
			Log.d("onServiceConnected", "执行了");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	}

	// 下面都是seek的监听必须实现的方法
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.pupup_volume_seek:
			if (fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
			}
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {// 写在这里不知道为什么不行
		// int pro = seekBar.getProgress();
		// // 改变播放进度
		// mi.seekTo(pro);
	}
}
