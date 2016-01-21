package com.ly.musicplay.pager;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ly.musicplay.R;
import com.ly.musicplay.utils.RotateUtils;
import com.ly.musicplay.view.MyToggleButton;
import com.ly.musicplay.view.MyToggleButton.ShowMenu;
import com.ly.musicplay.view.NoInterceptViewPager;

public class RotatePager extends BasePager implements OnClickListener {
	private ImageView icon_menu;
	private ImageView icon_home;

	private RelativeLayout level1;
	private RelativeLayout level2;
	private RelativeLayout level3;
	/**
	 * 判断 第3级菜单是否显示 true 为显示
	 */
	private boolean isLevel3Show = true;
	/**
	 * 判断 第2级菜单是否显示 true 为显示
	 */
	private boolean isLevel2Show = true;
	/**
	 * 判断 第1级菜单是否显示 true 为显示
	 */
	private boolean isLevel1show = true;

	private ArrayList<ImageView> imageList;
	private NoInterceptViewPager viewPager;

	private LinearLayout pointGroup;// 灰点和黑点的布局
	private TextView iamgeDesc;// 描述文字

	/**
	 * 上一个页面的位置
	 */
	protected int lastPosition;

	private int[] imageIds;
	private String[] imageDescriptions;
	public static Handler mHandler;// 发送消息
	private MyToggleButton my_toggle_btn;

	public RotatePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initViews() {
		super.initViews();
		View view = View.inflate(mActivity, R.layout.rotate_pager, null);
		icon_home = (ImageView) view.findViewById(R.id.icon_home);
		icon_menu = (ImageView) view.findViewById(R.id.icon_menu);
		level1 = (RelativeLayout) view.findViewById(R.id.level1);
		level2 = (RelativeLayout) view.findViewById(R.id.level2);
		level3 = (RelativeLayout) view.findViewById(R.id.level3);

		viewPager = (NoInterceptViewPager) view.findViewById(R.id.viewpager);
		pointGroup = (LinearLayout) view.findViewById(R.id.point_group);
		iamgeDesc = (TextView) view.findViewById(R.id.image_desc);

		imageIds = new int[] { R.drawable.a, R.drawable.b, R.drawable.c,
				R.drawable.d, R.drawable.e };
		imageDescriptions = new String[] { "巩俐不低俗，我就不能低俗",
				"扑树又回来啦！再唱经典老歌引万人大合唱", "揭秘北京电影如何升级", "乐视网TV版大派送", "热血屌丝的反杀" };
		iamgeDesc.setText(imageDescriptions[0]);

		my_toggle_btn = (MyToggleButton) view.findViewById(R.id.my_toggle_btn);

		my_toggle_btn.setShowMenu(new ShowMenu() {
			
			@Override
			public void onMenu() {
				isLevel1show = false;			
				changeLevel1State();
			}
			
			@Override
			public void offMenu() {
				isLevel1show = true;
				changeLevel1State();
			}
		});

		initViewPager();

		icon_home.setOnClickListener(this);
		icon_menu.setOnClickListener(this);
		flContent.addView(view);
	}

