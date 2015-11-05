package com.ly.musicplay.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ly.musicplay.R;
import com.ly.musicplay.receive.ServiceReceiver;
import com.ly.musicplay.service.BackgroundService;
import com.ly.musicplay.view.PushView;

public class DetilsMusicActivity extends Activity implements OnClickListener {
	private ImageButton returnMain;
	private PushView detil_name;
	private TextView detil_current;
	private TextView detil_total;
	private SeekBar detil_seek;
	private ImageButton detil_mode;
	private ImageButton detil_pre;
	private ImageButton detil_play;
	private ImageButton detil_next;
	private ImageButton detil_star;
	private ImageView detil_pic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detil_music);
		initView();
		detil_name.setText(BackgroundService.songName);

		returnMain.setOnClickListener(this);
		detil_pre.setOnClickListener(this);
		detil_play.setOnClickListener(this);
		detil_next.setOnClickListener(this);
	}

	private void initView() {
		returnMain = (ImageButton) findViewById(R.id.detil_return);
		detil_name = (PushView) findViewById(R.id.detil_name);
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
			detil_name.setText(BackgroundService.songName);
			break;
		case R.id.detil_play:
			intent.setAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
			sendBroadcast(intent);
			break;
		case R.id.detil_next:
			intent.setAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
			sendBroadcast(intent);
			detil_name.setText(BackgroundService.songName);
			break;

		default:
			break;
		}

	}
}
