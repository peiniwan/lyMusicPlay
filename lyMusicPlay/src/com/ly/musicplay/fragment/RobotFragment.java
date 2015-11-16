package com.ly.musicplay.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ly.musicplay.R;
import com.ly.musicplay.bean.ChatMessage;
import com.ly.musicplay.bean.ChatMessage.Type;
import com.ly.musicplay.http.HttpUtils;

/**
 * 机器人聊天
 * 
 * @author Administrator
 * 
 */
public class RobotFragment extends BaseFragment implements OnClickListener {
	/**
	 * 展示消息的listview
	 */
	private ListView mChatView;
	/**
	 * 文本域
	 */
	private EditText mMsg;
	/**
	 * 存储聊天消息
	 */
	private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
	/**
	 * 适配器
	 */
	private ChatMessageAdapter mAdapter;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ChatMessage from = (ChatMessage) msg.obj;
			mDatas.add(from);
			mAdapter.notifyDataSetChanged();
			mChatView.setSelection(mDatas.size() - 1);
		};
	};

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fargment_chatting, null);
		mChatView = (ListView) view.findViewById(R.id.id_chat_listView);
		Button bt = (Button) view.findViewById(R.id.id_chat_send);
		bt.setOnClickListener(this);
		mMsg = (EditText) view.findViewById(R.id.id_chat_msg);
		mDatas.add(new ChatMessage(Type.INPUT, "我是小蜜，很高兴为您服务"));
		mAdapter = new ChatMessageAdapter(mActivity, mDatas);
		mChatView.setAdapter(mAdapter);
		return view;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_chat_send:
			final String msg = mMsg.getText().toString();
			if (TextUtils.isEmpty(msg)) {
				Toast.makeText(mActivity, "您还没有填写信息...", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
			to.setDate(new Date());
			mDatas.add(to);

			mAdapter.notifyDataSetChanged();
			mChatView.setSelection(mDatas.size() - 1);// 至于listview最后

			mMsg.setText("");// 清空

			// 关闭软键盘
			InputMethodManager imm = (InputMethodManager) mActivity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			// 得到InputMethodManager的实例
			if (imm.isActive()) {
				// 如果开启
				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
						InputMethodManager.HIDE_NOT_ALWAYS);
				// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
			}

			new Thread() {
				public void run() {
					ChatMessage from = null;
					try {
						from = HttpUtils.sendMsg(msg);
					} catch (Exception e) {
						from = new ChatMessage(Type.INPUT, "服务器挂了...");
					}
					Message message = Message.obtain();
					message.obj = from;
					mHandler.sendMessage(message);
				};
			}.start();
			break;

		default:
			break;
		}

	}

}
