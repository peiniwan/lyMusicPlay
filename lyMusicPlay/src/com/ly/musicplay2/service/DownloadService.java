package com.ly.musicplay2.service;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ly.musicplay2.bean.Mp3Info;
import com.ly.musicplay2.pager.HttpPager;

/**
 * 下载歌曲的服务
 * 
 * @author Administrator
 * 
 */
public class DownloadService extends Service {
	String PATH = Environment.getExternalStorageDirectory().toString(); // 定义sd卡的路径

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 从Intent对象当中将Mp3Info对象取出
		Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

		downSong(mp3Info);
		return super.onStartCommand(intent, flags, startId);

	}

	/**
	 * 下载歌曲
	 * 
	 * @param mp3Info
	 */
	public void downSong(Mp3Info mp3Info) {
		String mp3Url = "http://10.0.2.2:8080/music/" + mp3Info.getMp3Name();

		// String target = PATH + "/mp3/" + mp3Info.getMp3Name();
		String target = PATH + mp3Info.getMp3Name();
		// 使用xutils下载
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.download(mp3Url, target, true, true,
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						Log.d("DownloadService", "downSong----arg0"
								+ arg0.result.getPath());
						HttpPager.tv.setText("下载成功" + arg0.result.getPath());// 弹出结果路径

					}

					@Override
					public void onFailure(
							com.lidroid.xutils.exception.HttpException arg0,
							String arg1) {
						// TODO Auto-generated method stub
						Log.d("DownloadService", "downSong ---arg1" + arg1);
						HttpPager.tv.setText("下载失败" + arg1);// 弹出结果路径
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						// TODO Auto-generated method stub
						super.onLoading(total, current, isUploading);
						// 设置进度条总长度
						HttpPager.pb.setMax((int) total);
						// 设置进度条当前进度
						HttpPager.pb.setProgress((int) current);
						HttpPager.tv_process.setText(current * 100 / total
								+ "%");
					}
				});
	}
}
