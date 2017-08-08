package net.pobbay.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.pobbay.entity.Sket;
import net.pobbay.gui.Label;
import net.pobbay.gui.Widget;
import net.pobbay.mms.R;
import net.pobbay.service.MyUtils;
import net.pobbay.util.ContextAll;
import net.pobbay.util.DeleteFile;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.rtp.RtpStream;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 画板类
 * 
 * @author yu
 * 
 */
public class Panel extends ImageView {
	private String wait = "图片正在下载请稍等....";
	private boolean loadingOK;
	private Bitmap loadBitmap;
	private Paint loadingPaint;
	/**
	 * 背景bitmap对象
	 */
	private Bitmap bgBitmap;
	/**
	 * 人物bitmap对象
	 */
	private Bitmap roleBitmap;
	private Bitmap sketBitmap;
	private AssetManager asset;
	private Bitmap httpbitmap;
	private Bitmap httprbitmap;
	private float rx, ry;
	/**
	 * 图片离X 和Y需要多远距离
	 */
	private float bgX, bgY;
	private float scale;
	private Canvas cacheCanvas;
	public Bitmap cacheBitmap;
	private DrawFilter filter = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG); //
	/**
	 * 人物
	 * */
	private Role role;
	private Label label;
	private Sket sket;
	/**
	 * 位置Position
	 */
	private float pox;
	private float poy;

	private float lastx;
	private float lasty;
	// 保存人物 文字的数组
	private List<Widget> widgets = new ArrayList<Widget>(3);

	boolean matrixCheck = false;
	int widthScreen;
	int heightScreen;
	DisplayMetrics dm;

	int viewWidth;
	int viewHeight;
	private boolean zoom;

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public Panel(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		widthScreen = dm.widthPixels;
		heightScreen = dm.heightPixels;
		asset = context.getAssets();
		// 大的画布 规格640, 960
		loadingPaint = new Paint();
		Typeface typeface = Typeface.createFromAsset(asset, "fonts/font1.ttf");
		loadingPaint.setAntiAlias(true);
		loadingPaint.setTypeface(typeface);
		loadingPaint.setColor(Color.RED);
		loadingPaint.setTextSize(60);
		// 将人物 文字new出来放进数组保存
		role = new Role(this);
		widgets.add(role);
		label = new Label(this);
		widgets.add(label);
		sket = new Sket(this);
		viewHeight = getHeight();
		viewWidth = getWidth();
		widgets.add(sket);
		loadBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.load);
	}

	// W是当前视图的宽度 H是当期视图的高度 oldw是旧的视图高度
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		float sx = w / 640.0f;
		float sy = h / 960.0f;
		// TODO 计算图片是否缩小 Math.min(a,b)比较ab的大小 返回小的数字
		scale = Math.min(1, Math.min(sx, sy));
		Log.e("画布改变宽高", "" + w + "||" + h);
		Log.e("画布改变图片的缩放", "" + scale);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void init() {

	}

	/**
	 * 释放内存
	 */
	public void dispose() {
		if (roleBitmap != null) {
			roleBitmap.recycle();
			roleBitmap = null;
		}

		if (bgBitmap != null) {
			bgBitmap.recycle();
			bgBitmap = null;
		}
		if (cacheBitmap != null) {
			cacheBitmap.recycle();
			cacheBitmap = null;
		}
	}

	// TODO 预览有调用次方法
	public void snapshot() {
		cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
		// 抗锯齿
		cacheCanvas.setDrawFilter(filter);

		if (bgBitmap != null) {
			// 给画布来个背景
			cacheCanvas.drawBitmap(bgBitmap, 0, 0, null);
		} else {
			cacheCanvas.drawColor(Color.WHITE);
		}

		float tmpX = role.getX();
		float tmpY = role.getY();
		role.setPosition(tmpX - bgX, tmpY - bgY);
		role.draw(cacheCanvas);
		role.setPosition(tmpX, tmpY);

		tmpX = label.getX();
		tmpY = label.getY();
		label.setPosition(tmpX, tmpY);

		float f = label.getTextSize();
		label.setTextSize(f);
		label.draw(cacheCanvas);

		if (sketBitmap != null) {
			cacheCanvas.drawBitmap(sketBitmap, 0, 0, null);
		}

	}

	// TODO 给画布设置选择的图片
	public void setBgBitmap(Bitmap bitmap) {
		if (bgBitmap != null) {
			bgBitmap.recycle();
		}
		if (bitmap == null)
			throw new NullPointerException();
		// 新的缩放后的bitmap
		bgBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(),
				true);
		loadingOK = false;
		invalidate();
	}

	public void setBgBitmap(String path, String g) {
		bitmappath = path;
		gg = g;
		loadingOK = true;
		invalidate();
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Message message = new Message();
//				httpbitmap = MyUtils.getBitmap(asset, bitmappath, gg);
//				MyUtils.recycleBitmap();
				if (httpbitmap != null) {
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
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				setBgBitmap(httpbitmap);
				break;
			case 2:
				setRole(httprbitmap);
				break;
			case 0:
				setErr();
				break;
			default:
				break;
			}
		}
	};

	public void setErr() {
		Toast.makeText(ContextAll.getContext(), "呜呜呜.下载失败了.我已经很努力了...",
				Toast.LENGTH_LONG).show();
		loadingOK = false;
		invalidate();
	}

	public void setRoleBitmap(String path, String gallery) {

		loadingOK = true;
		invalidate();
		bitmappath = path;
		gg = gallery;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
//				httprbitmap = MyUtils.getBitmap(asset, bitmappath, gg);
//				MyUtils.recycleBitmap();
				if (httprbitmap != null) {
					message.what = 2;
				} else {
					message.what = 0;
				}
				handler.sendMessage(message);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private void setRole(Bitmap r) {
		if (roleBitmap != null) {
			// 回收空间
			roleBitmap.recycle();
		}
		if (r.getWidth() >= getWidth() || r.getHeight() >= getHeight()) {
			roleBitmap = Bitmap.createScaledBitmap(r, r.getWidth() / 2,
					r.getHeight() / 2, true);
		} else {
			roleBitmap = r;
		}
		// center(true, true);
		rx = (getWidth() - roleBitmap.getWidth()) / 2;
		ry = (getHeight() - roleBitmap.getHeight()) / 2;
		role.setSize(roleBitmap.getWidth(), roleBitmap.getHeight());
		role.setPosition(rx, ry);
		loadingOK = false;
		invalidate();
	}

	public void setSketBitmap(Bitmap bit) {
		if (sketBitmap != null) {
			// 回收空间
			sketBitmap.recycle();
		}
		sketBitmap = bit;
		invalidate();
	}

	public Label getLabel() {
		return label;
	}

	public float getScale() {
		return scale;
	}

	public Sket getSket() {
		return sket;
	}

	// TODO 清除
	public void clear() {
		widgets.clear();
		DeleteFile.delete();
		if (bgBitmap != null) {
			bgBitmap.recycle();
			bgBitmap = null;
		}
		if (roleBitmap != null) {
			roleBitmap.recycle();
			roleBitmap = null;
		}
		if (sketBitmap != null) {
			sketBitmap.recycle();
			sketBitmap = null;
		}
		label.set("", null, 0, 0);
		widgets.add(new Role(this));
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.setDrawFilter(filter);
		if (bgBitmap != null) {
			canvas.drawBitmap(bgBitmap, 0, 0, null);
		}
		if (roleBitmap != null) {
			role.draw(canvas);
		}
		if (sketBitmap != null) {
			canvas.drawBitmap(sketBitmap, 0, 0, null);
		}
		canvas.restore();
		label.draw(canvas);
		if (loadingOK) {
			Log.e("", "画等待");
			canvas.drawBitmap(loadBitmap,
					(getWidth() - loadBitmap.getWidth()) / 2, 0, null);
		}
	}

	public String getLabelText() {
		String string = label.getText();
		label.setText("");
		invalidate();
		return string;
	}

	/**
	 * 用来保存点击移动的是人物还是文字对象
	 */
	private Widget toucherWidget = null;
	private String bitmappath;
	private String gg;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float fx = event.getX();
		float fx1 = event.getX() / scale;
		float fy = event.getY();
		float fy1 = event.getY() / scale;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN: {
			zoom = true;
			return false;
		}
		case MotionEvent.ACTION_DOWN: {
			boolean b = false;
			if (label.testHit(fx - label.getX(), fy - label.getY())) {
				b = label.touchDown(fx - label.getX(), fy - label.getY());
				toucherWidget = label;
			} else if (role.testHit(fx1 - role.getX(), fy1 - role.getY())) {
				b = role.touchDown(fx1 - role.getX(), fy1 - role.getY());
				toucherWidget = role;
			}
			return b;
		}

		case MotionEvent.ACTION_MOVE: {

			if (toucherWidget == label) {
				label.touchDrag(fx - label.getX(), fy - label.getY());
				return true;
			} else if (toucherWidget == role) {
				role.touchDrag(fx1 - role.getX(), fy1 - role.getY());
				if (zoom) {
					
				}
				return true;
			}
			return false;
		}

		case MotionEvent.ACTION_UP: {
			zoom = false;
			if (toucherWidget == label) {
				label.touchUp(fx - label.getX(), fy - label.getY());
				return true;
			} else if (toucherWidget == role) {
				role.touchUp(fx1 - role.getX(), fy1 - role.getY());
				return true;
			}
			toucherWidget = null;
			return false;
		}

		default:
			return super.onTouchEvent(event);
		}
	}

	public boolean testHit2(float x, float y) {
		Log.e("", " x,y,w,h" + x + "||" + y + "||");
		if (roleBitmap != null) {
			if (x < 0 || y < 0 || x > roleBitmap.getWidth()
					|| y > roleBitmap.getHeight()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}

	}

	public void setDown(float x, float y) {
		lastx = x + pox;
		lasty = y + poy;
		// draged = false;
	}

	public void setPostion(float x, float y) {
		x += pox;
		y += poy;
		this.pox += x - lastx;
		this.poy += y - lasty;
		Log.e("", "人物拖拽--" + x + "||" + lastx + "||" + pox);
		lastx = x;
		lasty = y;
		invalidate();
	}

	public Bitmap save() {
		// TODO Auto-generated method stub
		/* 在这里保存图片纯粹是为了方便,保存图片进行验证 */

		String fileUrl = Environment.getExternalStorageDirectory().toString()
				+ "/android/data/pobaby_share.png";

		try {
			FileOutputStream fos = new FileOutputStream(new File(fileUrl));
			cacheBitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cacheBitmap;
	}

	/**
	 * 图片等比缩放函数
	 */
	public Bitmap ChangeScale(Bitmap bitmap) {
		/* 设置图片缩小的比例 */
		float scaleWidth = bitmap.getWidth();
		float scaleHeight = bitmap.getHeight();

		/* 计算出这次要缩小的比例 */
		scaleWidth = 0.2f;
		scaleHeight = 0.2f;
		/* 产生reSize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	/**
	 * 人物对象
	 * 
	 * @author Yichou
	 * 
	 */
	private class Role extends Widget {

		public Role(View view) {
			super(view);
		}

		@Override
		public void draw(Canvas canvas) {
			if (roleBitmap != null) {
				// 进入自制 执行1次。px py都为0 ，选择人物后执行一次，移动时候不断执行改变值
				canvas.drawBitmap(roleBitmap, px, py, null);
			}
		}
	}

}