	/**
	 * ViewPager相关操作
	 */
	private void initViewPager() {
		imageList = new ArrayList<ImageView>();
		for (int i = 0; i < imageIds.length; i++) {

			// 初始化图片资源
			ImageView image = new ImageView(mActivity);
			image.setBackgroundResource(imageIds[i]);
			imageList.add(image);

			// 添加指示点
			ImageView point = new ImageView(mActivity);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			params.rightMargin = 20;
			point.setLayoutParams(params);

			point.setBackgroundResource(R.drawable.point_bg);// 状态选择器
			if (i == 0) {
				point.setEnabled(true);
			} else {
				point.setEnabled(false);
			}
			pointGroup.addView(point);
		}
		viewPager.setAdapter(new MyPagerAdapter());// 设置适配器
		viewPager.setOnPageChangeListener(new ViewPagerOnPageChange());// 设置页面改变监听的时候才设置当前的文字描述和黑点
		viewPager.setOnTouchListener(new ViewPagerTouchListener());// 设置触摸监听，使触摸的时候不轮播
		// 自动轮播条显示
		if (mHandler == null) {
			mHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
					// 继续延时3秒发消息,形成循环，可以handleMessage方法里发送消息的
					mHandler.sendEmptyMessageDelayed(0, 3000);
				};
			};
			mHandler.sendEmptyMessageDelayed(0, 3000);// 延时3秒后发消息
		}

	}

	class ViewPagerOnPageChange implements OnPageChangeListener {
		@Override
		/**
		 * 页面切换后调用 
		 * position  新的页面位置
		 */
		public void onPageSelected(int position) {

			position = position % imageList.size();

			// 设置文字描述内容
			iamgeDesc.setText(imageDescriptions[position]);

			// 改变指示点的状态
			// 把当前点enbale 为true
			pointGroup.getChildAt(position).setEnabled(true);
			// 把上一个点设为false
			pointGroup.getChildAt(lastPosition).setEnabled(false);
			lastPosition = position;

		}

		@Override
		/**
		 * 页面正在滑动的时候，回调
		 */
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		/**
		 * 当页面状态发生变化的时候，回调
		 */
		public void onPageScrollStateChanged(int state) {

		}

	}

	/**
	 * ViewPager的触摸监听
	 * 
	 * 
	 */
	class ViewPagerTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mHandler.removeCallbacksAndMessages(null);
				break;
			case MotionEvent.ACTION_CANCEL:
				// 因为当按下没抬起，而是滑了一下，那么事件就取消了，需要重新发送一下
			case MotionEvent.ACTION_UP:
				mHandler.sendEmptyMessageDelayed(0, 3000);
				break;
			default:
				break;
			}
			return false;// 必须返回FALSE，否则viewpager不能滑动
		}
	}

	private class MyPagerAdapter extends PagerAdapter {
		@Override
		/**
		 * 获得页面的总数
		 */
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		/**
		 * 获得相应位置上的view
		 */
		public Object instantiateItem(ViewGroup container, int position) {
			// 给 container 添加一个view
			container.addView(imageList.get(position % imageList.size()));
			return imageList.get(position % imageList.size());
		}

		@Override
		/**
		 * 判断 view和object的对应关系 
		 */
		public boolean isViewFromObject(View view, Object object) {
			if (view == object) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		/**
		 * 销毁对应位置上的object
		 */
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView((View) object);
			object = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.icon_menu: // 处理 menu 图标的点击事件
			// 如果第3级菜单是显示状态，那么将其隐藏
			if (isLevel3Show) {
				// 隐藏 第3级菜单
				RotateUtils.startAnimOut(level3);
			} else {
				// 如果第3级菜单是隐藏状态，那么将其显示
				RotateUtils.startAnimIn(level3);
			}

			isLevel3Show = !isLevel3Show;

			break;
		case R.id.icon_home: // 处理 home 图标 的点击事件
			// 如果第2级菜单是显示状态，那么就隐藏，2，3级菜单
			if (isLevel2Show) {
				RotateUtils.startAnimOut(level2);
				isLevel2Show = false;

				if (isLevel3Show) { // 如果此时，第3级菜单也显示，那也将其隐藏
					RotateUtils.startAnimOut(level3, 200);
					isLevel3Show = false;
				}

			} else {
				// 如果第2级菜单是隐藏状态，那么就显示2级菜单
				RotateUtils.startAnimIn(level2);
				isLevel2Show = true;
			}

			break;

		}

	}

	/**
	 * 改变第1级菜单的状态
	 */
	private void changeLevel1State() {
		// 如果第1级菜单是显示状态，那么就隐藏 1，2，3级菜单
		if (isLevel1show == true) {
			RotateUtils.startAnimOut(level1);
			isLevel1show = false;

			if (isLevel2Show) { // 判断2级菜单是否显示
				RotateUtils.startAnimOut(level2, 100);
				isLevel2Show = false;
				if (isLevel3Show) { // 判断3级菜单是否显示
					RotateUtils.startAnimOut(level3, 200);
					isLevel3Show = false;
				}
			}

		} else {
			// 如果第1级菜单是隐藏状态，那么就显示 1，2级菜单
			RotateUtils.startAnimIn(level1);
			isLevel1show = true;

			RotateUtils.startAnimIn(level2, 200);
			isLevel2Show = true;

		}

	}

}
