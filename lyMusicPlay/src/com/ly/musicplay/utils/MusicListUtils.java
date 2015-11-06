package com.ly.musicplay.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.ly.musicplay.bean.Music;

public class MusicListUtils {
	public static List<Music> getMusicList(Context context) {

		List<Music> musiclist = new ArrayList<Music>();

		// ContentProvider（内容提供者） 和 ContentResolver（内容解析器），用于管理和发布和应用程序相关的持久性数据
		ContentResolver resolver = context.getContentResolver();

		Cursor cursor = resolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				null);

		if (null == cursor) {
			return null;
		}
		cursor.moveToFirst();
		if (cursor.moveToFirst()) {
			do {
				Music m = new Music();
				// 自己建的类，用于存放查询到的音乐信息
				// 播放音乐是用的是创建MediaPlayer实例，为其传递音乐文件的路径
				long id = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
				String title = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.TITLE)); // 标题
				String arrtist = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 歌手
				if ("<unknown>".equals(arrtist)) {
					arrtist = "未知艺术家";
				}
				String album = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ALBUM)); // 专辑图片
				long size = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 大小
				long duration = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
				String url = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DATA)); // 音乐文件的路径
				String name = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));// 音乐文件名
				String albumId = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));// 音乐文件名
				if (duration >= 1000 && duration <= 900000) {
					// 此处添加music，音乐信息，到列表
					Music music = new Music(id, title, arrtist, albumId, size,
							duration, url, name);
					// System.out.println(music.toString());
					musiclist.add(music);
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			// 结果集移动到最后一列，再下移为空；释放资源
			cursor.close();
		}
		// f返回音乐文件列表

		return musiclist;
	}
}
