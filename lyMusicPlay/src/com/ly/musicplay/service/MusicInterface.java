package com.ly.musicplay.service;

/**
 * 音乐控制服务的binder接口
 * 
 * @author Administrator
 * 
 */
public interface MusicInterface {
	void play(String mp3Path);

	void next();

	void stop();

	void per();

	void startPause();

	void seekTo(int progress);

	String setPlayName(String dataSource);

	int modeChange(int mode);// 模式变化
	// void setHandler(Handler handler);//写在这里会报错，所以用了静态访问

}
