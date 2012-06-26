package cn.sklgroup.netApprover;

import java.util.Map;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.sklgroup.netApprover.base.BaseActivity;
import cn.sklgroup.netApprover.services.BootReceiver;
import cn.sklgroup.netApprover.services.DataService;
import cn.sklgroup.netApprover.util.AppSetting;
import cn.sklgroup.netApprover.util.CrashHandler;
import cn.sklgroup.netApprover.util.JSONUtil;
import cn.sklgroup.netApprover.util.ShortcutUtil;

public class LoginActivity extends BaseActivity {
  
	private Button btnLogin;
	private EditText editLoginame;
	private EditText editPassword;
	private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setBackground();
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(clickListener);
        editLoginame = (EditText) findViewById(R.id.edit_loginame);
        editPassword = (EditText) findViewById(R.id.edit_password);
        progressDialog = new ProgressDialog(this);
        
        
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	try {
    		//从 SharedPreferences 加载配置信息
			AppSetting.loadSetting(getApplicationContext());
			//判断是否程序第一次运行
			if(!AppSetting.FIRST_RUN){
				startDataService();
				Intent intent =new Intent(this, WebActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);  
				startActivity(intent);
				this.finish();
			}else{
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
    private void login(){
    	final String loginname = editLoginame.getText().toString();
    	final String password = editPassword.getText().toString();
    	progressDialog.setTitle(R.string.GENERAL_TIP);
    	progressDialog.setMessage(getText(R.string.GENERAL_NET_PROCESS));
    	progressDialog.show();
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String result =  DataService.request(new String[]{"service","message"}, new String[]{"args",loginname},new String[]{"args",password});
					Map<String,?> data = (Map<String, ?>) JSONUtil.decode(result);
					if("true".equals(data.get("sucess")))
						handler.sendEmptyMessage(MESSGE_SUCCESS);
					else 
						handler.sendEmptyMessage(MESSGE_ERROR);
				} catch (Exception e) {
					handler.sendEmptyMessage(MESSGE_ERROR);
				}
				
			}
		}).start();
    }
   
    private final int MESSGE_SUCCESS=0x12300;
    private final int MESSGE_ERROR=0x12400;
    
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MESSGE_SUCCESS:
				try {
					AppSetting.USER= editLoginame.getText().toString();
					AppSetting.PASSWORD= editPassword.getText().toString();
					AppSetting.FIRST_RUN = false;
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
    private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			
			switch (view.getId()) {
			case  R.id.btn_login:
				login();
				break;
			default:
				break;
			}
		}
	};
	
}