package net.pobbay.mms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.pobbay.adapter.GifListAdapter;
import net.pobbay.adapter.GifgridAdapter;
import net.pobbay.entity.Gif;
import net.pobbay.entity.GifDetail;
import net.pobbay.service.GifGridList;
import net.pobbay.service.GifList;
import net.pobbay.service.Item;
import net.pobbay.service.ListParser;
import net.pobbay.service.MyUtils;
import net.pobbay.util.ContextAll;
import net.pobbay.util.SingleLock;
import net.pobbay.view.ChoicenessGridActivity;
import net.pobbay.view.ChoicenessListActivity;
import net.pobbay.view.MyButton;
import net.pobbay.view.MyCamera;
import net.pobbay.view.MyGallery;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����ѡ����
 * 
 * @author yu
 * 
 */
public class ImagePicker extends FragmentActivity implements OnClickListener {
	private String path;
	private String filePath;
	private GifList gifList;
	public ArrayList<Gif> giflistAll;
	public TextView textView_loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_picker);
		textView_loading=(TextView)findViewById(R.id.textView_loading);
		setTitle("返回");

		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);
		findViewById(R.id.btn_clear).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_gallery).setOnClickListener(this);

		gifList = new GifList();
		path = getIntent().getStringExtra("path");
		giflistAll = new ArrayList<>();
		getData();
	}

	void getData(){
		textView_loading.setVisibility(View.VISIBLE);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				if (Integer.parseInt(path) == 0) {
					giflistAll = gifList.getlist(4);
				}
				if (Integer.parseInt(path) == 1) {
					giflistAll = gifList.getlist(3);
				}

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
	}
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
					textView_loading.setVisibility(View.INVISIBLE);
					ListFmg fragment = new ListFmg();
					FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					ft.add(R.id.simple_fragment, fragment).commit();
				break;
			case 0:
				Toast.makeText(ContextAll.getContext(), "网络连接失败！", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_titlebar_left:
			onBackPressed();
			break;
		case R.id.btn_gallery:
			Intent intent = new Intent();
			intent.setClass(this, MyGallery.class);
			startActivityForResult(intent, 0x1001);
			break;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg2 != null) {
			System.gc();
			filePath = arg2.getStringExtra("path");
			String gallery = arg2.getStringExtra("gallery");
			Intent intent = new Intent();
			intent.putExtra("gallery", gallery);
			intent.putExtra("path", filePath);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}

	private void setTitle(String s) {
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title));
		textView.setText(s);
		textView.setTypeface(typeface);
	}

}
