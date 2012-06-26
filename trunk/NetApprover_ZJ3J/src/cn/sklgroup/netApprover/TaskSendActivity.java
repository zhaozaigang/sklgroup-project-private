package cn.sklgroup.netApprover;

import java.util.Map;

import cn.sklgroup.netApprover.base.BaseActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TaskSendActivity extends BaseActivity {
    /** Called when the activity is first created. */
	private String[] nextOption;
	private TextView txtNext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_send);
        txtTitle.setText(R.string.TASK_SEND);
        btnNext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upload, 0, 0, 0);
        btnNext.setText(R.string.TASK_SUBMIT);
        btnNext.setOnClickListener(clickListener);
        nextOption = new String[]{"11.送各部门会签","12.报市场部或商务部经理",
        		"13.报分公司副总","14.报分公司领导","15.报基础设施部","17.报上级单位"};
        
        txtNext = (TextView)findViewById(R.id.txt_next);
        txtNext.setOnClickListener(clickListener);
    }
    private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case  R.id.title_btn_back:
				finish();
				break;
			case  R.id.title_btn_next:
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(TaskSendActivity.this, TaskActivity.class);
				startActivity(intent);
				TaskSendActivity.this.finish();
				break;
			case R.id.txt_next:
				showDialog(view.getId());
			default:
				break;
			}
		}
    };
    protected android.app.Dialog onCreateDialog(int id) {
    	
    	return new AlertDialog.Builder(this)
		.setTitle("下一步")
		.setSingleChoiceItems(nextOption,android.R.layout.select_dialog_singlechoice, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int checkedItem) {
				txtNext.setText(nextOption[checkedItem]);
				dialog.dismiss();
			}
		}).create();
    };
}