package com.ly.musicplay.pager;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ly.musicplay.R;
import com.ly.musicplay.activity.MainActivity;
import com.ly.musicplay.utils.DrawableUtils;
import com.ly.musicplay.utils.UiUtils;
import com.ly.musicplay.view.Flowlayout;

/**
 * 歌手界面，练习用代码写布局
 * 
 * @author Administrator
 * 
 */
public class ArrtistPager extends BasePager {
	SharedPreferences sharedPreferences;
	private ScrollView scrollView;

	public ArrtistPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initViews() {
		super.initViews();

	}

	@Override
	public void initData() {
		super.initData();
		sharedPreferences = mActivity.getSharedPreferences(
				MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);

		scrollView = new ScrollView(UiUtils.getContext());
		scrollView.setBackgroundResource(R.drawable.grid_item_bg_normal);
		Flowlayout layout = new Flowlayout(UiUtils.getContext());
		int padding = UiUtils.dip2px(13);
		layout.setPadding(padding, padding, padding, padding);
		// layout.setOrientation(LinearLayout.VERTICAL);// 设置线性布局的方向

		int backColor = 0xffcecece;
		Drawable pressedDrawable = DrawableUtils.createShape(backColor);// 按下显示的图片

		String string = sharedPreferences.getString("arrtists", null);
		String[] strings = string.split(",");// 只显示歌手，不显示.MP3

		for (int i = 0; i < strings.length; i++) {
			TextView textView = new TextView(UiUtils.getContext());
			final String str = strings[i];
			textView.setText(str);

			Random random = new Random(); // 创建随机
			int red = random.nextInt(200) + 22;
			int green = random.nextInt(200) + 22;
			int blue = random.nextInt(200) + 22;// 有可能都是0或255成白色或者黑色了
			int color = Color.rgb(red, green, blue);// 范围 0-255
			GradientDrawable createShape = DrawableUtils.createShape(color); // 默认显示的图片
			StateListDrawable createSelectorDrawable = DrawableUtils
					.createSelectorDrawable(pressedDrawable, createShape);// 创建状态选择器
			textView.setBackgroundDrawable(createSelectorDrawable);
			textView.setTextColor(Color.WHITE);
			// textView.setTextSize(UiUtils.dip2px(14));

			int textPaddingV = UiUtils.dip2px(4);
			int textPaddingH = UiUtils.dip2px(7);
			textView.setPadding(textPaddingH, textPaddingV, textPaddingH,
					textPaddingV); // padding设置了一样还是不居中？

			textView.setClickable(true);// 设置textView可以被点击
			textView.setOnClickListener(new OnClickListener() { // 设置点击事件

				@Override
				public void onClick(View v) {
					Toast.makeText(UiUtils.getContext(), str, 0).show();
					Intent intent = new Intent(mActivity, MainActivity.class);
					intent.putExtra("str", str);
					mActivity.startActivity(intent);
				}
			});
			layout.addView(textView, new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, -2));// -2 包裹内容
		}

		scrollView.addView(layout);
		flContent.addView(scrollView);
	}
}
