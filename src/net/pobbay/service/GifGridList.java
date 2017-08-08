package net.pobbay.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import net.pobbay.entity.Gif;
import net.pobbay.entity.GifDetail;
import net.pobbay.util.HttpUtil;

public class GifGridList {
	public ArrayList<GifDetail> getlist(int count, String path,
										String classpiy, int type) {
		ArrayList<GifDetail> giflistAll = new ArrayList<GifDetail>();
		List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("count", "" + count));
		list.add(new BasicNameValuePair("path", path));

		if (type == 2) {
			list.add(new BasicNameValuePair("gif", ""+2));
		}
		if(type==1){
			list.add(new BasicNameValuePair("gif", ""+1));
		}
		if(type==0){
			list.add(new BasicNameValuePair("gif", ""+0));
		}
		list.add(new BasicNameValuePair("class", classpiy));
		String result = HttpUtil.httpPost(
				"http://222.73.57.60:17173/chuanqing/image_path.asp?", list);

		if (result == null || result.equals("")) {
			Log.e("", "解析JSON无数据");
		}

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray contentArr = jsonObject.getJSONArray("list");
			JSONObject contentItem;
			GifDetail listItem = null;
			for (int i = 0; i < contentArr.length(); i++) {
				listItem = new GifDetail();
				contentItem = contentArr.getJSONObject(i);
				listItem.setSimg(contentItem.getString("simg"));
				listItem.setBimg(contentItem.getString("bimg"));
				giflistAll.add(listItem);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return giflistAll;
	}
}
