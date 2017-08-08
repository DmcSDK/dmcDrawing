package net.pobbay.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import net.pobbay.mms.R;
import net.pobbay.util.ContextAll;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MyCamera extends Activity {
	private ImageView imageView;
	private MyButton button;
	private String path;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		imageView = (ImageView) findViewById(R.id.imageview);
		button = (MyButton) findViewById(R.id.btu_galleryok);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(path==null){
					Toast.makeText(ContextAll.getContext(), "√ª”–Õº∆¨", 5000).show();
					finish();
				}else{
				Intent intent = new Intent();
				intent.putExtra("path", path);
				intent.putExtra("gallery", "gallery");
				setResult(Activity.RESULT_OK, intent);
				bitmap.recycle();
				finish();
				}
			}
		});
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(camera, 22);

	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(path==null){
			Toast.makeText(ContextAll.getContext(), "Œﬁ’’∆¨£¨«Î≈ƒ…„’’∆¨", 5000).show();
			finish();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 22 && resultCode == Activity.RESULT_OK
				&& null != data) {
			String sdState = Environment.getExternalStorageState();
			if (!sdState.equals(Environment.MEDIA_MOUNTED)) {

				return;
			}
			new DateFormat();			
			Bundle bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");
			
			FileOutputStream fout = null;
			String fname=Environment.getExternalStorageDirectory().toString()+"/android/data/pobaby_camera.jpeg";
			File file = new File(fname);
		
			try {
				fout = new FileOutputStream(file);
				bitmap.compress(CompressFormat.PNG, 100, fout);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					fout.flush();
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// œ‘ æÕº∆¨
			path = Environment.getExternalStorageDirectory().toString()+"/android/data/pobaby_camera.jpeg";
			imageView.setImageBitmap(bitmap);
		}
	}
}
