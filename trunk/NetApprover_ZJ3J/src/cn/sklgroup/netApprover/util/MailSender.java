package cn.sklgroup.netApprover.util;

import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author wanxianze@gmail.com
 * 2012-3-15
 */
public class MailSender {
	private Context context;
	private String title;
	public MailSender(Context context,String title) {
		this.context = context;
		this.title = title;
	}
	public void send(String[] emails,String subject,String text) {
		Intent intent = new Intent(Intent.ACTION_SEND); 
		intent.setType("plain/test"); 
		intent.putExtra(Intent.EXTRA_EMAIL,emails);    
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);   
		intent.putExtra(Intent.EXTRA_TEXT, text);   
		context.startActivity(Intent.createChooser(intent, title));           

	}
}
