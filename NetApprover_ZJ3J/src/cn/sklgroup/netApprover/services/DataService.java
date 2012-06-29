package cn.sklgroup.netApprover.services;




import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import cn.sklgroup.netApprover.R;
import cn.sklgroup.netApprover.WebActivity;
import cn.sklgroup.netApprover.util.AppSetting;
import cn.sklgroup.netApprover.util.DeviceUtil;
import cn.sklgroup.netApprover.util.JSONUtil;

public class DataService extends Service{
	private static String TAG= DataService.class.getSimpleName(); 
	public static boolean init=true;
	public static int lastNumber=0;
	
	private NotificationManager notificationManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		Log.d(TAG, "onCreate?"+init);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "START:"+startId);
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				
					try {
						String result = "";
						if(init){
							result = request(new String[]{"s","m"},new String[]{"a",AppSetting.USER},new String[]{"a","1"});
							init = false;
						}else{
							result = request(new String[]{"s","m"},new String[]{"a",AppSetting.USER});
							init = true;
						}
						
						if(!"".equals(result)){
							Map<String,?> json = (Map<String, ?>) JSONUtil.decode(result);
							if("1".equals(json.get("s")+"")){
								if("".equals(json.get("t")))
									return;
								Message msg= handler.obtainMessage(MESSAGE_NOTIFI);
								Bundle bundle = new Bundle();
								bundle.putString("title", json.get("t")+"");
								bundle.putString("message", json.get("m")+"");
								bundle.putString("len", json.get("l")+"");
								msg.setData(bundle);
								handler.sendMessage(msg);
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
		}}).start();
		if(Intent.ACTION_PICK.equals(intent.getAction())){
			new Thread(new Runnable(){
				@Override
				public void run() {
					
						try {
							boolean updateFlag = checkVersion(AppSetting.USER, AppSetting.PASSWORD);
							String url = "http://" +AppSetting.SERVER +":"+AppSetting.PORT+""+AppSetting.UPDATE_PATH;
							if(updateFlag)
								showNotification(getResources().getString(R.string.GENERAL_TIP), 
										getResources().getString(R.string.GENERAL_MSG_VERSION_NEW),url);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		
			}}).start();
		}
	}
	private final int MESSAGE_NOTIFI = 0x12300; 
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_NOTIFI:
				Bundle bundle = msg.getData();
				if(bundle.isEmpty())
					break;
				
				
				//if("".equals(bundle.getString("link")))
					showRunNotification(DataService.this,bundle);
//				else 
//					showNotification(bundle.getString("title"), bundle.getString("message"), bundle.getString("link"));
//				
				break;
			default:
				break;
			}
		};
	};
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	public static void showRunNotification(Context context,Bundle bundle){
		String title="",message="",link="",number="";
		NotificationManager mfm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(bundle.containsKey("title"))
			title = bundle.getString("title");
		if(bundle.containsKey("message"))
			message = bundle.getString("message");
		if(bundle.containsKey("link"))
			link = bundle.getString("link");
		if(bundle.containsKey("len"))
			number = bundle.getString("len");
		
		Notification notification = new Notification(R.drawable.notify_icon,message,System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR
		| Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent();
		intent.setClass(context, WebActivity.class);
		
		
		if(!"".equals(link))
			intent.putExtra(WebActivity.EXTRA_LINK, link);
		
		
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context,title, message, pendingIntent);
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.notifying_item);
		
		remoteViews.setTextViewText(R.id.list_item_title, title);
		remoteViews.setTextViewText(R.id.list_item_remark, message);
		//notification.defaults = Notification.DEFAULT_ALL;
		notification.contentView  = remoteViews;
		
		Intent clickIntent = new Intent(context, BootReceiver.class);
		clickIntent.setAction(Intent.ACTION_CALL_BUTTON);
		notification.contentView.setOnClickPendingIntent(R.id.btn_exit, PendingIntent.getBroadcast(context, 1, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		
		if(!"".equals(number)){
			int len = Integer.parseInt(number);
			notification.number =len;
			if(lastNumber!=len){
				notification.defaults = Notification.DEFAULT_ALL;
				lastNumber = len;
				mfm.notify(R.string.app_name, notification);
			}
		}else{
			notification.number =0;
			mfm.cancel(R.string.app_name);
		}
		
	}
	
	
	
	private void showNotification(String title,String message,String link){
		
		Notification notification = new Notification(R.drawable.notify_icon,message,System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;
		
		Intent intent = new Intent();
		if(!"".equals(link)){
			 Uri uri = Uri.parse(link);  
			 intent.setAction(Intent.ACTION_VIEW);
			 intent.setData(uri);
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this,title, message, pendingIntent);
//		
		RemoteViews remoteViews = new RemoteViews(this.getPackageName(),R.layout.notifying_item);
		
		remoteViews.setTextViewText(R.id.list_item_title,title);
		remoteViews.setTextViewText(R.id.list_item_remark,message);
		
		notification.contentView  = remoteViews;
		int  id = (int)System.currentTimeMillis();
		notificationManager.notify(id, notification);
	}
	public static void cancelNotification(Context context){
		
		NotificationManager mfm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mfm.cancel(R.string.app_name);
	}
	
	public static boolean login(String loginame,String password){
		Map<String,?> data = null;
		boolean result = false;
		try{
			String jsonStr =  DataService.request(new String[]{"s","l"}, new String[]{"a",loginame},new String[]{"a",password});
			data = (Map<String, ?>) JSONUtil.decode(jsonStr);
			result = "1".equals(data.get("s")+"");
			if(!result)
				throw new RuntimeException(data.get("m")+"");
			
		}catch (Exception e) {
			if(data==null)
				throw new RuntimeException("连接网络失败",e);
		}
		return result;
	}
	
	public static boolean checkVersion(String loginame,String password){
		Map<String,?> data = null;
		boolean result = false;
		String serverVer = "1.0";
		String localVer = DeviceUtil.getInstance().getDetail().get(DeviceUtil.APP_VERSION);
		try{
			String jsonStr =  DataService.request(new String[]{"s","u"});
			data = (Map<String, ?>) JSONUtil.decode(jsonStr);
			result = "1".equals(data.get("s")+"");
			serverVer = data.get("v")+"";
			if(!result)
				throw new RuntimeException(data.get("m")+"");
			else{
				result = !serverVer.equals(localVer);
			}
		}catch (Exception e) {
			if(data==null)
				throw new RuntimeException("连接网络失败",e);
		}
		return result;
	}
	
	public static String request(String[]... args){
		
		StringBuffer result = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		/** 采用POST方法 */
		
		String url = "http://" +AppSetting.SERVER +":"+AppSetting.PORT+""+AppSetting.PATH;
		Log.d(TAG, "url:" +url);
		HttpPost post = new HttpPost(url);
		try {
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();  

	        for (String[] values : args) {
				params.add(new BasicNameValuePair(values[0], values[1])); 
				Log.d(TAG, "params:" +values[0]+"="+values[1]);
			}
	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);  
	        // 传入请求体  
	        post.setEntity(entity);  
			/** 发出POST数据并获取返回数据 */
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(httpEntity.getContent()));
			
			String line = null;
			while ((line = buffReader.readLine()) != null)
				result.append(line);

		}catch (Exception e) {
			Log.e(TAG,e.getMessage());
		} finally {
			post.abort();
			client = null;
		}
		Log.d(TAG,result.toString());
		return result.toString();
	}
	
	
}
