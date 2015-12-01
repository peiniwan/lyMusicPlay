package com.ly.musicplay.pager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.ly.musicplay.R;
import com.ly.musicplay.activity.MainActivity;
import com.ly.musicplay.bean.Music;
import com.ly.musicplay.fragment.ContentFragment;
import com.ly.musicplay.receive.ServiceReceiver;
import com.ly.musicplay.service.BackgroundService;
import com.ly.musicplay.utils.MediaUtil;
import com.ly.musicplay.utils.MusicListUtils;

/**
 * 本地歌曲页面
 * 
 * @author Administrator
 * 
 */
public class SDPager extends BasePager {

	private List<String> musicNameList;// 歌曲名称，填充adapter
	public static List<String> musicArrtistList;// 歌手名称
	public static List<String> musicUrlList;// 存放找到的所有mp3的全路径
	public static int songNum; // 当前播放的歌曲在List中的下标
	public static MusicAdapter mAdapter;// 歌曲列表的adapter
	private ListView lv_sd;
	private SharedPreferences sharedPreferences;
	/**
	 * 通知栏
	 */
	public static ServiceReceiver receiver;// 通知栏广播
	public static NotificationManager manager;// 通知栏管理器
	public static TextView title_music_name;// 通知歌曲名字
	public static RemoteViews remoteViews;// 通知栏view对象

	public SDPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("SDPager的initData");
		lv_sd = new ListView(mActivity);
		// fastScrollEnabled
		lv_sd.setFastScrollEnabled(true);
		lv_sd.setBackgroundResource(R.drawable.sdpager);
		sharedPreferences = mActivity.getSharedPreferences(
				MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);

		// 通知栏
		receiver = new ServiceReceiver();// 通知的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PER);
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
		mActivity.registerReceiver(receiver, intentFilter);
		manager = (NotificationManager) mActivity
				.getSystemService(Context.NOTIFICATION_SERVICE);

		musicUrlList = new ArrayList<String>();// 创建播放歌曲的全路径的list
		musicNameList = new ArrayList<String>();// 歌曲名称，填充adapter
		musicArrtistList = new ArrayList<String>();// 歌手名称
		MusicList();// 扫描SD卡歌名，并设置listview

