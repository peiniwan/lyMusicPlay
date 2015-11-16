package com.ly.musicplay.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment基类
 * 
 * 
 */
public abstract class BaseFragment extends Fragment {

	public static Activity mActivity;

	// fragment创建，activity此时还没有创建完成
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}

	// 处理fragment的布局
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initViews();
	}

	// 依附的activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	// 子类必须实现初始化布局的方法
	public abstract View initViews();

	// 初始化数据, 可以不实现
	public void initData() {

	}

}
