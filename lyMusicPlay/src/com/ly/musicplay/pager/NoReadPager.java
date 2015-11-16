package com.ly.musicplay.pager;

import java.util.ArrayList;

import com.ly.musicplay.R;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ly.musicplay.utils.RotateUtils;
import com.ly.musicplay.utils.UiUtils;

public class NoReadPager extends BasePager implements OnClickListener {
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

	private ViewPager vp;
	private LinearLayout ll;
	private View view_red_point;
	private int mPointWidth;// 圆点间的距离
	// 图片资源ID
	private final int[] imageIds = { R.drawable.a, R.drawable.b, R.drawable.c,
			R.drawable.d, R.drawable.e };

	// 图片标题集合
	private final String[] imageDescriptions = { "巩俐不低俗，我就不能低俗",
			"扑树又回来啦！再唱经典老歌引万人大合唱", "揭秘北京电影如何升级", "乐视网TV版大派送", "热血屌丝的反杀" };
	private ArrayList<ImageView> imageList;
	private TextView tv;

	public NoReadPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initViews() {
		super.initViews();
		View view = View.inflate(mActivity, R.layout.custom_view, null);
		icon_home = (ImageView) view.findViewById(R.id.icon_home);
		icon_menu = (ImageView) view.findViewById(R.id.icon_menu);
		level1 = (RelativeLayout) view.findViewById(R.id.level1);
		level2 = (RelativeLayout) view.findViewById(R.id.level2);
		level3 = (RelativeLayout) view.findViewById(R.id.level3);

		vp = (ViewPager) view.findViewById(R.id.vp_guide);
		ll = (LinearLayout) view.findViewById(R.id.ll_point_group);
		view_red_point = view.findViewById(R.id.view_red_point);
		tv = (TextView) view.findViewById(R.id.tv);

		// initViewPager();
		// tv.setText(imageDescriptions[0]);
		// vp.setAdapter(new GuideAdapter());
		// vp.setOnPageChangeListener(new GuidePageListener());
		icon_home.setOnClickListener(this);
		icon_menu.setOnClickListener(this);
		flContent.addView(view);
	}

	private void initViewPager() {
		imageList = new ArrayList<ImageView>();
		// 初始化引导页的3个页面
		System.out.println("imageIds.length" + imageIds.length);
		for (int i = 0; i < imageIds.length; i++) {
			ImageView image = new ImageView(mActivity);
			image.setBackgroundResource(imageIds[i]);// 设置引导页背景,注意是Resource
			imageList.add(image);
		}
		// 初始化引导页的小圆点
		for (int i = 0; i < imageIds.length; i++) {
			View point = new View(mActivity);
			point.setBackgroundResource(R.drawable.shape_point_gray);// 设置引导页默认圆点
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					UiUtils.dip2px(10), UiUtils.dip2px(10));// 通过params设置布局的参数，括号里是宽高
			if (i > 0) {
				params.leftMargin = UiUtils.dip2px(10);// 设置圆点间隔
			}
			point.setLayoutParams(params);// 设置圆点的大小
			ll.addView(point);// 将圆点添加给线性布局
		}
		// 获取视图树, 对layout结束事件进行监听,获取小灰点的距离
		ll.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					// 当layout执行结束后回调此方法
					@Override
					public void onGlobalLayout() {
						System.out.println("layout 结束");
						ll.getViewTreeObserver().removeGlobalOnLayoutListener(
								this);
						mPointWidth = ll.getChildAt(1).getLeft()
								- ll.getChildAt(0).getLeft();
						System.out.println("圆点距离:" + mPointWidth);
					}
				});
	}

	/**
	 * ViewPager数据适配器
	 * 
	 * 
	 */
	class GuideAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return imageIds.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(imageList.get(position));
			return imageList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	/**
	 * viewpager的滑动监听
	 * 
	 * 
	 */
	class GuidePageListener implements OnPageChangeListener {
		// 滑动事件
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// System.out.println("当前位置:" + position + ";百分比:" + positionOffset
			// + ";移动距离:" + positionOffsetPixels);
			int len = (int) (mPointWidth * positionOffset) + position
					* mPointWidth;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view_red_point
					.getLayoutParams();// 获取当前红点的布局参数
			params.leftMargin = len;// 设置左边距
			view_red_point.setLayoutParams(params);// 重新给小红点设置布局参数
		}

		// 某个页面被选中
		@Override
		public void onPageSelected(int position) {
		}

		// 滑动状态发生变化
		@Override
		public void onPageScrollStateChanged(int state) {
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
		if (isLevel1show) {
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
