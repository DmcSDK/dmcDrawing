package net.pobbay.adapter;

import java.util.List;

import net.pobbay.entity.Gif;
import net.pobbay.mms.R;
import net.pobbay.util.SingleLock;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

public class GifListAdapter extends BaseAdapter{
	private Context context;
	private List<Gif> giflistAll;
	public GifListAdapter(Context context,List<Gif> giflistAll){
		this.context=context;
		this.giflistAll=giflistAll;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return giflistAll.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return giflistAll.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		LayoutInflater inflater =LayoutInflater.from(context);
		convertView=inflater.inflate(R.layout.listitem_style1, null);
		TextView textView=(TextView)convertView.findViewById(R.id.textView1);
		textView.setText(giflistAll.get(arg0).getTitle());
		return convertView;
	}

}
