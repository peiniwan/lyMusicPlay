package com.ly.musicplay.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ly.musicplay.fragment.ContentFragment;

/**
 * 用于通知栏操作歌曲
 * 
 * @author Administrator
 * 
 */
public class ServiceReceiver extends BroadcastReceiver {

	public static final String NOTIFICATION_ITEM_BUTTON_PER = "com.example.notification.ServiceReceiver.per";// ----通知栏上一首按钮
	public static final String NOTIFICATION_ITEM_BUTTON_PLAY = "com.example.notification.ServiceReceiver.play";// ----通知栏播放按钮
	public static final String NOTIFICATION_ITEM_BUTTON_NEXT = "com.example.notification.ServiceReceiver.next";// ----通知栏下一首按钮

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d("ServiceReceiver", "执行了");
		String action = intent.getAction();
		if (action.equals(NOTIFICATION_ITEM_BUTTON_PER)) {// ----通知栏播放按钮响应事件
			ContentFragment.mi.per();// 在切换了歌手的时候会掉俩次？
		} else if (action.equals(NOTIFICATION_ITEM_BUTTON_PLAY)) {
			// ----通知栏播放按钮响应事件
			ContentFragment.mi.startPause();
		} else if (action.equals(NOTIFICATION_ITEM_BUTTON_NEXT)) {
			// ----通知栏下一首按钮响应事件
			System.out.println("ServiceReceiver----------next");
			ContentFragment.mi.next();
		}
	}
}
