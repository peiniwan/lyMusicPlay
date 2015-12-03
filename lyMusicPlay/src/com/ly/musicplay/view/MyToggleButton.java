package com.ly.musicplay.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.ly.musicplay.R;

public class MyToggleButton extends View implements OnClickListener {
	/**
	 * 做为背景的图片
	 */
	private Bitmap backgroundBitmap;
	/**
	 * 可以滑动的图片
	 */
	private Bitmap slideBtn;
	private Paint paint;
	/**
	 * 滑动按钮的左边界
	 */
	private float slideBtn_left;

	/**
	 * 在代码里面创建对象的时候，使用此构造方法
	 */
	public MyToggleButton(Context context) {
		super(context);
	}

	/**
	 * 在布局文件中声名的view，创建时由系统自动调用。
	 * 
	 */
	public MyToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView();
	}

	private void initView() {
		// 初始化图片
		backgroundBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_background);
		slideBtn = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_button);

		// 初始化 画笔
		paint = new Paint();
		paint.setAntiAlias(true); // 打开抗矩齿

		// 添加onclick事件监听
		setOnClickListener(this);

		flushState();
	}

	@Override
	/**
	 * 测量尺寸时的回调方法 
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 设置当前view的大小 width :view的宽度 height :view的高度 （单位：像素）
		 */
		setMeasuredDimension(backgroundBitmap.getWidth(),
				backgroundBitmap.getHeight());
	}

	/**
	 * 当前开关的状态 true 为开
	 */
	private boolean currState = true;

	public boolean isChecked() {
		return currState;
	}

	public void setChecked() {
		if (!isDrag) {
			currState = !currState;
			flushState();
		}
	}

	@Override
	/**
	 * 绘制当前view的内容
	 */
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);

		// 绘制 背景
		/*
		 * backgroundBitmap 要绘制的图片 left 图片的左边届 top 图片的上边届 paint 绘制图片要使用的画笔
		 */
		canvas.drawBitmap(backgroundBitmap, 0, 0, paint);

		// 绘制 可滑动的按钮
		canvas.drawBitmap(slideBtn, slideBtn_left, 0, paint);
	}

	/**
	 * 判断是否发生拖动， 如果拖动了，就不再响应 onclick 事件
	 * 
	 */
	private boolean isDrag = false;

	@Override
	public void onClick(View v) {
		/*
		 * 如果没有拖动，才执行改变状态的动作
		 */
		if (!isDrag) {
			currState = !currState;
			flushState();
		}
	}

	/**
	 * down 事件时的x值
	 */
	private int firstX;
	/**
	 * touch 事件的上一个x值
	 */
	private int lastX;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		SwitchDrag(event);

		flushView();

		return true; // 将事件消费掉
	}

	public interface ShowMenu {
		void onMenu();

		public void offMenu();
	}

	// , ShowMenu showMenu
	public void SwitchDrag(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			firstX = lastX = (int) event.getX();
			isDrag = false;

			break;
		case MotionEvent.ACTION_MOVE:

			// 判断是否发生拖动
			if (Math.abs(event.getX() - firstX) > 5) {
				isDrag = true;
			}

			// 计算 手指在屏幕上移动的距离
			int dis = (int) (event.getX() - lastX);

			// 将本次的位置 设置给lastX
			lastX = (int) event.getX();

			// 根据手指移动的距离，改变slideBtn_left 的值
			slideBtn_left = slideBtn_left + dis;
			break;
		case MotionEvent.ACTION_UP:

			// 在发生拖动的情况下，根据最后的位置，判断当前开关的状态
			if (isDrag) {

				int maxLeft = backgroundBitmap.getWidth() - slideBtn.getWidth(); // slideBtn
																					// 左边届最大值
				/*
				 * 根据 slideBtn_left 判断，当前应是什么状态
				 */
				if (slideBtn_left > maxLeft / 2) { // 此时应为 打开的状态
					currState = true;
					// showMenu.onMenu();
				} else {
					currState = false;
					// showMenu.offMenu();

				}
				flushState();
			}
			break;
		}
	}

	private void flushState() {
		if (currState) {
			slideBtn_left = backgroundBitmap.getWidth() - slideBtn.getWidth();
		} else {
			slideBtn_left = 0;
		}

		flushView();
	}

	private void flushView() {
		/*
		 * 对 slideBtn_left 的值进行判断 ，确保其在合理的位置 即 0<=slideBtn_left <= maxLeft
		 */

		int maxLeft = backgroundBitmap.getWidth() - slideBtn.getWidth(); // slideBtn
																			// 左边届最大值

		// 确保 slideBtn_left >= 0
		slideBtn_left = (slideBtn_left > 0) ? slideBtn_left : 0;

		// 确保 slideBtn_left <=maxLeft
		slideBtn_left = (slideBtn_left < maxLeft) ? slideBtn_left : maxLeft;

		/*
		 * 刷新当前视图 导致 执行onDraw执行
		 */
		invalidate();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);// 用getParent去请求,不拦截
		return super.dispatchTouchEvent(event);
	}

}
