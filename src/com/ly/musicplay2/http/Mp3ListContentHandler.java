package com.ly.musicplay2.http;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ly.musicplay2.bean.Mp3Info;

/**
 * sax解析数据
 * 
 * @author Administrator
 * 
 */
public class Mp3ListContentHandler extends DefaultHandler {
	private List<Mp3Info> infos = null;
	private Mp3Info mp3Info = null;
	private String tagName = null;

	public Mp3ListContentHandler(List<Mp3Info> infos) {
		super();
		this.infos = infos;
	}

	public List<Mp3Info> getInfos() {
		return infos;
	}

	public void setInfos(List<Mp3Info> infos) {
		this.infos = infos;
	}

	/**
	 * 开始 XML解析的时候调用
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		this.tagName = localName;
		if (tagName.equals("resource")) {
			mp3Info = new Mp3Info();
		}
	}

	/**
	 * 在解析结点中具体内容的时候就会调用characters()方法
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String temp = new String(ch, start, length);
		if (tagName.equals("id")) {
			mp3Info.setId(temp);
		} else if (tagName.equals("mp3.name")) {
			mp3Info.setMp3Name(temp);
		} else if (tagName.equals("mp3.size")) {
			mp3Info.setMp3Size(temp);
		} else if (tagName.equals("lrc.name")) {
			mp3Info.setLrcName(temp);
		} else if (tagName.equals("lrc.size")) {
			mp3Info.setLrcSize(temp);
		}
		super.characters(ch, start, length);
	}

	/**
	 * xml尾的事件类型
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if (qName.equals("resource")) {
			infos.add(mp3Info);
		}
		tagName = "";
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

}
