package com.ly.musicplay2.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 访问网络存放歌曲信息的文件 解析出来就知道需要下载哪首歌了
 * 
 * @author Administrator
 * 
 */
public class HttpDownloader {
	public static URL url = null;

	/**
	 * 根据URL下载文件，前提是这个文件当中的内容是文本，函数的返回值就是文件当中的内容 1.创建一个URL对象
	 * 2.通过URL对象，创建一个HttpURLConnection对象 3.得到InputStram 4.从InputStream当中读取数据
	 * 
	 */

	public static void download(final String urlStr,
			final HttpCallbackListener listener) {

		new Thread(new Runnable() {

			private HttpURLConnection urlConn;

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				String line = null;
				BufferedReader buffer = null;

				try {
					// 创建一个URL对象
					url = new URL(urlStr);
					urlConn = (HttpURLConnection) url.openConnection();
					urlConn.setRequestMethod("GET");
					urlConn.setReadTimeout(5000);
					urlConn.setConnectTimeout(5000);
					urlConn.setDoInput(true);
					urlConn.setDoOutput(true);
					// urlConn.connect();
					InputStream inputStream = urlConn.getInputStream();
					// 使用IO流读取数据
					buffer = new BufferedReader(new InputStreamReader(
							inputStream));
					while ((line = buffer.readLine()) != null) {
						sb.append(line);
					}
					if (listener != null) {
						// 回调onFinish()方法
						listener.onFinish(sb.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					}
				} finally {
					if (urlConn != null) {
						urlConn.disconnect();
					}
				}
			}
		}).start();

	}

}
