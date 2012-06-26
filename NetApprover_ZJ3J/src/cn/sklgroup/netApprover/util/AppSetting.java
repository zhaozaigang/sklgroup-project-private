package cn.sklgroup.netApprover.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppSetting {
	
	
	public static  String  CONFIG_VERSION = "1";
	
	public static String ERROR_HEAD ="ERROR:";
	public static String ENCODE ="UTF-8";
	
	public static String USER="";
	public static String PASSWORD="";

	//public static String BASE_SERVER_PATH="http://192.168.2.189:8080/mobileServer/services";
	public static String BASE_SERVER_PATH="http://www.mobile.sklgroup.cn/services";
	public static String SERVER ="61.183.130.134";
	public static int PORT =801;
	public static String PATH="/s.action?";
	public static String INDEX_PATH = "/m?";
	public static boolean FIRST_RUN = true;
	public static boolean ENABLE_PUSH = true;
	
	public static String CACHE_PATH="mnt/sdcard/.cache";
	public static String DOWNLOAD_PATH="mnt/sdcard/download";
	public static boolean LOG =false;
	
	public static int DATA_SERVICE_INTERVAL= 1000*60*3;
	
	public static void loadSetting(Context context)throws Exception
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Field[] fields = AppSetting.class.getFields();
		Map<String,?> map = preferences.getAll();
		if(map.isEmpty()|| !CONFIG_VERSION.equals(map.get("config_version"))){ 
			saveSetting(context);
			return;
		}
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = map.get(key).toString();
			if("".equals(value))
				continue;
			for (int i = 0; i < fields.length; i++) {
				Class<?> fieldClass = fields[i].getType();
				if(key.equalsIgnoreCase(fields[i].getName())){
					
						if(fieldClass.equals(int.class)){
							fields[i].set(null,Integer.valueOf(value).intValue());
						}
						else if(fieldClass.equals(float.class)){
							fields[i].setFloat(null,Integer.valueOf(value).floatValue());
						}
						else if(fieldClass.equals(double.class)){
							fields[i].setDouble(null,Integer.valueOf(value).doubleValue());
						}
						else if(fieldClass.equals(boolean.class)){
							fields[i].setBoolean(null,Boolean.valueOf(value).booleanValue());
						}
						else if(fieldClass.equals(String.class))
							fields[i].set(null,map.get(key).toString());
						else
							continue;
				}
			}
		}
	}	
	
	public static void saveSetting(Context context)throws Exception
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		Field[] fields = AppSetting.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			editor.putString(fields[i].getName().toLowerCase(),fields[i].get(null)+"");
		}
		editor.commit();
	}
	
	public static void clearSetting(Context context)throws Exception{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}

}
