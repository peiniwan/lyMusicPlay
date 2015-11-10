package com.ly.musicplay.activity;

import java.util.ArrayList;

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

import com.ly.musicplay.R;
import com.ly.musicplay.receive.ServiceReceiver;
import com.ly.musicplay.service.BackgroundService;
import com.ly.musicplay.service.MusicInterface;
import com.ly.musicplay.utils.MediaUtil;

public class DetilsMusicActivity extends BaseActivity implements
		OnClickListener, OnSeekBarChangeListener {
	private ImageButton returnMain;
	public static TextView detil_name;
	private TextView detil_current;
	private TextView detil_total;
	private SeekBar detil_seek;
	private ImageButton detil_mode;
	private ImageButton detil_pre;
	private ImageButton detil_play;
	private ImageButton detil_next;
	private ImageButton detil_star;
	public static ImageView detil_pic;

	public MusicInterface mi;// 访问服务的方法
	public MyServiceConn conn;// 绑定服务时要实现的

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

	private SeekBar seekVolumeBar;// 音量进度条
	private PopupWindow popupVolume;
	private AudioManager audioManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detil_music);
		setBehindContentView(R.layout.right_menu);// 设置侧边栏布局
		Intent intent = new Intent(this, BackgroundService.class);
		MyServiceConn conn = new MyServiceConn();
		startService(intent);// 开启后台播放服务
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		initView();
		initPopupVolume();// 初始化音量窗口
		preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		mode = preferences.getInt(MainActivity.PREFERENCES_MODE, 0);
		detil_mode.setImageResource(modeImage[mode]);

		if (BackgroundService.songName != null
				&& BackgroundService.currMp3Path != null) {
			detil_name.setText(BackgroundService.songName);
			Bitmap bitmap = MediaUtil.getLargeBitmap(
					BackgroundService.currMp3Path, this);
			detil_pic.setImageBitmap(bitmap);
		}
		BackgroundService.setHandler(handler);
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
			// detil_name.setText(BackgroundService.songName);
			// System.out.println("DetilsMusicActivity歌名"
			// + BackgroundService.songName);//
			// 这样写BackgroundService.songName在服务里输出的和这里不一样,并且少了.mp3后缀
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
			int modeChange = mi.modeChange(mode);
			detil_mode.setImageResource(modeImage[modeChange]);
			preferences.edit()
					.putInt(MainActivity.PREFERENCES_MODE, modeChange).commit();
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
