package net.pobbay.service;

import java.io.IOException;
import java.io.InputStream;

import net.pobbay.util.BitmapCache;
import net.pobbay.util.BitmapManager;
import net.pobbay.util.FileUtils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class MyUtils {

	private static Bitmap bitmap1;
	private static Bitmap bitmap2;

	public static Bitmap getBitmap(AssetManager a, String path, String gallery) {

		InputStream is = null;
		BitmapManager manager = new BitmapManager();
		if(bitmap1!=null){
			
		bitmap1 .recycle();
		bitmap1=null;
		}
		System.gc();
		if (gallery.equals("gallery")) {
		Long size=	FileUtils.getFileSize(path);
		if(size>2500){
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 3;
			bitmap1 = BitmapFactory.decodeFile(path,options);
		}else{
			bitmap1 = BitmapFactory.decodeFile(path);
		}
			
		} else {
			try {
				bitmap1 = manager.downloadBitmap(path, 0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap1;
	}

	public static Bitmap getBitmap2(AssetManager a, String path, String gallery) {

		InputStream is = null;
		BitmapManager manager = new BitmapManager();
	
		if (gallery.equals("gallery")) {
			if (path.equals(Environment.getExternalStorageDirectory().toString()
					+ "/android/data/pobaby_camera.jpeg") ) {
				bitmap2 = BitmapFactory.decodeFile(path);
			} else {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 5;
				bitmap2 = BitmapFactory.decodeFile(path, options);
			}
		} else {
			try {
				bitmap2 = manager.downloadBitmap(path, 0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap2;
	}

	public static void recycleBitmap() {
		if (bitmap2 != null) {
			bitmap2.recycle();
			bitmap2 = null;
		}
	}
}
