package com.ly.musicplay.service;

import java.io.File;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ly.musicplay.R;
import com.ly.musicplay.activity.DetilsMusicActivity;
import com.ly.musicplay.activity.MainActivity;
import com.ly.musicplay.fragment.ContentFragment;
import com.ly.musicplay.pager.SDPager;
import com.ly.musicplay.utils.MediaUtil;

/**
 * 后台播放服务
 */
public class BackgroundService extends Service {

	private MusicController mBinder = new MusicController();
	public static MediaPlayer mediaPlayer;
	public static String songName; // 当前播放的歌曲名
	private PhoneReceiver phoneReceiver;
	private static Handler mHandler;
	public static String currMp3Path;// 当前播放歌曲路径

	private final int MODE_NORMAL = 0;// 顺序播放，放到最后一首停止
	private final int MODE_REPEAT_ONE = 1;// 单曲循环
	private final int MODE_REPEAT_ALL = 2;// 全部循环
	private final int MODE_RANDOM = 3;// 随即播放
	SharedPreferences sharedPreferences;
	int mode;// 播放 模式

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferences = getSharedPreferences(MainActivity.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		mode = sharedPreferences.getInt(MainActivity.PREFERENCES_MODE, 0);// 播放模式默认是顺序播放
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}

		// 电话来去电广播
		phoneReceiver = new PhoneReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);// 设置拨号动作
		registerReceiver(phoneReceiver, filter);// 注册广播
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(phoneReceiver);// 取消监听
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
				mBinder.startPause();// 不管是传过来的mi,还是mBinder都不行，挂了不继续唱
				// mediaPlayer.pause();
			} else {
				// 如果是来电
				TelephonyManager tManager = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);// 电话系统服务
				if (tManager.getCallState() == TelephonyManager.CALL_STATE_RINGING
						&& mediaPlayer != null && mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				}
			}
		}

	}

	/**
	 * 音乐操作管理
	 * 
	 * @author Administrator
	 * 
	 */
	class MusicController extends Binder implements MusicInterface {

		public void play(String mp3Path) {
			currMp3Path = mp3Path;
			if (mediaPlayer != null) {
				mediaPlayer.reset(); // 重置多媒体
				try {
					mediaPlayer.setDataSource(mp3Path);// 为多媒体对象设置播放路径
					mediaPlayer.prepare();
					SDPager.mAdapter.notifyDataSetChanged();

					String PlayName = setPlayName(mp3Path);// 把路径截取成歌名
					String[] split = PlayName.split("-");
					ContentFragment.tv_musicname.setText(split[1]);// 设置当前播放的歌曲名字
					ContentFragment.tv_musicartist.setText(split[0]);// 设置当前播放的歌手名字

					// 设置play界面的专辑图片和歌曲名字
					if (DetilsMusicActivity.detil_name != null
							&& DetilsMusicActivity.detil_pic != null) {
						DetilsMusicActivity.detil_name
								.setText(setPlayName(mp3Path));
						DetilsMusicActivity.detil_pic.setImageBitmap(MediaUtil
								.getLargeBitmap(mp3Path,
										getApplicationContext()));
					}

					ContentFragment.content_iv.setImageBitmap(MediaUtil
							.getSamllBitmap(mp3Path, getApplicationContext())); // 设置首页的歌曲专辑
					ContentFragment.play
							.setImageResource(R.drawable.main_btn_play);// 设置播放暂停的图片
					SDPager.showCustomView();// 再次刷新通知界面

					mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
						// 准备完成调用此方法
						@Override
						public void onPrepared(MediaPlayer mp) {
							mediaPlayer.start();
							mHandler.post(updateThread);// 递归
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
				Message msg = mHandler.obtainMessage();

				// 把进度封装至消息对象中
				Bundle bundle = new Bundle();
				bundle.putInt("duration", duration);
				bundle.putInt("currentPosition", currentPosition);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
				// 每次延迟100毫秒再启动线程
				mHandler.postDelayed(updateThread, 100);
			}

		};

		public void stop() {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				// 释放占用的资源，此时player对象已经废掉了
				mediaPlayer.release();
				mediaPlayer = null;
				// 取消线程
				mHandler.removeCallbacks(updateThread);
			}
		}

		public void next() {
			String songName = nextSong();
			if (songName != null) {
				play(songName);
			}
		}

		public void per() {
			String songName = perSong();
			play(songName);
		}

		public int modeChange(int mode) {
			mode = sharedPreferences.getInt(MainActivity.PREFERENCES_MODE, 0);// 调用的时候判断当前的模式
			if (mode < MODE_RANDOM) {
				mode++;
			} else {
				mode = MODE_NORMAL;
			}
			switch (mode) {
			case MODE_NORMAL:
				Toast.makeText(getApplicationContext(), "顺序播放",
						Toast.LENGTH_SHORT).show();
				break;

			case MODE_REPEAT_ONE:
				Toast.makeText(getApplicationContext(), "单曲循环",
						Toast.LENGTH_SHORT).show();
				break;

			case MODE_REPEAT_ALL:
				Toast.makeText(getApplicationContext(), "全部循环",
						Toast.LENGTH_SHORT).show();
				break;

			case MODE_RANDOM:
				Toast.makeText(getApplicationContext(), "随机播放",
						Toast.LENGTH_SHORT).show();
				break;
			}
			return mode;
		}

		public void startPause() {
			Log.d("pause", "执行了");
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					ContentFragment.play
							.setImageResource(R.drawable.main_btn_pause);// 这个起作用
					SDPager.remoteViews.setImageViewResource(
							R.id.paly_pause_music, R.drawable.music_pause);// 这个不起作用
				} else {
					mediaPlayer.start();
					ContentFragment.play
							.setImageResource(R.drawable.main_btn_play);
					SDPager.remoteViews.setImageViewResource(
							R.id.paly_pause_music, R.drawable.music_play);
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

	/**
	 * 设置handler，给进度条发送消息用
	 * 
	 * @param handler
	 */
	public static void setHandler(Handler handler) {
		mHandler = handler;
	}

	/**
	 * 上一首，根据模式返回对应的歌曲路径
	 * 
	 * @return
	 */
	public String perSong() {
		if (mediaPlayer != null) {
			if (SDPager.musicUrlList != null) {
				mode = sharedPreferences.getInt(MainActivity.PREFERENCES_MODE,
						0);
				switch (mode) {
				case MODE_NORMAL:
					if (SDPager.songNum != 0)
						SDPager.songNum = SDPager.songNum - 1;
					break;
				case MODE_REPEAT_ONE:
					break;
				case MODE_REPEAT_ALL:
					if (SDPager.songNum == 0) {
						SDPager.songNum = (SDPager.musicUrlList.size() - 1);
					} else {
						SDPager.songNum = SDPager.songNum - 1;
					}
					break;
				case MODE_RANDOM:
					SDPager.songNum = (int) (Math.random() * SDPager.musicUrlList
							.size());
					break;
				default:
					break;
				}
				songName = SDPager.musicUrlList.get(SDPager.songNum);
			}
		}
		return songName;

	}

	public String nextSong() {
		if (mediaPlayer != null) {
			if (SDPager.musicUrlList != null) {
				mode = sharedPreferences.getInt(MainActivity.PREFERENCES_MODE,
						0);
				switch (mode) {
				case MODE_NORMAL:
					if (SDPager.songNum != (SDPager.musicUrlList.size() - 1)) {
						SDPager.songNum = SDPager.songNum + 1;
					} else {
						Toast.makeText(getApplicationContext(), "现在最后一首歌曲了哦", 0)
								.show();
					}
					break;
				case MODE_REPEAT_ONE:
					break;
				case MODE_REPEAT_ALL:
					if (SDPager.songNum == SDPager.musicUrlList.size() - 1) {
						SDPager.songNum = 0;
					} else {
						SDPager.songNum = SDPager.songNum + 1;
					}
					break;
				case MODE_RANDOM:
					SDPager.songNum = (int) (Math.random() * SDPager.musicUrlList
							.size());
					break;
				default:
					break;
				}
				songName = SDPager.musicUrlList.get(SDPager.songNum);
			}
		}
		return songName;
	}
}
