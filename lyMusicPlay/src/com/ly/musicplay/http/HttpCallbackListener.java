package com.ly.musicplay.http;

/**
 * 为了写网络的下载的公共类，需要传这个接口
 * @author Administrator
 * 
 */
public interface HttpCallbackListener {
	void onFinish(String response);

	void onError(Exception e);
}
