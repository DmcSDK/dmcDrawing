package net.pobbay.adapter;

import java.util.ArrayList;

import net.pobbay.entity.GifDetail;
import net.pobbay.mms.R;
import net.pobbay.util.BitmapManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GifgridAdapter extends BaseAdapter{
	private ArrayList<GifDetail> details;
	private Context context;
	private Bitmap defaultBitmap;
	private BitmapManager bitmapManager;
	private BitmapManager bm ;
	private Bitmap border;
	private Drawable drawable;
	public GifgridAdapter(Context context1,ArrayList<GifDetail> details1){
		this.context=context1;
		this.details=details1;
		bitmapManager = new BitmapManager(defaultBitmap);
		bm = new BitmapManager();
		border = BitmapFactory.decodeResource(context1.getResources(),
				R.drawable.border);
		NinePatch ninePatch = new NinePatch(border,
				border.getNinePatchChunk(), null);
		drawable = new NinePatchDrawable(context1.getResources(), ninePatch);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return details.size();
	}

	@Override
	public Object getItem(int arg0) {
		return details.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ImageView i;
		if (convertView == null) {
			i = new ImageView(context);
			i.setLayoutParams(new GridView.LayoutParams(145, 145));
			i.setScaleType(ImageView.ScaleType.CENTER);
			i.setBackgroundDrawable(drawable);
		} else {
			i = (ImageView) convertView;
		}
		String url=details.get(arg0).getSimg();
		bm.loadBitmap(url, i);
		return i;
	}
	
}
