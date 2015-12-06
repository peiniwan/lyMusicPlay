package com.ly.musicplay.fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.ly.musicplay.R;
import com.ly.musicplay.bean.ChatMessage;
import com.ly.musicplay.bean.ChatMessage.Type;
import com.ly.musicplay.utils.BaseApplication;

/**
 * 机器人聊天的adapter
 * 
 * @author Administrator
 * 
 */
public class ChatMessageAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ChatMessage> mDatas;

	public ChatMessageAdapter(Context context, List<ChatMessage> datas) {
		mInflater = LayoutInflater.from(context);
		mDatas = datas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 接受到消息为1，发送消息为0
	 */
	@Override
	public int getItemViewType(int position) {
		ChatMessage msg = mDatas.get(position);
		return msg.getType() == Type.INPUT ? 1 : 0;
	}

	/**
	 * 有俩种不一样的类型
	 */
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage chatMessage = mDatas.get(position);

		ViewHolder viewHolder = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (chatMessage.getType() == Type.INPUT) {
				convertView = mInflater.inflate(
						R.layout.fragment_chat_from_msg, null);
				viewHolder.createDate = (TextView) convertView
						.findViewById(R.id.chat_from_createDate);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_from_content);
				viewHolder.ly = (LinearLayout) convertView
						.findViewById(R.id.pic_url);
				viewHolder.iv = (ImageView) convertView
						.findViewById(R.id.chat_url);

				convertView.setTag(viewHolder);
			} else {
				convertView = mInflater.inflate(
						R.layout.fragment_chat_send_msg, null);
				viewHolder.createDate = (TextView) convertView
						.findViewById(R.id.chat_send_createDate);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_send_content);
				convertView.findViewById(R.id.chat_send_content);
				convertView.setTag(viewHolder);
			}

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (chatMessage.getUrl() != null) {
			viewHolder.ly.setVisibility(View.GONE);
			BitmapUtils bitmapUtils = new BitmapUtils(// 图片为什么显示不出来？因为bitmap没获取到，浏览器打开URL是一堆图片，可能跟这个有关系
					BaseApplication.getApplication());
			// Bitmap bitmap = downloadBitmap(decode);
			// viewHolder.iv.setImageBitmap(bitmap);
			new BitmapTask().execute(viewHolder.iv, chatMessage.getUrl());// 启动AsyncTask,参数会在doInbackground中获取
			// bitmapUtils.display(viewHolder.iv, decode);
			System.out.println("getUrl--------" + chatMessage.getUrl());

		}
		viewHolder.content.setText(chatMessage.getMsg());
		viewHolder.createDate.setText(chatMessage.getDateStr());

		return convertView;
	}

	/**
	 * Handler和线程池的封装
	 * 
	 * 第一个泛型: 参数类型 第二个泛型: 更新进度的泛型, 第三个泛型是onPostExecute的返回结果
	 * 
	 * 
	 */
	class BitmapTask extends AsyncTask<Object, Void, Bitmap> {// 此处的obj就是下面的参数，执行exe方法时传的下面的参数，根据需要去传，比如url。
		// doinback方法params通过get（）方法就能获取这俩参数，然后执行需要耗时操作，也能将参数写在params...前面。而它类型就是构造函数里的第三个参数
		private ImageView ivPic;
		private String url;

		/**
		 * 后台耗时方法在此执行, 子线程
		 */
		@Override
		protected Bitmap doInBackground(Object... params) {
			ivPic = (ImageView) params[0];
			url = (String) params[1];
			ivPic.setTag(url);// 将url和imageview绑定
			return downloadBitmap(url);
		}

		/**
		 * 更新进度, 主线程
		 */
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * 耗时方法结束后,执行该方法, 主线程
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				String bindUrl = (String) ivPic.getTag();
				if (url.equals(bindUrl)) {// 确保图片设定给了正确的imageview
					ivPic.setImageBitmap(result);
					System.out.println("从网络读取图片啦...");
				}
			}
		}
	}

	/**
	 * 下载图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap downloadBitmap(String url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream inputStream = conn.getInputStream();

				// 图片压缩处理
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = 2;// 宽高都压缩为原来的二分之一, 此参数需要根据图片要展示的大小来确定
				option.inPreferredConfig = Bitmap.Config.RGB_565;// 设置图片格式

				Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
						option);
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}
		return null;
	}

	private class ViewHolder {
		public TextView createDate;
		public TextView name;
		public TextView content;
		public LinearLayout ly;
		public ImageView iv;
	}

}
