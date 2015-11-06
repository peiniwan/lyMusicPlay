package com.ly.musicplay.service;

import java.io.File;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ly.musicplay.R;
import com.ly.musicplay.activity.DetilsMusicActivity;
import com.ly.musicplay.bean.Music;
import com.ly.musicplay.fragment.ContentFragment;
import com.ly.musicplay.pager.SDPager;
import com.ly.musicplay.utils.MusicListUtils;

/**
 * 后台播放服务
 */
public class BackgroundService extends Service {

	private MusicController mBinder = new MusicController();
	public static MediaPlayer mediaPlayer;
	public static String songName; // 当前播放的歌曲名
	private PhoneReceiver phoneReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean stopService(Intent name) {
		Log.d("BackgroundService", "stopService执行了");
		return super.stopService(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
		// phoneReceiver = new PhoneReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);// 设置拨号动作
		// registerReceiver(phoneReceiver, filter);// 注册广播

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(phoneReceiver);// 取消监听
		Log.d("BackgroundService", "onDestroy执行了");
	}

	/**
	 * 广播
	 * 
	 * @author Administrator
	 * 
	 */
	class PhoneReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				mBinder.startPause();// 不管是传过来的mi,还是mBinder都不行
			} else {
				// 如果是来电
				TelephonyManager tManager = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);
				switch (tManager.getCallState()) {

				case TelephonyManager.CALL_STATE_RINGING:
					mBinder.startPause();// 可以暂停，挂了不会继续唱歌

				case TelephonyManager.CALL_STATE_OFFHOOK:

				case TelephonyManager.CALL_STATE_IDLE:

					break;
				}
			}
		}

	}

	class MusicController extends Binder implements MusicInterface {

		public void play(String mp3Path) {
			Log.d("play", "执行了");
			if (mediaPlayer != null) {
				mediaPlayer.reset(); // 重置多媒体
				try {
					mediaPlayer.setDataSource(mp3Path);// 为多媒体对象设置播放路径
					mediaPlayer.prepare();
					SDPager.mAdapter.notifyDataSetChanged();
					ContentFragment.tv_musicname.setText(setPlayName(mp3Path));// 设置当前播放的歌曲名字
					if (DetilsMusicActivity.detil_name != null) {
						DetilsMusicActivity.detil_name
								.setText(setPlayName(mp3Path));

					}
					getPlayPIC(mp3Path);
					ContentFragment.play.setImageResource(R.drawable.play);// 设置播放暂停的图片
					SDPager.showCustomView();
					mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
						// 准备完成调用此方法
						@Override
						public void onPrepared(MediaPlayer mp) {
							mediaPlayer.start();
							ContentFragment.handler.post(updateThread);// 递归

						}
					});

					mediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {
								public void onCompletion(MediaPlayer arg0) {
									next();// 如果当前歌曲播放完毕,自动播放下一首.
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 发送播放进度
		 */
		Runnable updateThread = new Runnable() {
			public void run() {

				// 获取歌曲总时长
				int duration = mediaPlayer.getDuration();
				// 获取歌曲当前播放进度
				int currentPosition = mediaPlayer.getCurrentPosition();
				Message msg = ContentFragment.handler.obtainMessage();

				// 把进度封装至消息对象中
				Bundle bundle = new Bundle();
				bundle.putInt("duration", duration);
				bundle.putInt("currentPosition", currentPosition);
				msg.setData(bundle);
				ContentFragment.handler.sendMessage(msg);
				// 每次延迟100毫秒再启动线程
				ContentFragment.handler.postDelayed(updateThread, 100);
			}

		};

		public void stop() {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				// 释放占用的资源，此时player对象已经废掉了
				mediaPlayer.release();
				mediaPlayer = null;
				// 取消线程
				ContentFragment.handler.removeCallbacks(updateThread);
			}
		}

		public void next() {
			Log.d("next", "执行了");
			Log.d("BackgroundService", "正在播放的MusicActivity.songNum "
					+ SDPager.songNum);
			if (mediaPlayer != null) {
				if (SDPager.musicUrlList != null) {
					if (SDPager.songNum == (SDPager.musicUrlList.size() - 1)) {
						SDPager.songNum = 0;
					} else {
						SDPager.songNum = SDPager.songNum + 1;
					}
					Log.d("BackgroundService", "判断完后播放的SDPager.songNum "
							+ SDPager.songNum);
					songName = SDPager.musicUrlList.get(SDPager.songNum);
					Log.d("BackgroundService", "songName------" + songName);
					play(songName);
				}
			}

		}

		public void per() {
			Log.d("per", "执行了");
			Log.d("BackgroundService", "正在播放的SDPager.songNum "
					+ SDPager.songNum);
			if (mediaPlayer != null) {
				if (SDPager.musicUrlList != null) {
					if (SDPager.songNum == 0) {
						SDPager.songNum = (SDPager.musicUrlList.size() - 1);
					} else {
						SDPager.songNum = SDPager.songNum - 1;
					}
					Log.d("BackgroundService", "判断完后播放的SDPager.songNum "
							+ SDPager.songNum);
					songName = SDPager.musicUrlList.get(SDPager.songNum);
					Log.d("BackgroundService", "songName------" + songName);
					play(songName);
				}
			}
		}

		public void startPause() {
			Log.d("pause", "执行了");
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					ContentFragment.play.setImageResource(R.drawable.stop);
				} else {
					mediaPlayer.start();
					ContentFragment.play.setImageResource(R.drawable.play);
				}
			}
		}

		public void seekTo(int progress) {
			if (mediaPlayer != null) {
				mediaPlayer.seekTo(progress);
			}
		}

		public String setPlayName(String dataSource) {
			File file = new File(dataSource);// 假设为D:\\mm.mp3
			String name = file.getName();// name=mm.mp3
			int index = name.lastIndexOf(".");// 找到最后一个.
			return songName = name.substring(0, index);// 截取歌名
		}

	}

	public void getPlayPIC(String mp3Path) {
		List<Music> musicList = MusicListUtils
				.getMusicList(getApplicationContext());
		for (int i = 0; i < musicList.size(); i++) {
			if (mp3Path == musicList.get(i).getUrl()) {
				System.out.println(i);
				String albumId = musicList.get(i).getAlbumId();
			}
		}

	}

}
