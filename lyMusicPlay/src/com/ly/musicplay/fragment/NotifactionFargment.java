package com.ly.musicplay.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ly.musicplay.R;
import com.ly.musicplay.activity.MainActivity;
import com.ly.musicplay.pager.BasePager;
import com.ly.musicplay.pager.LaJiPager;
import com.ly.musicplay.pager.MorePager;
import com.ly.musicplay.pager.RotatePager;
import com.ly.musicplay.pager.WaterPager;
import com.ly.musicplay.view.NoInterceptViewPager;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 消息中心
 * 
 * @author Administrator
 * 
 */
public class NotifactionFargment extends BaseFragment implements
		OnClickListener {

	private NoInterceptViewPager mViewPager;
	private TabPageIndicator mIndicator;// 页面上的标签
	private ArrayList<BasePager> mPagerList;// 存放view页面
	private List<String> tab;// //存放标签内容
	private ImageButton nextPage;// 下一页
	private ImageButton btnMenu;// 点击显示和隐藏侧边栏

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_notifation, null);
		mViewPager = (NoInterceptViewPager) view
				.findViewById(R.id.vp_menu_detail);// viewpager实例
		mIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);// TabPageIndicator实例

		nextPage = (ImageButton) view.findViewById(R.id.btn_next);
		btnMenu = (ImageButton) view.findViewById(R.id.btn_menu);
		nextPage.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		super.initData();
		mPagerList = new ArrayList<BasePager>();
		mPagerList.add(new RotatePager(mActivity));
		mPagerList.add(new WaterPager(mActivity));
		mPagerList.add(new LaJiPager(mActivity));
		mPagerList.add(new MorePager(mActivity));
		String[] objects = new String[] { "旋转菜单", "绚丽水波", "已读消息", "更多介绍" };
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
			BasePager basePager = mPagerList.get(position);
			container.addView(basePager.mRootView);
			return basePager.mRootView;
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

	// TabPageIndicator好像只能把Tab的图片设置在右边用来点下一页，而不能设置在左边点下一页，xml文件会报错
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:// 下一页
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
