package cn.sklgroup.netApprover.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 
 * @author Kingly
 * 2012-1-17
 */
public class DeviceUtil {
	
	public final static String APP_ID ="APP_ID";
	public final static String APP_VERSION="APP_VERSION";
	public final static String APP_CODE="APP_CODE";
	public final static String IMEI="IMEI";
	public final static String PHONE="PHONE";
	public final static String MODEL="MODEL";
	public final static String SDK="SDK";
	public final static String RESOLUTION="RESOLUTION";
	public final static String DEVICE="DEVICE";
	public final static String DISPLAY="DISPLAY";
	
	
	private DeviceUtil(){}
	
	private static HashMap<String,String> DERVICE_DETAIL= null;
	private static DeviceUtil INSTANCE = new DeviceUtil();
	
	
	public static DeviceUtil getInstance(){
		return INSTANCE;
	}
	public HashMap<String,String> getDetail(){
		return DERVICE_DETAIL;
	}
	public void init(Context ctx){
		DERVICE_DETAIL= new HashMap<String, String>();
		PackageManager pm =null;  
		PackageInfo pi = null;
		TelephonyManager telMgr=null;
		WindowManager windowMgr=null;
		try {
			 pm = ctx.getPackageManager();
			 telMgr= (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE); 
			 windowMgr= (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
			 pi= pm.getPackageInfo(ctx.getPackageName(),PackageManager.GET_ACTIVITIES);   
	        
	       
		} catch (Exception e) {
			// TODO: handle exception
		}
      
        	
		if (pi != null) { 
        	DERVICE_DETAIL.put("APP_ID",pi.packageName);
        	DERVICE_DETAIL.put("APP_VERSION",pi.versionName == null ? "not set" : pi.versionName);
        	DERVICE_DETAIL.put("APP_CODE",pi.versionCode+"");
		}
		if(telMgr!=null){
			DERVICE_DETAIL.put("IMEI",telMgr.getDeviceId());
        	DERVICE_DETAIL.put("PHONE", telMgr.getLine1Number());
        	DERVICE_DETAIL.put("MODEL", Build.MODEL);
        	DERVICE_DETAIL.put("SDK", Build.VERSION.SDK);
		}
		if(windowMgr!=null){
			DisplayMetrics display  = new DisplayMetrics();
    		windowMgr.getDefaultDisplay().getMetrics(display);
			DERVICE_DETAIL.put("RESOLUTION",display.widthPixels +"x"+display.heightPixels);
        	
		}
    	
    	
    	DERVICE_DETAIL.put("VERSION_CODES.BASE",Build.VERSION_CODES.BASE+"");
    	DERVICE_DETAIL.put("VERSION.RELEASE",Build.VERSION.RELEASE);
    	DERVICE_DETAIL.put("CPU_ABI", android.os.Build.CPU_ABI);
    	DERVICE_DETAIL.put("TAGS", android.os.Build.TAGS);
    	DERVICE_DETAIL.put("DEVICE", android.os.Build.DEVICE);
    	DERVICE_DETAIL.put("DISPLAY", android.os.Build.DISPLAY);
    	DERVICE_DETAIL.put("BRAND", android.os.Build.BRAND);
    	DERVICE_DETAIL.put("BOARD", android.os.Build.BOARD);
    	DERVICE_DETAIL.put("USER", android.os.Build.USER);
    	DERVICE_DETAIL.put("MEMORY",getAvailMemory(ctx)+"/"+getTotalMemory(ctx));
        
	}
	public String getAvailMemory(Context ctx) {// ��ȡandroid��ǰ�����ڴ��С 

		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		//mi.availMem; ��ǰϵͳ�Ŀ����ڴ� 
		return Formatter.formatFileSize(ctx, mi.availMem);// ����ȡ���ڴ��С��� 
	}

	public String getTotalMemory(Context ctx) {
		String fileName = "/proc/meminfo";// ϵͳ�ڴ���Ϣ�ļ� 
		
		String[] arrayOfString;
		long initial_memory = 0;

		try {
		FileReader localFileReader = new FileReader(fileName);
		BufferedReader localBufferedReader = new BufferedReader(
		localFileReader, 8192);
		String line = localBufferedReader.readLine();// ��ȡmeminfo��һ�У�ϵͳ���ڴ��С 

		arrayOfString = line.split("\\s+");
		for (String num : arrayOfString) {
			Log.i(getClass().getSimpleName(), num + "\t");
		}
		initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// ���ϵͳ���ڴ棬��λ��KB������1024ת��ΪByte 
		localBufferedReader.close();

		} catch (IOException e) {
		}
		return Formatter.formatFileSize(ctx, initial_memory);// Byteת��ΪKB����MB���ڴ��С��� 
	}

}
