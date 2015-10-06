package com.ly.musicplay2.pager;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ly.musicplay2.R;
import com.ly.musicplay2.bean.Mp3Info;
import com.ly.musicplay2.http.HttpCallbackListener;
import com.ly.musicplay2.http.HttpDownloader;
import com.ly.musicplay2.http.Mp3ListContentHandler;
import com.ly.musicplay2.service.DownloadService;

/**
 * 显示tomcat的歌曲，并下载
 * 
 * @author Administrator
 * 
 */
public class HttpPager extends BasePager {
	private ListView lv;
	private SimpleAdapter simpleAdapter;
	public static TextView tv;
	public static ProgressBar pb;
	public static TextView tv_process;

	private List<Mp3Info> mp3Infos = null;// 存放MP3歌曲
	String PATH = Environment.getExternalStorageDirectory().toString(); // 定义sd卡的路径
	final List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();// 适配器数据

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				mp3Infos = (List<Mp3Info>) msg.obj;// 获取子线程发回来的数据
				for (Mp3Info mp3Info : mp3Infos) {// 设置适配器并添加到list中
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("mp3_name", mp3Info.getMp3Name());
					String m = getM(mp3Info.getMp3Size());// 将大小变成M
					map.put("mp3_size", m + "M");
					list.add(map);
					simpleAdapter.notifyDataSetChanged();// 刷新simpleAdapter
				}

				break;

			default:
				break;
			}
		}
	};

	public HttpPager(Activity activity) {
		super(activity);

	}

	@Override
	public void initViews() {
		super.initViews();
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void initData() {
		super.initData();
		Log.d("HttpPager", "initData执行了");
		View view = View.inflate(mActivity, R.layout.http_pager, null);// 填充布局
		lv = (ListView) view.findViewById(R.id.lv);
		tv = (TextView) view.findViewById(R.id.tv);
		pb = (ProgressBar) view.findViewById(R.id.pb);
		tv_process = (TextView) view.findViewById(R.id.tv_process);

		updateListView();// 获取网络中的歌曲信息
		simpleAdapter = new SimpleAdapter(mActivity, list,
				R.layout.mp3info_item, new String[] { "mp3_name", "mp3_size" },
				new int[] { R.id.mp3_name, R.id.mp3_size });
		lv.setAdapter(simpleAdapter);
		flContent.addView(view);// listview添加给FrameLayout

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Mp3Info mp3Info = mp3Infos.get(arg2);
				Intent intent = new Intent(mActivity, DownloadService.class);//启动下载
				// 将Mp3Info对象存入到Intent对象当中
				intent.putExtra("mp3Info", mp3Info);
				// 启动Service
				mActivity.startService(intent);
			}
		});
	}

	public String getM(String z) { // 该参数表示字节的值
		double m;
		double zz = Double.parseDouble(z);// 字符串转成double
		m = zz / 1024.0 / 1024;
		// DecimalFormat转换最简便
		DecimalFormat df = new DecimalFormat("#.00");
		String mm = df.format(m);
		return mm; // 返回kb转换之后的M值
	}

	/**
	 * sax解析xml
	 * 
	 * @param xmlStr
	 *            :从网络获取的xml
	 * @return
	 */
	private List<Mp3Info> parse(String xmlStr) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<Mp3Info> infos = new ArrayList<Mp3Info>();
		try {
			XMLReader xmlReader = saxParserFactory.newSAXParser()
					.getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(
					infos);
			xmlReader.setContentHandler(mp3ListContentHandler);
			xmlReader.parse(new InputSource(new StringReader(xmlStr)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infos;
	}

	/**
	 * 获取网络中的歌曲信息
	 */
	private void updateListView() {
		// 获取服务器resources.xml歌曲的信息
		HttpDownloader.download("http://10.0.2.2:8080/music/resources.xml",
		// HttpDownloader.download("http://192.168.1.100:8080/music/resources.xml",
				new HttpCallbackListener() {

					@Override
					public void onFinish(String response) {
						mp3Infos = parse(response);// 将获取的信息解析出来
						Message msg = new Message();
						msg.what = 1;
						msg.obj = mp3Infos;
						// 消息对象可以携带数据
						handler.sendMessage(msg);
					}

					@Override
					public void onError(Exception e) {

					}
				});
		Log.d("HttpPager", "updateListView执行了");
	}

}
