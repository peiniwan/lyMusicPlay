package com.ly.musicplay.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class LocalCacheUtils {
	public static final String CACHE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/plus_music_play";

	/**
	 * 从本地sdcard读图片
	 */
	public Bitmap getBitmapFromLocal(String url) {
		try {
			String fileName = MD5Encoder.encode(url);
			File file = new File(CACHE_PATH, fileName);
			if (file.exists()) {
				Bitmap bitmap = android.graphics.BitmapFactory
						.decodeStream(new FileInputStream(file));
				// decodeStream(new FileInputStream(
				// file));// decodeStream放的是输入输出流
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 向sdcard写图片
	 * 
	 * @param url
	 * @param bitmap
	 */
	public void setBitmapToLocal(String url, Bitmap bitmap) {
		try {
			String fileName = MD5Encoder.encode(url);
			File file = new File(CACHE_PATH, fileName);
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {// 如果文件夹不存在, 创建文件夹
				parentFile.mkdirs();
			}
			// 将图片保存在本地
			bitmap.compress(CompressFormat.JPEG, 100,
					new FileOutputStream(file));// 100是质量
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
