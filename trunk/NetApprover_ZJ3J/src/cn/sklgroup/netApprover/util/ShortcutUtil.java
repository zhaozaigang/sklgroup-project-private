package cn.sklgroup.netApprover.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.net.Uri;
import cn.sklgroup.netApprover.R;

/**
 * 
 * @author wanxianze@gmail.com
 *
 */
public class ShortcutUtil {
	private Context  context;
	public ShortcutUtil(Context context) {
		this.context = context;
	}
	/**
	 * 是否存在 快捷方式
	 * @return
	 */
	public boolean hasShortcut()
	{
	        boolean isInstallShortcut = false;
	        final ContentResolver cr = context.getContentResolver();
	        final String AUTHORITY ="com.android.launcher.settings";
	        final Uri CONTENT_URI = Uri.parse("content://" +AUTHORITY + "/favorites?notify=true");
	        Cursor c = cr.query(CONTENT_URI,new String[] {"title","iconResource" },"title=?",
	        new String[] {context.getString(R.string.app_name).trim()}, null);
	        if(c!=null && c.getCount()>0){
	            isInstallShortcut = true ;
	        }
	        if(c!=null && !c.isClosed())
	        	c.close();
	        return isInstallShortcut ;
	}
	
	/** 
	 * 为程序创建桌面快捷方式 
	 */ 
	public void addShortcut(Activity activity){  
	    Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");  
	           
	    //快捷方式的名称  
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));  
	    shortcut.putExtra("duplicate", false); //不允许重复创建  
	           
	    //指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer  
	    //注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序  
	    ComponentName componentName = new ComponentName(context,activity.getClass());
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(componentName));  
	   
	    //快捷方式的图标  
	    ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, R.drawable.icon);  
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);  
	    context.sendBroadcast(shortcut);  
	}  
	/**
	 * 删除程序的快捷方式 
     */ 
    public void delShortcut(Activity activity){  
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");  
               
        //快捷方式的名称  
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));  
               
        //指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer  
        //注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式  
        String appClass = context.getPackageName() + "." +activity.getLocalClassName();  
        ComponentName comp = new ComponentName(activity.getPackageName(), appClass);  
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));  
               
        context.sendBroadcast(shortcut);  
               
    }  
}
