package cn.sklgroup.netApprover.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class NetClient {
	public NetClient(String host,String port,String dir) {
		
	}
	
	private String TAG= getClass().getSimpleName(); 
	
	public String request(String host,String content) {
		StringBuffer result = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		/** 采用POST方法 */
		HttpPost post = new HttpPost(host);
		try {
			
			StringEntity query = new StringEntity(content);
			post.setEntity(query);
			/** 发出POST数据并获取返回数据 */
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			
			String line = null;
			while ((line = buffReader.readLine()) != null)
				result.append(line);

		}catch (Exception e) {
			Log.e(TAG,e.getMessage());
		} finally {
			post.abort();
			client = null;
		}
		return result.toString();
	}
	public void createHttpClient(){
		
		
	}
}
