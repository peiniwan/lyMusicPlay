package com.ly.musicplay.http;

/**
 * 聊天机器人自定义异常
 * 
 * @author Administrator
 * 
 */
public class CommonException extends RuntimeException {

	public CommonException() {
		super();
	}

	public CommonException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CommonException(String detailMessage) {
		super(detailMessage);
	}

	public CommonException(Throwable throwable) {
		super(throwable);
	}

}
