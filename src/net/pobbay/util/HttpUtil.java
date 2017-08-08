package net.pobbay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	public static String httpGet(String uri){
		String result="";

		HttpGet httpGet=new HttpGet(uri);
		HttpResponse response;
		try {
			response = new DefaultHttpClient().execute(httpGet);
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity=response.getEntity();
				result=EntityUtils.toString(entity, HTTP.UTF_8);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		httpGet.abort();
		return result;
	}
	
	public static String httpPost(String uri,List<BasicNameValuePair>  params){
		String result="";
		HttpPost post = new HttpPost(uri);
		org.apache.http.client.HttpClient client=new DefaultHttpClient();
		HttpParams params2 =null;
		params2=client.getParams();
		HttpResponse response;
		org.apache.http.params.HttpConnectionParams.setConnectionTimeout(params2, 60000);
		org.apache.http.params.HttpConnectionParams.setConnectionTimeout(params2, 60000);
		try {
			
			if(params!=null){
				post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			}
			response = client.execute(post);  
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity=response.getEntity();
				result=EntityUtils.toString(entity, HTTP.UTF_8);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		post.abort();
		return result;
	}
	
	public static String _MakeURL(String p_url,String...  params) {
		StringBuilder url = new StringBuilder(p_url);
		if(url.indexOf("?")<0)
			url.append('?');

		for(int i=0;i<params.length;i++){
			url.append('&');
			url.append("params"+i);
			url.append('=');
			url.append(params[i]);
		}

		return url.toString().replace("?&", "?");
	}
}
