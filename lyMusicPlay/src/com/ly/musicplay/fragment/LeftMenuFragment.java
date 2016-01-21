package com.ly.musicplay.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ly.musicplay.R;
import com.ly.musicplay.activity.MainActivity;
import com.ly.musicplay.utils.ActivityCollector;

/**
 * 侧边栏
 * 
 * @author Administrator
 * 
 */
public class LeftMenuFragment extends BaseFragment {
	private List<String> menu;// 存放菜单
	private int mCurrentPos;// 当前被点击的菜单项
	public static MenuAdapter mAdapter;
	private ListView lv;

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.left_menu, null);
		lv = (ListView) view.findViewById(R.id.lv_list);

		return view;
	}

	@Override
	public void initData() {
		super.initData();
		menu = new ArrayList<String>();
		menu.add("音乐中心");
		menu.add("游戏中心");
		menu.add("地图中心");
		menu.add("新闻中心");
		menu.add("机器人");
		menu.add("退出");
		mAdapter = new MenuAdapter();
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Fragment newContent = null;
				mCurrentPos = arg2;// 当前点击的菜单
				mAdapter.notifyDataSetChanged();// 需要刷新，就会调用BaseAdapter的getview方法
				// editProcess(arg2);// 退出程序

				switch (arg2) {
				case 0:
					newContent = new ContentFragment();
					break;
				case 1:
					newContent = new GameFargment();
					break;
				case 2:
					newContent = new MapFargment();
					break;
				case 3:
					newContent = new NewsFragment();
					break;
				case 4:
					newContent = new RobotFragment();
					break;
				case 5:
					MainActivity mainui = (MainActivity) mActivity;
					mainui.finishAll();
					break;
				default:
					break;
				}
				if (newContent != null) {
					switchFragment(newContent);
				}
				toggleSlidingMenu();// 隐藏
			}
		});
	}

	/**
	 * 切换页面
	 * 
	 * @param newContent
	 */
	protected void switchFragment(Fragment newContent) {
		if (getActivity() == null) {
			return;
		}
		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchConent(newContent);
		}

	}

	/**
	 * 切换SlidingMenu的状态
	 */
	protected void toggleSlidingMenu() {
		MainActivity mainUi = (MainActivity) mActivity;// 拿到mainactivity
		SlidingMenu slidingMenu = mainUi.getSlidingMenu();// 拿到slidingmenu对象
		slidingMenu.toggle();// 切换状态, 显示时隐藏, 隐藏时显示
	}

	/**
	 * 菜单选项适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menu.size();
		}

		@Override
		public String getItem(int position) {
			return menu.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				view = View.inflate(mActivity, R.layout.left_menu_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tv = (TextView) view.findViewById(R.id.tv_title);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.tv.setText(getItem(position));
			// viewHolder.tv.setText(menu.get(position));
			if (mCurrentPos == position) {// 判断当前绘制的view是否被选中
				// 显示红色
				viewHolder.tv.setEnabled(true);
			} else {
				viewHolder.tv.setEnabled(false);
			}
			return view;
		}

		class ViewHolder {
			TextView tv;
		}

	}

}
