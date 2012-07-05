package cn.sklgroup.netApprover.services;

import cn.sklgroup.netApprover.R;
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
    private static boolean CLOSE = false;
    private static boolean FIRST_START;
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	/**
    	 * 得到全局闹钟管理器
    	 */
    	am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
    	
    	mAlarmSender = PendingIntent.getService(context, 0, new Intent(context,DataService.class), 0);
	    try{
	        if(Intent.ACTION_CALL_BUTTON.equals(intent.getAction())){
	        	//退出程序	
		        am.cancel(mAlarmSender);
		        Toast.makeText(context, R.string.DISABLE_PUSH_MSG, Toast.LENGTH_SHORT).show();
		        //清除通知
		        DataService.cancelNotification(context);
		        //AppSetting.ENABLE_PUSH =false;
		        CLOSE = true;
	    	}else {
	    		AppSetting.loadSetting(context);
	    		if(Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
	    		{
	    			//AppSetting.ENABLE_PUSH = true;
	    			//AppSetting.FIRST_RUN = false;
	    			//DataService.lastNumber = 0;
	    			CLOSE = false;
	    			DataService.init =true;
	    			DataService.lastNumber = -1;
	    			
	    		}
	    		if(AppSetting.FIRST_RUN || CLOSE)
	    			return;
				
				Toast.makeText(context,R.string.ENABLE_PUSH, Toast.LENGTH_SHORT).show();
			       
	    		context.startService(new Intent(context, DataService.class));
	    		int interval = AppSetting.DATA_SERVICE_INTERVAL;
				
	    		am.cancel(mAlarmSender);
				long firstTime = SystemClock.elapsedRealtime();
				am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
			        		interval, mAlarmSender);
	    	}
	        AppSetting.saveSetting(context.getApplicationContext());
	    }catch (Exception e) {
	    	Log.d(TAG, "", e);
		}
        
    }

}
