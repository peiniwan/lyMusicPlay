package com.ly.musicplay.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ly.musicplay.R;
import com.ly.musicplay.fragment.ContentFragment;
import com.ly.musicplay.fragment.LeftMenuFragment;
import com.ly.musicplay.utils.ActivityCollector;

/**
 * 主界面
 * 
 * @author Administrator
 * 
 */
public class MainActivity extends BaseActivity {

	private SlidingMenu slidingMenu;
	private Fragment mContent;
	private FragmentTransaction transaction;
	public static final String PREFERENCES_NAME = "settings";// SharedPreferences名称
	public static final String PREFERENCES_MODE = "mode";// 存储播放模式

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题
		setContentView(R.layout.activity_main);
		initSlidingMenu(savedInstanceState);

	}

	/**
	 * 初始化SlidingMenu
	 * 
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {
		// 如果保存的状态不为空则得到之前保存的Fragment，否则实例化Fragment
		if (savedInstanceState != null) {
			mContent = getFragmentManager().getFragment(savedInstanceState,
					"mContent");// 获取保存的Fragment
		}
		if (mContent == null) {
			mContent = new ContentFragment();// 不为空是new音乐中心页面
		}
		setBehindContentView(R.layout.left_menu);// 设置侧边栏布局

		// 设置侧边栏
		FragmentManager fragmentManager = getFragmentManager();
		transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.left_menu, new LeftMenuFragment());
		transaction.addToBackStack(null);// 模拟返回栈
		transaction.commit();

		switchConent(mContent);// 一进去程序就进入音乐中心，并且保存起来

		slidingMenu = getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置全屏触摸
		slidingMenu.setSecondaryMenu(R.layout.left_menu);// 设置右侧边栏
		slidingMenu.setMode(SlidingMenu.LEFT);// 设置展现模式

		int width = getWindowManager().getDefaultDisplay().getWidth();// 获取屏幕宽度
		slidingMenu.setBehindOffset(width * 200 / 320);// 设置预留屏幕的宽度，按比例

	}

	/**
	 * 保存fragment
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getFragmentManager().putFragment(outState, "mContent", mContent);
	}

	/**
	 * 切换Fragment
	 * 
	 * @param fragment
	 */
	public void switchConent(Fragment fragment) {
		mContent = fragment;
		getFragmentManager().beginTransaction()
				.replace(R.id.fl_content, fragment).addToBackStack(null)
				.commit();// 加上这个addToBackStack(null)，ondestory不会调用
		getSlidingMenu().showContent();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		// 所有的activity最小化 不会执行ondestory 只执行onstop方法
	}

	/**
	 * 点侧边栏的退出按钮时销毁activity
	 */
	public void finishAll() {
		ContentFragment.stop();// 停止音乐
		ActivityCollector.finishAll();// 销毁所有activity
	}
}
