//package cn.sklgroup.netApprover.view;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//
//import cn.sklgroup.netApprover.R;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.TableLayout;
//
//public class DetailView extends TableLayout{
//
//	private int firstDrawable;
//	private int itemDrawable;
//	private int lastDrawable;
//	private int itemLayout;
//	private HashMap<String,?> data = new LinkedHashMap<String, Object>();
//	
//	public DetailView(Context context) {
//		super(context);
//	}
//	public DetailView(Context context,AttributeSet attrs) {
//		super(context,attrs);
//		TypedArray typedArray = context.obtainStyledAttributes(attrs,
//				R.styleable.);
//		firstDrawable = typedArray.getResourceId(R.styleable.De_firstDrawable, -1);
//		itemDrawable = typedArray.getResourceId(R.styleable.ToolBar_itemDrawable, -1);
//		lastDrawable = typedArray.getResourceId(R.styleable.ToolBar_lastDrawable, -1);
//		itemLayout = typedArray.getResourceId(index, defValue)
//		typedArray.recycle();
//	}
//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		// TODO Auto-generated method stub
//		super.onLayout(changed, l, t, r, b);
//		this.design();
//	}
//	protected void design(){
//		int len  = this.getChildCount();
//		for (int i = 0; i < len; i++) {
//			View view = this.getChildAt(i);
//			if(i==0 && firstDrawable>0)
//				view.setBackgroundResource(firstDrawable);
//			else if(i<len-1 && itemDrawable>0)
//				view.setBackgroundResource(itemDrawable);
//			else if(lastDrawable!=0)
//				view.setBackgroundResource(lastDrawable);
//		}
//	}
//	public void setSelected(int i,boolean selected) {
//		// TODO Auto-generated method stub
//		getChildAt(i).requestFocus();
//		super.setSelected(selected);
//	}
//	public void setData(HashMap<String, ?> data) {
//		this.data = data;
//		this.removeAllViews();
//		this.bindView();
//	}
//	private void bindView(){
//		itemLayout
//	}
//}
