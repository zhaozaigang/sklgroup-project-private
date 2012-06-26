
package cn.sklgroup.netApprover.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.sklgroup.netApprover.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author wanxianze@gmail.com
 * 2012-3-15
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	private static String TAG = CrashHandler.class.getSimpleName();
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler INSTANCE = new CrashHandler();
	private Context mContext;
	private final static String ENCODE = "UTF-8";
	private final static String CRASH_REPORT ="crash.report";
	private final static String NEWLIEN ="\r\n";  
	private final static String TAB ="\r\t"; 
	private CrashHandler(){}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	
	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(this.getClass().getSimpleName(), "error : ", e);
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	
	protected boolean handleException(Throwable ex) {
		if (ex == null)
			return false;
		final String msg =mContext.getString(R.string.GENERAL_FC) + ex.getLocalizedMessage(); 
		new Thread(){
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		saveThrowableToFile(ex);
		return true;
	}
	
	
	private void saveThrowableToFile(Throwable ex) {
		FileOutputStream inputStream = null;
		try {
			String stackTrace = collectThrowableStackTrace(ex);
			String deviceDetail = collectDeviceDetail(mContext);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date ="crash at "+dateFormat.format(new Date())+NEWLIEN;
			//stackTrace = collectLog();
			inputStream = new FileOutputStream(new File(mContext.getFilesDir(),CRASH_REPORT),false);
			inputStream.write(deviceDetail.getBytes(ENCODE));
			inputStream.write(date.getBytes(ENCODE));
			inputStream.write(stackTrace.getBytes(ENCODE));
		} catch (Exception e) {
			 Log.e(getClass().getSimpleName(), "Error while write exception to file", e);   
		}finally{
				try {
					if(inputStream!=null)
						inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
	}

	private String collectThrowableStackTrace(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		return writer.toString();
	}
	private String collectDeviceDetail(Context ctx) { 
		StringBuilder stringBuilder = new StringBuilder();
		
		DeviceUtil.getInstance().init(ctx);
		HashMap<String,String> device = DeviceUtil.getInstance().getDetail();
		
		if(device!=null){
			for (Map.Entry<String, String> m : device.entrySet()) {
				stringBuilder.append(m.getKey())
				.append(":")
				.append(TAB)
				.append(m.getValue())
				.append(NEWLIEN);
			}
		}
		return stringBuilder.toString();
	} 
	private static String readCrashReport(File file){
		 FileInputStream fileInputStream = null;
		 String result = null;
		 try {
			
			fileInputStream = new FileInputStream(file);
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
	        byte[] buffer = new byte[1024];
	        int readLength = 0 ;
	        while ((readLength=fileInputStream.read(buffer))>-1)
	        	outputStream.write(buffer,0,readLength);
	        result = new String(outputStream.toByteArray(),ENCODE);
		} catch (Exception e) {
			try {
				if(fileInputStream!=null)
					fileInputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	    return result;
	}
	public static boolean reportExists(Context ctx){
		File file = new File(ctx.getFilesDir(),CRASH_REPORT);
		if(!file.exists() || file.length()<=0)
			return false;
		else 
			return true;
	}
	public static void sendCrashReport(Context ctx){
		
		final File file = new File(ctx.getFilesDir(),CRASH_REPORT);
		if(!file.exists() || file.length()<=0)
			return ;
        
		final String content = readCrashReport(file);
		Log.d(TAG, content);
        final Context context = ctx;
        new AlertDialog.Builder(ctx)
        .setTitle(R.string.GENERAL_TIP)
        .setMessage(R.string.GENERAL_SEND_BUG_MESSAGE)
        .setPositiveButton(R.string.GENERAL_CONFIRM,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				String title = context.getResources().getString(R.string.GENERAL_SEND_BUG_TITLE);
				new MailSender(context,title).send(new String[]{"xianze.wang@ebestmobile.com"},"SFA-BUG", content);
				
//				Intent intent = new Intent(context,FreebackActivity.class);
//				intent.putExtra(FreebackActivity.EXTRA_CONTACT, GlobalInfo.User==null?"SFA":GlobalInfo.User.UserID);
//				intent.putExtra(FreebackActivity.EXTRA_CONTENT, content);
//				context.startActivity(intent);
				file.delete();
			}
		}).setNegativeButton(R.string.GENERAL_CANCEL,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				file.delete();
			}
		}).create().show();
	}
	
	public static String  collectLog()
	{
		StringBuilder builder = new StringBuilder();
		try
		{
			ArrayList<String> cmdLine=new ArrayList<String>();   
			cmdLine.add("logcat");
			cmdLine.add("-d");
			
			ArrayList<String> clearLog=new ArrayList<String>(); 
			clearLog.add("logcat");
			clearLog.add("-c");
			
			Process process=Runtime.getRuntime().exec(cmdLine.toArray(new String[cmdLine.size()]));   
			BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));

			String str=null;
			while((str=bufferedReader.readLine())!=null)	
			{
				Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()])); 
				builder.append(str);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return builder.toString();
	}
}
