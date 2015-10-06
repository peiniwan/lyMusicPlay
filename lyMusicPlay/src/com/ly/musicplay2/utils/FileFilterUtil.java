package com.ly.musicplay2.utils;

import java.io.File;
import java.io.FilenameFilter;
/**
 * 过滤MP3结尾的歌曲
 * @author Administrator
 *
 */
public class FileFilterUtil implements FilenameFilter{

	@Override
	public boolean accept(File dir, String filename) {
		// TODO Auto-generated method stub
		return filename.endsWith(".mp3");
	}

}
