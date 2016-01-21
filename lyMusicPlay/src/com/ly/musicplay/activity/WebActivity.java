package com.ly.musicplay.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ly.musicplay.R;

public class WebActivity extends BaseActivity implements OnClickListener {
	private WebView mWebView;
	private ImageButton btnBack;// 后退按钮
	private ImageButton btnSize;// 设置网页文本大小
	private ImageButton btnShare;// 分享
	private TextView tv_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web);
		setBehindContentView(R.layout.left_menu);// 这个得写
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);// 取消侧边栏

		mWebView = (WebView) findViewById(R.id.wv_web);
		btnBack = (ImageButton) findViewById(R.id.btn_back);
		btnSize = (ImageButton) findViewById(R.id.btn_size);
		btnShare = (ImageButton) findViewById(R.id.btn_share);
		tv_title = (TextView) findViewById(R.id.title);

		btnBack.setOnClickListener(this);
		btnSize.setOnClickListener(this);
		btnShare.setOnClickListener(this);

		String url = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		tv_title.setText(title);
		WebSettings settings = mWebView.getSettings();// 获取设置对象
		settings.setJavaScriptEnabled(true);// 表示支持js，即网页的阅读全文
		settings.setBuiltInZoomControls(true);// 显示放大缩小按钮
		settings.setUseWideViewPort(true);// 支持双击缩放
		mWebView.setWebViewClient(new WebViewClient() {
			/**
			 * 网页开始加载
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				System.out.println("网页开始加载");
			}

			/**
			 * 网页加载结束
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				System.out.println("网页开始结束");
			}

			/**
			 * 所有跳转的链接都会在此方法中回调
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// tel:110,在这里可以获取连接，可以做很多操作，比如获取了110，我们可以打电话，获取的URL和我们黄网数据库匹配了，可以拦截等
				System.out.println("跳转url:" + url);
				view.loadUrl(url);// 自己的应用中打开网页，不会调到系统浏览器了
				return true;
				// return super.shouldOverrideUrlLoading(view, url);
			}
		});
		// mWebView.goBack()//比如弄个上一页下一页的按钮执行这个方法
		mWebView.setWebChromeClient(new WebChromeClient() {
			/**
			 * 进度发生变化
			 */
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}

			/**
			 * 获取网页标题
			 */
			@Override
			public void onReceivedTitle(WebView view, String title) {
				tv_title.setText(title);
				super.onReceivedTitle(view, title);
			}
		});
		mWebView.loadUrl(url);// 默认加载网页，可以随便写
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();// 销毁activity，即显示出了上一个页面
			break;
		case R.id.btn_size:
			showChooseDialog();
			break;
		case R.id.btn_share:
			showShare();
			break;
		default:
			break;
		}
	}

	private int mCurrentChooseItem;// 记录当前选中的item, 点击确定前
	private int mCurrentItem = 2;// 记录当前选中的item,
									// 点击确定后，默认是标准字体，有个问题：在进来又变回了标准，可以保存起来
	private String title;

	/**
	 * 显示选择对话框
	 */
	private void showChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] items = new String[] { "超大号字体", "大号字体", "正常字体", "小号字体",
				"超小号字体" };
		builder.setTitle("字体设置");
		builder.setSingleChoiceItems(items, mCurrentItem,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("选中:" + which);
						mCurrentChooseItem = which;
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				WebSettings settings = mWebView.getSettings();
				switch (mCurrentChooseItem) {
				case 0:
					settings.setTextSize(TextSize.LARGEST);
					break;
				case 1:
					settings.setTextSize(TextSize.LARGER);
					break;
				case 2:
					settings.setTextSize(TextSize.NORMAL);
					break;
				case 3:
					settings.setTextSize(TextSize.SMALLER);
					break;
				case 4:
					settings.setTextSize(TextSize.SMALLEST);
					break;
				default:
					break;
				}
				mCurrentItem = mCurrentChooseItem;
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 分享, 注意在sdcard根目录放test.jpg，就是这个天气图片
	 */
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();

		oks.setTheme(OnekeyShareTheme.SKYBLUE);// 设置天蓝色的主题

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));

		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(title);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}
}
