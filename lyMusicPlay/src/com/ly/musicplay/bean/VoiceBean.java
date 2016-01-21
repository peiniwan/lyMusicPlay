package com.ly.musicplay.bean;

import java.util.ArrayList;

/**
 * 语音信息封装
 * 
 */
public class VoiceBean {

	public ArrayList<WSBean> ws;

	public class WSBean {
		public ArrayList<CWBean> cw;
	}

	public class CWBean {
		public String w;
	}
}
