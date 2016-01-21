package com.ly.musicplay2.pager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
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
import com.ly.musicplay2.activity.MainActivity;
import com.ly.musicplay2.fragment.ContentFragment;
import com.ly.musicplay2.receive.ServiceReceiver;
import com.ly.musicplay2.service.BackgroundService;
import com.ly.musicplay2.utils.FileFilterUtil;

/**
 * 本地歌曲页面
 * 
 * @author Administrator
 * 
 */
public class SDPager extends BasePager {
	private static final String PATH = new String(Environment
			.getExternalStorageDirectory().toString()); // 定义sd卡的 路径

	private List<String> myMusicList = new ArrayList<String>();// 歌曲名称，填充adapter
	public static List<String> musicList;// 存放找到的所有mp3的全路径
	public static int songNum; // 当前播放的歌曲在List中的下标
	private ListView lv_sd;

	public static musicAdapter mAdapter;// 歌曲列表的adapter
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

		mAdapter = new musicAdapter();

		receiver = new ServiceReceiver();// 通知的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PER);
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_PLAY);
		intentFilter.addAction(ServiceReceiver.NOTIFICATION_ITEM_BUTTON_NEXT);
		mActivity.registerReceiver(receiver, intentFilter);
		manager = (NotificationManager) mActivity
				.getSystemService(Context.NOTIFICATION_SERVICE);

		MusicList();// 找到SD卡歌曲，并设置listview
		musicList = new ArrayList<String>();// 创建播放歌曲的全路径的list
		File home = new File(PATH); // 创建一个路径为PATH的文件夹
		if (home.listFiles(new FileFilterUtil()).length > 0) {
			for (File file : home.listFiles(new FileFilterUtil())) {
				musicList.add(PATH + File.separator + file.getName());
			}
			lv_sd.setOnItemClickListener(new OnItemClickListener() {

				private String mp3Path;

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Log.d("onItemClick", "执行了");
					songNum = arg2;// 当前播放哪首歌
					mAdapter.notifyDataSetChanged();// 需要刷新，就会调用BaseAdapter的getview方法
					showCustomView();
					mp3Path = musicList.get(arg2).toString();
					System.out.println("mp3Path---------" + mp3Path);
					ContentFragment.mi.play(mp3Path);

				}

			});
		}

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

	// public static PendingIntent getDefalutIntent(int flags) {
	// PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity, 1,
	// new Intent(), flags);
	// return pendingIntent;
	// }

	/**
	 * 歌曲列表
	 */
	private void MusicList() {
		File home = new File(PATH); // 创建一个路径为PATH的文件夹
		if (home.listFiles(new FileFilterUtil()).length > 0) { //
			// 如果文件夹下的歌曲数目大于0，则执行下面的方法
			for (File file : home.listFiles(new FileFilterUtil())) { //
				// 遍历home文件夹下面的歌曲
				myMusicList.add(file.getName()); // 把每一次遍历到的歌曲名字添加到myMusicList表中
			}

			// ArrayAdapter<String> musicAdapter = new ArrayAdapter<String>(
			// mActivity, R.layout.list_item_home, R.id.tv_item,
			// myMusicList);
			lv_sd.setAdapter(mAdapter); // 添加适配器
			flContent.addView(lv_sd);
		}
	}

	public class musicAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myMusicList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myMusicList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
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

			viewHolder.tv_item.setText(myMusicList.get(position));
			if (songNum == position) {
				viewHolder.tv_item.setEnabled(true);
			} else {
				viewHolder.tv_item.setEnabled(false);
			}
			return view;
		}

		class ViewHolder {
			TextView tv_item;
		}
	}

}
