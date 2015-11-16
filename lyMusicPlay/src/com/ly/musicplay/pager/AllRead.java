package com.ly.musicplay.pager;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.ly.musicplay.R;

/**
 * 全部消息。主要是练习了值动画
 * 
 * @author Administrator
 * 
 */
public class AllRead extends BasePager implements OnClickListener {
	private LinearLayout ly_more;
	private LinearLayout ly_des;
	private ImageView arrow;

	public AllRead(Activity activity) {
		super(activity);
	}

	@Override
	public void initViews() {
		super.initViews();
		View view = View.inflate(mActivity, R.layout.allread_pager, null);
		ly_more = (LinearLayout) view.findViewById(R.id.ly_more);
		ly_des = (LinearLayout) view.findViewById(R.id.ly_des);
		arrow = (ImageView) view.findViewById(R.id.arrow);

		LayoutParams layoutParams = (LayoutParams) ly_des.getLayoutParams();
		layoutParams.height = 0;// 默认为0
		ly_des.setLayoutParams(layoutParams);
		// 必须经过上面这三步才能设置大小
		arrow.setImageResource(R.drawable.arrow_down);
		ly_more.setOnClickListener(this);
		flContent.addView(view);
	}

	@Override
	public void initData() {
		super.initData();

	}

	boolean flag = false;// 默认不展开

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ly_more) {
			int startHeight;
			int targetHeight;
			if (!flag) { // 展开的动画
				startHeight = 0;
				targetHeight = getMeasureHeight();
				flag = true;
				// ly_des.setVisibility(View.VISIBLE);这样写也可以，不过显的特别突兀
				ly_des.getMeasuredHeight(); // 0
			} else {
				flag = false;
				// ly_des.setVisibility(View.GONE);
				startHeight = getMeasureHeight();
				targetHeight = 0;
			}
			// 值动画
			ValueAnimator animator = ValueAnimator.ofInt(startHeight,
					targetHeight);
			final LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) ly_des
					.getLayoutParams();
			animator.addUpdateListener(new AnimatorUpdateListener() { // 监听值的变化

				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					int value = (Integer) animator.getAnimatedValue();// 运行当前时间点的一个值
					layoutParams.height = value;
					ly_des.setLayoutParams(layoutParams);// 刷新界面
					System.out.println(value);
				}
			});

			animator.addListener(new AnimatorListener() { // 监听动画执行
				// 当动画开始执行的时候调用
				@Override
				public void onAnimationStart(Animator arg0) {
				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					if (flag) {
						arrow.setImageResource(R.drawable.arrow_up);
					} else {
						arrow.setImageResource(R.drawable.arrow_down);
					}
				}

				@Override
				public void onAnimationCancel(Animator arg0) {

				}
			});

			animator.setDuration(500);
			animator.start();
		}

	}

	private int getMeasureHeight() {
		int width = ly_des.getMeasuredWidth(); // 由于宽度不会发生变化 宽度的值取出来
		ly_des.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;// 让高度包裹内容，可以不写
		// 得先重新设置完规则再测量
		// 参数1 测量控件mode 参数2 大小
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY,
				width); // mode+size
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
				MeasureSpec.AT_MOST, 1000);// 我的高度 最大是1000
		// 测量规则 宽度是一个精确的值width, 高度最大是1000,以实际为准
		ly_des.measure(widthMeasureSpec, heightMeasureSpec); // 通过该方法重新测量控件

		return ly_des.getMeasuredHeight();
	}

}
