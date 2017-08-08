package net.pobbay.view;

import java.io.FileNotFoundException;

import net.pobbay.mms.R;
import net.pobbay.util.ContextAll;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class MyGallery extends Activity {
	private GridView gridView;
	private ImageView imageView;
	private Button button;
	private String path;
	private Bitmap bitmap1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		imageView = (ImageView) findViewById(R.id.imageview);
		Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
		imageIntent.setType("image/*");
		startActivityForResult(imageIntent, 1);
		button = (Button) findViewById(R.id.btu_galleryok);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(path==null){
			Toast.makeText(ContextAll.getContext(), "图片获取失败！", Toast.LENGTH_LONG).show();
			finish();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			Cursor cursor = cr.query(uri, null, null, null, null);
			cursor.moveToFirst();
			path = cursor.getString(1);
			if(path==null){
				Toast.makeText(ContextAll.getContext(), "图片为空", Toast.LENGTH_LONG).show();
				finish();
			}else{
				Intent intent = new Intent();
				intent.putExtra("path", path);
				intent.putExtra("gallery", "gallery");
				setResult(Activity.RESULT_OK, intent);
				finish();
				bitmap1=null;
			}
			cursor.close();
		}
	}

}
