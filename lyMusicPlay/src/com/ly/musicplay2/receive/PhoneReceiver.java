package com.ly.musicplay2.receive;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.ly.musicplay2.fragment.ContentFragment;
/**
 * 来电去电广播
 * 可是没有效果
 * @author Administrator
 *
 */
public class PhoneReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			ContentFragment.mi.startPause();
		} else {
			// 如果是来电
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tManager.getCallState()) {

			case TelephonyManager.CALL_STATE_RINGING:
				ContentFragment.mi.startPause();

			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			case TelephonyManager.CALL_STATE_IDLE:
				ContentFragment.mi.startPause();
				break;
			}
		}
	}

}
