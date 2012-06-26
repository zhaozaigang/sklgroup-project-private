package cn.sklgroup.netApprover;

import java.util.ArrayList;
import java.util.List;

import cn.sklgroup.netApprover.base.BaseActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TaskActivity extends BaseActivity {
    /** Called when the activity is first created. */
	private ListView listView;
	List<Object> data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_task);
        
        txtTitle.setText(R.string.TASK_WAITING);
        btnBack.setOnClickListener(clickListener);

        btnNext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_refresh, 0, 0, 0);
        btnNext.setOnClickListener(clickListener);
        
        listView = (ListView) findViewById(R.id.listview);
        data= new ArrayList<Object>();
        //............
        data.add("1");
        data.add("2");
        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(itemClickListener);
    }
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long id) {
			goActivity(TaskDetailActivity.class);
		}
    	
	};
	
	private BaseAdapter taskAdapter = new BaseAdapter(){

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data!=null?data.size():0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = LayoutInflater.from(TaskActivity.this).inflate(R.layout.task_item,null);
				
			}
			TextView textView = (TextView) convertView.findViewById(R.id.txt_name);
			textView.setText(getItem(position)+"");
			return convertView;
		}
    	
    };
	private OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case  R.id.title_btn_back:
					finish();
					break;
				default:
					break;
				}
			}
	};
}