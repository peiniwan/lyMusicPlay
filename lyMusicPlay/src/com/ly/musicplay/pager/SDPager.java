package com.ly.musicplay.pager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
import com.ly.musicplay.utils.MusicListUtils;

/**
 * 本地歌曲页面
 * 
 * @author Administrator
 * 
 */
public class SDPager extends BasePager {

	private List<String> musicNameList = new ArrayList<String>();// 歌曲名称，填充adapter
	public static List<String> musicUrlList;// 存放找到的所有mp3的全路径
	public static int songNum; // 当前播放的歌曲在List中的下标
	private ListView lv_sd;

	public static MusicAdapter mAdapter;// 歌曲列表的adapter
	public static ServiceReceiver receiver;// 通知栏广播
	private static NotificationManager manager;
	public static TextView title_music_name;// 通知歌曲名字

	public SDPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {

		lv_sd = new ListView(mActivity);
		lv_sd.setBackgroundResource(R.drawable.bg);// 设置播放暂停的图片

		receiver = new ServiceReceiver();// 通知的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PER);
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
		mActivity.registerReceiver(receiver, intentFilter);
		manager = (NotificationManager) mActivity
				.getSystemService(Context.NOTIFICATION_SERVICE);

		MusicList();// 扫描SD卡歌名，并设置listview
		musicUrlList = new ArrayList<String>();// 创建播放歌曲的全路径的list
		List<Music> list = MusicListUtils.getMusicList(mActivity);
		for (Music music : list) {
			String url = music.getUrl();
			musicUrlList.add(url);
		}
		lv_sd.setOnItemClickListener(new OnItemClickListener() {

			private String mp3Path;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d("onItemClick", "执行了");
				songNum = arg2;// 当前播放哪首歌
				mAdapter.notifyDataSetChanged();// 需要刷新，就会调用BaseAdapter的getview方法
				showCustomView();
				mp3Path = musicUrlList.get(arg2);
				System.out.println("mp3Path---------" + mp3Path);
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
		RemoteViews remoteViews = new RemoteViews(mActivity.getPackageName(),
				R.layout.notyfiction);// 通知栏上只能用这个view
		remoteViews.setTextViewText(R.id.title_music_name,
				BackgroundService.songName); // 设置textview

		if (BackgroundService.mediaPlayer.isPlaying()) {// 这步代码执行不了，因为它一直是false?
			Log.d("showCustomView1", BackgroundService.mediaPlayer.isPlaying()
					+ "");
			remoteViews.setImageViewResource(R.id.paly_pause_music,
					R.drawable.music_play);
		} else {
			Log.d("showCustomView2", BackgroundService.mediaPlayer.isPlaying()
					+ "");
			remoteViews.setImageViewResource(R.id.paly_pause_music,
					R.drawable.music_pause);
		}

		// 设置按钮事件 -- 发送广播 --广播接收后进行对应的处理
		Intent buttonPlayIntent = new Intent(
				ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PER); // ----设置通知栏按钮广播
		PendingIntent pendButtonPlayIntent = PendingIntent.getBroadcast(
				mActivity, 0, buttonPlayIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.pre_music,
				pendButtonPlayIntent);// ----设置对应的按钮ID监控

		Intent buttonPlayIntent1 = new Intent(
				ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY); // ----设置通知栏按钮广播
		PendingIntent pendButtonPlayIntent1 = PendingIntent.getBroadcast(
				mActivity, 0, buttonPlayIntent1,
				PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.paly_pause_music,
				pendButtonPlayIntent1);// ----设置对应的按钮ID监控

		Intent buttonPlayIntent2 = new Intent(
				ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT); // ----设置通知栏按钮广播
		PendingIntent pendButtonPlayIntent2 = PendingIntent.getBroadcast(
				mActivity, 0, buttonPlayIntent2,
				PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.next_music,
				pendButtonPlayIntent2);// ----设置对应的按钮ID监控

		Intent resultIntent = new Intent(mActivity, MainActivity.class);// 点击图片返回activity
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mActivity);// 这里获取PendingIntent是通过创建TaskStackBuilder对象
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);// 表示更新的PendingIntent
		remoteViews.setOnClickPendingIntent(R.id.songer_pic,
				resultPendingIntent);// ----设置对应的按钮ID监控
		remoteViews.setOnClickPendingIntent(R.id.title_music_name,
				resultPendingIntent);// ----设置对应的按钮ID监控

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				mActivity);
		builder.setContent(remoteViews)
				.setSmallIcon(R.drawable.ic_notification).setOngoing(true)
				.setTicker("music is playing");
		// .setContentIntent(
		// getDefalutIntent(Notification.FLAG_ONGOING_EVENT));
		Notification notify = builder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;// 发起正在运行事件（活动中）
		manager.notify(1, notify);

	}

	/**
	 * 歌曲列表
	 */
	private void MusicList() {
		List<Music> list = MusicListUtils.getMusicList(mActivity);
		for (Music music : list) {
			String name = music.getName();
			musicNameList.add(name); // 把每一次遍历到的歌曲名字添加到myMusicList表中
		}
		mAdapter = new MusicAdapter();
		lv_sd.setAdapter(mAdapter); // 添加适配器
		flContent.addView(lv_sd);
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
			if (songNum == position) {
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
