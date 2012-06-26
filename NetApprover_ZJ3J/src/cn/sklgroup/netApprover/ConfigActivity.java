package cn.sklgroup.netApprover;

import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.sklgroup.netApprover.base.BaseActivity;
import cn.sklgroup.netApprover.services.BootReceiver;
import cn.sklgroup.netApprover.services.DataService;
import cn.sklgroup.netApprover.util.AppSetting;
import cn.sklgroup.netApprover.util.CrashHandler;
import cn.sklgroup.netApprover.util.JSONUtil;
import cn.sklgroup.netApprover.util.ShortcutUtil;

public class ConfigActivity extends BaseActivity {
  
	private Button btnSave;
	private Button btnCancel;
	private EditText editLoginame;
	private EditText editPassword;
	private ProgressDialog progressDialog;
	private TextView txtInterval,txtVersion;
	private int[] intervals;
	private String[] intervalTexts;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        //setBackground();
        
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(clickListener);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(clickListener);
        editLoginame = (EditText) findViewById(R.id.edit_loginame);
        editPassword = (EditText) findViewById(R.id.edit_password);

        progressDialog = new ProgressDialog(this);
        
        txtVersion = (TextView) findViewById(R.id.txt_version);
        txtVersion.setText(getVersion(this));
        txtVersion.setOnClickListener(clickListener);
        
      
        intervals = getResources().getIntArray(R.array.SERVICE_INTERVAL_I);
        intervalTexts = getResources().getStringArray(R.array.SERVICE_INTERVAL);
        
        txtInterval = (TextView) findViewById(R.id.txt_interval);
        txtInterval.setOnClickListener(clickListener);
        txtInterval.setText(intervalTexts[0]);
		txtInterval.setTag(intervals[0]);
		
		CrashHandler.getInstance().init(getApplicationContext());
		
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	try {
    		if(CrashHandler.reportExists(this)){
    			CrashHandler.sendCrashReport(this);
    			return ;
    		}
    		
    		String action = getIntent().getAction();
    		//从 SharedPreferences 加载配置信息
			AppSetting.loadSetting(getApplicationContext());
			//判断是否程序第一次运行
			if(Intent.ACTION_EDIT.equals(action)){
				editLoginame.setText(AppSetting.USER);
				editPassword.setText(AppSetting.PASSWORD);
				for (int i = 0; i < intervals.length; i++) {
					if(intervals[i]==AppSetting.DATA_SERVICE_INTERVAL){
						txtInterval.setText(intervalTexts[i]);
						txtInterval.setTag(intervals[i]);
						break;
					}
				}
			}else if(!AppSetting.FIRST_RUN){
				CrashHandler.sendCrashReport(this);
				startDataService();
				Intent intent =new Intent(this, WebActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
				startActivity(intent);
				this.finish();
			}else {
				
				//创建桌面快捷方式
				ShortcutUtil shortcutUtil = new ShortcutUtil(this);
				if(!shortcutUtil.hasShortcut()){
					shortcutUtil.addShortcut(this);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
    }
    public void setBackground(){
    	Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.login_bg_2);
    	BitmapDrawable drawable = new BitmapDrawable(bitmap);
    	drawable.setTileModeXY(TileMode.REPEAT , TileMode.MIRROR);
    	drawable.setDither(true);
    	this.getWindow().getDecorView().setBackgroundDrawable(drawable);
    }
    public void startDataService(){
    	
    	Intent intent = new Intent(this,BootReceiver.class);
		//PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		sendBroadcast(intent);
    }
    private String getVersion(Context ctx){
    	String result = null;
    	 try {
    		PackageManager pm = ctx.getPackageManager();
    		PackageInfo  pi= pm.getPackageInfo(ctx.getPackageName(),PackageManager.GET_ACTIVITIES);
    		result= pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		return result;
    } 
    private void login(){
    	final String loginame = editLoginame.getText().toString();
    	final String password = editPassword.getText().toString();
    	progressDialog.setTitle(R.string.GENERAL_TIP);
    	progressDialog.setMessage(getText(R.string.GENERAL_NET_PROCESS));
    	progressDialog.show();
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					if(DataService.login(loginame, password))
						handler.sendEmptyMessage(MESSGE_SUCCESS);
				} catch (Exception e) {
					Message msg = handler.obtainMessage(MESSGE_MESSAGE,e.getMessage());
					handler.sendMessage(msg);
				}
				
			}
		}).start();
    }
   
    private final int MESSGE_SUCCESS=0x12300;
    private final int MESSGE_MESSAGE=0x12400;
    private final int MESSGE_ERROR=0x12500;
    
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MESSGE_SUCCESS:
				try {
					AppSetting.USER= editLoginame.getText().toString();
					AppSetting.PASSWORD= editPassword.getText().toString();
					AppSetting.FIRST_RUN = false;
					if(txtInterval.getTag()!=null)
						AppSetting.DATA_SERVICE_INTERVAL = Integer.parseInt(txtInterval.getTag().toString());
					AppSetting.saveSetting(getApplicationContext());
					startDataService();
					finish();
				} catch (Exception e) {
					showToast(R.string.GENERAL_LOGIN_ERROR);
				}finally{
					if(progressDialog.isShowing())
						progressDialog.dismiss();
				}
				break;
			case MESSGE_MESSAGE:
				showToast(msg.obj+"");
				if(progressDialog.isShowing())
					progressDialog.dismiss();
				break;
			case MESSGE_ERROR:
				showToast(R.string.GENERAL_LOGIN_ERROR);
				if(progressDialog.isShowing())
					progressDialog.dismiss();
				break;
			default:
				break;
			}
    	};
    };
    protected android.app.Dialog onCreateDialog(int id) {
    	switch (id) {
    	case R.id.txt_interval:
	    	return new AlertDialog.Builder(this)
	    	.setTitle(R.string.GENERAL_TIP)
			.setItems(intervalTexts, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int i) {
					txtInterval.setText(intervalTexts[i]);
					txtInterval.setTag(intervals[i]);

				}
			}).create();
		default:
			return super.onCreateDialog(id);
		}
    	
    };
    
    private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			
			switch (view.getId()) {
			case  R.id.btn_login:
				login();
				break;
			case R.id.txt_version:
				break;
			case R.id.txt_interval:
				showDialog(R.id.txt_interval);
				break;
			case R.id.btn_save:
				login();
				break;
			case R.id.btn_cancel:
				finish();
				break;
			default:
				break;
			}
		}
	};
	
}