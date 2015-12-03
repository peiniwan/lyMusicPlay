package com.ly.musicplay.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.ly.musicplay.R;
import com.ly.musicplay.activity.MainActivity;
import com.ly.musicplay.activity.WebActivity;
import com.ly.musicplay.bean.NewsData;
import com.ly.musicplay.bean.NewsData.ItemNews;
import com.ly.musicplay.http.HttpCallbackListener;
import com.ly.musicplay.http.HttpDownloader;
import com.ly.musicplay.utils.SDstore;

public class NewsFragment extends BaseFragment {

	private EditText et_key;// 搜索框
	private ListView lv_news;// 新闻列表
	private LinearLayout ll_loading;// 加载进度
	private ImageButton bt_search;// 搜索按钮

	private NewsAdapter mNewsAdapter;
	private ArrayList<ItemNews> result;
	private SharedPreferences sharedPreferences;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				String response = (String) msg.obj;
				parseData(response);// 子线程刷新ui
				ll_loading.setVisibility(View.INVISIBLE);
				lv_news.setVisibility(View.VISIBLE);
				break;
			case 1:
				Toast.makeText(mActivity, "请检查网络设置或保持网络畅通", 0).show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_news, null);
		et_key = (EditText) view.findViewById(R.id.et_key);
		bt_search = (ImageButton) view.findViewById(R.id.bt_search);
		ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
		lv_news = (ListView) view.findViewById(R.id.lv_news);
		return view;
	}

	@Override
	public void initData() {
		sharedPreferences = mActivity.getSharedPreferences(
				MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String savekey = sharedPreferences.getString("savekey", null);// 获取上次搜索保存的关键字
		if (savekey != null) {
			String result = SDstore.read2sd(getQueryUrl(savekey));// 读取保存的内容
			parseData(result);// 解析并展示界面
		}

		bt_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDataFromServer();
			}
		});
		lv_news.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mActivity, WebActivity.class);
				intent.putExtra("url", result.get(position).url);
				intent.putExtra("title", result.get(position).title);
				mActivity.startActivity(intent);
			}
		});
	}

	private String getQueryUrl(String key) {
		if (key != null && !key.equals("")) {
			try {
				// http://op.juhe.cn/onebox/news/query?key=c3fb10056c4967ff23e613d82ead71ee&q=%E6%99%AE%E4%BA%AC
				String encode = URLEncoder.encode(key, "UTF-8");
				String queryUrl = "http://op.juhe.cn/onebox/news/query?"
						+ "key=c3fb10056c4967ff23e613d82ead71ee&q=" + encode;
				// System.out.println("queryUrl----------" + queryUrl);
				return queryUrl;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(mActivity, "请输入关键字", 0).show();
		}
		return null;
	}

	private void getDataFromServer() {
		ll_loading.setVisibility(View.VISIBLE);
		lv_news.setVisibility(View.INVISIBLE);
		final String key = et_key.getText().toString().trim();
		final String queryUrl = getQueryUrl(key);
		if (queryUrl != null)
			HttpDownloader.download(queryUrl, new HttpCallbackListener() {
				Message message = Message.obtain();

				@Override
				public void onFinish(final String response) {
					SDstore.write2sd(queryUrl, response);// 保存数据
					sharedPreferences.edit().putString("savekey", key).commit();// 保存搜索的字

					message.what = 0;
					message.obj = response;
					handler.sendMessage(message);
					// mActivity.runOnUiThread(new Runnable() {
					// @Override
					// public void run() {
					// if (response != null) {
					// parseData(response);// 子线程刷新ui
					// ll_loading.setVisibility(View.INVISIBLE);
					// lv_news.setVisibility(View.VISIBLE);
					// }
					// }
					// });
				}

				@Override
				public void onError(final Exception e) {
					message.what = 1;
					handler.sendMessage(message);
					// mActivity.runOnUiThread(new Runnable() {
					// @Override
					// public void run() {
					// Toast.makeText(mActivity, "请检查网络设置或保持网络畅通", 0)
					// .show();
					// }
					// });

				}
			});
	}

	/**
	 * 解析数据
	 * 
	 * @param result
	 */
	protected void parseData(String data) {
		Gson gson = new Gson();
		NewsData newsData = gson.fromJson(data, NewsData.class);
		result = newsData.result;
		if (result != null) {
			if (mNewsAdapter == null) {
				mNewsAdapter = new NewsAdapter();
				lv_news.setAdapter(mNewsAdapter);
			} else {
				mNewsAdapter.notifyDataSetChanged();
			}
			System.out.println("	result.size()-------------" + result.size());
		} else {
			Toast.makeText(mActivity, "没有相关信息", 0).show();
		}
	}

	class NewsAdapter extends BaseAdapter {
		private BitmapUtils utils;

		public NewsAdapter() {
			utils = new BitmapUtils(mActivity);
			utils.configDefaultLoadingImage(R.drawable.news_default);
		}

		@Override
		public int getCount() {
			return result.size();
		}

		@Override
		public Object getItem(int position) {
			return result.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.list_news_item,
						null);
				holder = new ViewHolder();
				holder.ivPic = (ImageView) convertView
						.findViewById(R.id.iv_pic);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.tv_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ItemNews itemNews = result.get(position);

			holder.tvTitle.setText(itemNews.title);
			holder.tvDate.setText(itemNews.pdate_src);
			if (!itemNews.img.equals("")) {
				utils.display(holder.ivPic, itemNews.img);
			} else {
				holder.ivPic.setImageResource(R.drawable.news_default);
			}
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView tvTitle;
		public TextView tvDate;
		public ImageView ivPic;
	}

}
