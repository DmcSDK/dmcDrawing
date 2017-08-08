package net.pobbay.view;

import java.util.ArrayList;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

import net.pobbay.adapter.GifgridAdapter;
import net.pobbay.entity.GifDetail;
import net.pobbay.mms.R;
import net.pobbay.service.GifGridList;
import net.pobbay.util.ContextAll;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChoicenessGridActivity extends Activity{
	private MyButton button;
	private ArrayList<GifDetail> details;
	private GridView gridView;
	private GifgridAdapter adapter;
	private int count;
	private String path;
	private String url;
	private GifGridList g;
	public ViewGroup bg;
	private String classtiy;
	private boolean now;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gif_grid);

		button=(MyButton)findViewById(R.id.btn_clear);
		button.setVisibility(View.INVISIBLE);
		bg=(ViewGroup)findViewById(R.id.gifbg);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title));
		textView.setText(R.string.title_choiceness);
		textView.setTypeface(typeface);
		bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.loading));
		findViewById(R.id.btn_titlebar_left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChoicenessGridActivity.this.finish();
			}
		});
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		count=bundle.getInt("count");
		path=bundle.getString("path");
		classtiy = bundle.getString("class");
		gridView=(GridView)findViewById(R.id.gif_myGrid);
		details=new ArrayList<GifDetail>();
		g = new GifGridList();
		Runnable runnable=new Runnable() {
			@Override
			public void run() {
				details=g.getlist(count,path,classtiy,2);
				Message message=new Message();
				if(details.size()>0){
					message.what=1;
				}else{
					message.what=0;
				}
				handler.sendMessage(message);
			}
		};
		Thread thread=new Thread(runnable);
		thread.start();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				if(details.get(arg2)!=null){
					url=details.get(arg2).getBimg();
				}else{
					Toast.makeText(ContextAll.getContext(), "正在下载中,表急嘛:）...",Toast.LENGTH_SHORT ).show();
				}
				Intent intent=new Intent(ChoicenessGridActivity.this, ChoicenessViewActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		now=true;
		super.onDestroy();
	}
	private Handler handler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					if(now){

					}else{
						bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_bg));
						adapter = new GifgridAdapter(ChoicenessGridActivity.this,details);
						gridView.setAdapter(adapter);
					}
					break;
				case 0:
					Toast.makeText(ChoicenessGridActivity.this, "无法获取数据请检查网络", 5000).show();
					break;
			}
		};
	};
}
