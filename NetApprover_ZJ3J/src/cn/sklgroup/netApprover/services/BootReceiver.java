package cn.sklgroup.netApprover.services;

import cn.sklgroup.netApprover.util.AppSetting;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
	private String TAG = BootReceiver.class.getSimpleName();
    private PendingIntent mAlarmSender;
    private AlarmManager am;
    private static boolean close = false;
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	/**
    	 * 得到全局闹钟管理器
    	 */
    	am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
    	
    	mAlarmSender = PendingIntent.getService(context, 0, new Intent(context,DataService.class), 0);
	    
        if(Intent.ACTION_CALL_BUTTON.equals(intent.getAction())){
        	//退出程序	
	        am.cancel(mAlarmSender);
	        Toast.makeText(context, "推送已关闭", Toast.LENGTH_SHORT).show();
	        //清除通知
	        DataService.cancelNotification(context);
	        close = true;
    	}else {
    		if(close)
    			return;
    		context.startService(new Intent(context, DataService.class));
    		resetAlarm(context);
    	}
        
    }
    /**
     * 根据配置设置闹钟
     * @param context
     * @param mAlarmSender
     */
    private void resetAlarm(Context context){
   	 try {
			AppSetting.loadSetting(context);
			//** 访问服务器的时间间隔
			int interval = AppSetting.DATA_SERVICE_INTERVAL/2;
			if(!AppSetting.FIRST_RUN){
				am.cancel(mAlarmSender);
				long firstTime = SystemClock.elapsedRealtime();
				am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
		        		interval, mAlarmSender);
			}
		} catch (Exception e) {
			Log.d(TAG, "", e);
		}
   }
}