		lv_sd.setOnItemClickListener(new OnItemClickListener() {

			private String mp3Path;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				songNum = arg2;// 记录当前播放哪首歌
				mAdapter.notifyDataSetChanged();// 需要刷新，就会调用BaseAdapter的getview方法
				showCustomView();// 点击展示通知栏
				mp3Path = musicUrlList.get(arg2);// 记录当前歌曲的路径
				ContentFragment.mi.play(mp3Path);
			}

		});
	}

	@Override
	public void initViews() {
		super.initViews();
	}

	/**
	 * 设置通知栏界面和按钮意图
	 */
	public static void showCustomView() {
		Log.d("showCustomView", "执行了");
		remoteViews = new RemoteViews(mActivity.getPackageName(),
				R.layout.notyfiction);
		remoteViews.setTextViewText(R.id.title_music_name,
				BackgroundService.songName); // 设置textview
		if (BackgroundService.currMp3Path != null) {// 设置专辑图片
			Bitmap bitmap = MediaUtil.getSamllBitmap(
					BackgroundService.currMp3Path, mActivity);
			remoteViews.setImageViewBitmap(R.id.songer_pic, bitmap);
		}
		// if (BackgroundService.mediaPlayer.isPlaying()) {//
		// 这些代码执行不了，不知道为什么？写在服务里也不行
		// Log.d("showCustomView1", BackgroundService.mediaPlayer.isPlaying()
		// + "");
		// remoteViews.setImageViewResource(R.id.paly_pause_music,
		// R.drawable.music_play);
		// } else {
		// Log.d("showCustomView2", BackgroundService.mediaPlayer.isPlaying()
		// + "");
		// remoteViews.setImageViewResource(R.id.paly_pause_music,
		// R.drawable.music_pause);
		// }

		// 设置按钮事件 -- 发送广播 --广播接收后进行对应的处理
		Intent buttonPlayIntent = new Intent(
				ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PER); // ----设置上一首广播
		PendingIntent pendButtonPlayIntent = PendingIntent.getBroadcast(
				mActivity, 0, buttonPlayIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.pre_music,
				pendButtonPlayIntent);// ----设置上一首按钮ID监控

		Intent buttonPlayIntent1 = new Intent(
				ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY); // ----设置播放暂停广播
		PendingIntent pendButtonPlayIntent1 = PendingIntent.getBroadcast(
				mActivity, 0, buttonPlayIntent1, 0);
		remoteViews.setOnClickPendingIntent(R.id.paly_pause_music,
				pendButtonPlayIntent1);// ----设置播放暂停监控

		Intent buttonPlayIntent2 = new Intent(
				ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT); // ----设置下一首广播
		PendingIntent pendButtonPlayIntent2 = PendingIntent.getBroadcast(
				mActivity, 0, buttonPlayIntent2, 0);
		remoteViews.setOnClickPendingIntent(R.id.next_music,
				pendButtonPlayIntent2);// ----设置下一首监控

		Intent resultIntent = new Intent(mActivity, MainActivity.class);// 点击返回activity
		PendingIntent resultPendingIntent = PendingIntent.getActivity(
				mActivity, 0, resultIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.music_notifi_lay,
				resultPendingIntent);// ----设置对应的按钮ID监控

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				mActivity);
		builder.setContent(remoteViews)
				.setSmallIcon(R.drawable.ic_notification)
				.setTicker("music is playing");
		Notification notify = builder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;// 发起正在运行事件（活动中）
		manager.notify(1, notify);
	}

	/**
	 * 设置listview数据
	 */
	private void MusicList() {
		String arrtists = sharedPreferences.getString("arrtists", "");// 获取保存的歌手
		List<Music> list = MusicListUtils.getMusicList(mActivity);// 获取歌曲信息集合
		if (arrtists.equals("")) {// 程序第一次进来的时候保存所有的歌手
			for (Music music : list) {
				String name = music.getName();
				String arrtist = music.getArrtist();
				String url = music.getUrl();
				if (!arrtists.contains(arrtist)) {// 保存起来
					arrtists = arrtists + arrtist + ",";
					sharedPreferences.edit().putString("arrtists", arrtists)
							.commit();
				}
				musicUrlList.add(url);
				musicNameList.add(name);
			}
		} else {
			for (Music music : list) {
				String name = music.getName();
				String url = music.getUrl();
				boolean isAllArrtist = sharedPreferences.getBoolean(
						"isAllArrtist", false);// 当点了全部歌曲置为TRUE，显示全部歌曲
				if (isAllArrtist == true) {
					musicUrlList.add(url);
					musicNameList.add(name);
				} else if (name.contains(arrtists) && url.contains(arrtists)) {// 当点了某个歌手置为FALSE，显示歌手对应的歌曲
					musicUrlList.add(url);
					musicNameList.add(name);
				}
			}
		}
		mAdapter = new MusicAdapter();
		// lv_sd.removeAllViews();
		mAdapter.notifyDataSetChanged();
		lv_sd.setAdapter(mAdapter); // 添加适配器
		flContent.addView(lv_sd);// 将viewpage添加到FrameLayout
	}

	public class MusicAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return musicNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return musicNameList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = View.inflate(mActivity, R.layout.list_item_home, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_item = (TextView) view.findViewById(R.id.tv_item);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.tv_item.setText(musicNameList.get(position));
			if (songNum == position) {// 如果歌曲索引等于position就显示红色
				viewHolder.tv_item.setEnabled(true);
			} else {
				viewHolder.tv_item.setEnabled(false);
			}
			return view;
		}

	}

	class ViewHolder {
		TextView tv_item;
	}

}
