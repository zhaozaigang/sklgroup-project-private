package cn.sklgroup.netApprover;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class TaskInfoActivity extends Activity {
    /** Called when the activity is first created. */
	private GridView gridTools;
	private List<Map<String,?>> tools;
	private WebView webview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_info); 
        gridTools = (GridView) findViewById(R.id.grid_tools);
        gridTools.setAdapter(toolAdapter);
        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings =  webview.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
       // webview.addJavascriptInterface(new JavascriptImpl(this), "android");
        webview.setWebViewClient(new BaseWebViewClient ()); 
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.loadUrl("file:///android_asset/page/task_info.html");
        this.createTools();
        
    }
    class BaseWebViewClient extends WebViewClient{
    	public BaseWebViewClient() {
			super();
		}
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		// TODO Auto-generated method stub
    		return super.shouldOverrideUrlLoading(view, url);
    	}
    	
    	@Override
    	public void onReceivedError(WebView view, int errorCode,
    			String description, String failingUrl) {
    		Toast.makeText(TaskInfoActivity.this,"加载"+failingUrl+"失败 [code="+errorCode+"]"+description,Toast.LENGTH_LONG).show();
    		super.onReceivedError(view, errorCode, description, failingUrl);
    	}
    }
    private void createTools(){
    	tools = new ArrayList<Map<String,?>>();
        Map<String,String> item1 = new HashMap<String, String>();
     	item1.put("NAME",getResources().getString(R.string.TASK_PROJECT));
     	item1.put("URL","file:///android_asset/page/task_info.html");
     	tools.add(item1);
     	
     	Map<String,String> item2 = new HashMap<String, String>();
     	item2.put("NAME",getResources().getString(R.string.TASK_PRICE_TYPE));
     	item2.put("URL","file:///android_asset/page/task_price.html");
     	tools.add(item2);
     	
     	Map<String,String> item3 = new HashMap<String, String>();
     	item3.put("NAME",getResources().getString(R.string.TASK_INTENDER_PAY));
     	item3.put("URL","file:///android_asset/page/task_intender_pay.html");
     	tools.add(item3);
     	
     	Map<String,String> item4 = new HashMap<String, String>();
     	item4.put("NAME",getResources().getString(R.string.TASK_INTENDER_DAY));
     	item4.put("URL","file:///android_asset/page/task_intender_day.html");
     	tools.add(item4);
     	
    	Map<String,String> item5 = new HashMap<String, String>();
    	item5.put("NAME",getResources().getString(R.string.TASK_INTENDER_QUALITY));
    	item5.put("URL","file:///android_asset/page/task_intender_quality.html");
     	tools.add(item5);
     	
     	Map<String,String> item6 = new HashMap<String, String>();
    	item6.put("NAME",getResources().getString(R.string.TASK_INTENDER_SAFE));
    	item6.put("URL","file:///android_asset/page/task_intender_safe.html");
     	tools.add(item6);
     	
     	LayoutParams params = new LayoutParams(toolAdapter.getCount() * 120+40,
                LayoutParams.WRAP_CONTENT);
     	gridTools.setLayoutParams(params);
    	gridTools.setSelected(true);
    	gridTools.requestFocusFromTouch();
    	gridTools.setStretchMode(GridView.NO_STRETCH);
    	gridTools.setColumnWidth(120);
    	gridTools.setNumColumns(toolAdapter.getCount());
    	gridTools.setOnItemClickListener(itemClickListener);
    	gridTools.setSelection(0);
    }
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> adapterView, View view, int position,
    			long id) {
    		Log.d("TASK","MESSAGE.......");
    		int len =adapterView.getChildCount();
    		for (int i = 0;i<len;i++) {
    			View convertView = adapterView.getChildAt(i);
    			convertView.findViewById(android.R.id.icon1).setBackgroundResource(android.R.color.transparent);
    		}
    		//gridTools.scrollTo(-view.getLeft(),0);
    		
    		Map<String, ?> item = (Map<String, ?>) toolAdapter.getItem(position);
    		view.findViewById(android.R.id.icon1).setBackgroundResource(android.R.color.white);
    		String url =item.get("URL")==null?"":item.get("URL").toString();
    		if("".equals(url)){
    			webview.loadDataWithBaseURL("", "正在开发中....", "text/html", "utf-8", null);
    		}else{
    			webview.loadUrl(url);
    		}
    		
    	}
	};
	
	private OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case  R.id.title_btn_back:
					finish();
					break;
				default:
					break;
				}
			}
	};
	private BaseAdapter toolAdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(TaskInfoActivity.this).inflate(
						R.layout.sub_item, null);
			}
			
			Map<String,?> item = (Map<String, ?>) getItem(position);
			TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
			ImageView imageView = (ImageView) convertView.findViewById(android.R.id.icon1);
			textView.setText(item.get("NAME")+"");
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return tools.get(position);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tools==null?0:tools.size();
		}
	};
}