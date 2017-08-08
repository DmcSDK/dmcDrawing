package net.pobbay.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import net.pobbay.entity.Gif;
import net.pobbay.util.HttpUtil;

public class GifList {

	private String result;

	public ArrayList<Gif> getlist(int j) {
		ArrayList<Gif> giflistAll = new ArrayList<Gif>();
		List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		if (j == 1) {
			result = HttpUtil.httpPost(
					"http://222.73.57.60:17173/chuanqing/gif.asp", list);
		}
		if(j==2){
			result = HttpUtil.httpPost(
					"http://www.pobaby.net/app/chuanqing/complet.asp", list);
		}
		if(j==3){
			result = HttpUtil.httpPost(
					"http://222.73.57.60:17173/chuanqing/preson.asp", list);
		}
		if(j==4){
			result = HttpUtil.httpPost(
					"http://www.pobaby.net/app/chuanqing/background.asp", list);
		}
		if (result == null || result.equals("")) {
			Log.e("", "解析JSON无数据");
		}

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray contentArr = jsonObject.getJSONArray("items");
			JSONObject contentItem;
			Gif listItem = null;
			for (int i = 0; i < contentArr.length(); i++) {
				listItem = new Gif();
				contentItem = contentArr.getJSONObject(i);
				listItem.setTitle(contentItem.getString("title"));
				listItem.setNov(contentItem.getString("new"));
				listItem.setCount(contentItem.getInt("count"));
				listItem.setLock(contentItem.getInt("lock"));
				listItem.setPath(contentItem.getString("path"));
				listItem.setClassify(contentItem.getString("class"));
				listItem.setGif(contentItem.getInt("gif"));
				giflistAll.add(listItem);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return giflistAll;
	}
	public static String getVersion(){
		String version="";
		ArrayList<Gif> giflistAll = new ArrayList<Gif>();
		List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		String	result = HttpUtil.httpPost(
				"http://222.73.57.60:17173/chuanqing/version.asp",list);

		if (result == null || result.equals("")) {
			Log.e("", "解析JSON无数据");
		}

		try {
			JSONObject jsonObject = new JSONObject(result);

			version=jsonObject.getString("version");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return version;
	}
}
