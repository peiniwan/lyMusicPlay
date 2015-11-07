package com.ly.musicplay.activity;

import java.util.ArrayList;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ly.musicplay.R;
import com.ly.musicplay.fragment.ContentFragment;
import com.ly.musicplay.receive.ServiceReceiver;
import com.ly.musicplay.service.BackgroundService;
import com.ly.musicplay.utils.MediaUtil;

public class DetilsMusicActivity extends Activity implements OnClickListener {
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
			// detil_total.setText(setPlayInfo(currentPostition / 1000,
			// duration / 1000));
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detil_music);
		initView();

		detil_name.setText(BackgroundService.songName);
		Bitmap bitmap = MediaUtil.getLargeBitmap(BackgroundService.currMp3Path,
				this);
		detil_pic.setImageBitmap(bitmap);
		BackgroundService.setHandler(handler);
		SeekBarChange();
		returnMain.setOnClickListener(this);
		detil_pre.setOnClickListener(this);
		detil_play.setOnClickListener(this);
		detil_next.setOnClickListener(this);
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
				ContentFragment.mi.seekTo(progress);
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
			intent.setAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PER);
			sendBroadcast(intent);
			// detil_name.setText(BackgroundService.songName);
			// System.out.println("DetilsMusicActivity歌名"
			// + BackgroundService.songName);//
			// 这样写BackgroundService.songName在服务里输出的和这里不一样,并且少了.mp3后缀
			break;
		case R.id.detil_play:
			intent.setAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
			sendBroadcast(intent);
			if (BackgroundService.mediaPlayer.isPlaying()) {
				detil_play.setImageResource(R.drawable.player_btn_play_style);
			} else {
				detil_play.setImageResource(R.drawable.player_btn_pause_style);
			}

			break;
		case R.id.detil_next:
			intent.setAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
			sendBroadcast(intent);
			break;

		default:
			break;
		}

	}
}
