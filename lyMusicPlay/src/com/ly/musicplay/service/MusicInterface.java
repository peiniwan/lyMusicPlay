package com.ly.musicplay.service;

public interface MusicInterface {
	void play(String mp3Path);

	void next();

	void stop();

	void per();

	void startPause();

	void seekTo(int progress);

	String setPlayName(String dataSource);

}
