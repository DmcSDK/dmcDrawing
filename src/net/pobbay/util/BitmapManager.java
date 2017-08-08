package net.pobbay.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
/**
 * 异步线程加载图片工具类
 * 使用说明：
 * BitmapManager bmpManager;
 * bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.loading));
 * bmpManager.loadBitmap(imageURL, imageView);
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-25
 */
public class BitmapManager {

	private static HashMap<String, SoftReference<Bitmap>> cache;
	private static ExecutorService pool;
	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;

	static {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5);  //固定线程池
		imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	}

	public BitmapManager(){}

	public BitmapManager(Bitmap def) {
		this.defaultBmp = def;
	}

	/**
	 * 设置默认图片
	 * @param bmp
	 */
	public void setDefaultBmp(Bitmap bmp) {
		defaultBmp = bmp;
	}

	/**
	 * 加载图片
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, this.defaultBmp, 0, 0);
	}

	/*  public void loadBitmap(Context context,String url, ImageView imageView) {
    	loadBitmap(context,url, imageView, this.defaultBmp, 0, 0);
    }*/
	/**
	 * 加载图片-可设置加载失败后显示的默认图片
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可指定显示图片的高宽
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp, int width, int height) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {

			//显示缓存图片
			imageView.setImageBitmap(bitmap);
		} else {
			//加载SD卡中的图片缓存
			String filename = FileUtils.getFileName(url);
			String filepath = imageView.getContext().getFilesDir() + File.separator + filename;
			File file = new File(filepath);
			if(file.exists()){
				//显示SD卡中的图片缓存
				Bitmap bmp = ImageUtils.getBitmap(imageView.getContext(), filename);
				//放入缓存
				cache.put(url, new SoftReference<Bitmap>(bmp));
				imageView.setImageBitmap(bmp);
			}else{
				//线程加载网络图片

				imageView.setImageBitmap(defaultBmp);
				queueJob(url, imageView, width, height);
			}
			bitmap = null;
		}
	}

	public void loadAdjustBitmap(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			//设置图片比例
			int iv_W = getSceenWidth(imageView.getContext());
			int iv_H = bitmap.getHeight()*iv_W/bitmap.getWidth();
			android.view.ViewGroup.LayoutParams layout = imageView.getLayoutParams();
			layout.height = iv_H;
			layout.width = iv_W;
			imageView.setLayoutParams(layout);

			//显示缓存图片
			imageView.setImageBitmap(bitmap);
		} else {
			//加载SD卡中的图片缓存
			String filename = FileUtils.getFileName(url);
			String filepath = imageView.getContext().getFilesDir() + File.separator + filename;
			File file = new File(filepath);
			if(file.exists()){
				//显示SD卡中的图片缓存
				Bitmap bmp = ImageUtils.getBitmap(imageView.getContext(), filename);
				//放入缓存
				cache.put(url, new SoftReference<Bitmap>(bmp));
				if(bmp==null){
					return ;
				}
				//设置图片比例
				int iv_W = getSceenWidth(imageView.getContext());
				int iv_H = bmp.getHeight()*iv_W/bmp.getWidth();
				android.view.ViewGroup.LayoutParams layout = imageView.getLayoutParams();
				layout.height = iv_H;
				layout.width = iv_W;
				imageView.setLayoutParams(layout);

				imageView.setImageBitmap(bmp);
			}else{
				//线程加载网络图片
				imageView.setImageBitmap(defaultBmp);
				//设置图片比例
				int iv_W = getSceenWidth(imageView.getContext());
				int iv_H = defaultBmp.getHeight()*iv_W/defaultBmp.getWidth();
				android.view.ViewGroup.LayoutParams layout = imageView.getLayoutParams();
				layout.height = iv_H;
				layout.width = iv_W;
				imageView.setLayoutParams(layout);
				queueAdjustJob(url, imageView, 0, 0);
			}
			bitmap = null;
		}
	}

	public void queueAdjustJob(final String url, final ImageView imageView, final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						Bitmap bmp = (Bitmap) msg.obj;
						//设置图片比例
						int iv_W = getSceenWidth(imageView.getContext());
						int iv_H = bmp.getHeight()*iv_W/bmp.getWidth();
						android.view.ViewGroup.LayoutParams layout = imageView.getLayoutParams();
						layout.height = iv_H;
						imageView.setLayoutParams(layout);

						imageView.setImageBitmap(bmp);
						try {
							//向SD卡中写入图片缓存
							ImageUtils.saveImage(imageView.getContext(), FileUtils.getFileName(url), (Bitmap) msg.obj);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else{
						return;
					}
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, width, height);
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * @Title 广告图片
	 * @Description 下载广告中的图片，并保存到本机
	 * @param context 上下文
	 * @param url 图片路径
	 * @param width
	 * @param height
	 * @return void
	 */
	public void getAdImage(final Context context, final String url,
						   final int width, final int height) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.obj != null) {
					try {
						// 向SD卡中写入图片缓存
						ImageUtils.saveImage(context,
								FileUtils.getFileName(url), (Bitmap) msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, width, height);
				handler.sendMessage(message);
			}
		});
	}


	/**
	 * 从缓存中获取图片
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		if (cache.containsKey(url)) {
			bitmap = cache.get(url).get();
		}
		return bitmap;
	}
	/**
	 * 下载GIF类
	 * @param path
	 * @param cache
	 * @return
	 * @throws Exception
	 */
	public Uri getImageURI(String path) throws Exception {

		File file = new File( Environment.getExternalStorageDirectory().toString()
				+ "/android/data/pobaby_gif.gif");
		// 如果图片存在本地缓存目录，则不去服务器下载
//	        if (file.exists()) {
//	            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
//	        } else {
		// 从网络上获取图片
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
			// 返回一个URI对象
			return Uri.fromFile(file);
		}
//	        }
		return null;
	}
	/**
	 * 下载png类
	 * @param path
	 * @param cache
	 * @return
	 * @throws Exception
	 */
	public Uri getImageURI2(String path) throws Exception {

		File file = new File( Environment.getExternalStorageDirectory().toString()
				+ "/android/data/pobaby_png_c.jpg");
		// 如果图片存在本地缓存目录，则不去服务器下载
//		        if (file.exists()) {
//		            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
//		        } else {
		// 从网络上获取图片
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
			// 返回一个URI对象
			return Uri.fromFile(file);
		}
//		        }
		return null;
	}
	/**
	 * 从网络中加载图片
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String url, final ImageView imageView, final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						Bitmap bmp = (Bitmap) msg.obj;

						imageView.setImageBitmap(bmp);
						try {
							//向SD卡中写入图片缓存
							ImageUtils.saveImage(imageView.getContext(), FileUtils.getFileName(url), (Bitmap) msg.obj);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else{
						return;
					}
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, width, height);
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * 下载图片-可指定显示图片的高宽
	 * @param url
	 * @param width
	 * @param height
	 */
	public Bitmap downloadBitmap(String url, int width, int height) {
		Bitmap bitmap = null;
		try {
			//http加载图片
			bitmap = NetClient.getNetBitmap(url);
			if(width > 0 && height > 0) {
				//指定显示图片的高宽
				//bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
				//ARGB_8888 可以使图片背景透明
				bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			}
			//放入缓存
			cache.put(url, new SoftReference<Bitmap>(bitmap));
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public int getSceenWidth(Context context){

//		((Activity)context).getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();

		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;

		int height = dm.heightPixels;

		return width;

	}

}