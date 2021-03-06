package com.ly.musicplay2.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ly.musicplay.R;
import com.ly.musicplay2.fragment.ContentFragment;
import com.ly.musicplay2.fragment.RightMenuFragment;

/**
 * 主界面
 * 
 * @author Administrator
 * 
 */
public class MainActivity extends SlidingFragmentActivity {

	private SlidingMenu slidingMenu;
	private Fragment mContent;
	private FragmentTransaction transaction;

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
	 * @param savedInstanceState
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
		setBehindContentView(R.layout.right_menu);// 设置侧边栏布局

		// 设置侧边栏
		FragmentManager fragmentManager = getFragmentManager();
		transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.right_menu, new RightMenuFragment());
		transaction.addToBackStack(null);// 模拟返回栈
		transaction.commit();

		switchConent(mContent);// 一进去程序就进入音乐中心，并且保存起来

		slidingMenu = getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// 设置全屏触摸
		slidingMenu.setSecondaryMenu(R.layout.right_menu);// 设置右侧边栏
		slidingMenu.setMode(SlidingMenu.RIGHT);// 设置展现模式
		slidingMenu.setBehindOffset(400);// 设置预留屏幕的宽度

	}

	/**
	 * 保存fragment
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		getFragmentManager().putFragment(outState, "mContent", mContent);
		Log.d("MainActivity", "mContent" + mContent);
		Log.d("MainActivity", "onSaveInstanceState执行了");
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

	/**
	 * 点侧边栏的退出按钮时销毁activity
	 */
	public void finishAll() {
		ContentFragment.stop();// 停止音乐
		finish();
	}
}
