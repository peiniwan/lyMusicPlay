package com.ly.musicplay.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryCacheUtils {
	private LruCache<String, Bitmap> mMemoryCache;
	public MemoryCacheUtils() {
		long maxMemory = Runtime.getRuntime().maxMemory() / 8;//主流都是分配16m的8/1
		mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				int byteCount = value.getRowBytes() * value.getHeight();// 获取图片占用内存大小
				return byteCount;
			}
		};
	}
	/**
	 * 从内存读
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromMemory(String url) {

		return mMemoryCache.get(url);
	}
	/**
	 * 写内存
	 * 
	 * @param url
	 * @param bitmap
	 */
	public void setBitmapToMemory(String url, Bitmap bitmap) {

		mMemoryCache.put(url, bitmap);
	}
}