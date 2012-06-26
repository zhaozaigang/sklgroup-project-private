package cn.sklgroup.netApprover.base;

import cn.sklgroup.netApprover.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author wanxianze@gmail.com
 * 2012-3-15
 */
public class BaseTabActivity extends TabActivity{
	protected TextView txtTitle;
	protected Button btnNext,btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getDecorView().setBackgroundResource(R.color.activity_backgrond);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE  |
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	}	
	protected void showToast(String msg){
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	}
	protected void showToast(int resId){
		Toast.makeText(this,resId,Toast.LENGTH_SHORT).show();
	}
	public void goActivity (Class<?> activity) {
		Intent intent = new Intent();
		intent.setClass(this,activity);
		startActivity(intent);
	}
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		txtTitle = (TextView)findViewById(R.id.title_txt);
		btnBack = (Button) findViewById(R.id.title_btn_back);
		btnNext = (Button) findViewById(R.id.title_btn_next);
	}
}
