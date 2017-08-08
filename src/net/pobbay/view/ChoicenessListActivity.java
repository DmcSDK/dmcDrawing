package net.pobbay.view;

import java.util.ArrayList;

import net.pobbay.adapter.GifListAdapter;
import net.pobbay.entity.Gif;
import net.pobbay.mms.ImagePicker;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChoicenessListActivity extends Activity {

	private ListView listView;
	private GifListAdapter adapter;
	private GifList gifList;
	private ArrayList<Gif> giflistAll;
	private MyButton button;
	private String classfiy;
	private Long lock;
	public ViewGroup bg;
	private boolean now;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.giflist);
		gifList = new GifList();
		bg = (ViewGroup) findViewById(R.id.choiceness);
		button = (MyButton) findViewById(R.id.btn_clear);
		button.setVisibility(View.INVISIBLE);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title));
		textView.setText(R.string.title_choiceness);
		textView.setTypeface(typeface);
		bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.loading));
		findViewById(R.id.btn_titlebar_left).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						ChoicenessListActivity.this.finish();
					}
				});
		listView = (ListView) findViewById(R.id.gif_listView);
		giflistAll = new ArrayList<Gif>();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				giflistAll = gifList.getlist(2);
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
				Log.e("", "点击了类型" + classfiy);
				Intent intent = new Intent(ChoicenessListActivity.this,
						ChoicenessGridActivity.class);
				intent.putExtra("path", path);
				intent.putExtra("class", classfiy);
				intent.putExtra("count", count);
				startActivity(intent);

			}
		});

		// MogoOffer.setMogoOfferListCallback(this);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		now = true;
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					if (now) {

					} else {
						bg.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.select_bg));
						adapter = new GifListAdapter(ChoicenessListActivity.this,
								giflistAll);
						listView.setAdapter(adapter);
					}
					break;
				case 0:
					Toast.makeText(ChoicenessListActivity.this, "无法获取数据请检查网络", 5000)
							.show();
					break;
			}
		};
	};

}
