package cn.sklgroup.netApprover;


import cn.sklgroup.netApprover.base.BaseTabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
public class TaskDetailActivity extends BaseTabActivity implements TabContentFactory{


	/** Called when the activity is first created. */
	private TabHost tabHost =null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.task_detail);
        
       
        txtTitle.setText(R.string.TASK_DETAIL);
        btnBack.setOnClickListener(clickListener);

        btnNext.setText(R.string.GENERAL_SEND);
        btnNext.setOnClickListener(clickListener);
        this.createTabHost();
    }
    private void createTabHost() {
    	tabHost= this.getTabHost();
		final View itemInfo= LayoutInflater.from(this).inflate(R.layout.nave_item, null);
		itemInfo.setBackgroundResource(R.drawable.selector_bar_left);
		((TextView)itemInfo).setText(R.string.TASK_INFO);
		
		final View itemComment= LayoutInflater.from(this).inflate(R.layout.nave_item, null);
		itemComment.setBackgroundResource(R.drawable.selector_bar_item);
		((TextView)itemComment).setText(R.string.TASK_COMMENT);
		
		final View itemRecord= LayoutInflater.from(this).inflate(R.layout.nave_item, null);
		itemRecord.setBackgroundResource(R.drawable.selector_bar_right);
		((TextView)itemRecord).setText(R.string.TASK_RECORD);
		
		Intent infoIntent = new Intent(this,TaskInfoActivity.class);
		Intent commentIntent = new Intent(this,TaskCommentActivity.class);
		Intent recordIntent = new Intent(this,TaskInfoActivity.class);
		tabHost.addTab(tabHost.newTabSpec("index")
				.setIndicator(itemInfo)
				.setContent(infoIntent));
		tabHost.addTab(tabHost.newTabSpec("office")
				.setIndicator(itemComment)
				.setContent(commentIntent));
		tabHost.addTab(tabHost.newTabSpec("doc")
				.setIndicator(itemRecord)
				.setContent(this));
		tabHost.setOnTabChangedListener(changeListener);
		
    }
    private OnTabChangeListener changeListener = new OnTabChangeListener() {
		
		@Override
		public void onTabChanged(String spec) {
			tabHost.getCurrentTabView().requestFocus();
		}
	};
	private OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case  R.id.title_btn_back:
					finish();
					break;
				case  R.id.title_btn_next:
					goActivity(TaskSendActivity.class);
					break;
				default:
					break;
				}
			}
	};
	@Override
	public View createTabContent(String tag) {
		final TextView txt_demo = new TextView(this);
		txt_demo.setText("暂无记录");
		return txt_demo;
	}
}