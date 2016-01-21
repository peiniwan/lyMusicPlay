package com.ly.musicplay.bean;

/**
 * 聊天机器人解析json返回的bean对象
 * 
 * @author Administrator
 * 
 */
public class Result {
	private int code;
	private String text;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Result() {
	}

	public Result(int resultCode, String msg) {
		this.code = resultCode;
		this.text = msg;
	}

	public Result(int resultCode) {
		this.code = resultCode;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
