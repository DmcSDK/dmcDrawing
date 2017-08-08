package net.pobbay.view;

import java.util.ArrayList;


import net.pobbay.adapter.GifListAdapter;
import net.pobbay.entity.Gif;
import net.pobbay.mms.HomePage;
import net.pobbay.mms.R;
import net.pobbay.service.GifList;
import net.pobbay.util.SingleLock;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GifListActivity extends Activity  {
	private ListView listView;
	private GifListAdapter adapter;
	private GifList gifList;
	private ArrayList<Gif> giflistAll;
	private MyButton button;
	private String classfiy;
	private Long lock;
	private boolean now;
	public TextView textView_loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.giflist);
		gifList = new GifList();
		button = (MyButton) findViewById(R.id.btn_clear);
		button.setVisibility(View.INVISIBLE);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title));
		textView.setText(R.string.title_gif);
		textView.setTypeface(typeface);
		textView_loading=(TextView)findViewById(R.id.textView_loading);
		listView = (ListView) findViewById(R.id.gif_listView);
		giflistAll = new ArrayList<Gif>();
		findViewById(R.id.btn_titlebar_left).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						GifListActivity.this.finish();
					}
				});
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				giflistAll = gifList.getlist(1);
				Message message = new Message();
				if (giflistAll.size() > 0) {
					message.what = 1;
				} else {
					message.what = 0;
				}
				handler.sendMessage(message);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			
					classfiy = giflistAll.get(arg2).getClassify();
					int count = giflistAll.get(arg2).getCount();
					String path = giflistAll.get(arg2).getPath();
					Log.e("", "���������" + classfiy);
					Intent intent = new Intent(GifListActivity.this,
							GifgridActivity.class);
					intent.putExtra("class", classfiy);
					intent.putExtra("path", path);
					intent.putExtra("count", count);
					startActivity(intent);
					finish();
			}
		});
	

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		now = true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			startActivity(new Intent(this, HomePage.class));
		}
		return super.onKeyDown(keyCode, event);
	}
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			textView_loading.setVisibility(View.INVISIBLE);
			switch (msg.what) {

			case 1:
				if (now) {

				} else {

				adapter = new GifListAdapter(GifListActivity.this, giflistAll);
				listView.setAdapter(adapter);
				}
				break;
			case 0:
				Toast.makeText(GifListActivity.this, "�޷���ȡ������������", 5000)
						.show();
				break;
			}
		};
	};



	
}
