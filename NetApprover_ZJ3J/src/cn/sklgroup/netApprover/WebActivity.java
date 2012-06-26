package cn.sklgroup.netApprover;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.sklgroup.netApprover.base.BaseActivity;
import cn.sklgroup.netApprover.services.BootReceiver;
import cn.sklgroup.netApprover.util.AppSetting;
import cn.sklgroup.netApprover.util.CrashHandler;

public class WebActivity extends BaseActivity {
	public static final String EXTRA_LINK="web.link";
	private static final String TAG = ChromeClient.class.getSimpleName();
	private static final int DIALOG_LADING = 0x123;
	private static final int DIALOG_EXIT = 0x124;
	private WebView webview;
	private ProgressBar progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		webview = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webview.getSettings();
		webSettings.setAppCacheEnabled(false);
		webSettings.setJavaScriptEnabled(true);
		webview.setWebViewClient(new BaseWebViewClient ());
		webview.setWebChromeClient(new ChromeClient());
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		String url = "http://" +AppSetting.SERVER +":"+AppSetting.PORT+""+AppSetting.INDEX_PATH;
		
		Intent data = getIntent();
		if(data.hasExtra(EXTRA_LINK)){
			url = data.getStringExtra(EXTRA_LINK);
		}else{
			url += "u="+AppSetting.USER+"&p="+AppSetting.PASSWORD;
		}
		webview.loadUrl(url);
	}
	@Override
	protected void onStart() {
		super.onStart();
		if(CrashHandler.reportExists(this)){
			CrashHandler.sendCrashReport(this);
			return ;
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EXIT:
			return new AlertDialog.Builder(this)
				.setTitle(R.string.GENERAL_TIP)
				.setMessage(R.string.GENERAL_CONFIRM_EXIT)
				.setPositiveButton(R.string.GENERAL_EXIT,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				})
				.setNegativeButton(R.string.GENERAL_CANCEL,null)
				.create();
		case DIALOG_LADING:
			return new AlertDialog.Builder(this)
				.setTitle(R.string.GENERAL_CONFIRM)
				.setMessage(R.string.GENERAL_NET_PROCESS)
				.create();
		default:
			return super.onCreateDialog(id);
		}

	}
	@Override
	public void onBackPressed() {
		if(webview.canGoBack())
			webview.goBack();
		else{
			showDialog(DIALOG_EXIT);
		}
	}
	class ChromeClient extends WebChromeClient {
		

		// 处理Alert事件
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			// 构建一个Builder来显示网页中的alert对话框
			new AlertDialog.Builder(WebActivity.this).setTitle("提示")
					.setMessage(message).setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}

							}).setCancelable(false).create().show();

			return true;

		}

		@Override
		public void onReceivedTitle(WebView view, String title) {

			//WebActivity.this.setTitle("可以用onReceivedTitle()方法修改网页标题");
			//Toast.makeText(WebActivity.this, title, Toast.LENGTH_SHORT).show();
			super.onReceivedTitle(view, title);

		}
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			progressBar.setProgress(newProgress);
			Log.d(TAG, "onProgressChanged:"+newProgress);
			if(progressBar.getProgress()==progressBar.getMax())
				progressBar.setVisibility(View.GONE);
			else
				progressBar.setVisibility(View.VISIBLE);
			//super.onProgressChanged(view, newProgress);
		}
		// 处理Confirm事件

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			new AlertDialog.Builder(WebActivity.this).setTitle("确认")
					.setMessage(message).setPositiveButton("确定",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}
							}).setNeutralButton(android.R.string.cancel,
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									result.cancel();
								}
							}).setCancelable(false).create().show();
			return true;
		}

		// 处理提示事件

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue,

				JsPromptResult result) {
			// 看看默认的效果
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, R.string.GENERAL_SET);
		if(AppSetting.ENABLE_PUSH)
			menu.add(0, 2, 2, R.string.DISABLE_PUSH);
		else
			menu.add(0, 2, 2, R.string.ENABLE_PUSH);
		menu.add(0, 3, 3, R.string.GENERAL_EXIT);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==3){
			this.finish();
		}else if(item.getItemId()==1){
			Intent intent = new Intent(this,ConfigActivity.class);
			intent.setAction(Intent.ACTION_EDIT);
			startActivity(intent);
		}else if(item.getItemId()==2){
			Intent intent = new Intent(this,BootReceiver.class);
			if(AppSetting.ENABLE_PUSH)
				intent.setAction(Intent.ACTION_CALL_BUTTON);
			else
				intent.setAction(Intent.ACTION_MEDIA_BUTTON);
			sendBroadcast(intent);
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	class BaseWebViewClient extends WebViewClient {
		public BaseWebViewClient() {
			super();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub

			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Toast.makeText(
					WebActivity.this,
					"加载" + failingUrl + "失败 [code=" + errorCode + "]"
							+ description, Toast.LENGTH_LONG).show();
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}
}