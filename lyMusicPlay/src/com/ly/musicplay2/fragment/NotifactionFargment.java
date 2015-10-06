package com.ly.musicplay2.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ly.musicplay2.R;
import com.ly.musicplay2.activity.MainActivity;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 消息中心
 * 
 * @author Administrator
 * 
 */
public class NotifactionFargment extends BaseFragment implements
		OnClickListener {

	private ViewPager mViewPager;
	private TabPageIndicator mIndicator;// 页面上的标签
	private ArrayList<TextView> mPagerList;// 存放viewpager页面
	private List<String> tab;// //存放标签内容
	private ImageView nextPage;// 下一页
	private ImageButton btnMenu;// 点击显示和隐藏侧边栏

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_notifation, null);
		mViewPager = (ViewPager) view.findViewById(R.id.vp_menu_detail);// viewpager实例
		mIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);// TabPageIndicator实例

		nextPage = (ImageView) view.findViewById(R.id.btn_next);
		btnMenu = (ImageButton) view.findViewById(R.id.btn_menu);
		nextPage.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		super.initData();
		TextView text1 = new TextView(mActivity);
		text1.setText("这里空空如也，木有人给你发消息哦~~");
		text1.setTextColor(Color.GRAY);
		text1.setTextSize(14);
		text1.setGravity(Gravity.CENTER);

		TextView text2 = new TextView(mActivity);
		text2.setText("您没有任何未读消息哦~~");
		text2.setTextColor(Color.GRAY);
		text2.setTextSize(14);
		text2.setGravity(Gravity.CENTER);

		TextView text3 = new TextView(mActivity);
		text3.setText("没有~~");
		text3.setTextColor(Color.GRAY);
		text3.setTextSize(14);
		text3.setGravity(Gravity.CENTER);

		mPagerList = new ArrayList<TextView>();
		mPagerList.add(text1);
		mPagerList.add(text2);
		mPagerList.add(text3);
		String[] objects = new String[] { "全部消息", "未读消息", "已读消息" };
		tab = Arrays.asList(objects);// 转化成list
		mViewPager.setAdapter(new MenuDetailAdapter());
		// 将viewpager和mIndicator关联起来,必须在viewpager设置完adapter后才能调用
		mIndicator.setViewPager(mViewPager);
	}

	/**
	 * viewpager适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class MenuDetailAdapter extends PagerAdapter {

		/**
		 * 重写此方法,返回页面标题,用于viewpagerIndicator的页签显示
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			return tab.get(position);
		}

		@Override
		public int getCount() {
			return mPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TextView textView = mPagerList.get(position);
			container.addView(textView);
			return mPagerList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	/**
	 * 隐藏、显示侧边栏
	 */
	protected void toggleSlidingMenu() {
		MainActivity mainUi = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUi.getSlidingMenu();
		slidingMenu.toggle();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next://下一页
			int currentItem = mViewPager.getCurrentItem();
			mViewPager.setCurrentItem(++currentItem);
			break;
		case R.id.btn_menu:
			toggleSlidingMenu();
			break;
		default:
			break;
		}
	}

}
