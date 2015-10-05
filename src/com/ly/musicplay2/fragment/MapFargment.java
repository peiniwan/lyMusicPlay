package com.ly.musicplay2.fragment;

import android.view.View;

import com.ly.musicplay2.R;

public class MapFargment extends BaseFragment {

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.fragment_map, null);
		// mapView = (MapView) view.findViewById(R.id.map_view);
		// manager = new BMapManager(mActivity);
		// manager.init("AuWu11GvW5mAlqhaZ13d0m8W", null);
		// mapView.setBuiltInZoomControls(true);
		return view;
	}

	@Override
	public void initData() {
		super.initData();

	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

}
