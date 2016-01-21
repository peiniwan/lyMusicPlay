package com.ly.musicplay.bean;

import java.util.ArrayList;

public class NewsData {
	public String reason;
	public ArrayList<ItemNews> result;

	@Override
	public String toString() {
		return "NewsData [reason=" + reason + ", result=" + result + "]";
	}

	public class ItemNews {
		// "title":"高配公开版 华为P8西安报价2680元卖",
		// "content":"经过几代的发展,<em>华为<\/em>P系列已经成为一个非常成熟的产品系列.......",
		// "img_width":"375",
		// "full_title":"高配公开版 华为P8西安报价2680元卖",
		// "pdate":"6分钟前",
		// "src":"泡泡网",
		// "img_length":"500",
		// "img":"http:\/\/p0.qhimg.com\/t018be2372d0a6256df.jpg",
		// "url":"http:\/\/www.pcpop.com\/doc\/1\/1262\/1262259.shtml",
		// "pdate_src":"2015-12-02 09:32:00"
		public String title;

		public String content;
		public String pdate;
		public String src;
		public String img;
		public String url;
		public String pdate_src;

		@Override
		public String toString() {
			return "ItemNews [title=" + title + ", content=" + content
					+ ", pdate=" + pdate + ", src=" + src + ", img=" + img
					+ ", url=" + url + ", pdate_src=" + pdate_src + "]";
		}
	}

}
