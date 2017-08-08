package net.pobbay.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.pobbay.entity.DrawPath;
import net.pobbay.service.ColorListener;
import net.pobbay.util.ColorPickerDialog;
import net.pobbay.util.ColorPickerDialog.OnColorChangedListener;
import net.pobbay.util.ContextAll;
import net.pobbay.util.FileUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Panel2 extends View implements OnColorChangedListener {
	private Context context;
	private Bitmap bgmap;
	private Paint paint;
	private Paint mBitmapPaint;// 画布的画笔
	private Canvas bgCanvas;
	private Canvas bb;
	private Path path;
	private static final float TOUCH_TOLERANCE = 5;
	private int dmc;
	private float cur_x, cur_y;
	private float bgX, bgY;
	// 保存Path路径的集合,用List集合来模拟栈
	private static List<DrawPath> savePath;
	// 记录Path路径的对象
	private DrawPath dp;
	private Bitmap previewBitmap;
	int widthScreen;
	int heightScreen;
	DisplayMetrics dm;
	private int topWidth=0;
	private Bitmap drawBitmap;
	private int paint_width=20;
	private int paint_color;
	private boolean a;
	private Bitmap sketMap;
	private float point1[] = new float[2];
	private float point2[] = new float[2];
	private boolean b = true;
	private boolean c = false;
	public int cx;
	public int cy;
	public Panel2(Context context, Bitmap previewBitmap) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		widthScreen = dm.widthPixels;
		heightScreen = dm.heightPixels;
		this.previewBitmap = previewBitmap;
		bgmap = Bitmap.createBitmap(widthScreen, heightScreen, Config.ARGB_8888);
		bgCanvas = new Canvas(bgmap);
		savePath = new ArrayList<>();
		paint_color = Color.BLACK;
		setPosition();
		a = false;
	}

	public void setPosition() {
		int c = previewBitmap.getWidth();
		bgX = (widthScreen - c) / 2;
		bgX = 0;
		bgY = 0;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bgmap, bgX, bgY, null);
		if (a) {
			canvas.drawBitmap(drawBitmap, bgX, bgY, null);
			a = false;
		}
		if (dmc == 2 && paint != null && path != null) {
			canvas.drawPath(path, paint);
		}
	}

	public void goDraw(int width) {
		if(topWidth<width){
			topWidth=width;
		}
		paint_width = width;
		dmc = 2;
		createPaint(paint_color, width, 0);
	}

	public void createPaint(int color, int width, int c) {
		paint = new Paint();
		paint.setColor(color);
		paint.setDither(true);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(width);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		paint.setStrokeCap(Paint.Cap.ROUND);// 形状
		if (c == 1) {
			// paint.setAlpha(1);
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		}
	}

	public void back() {
		System.out.println("back");
		undo();
	}

	public void eraser() {
		createPaint(Color.YELLOW, 40, 1);
	}

	public void color(Context context1) {

		ColorListener colorListener = new ColorListener(this);
		Dialog dialog = new ColorPickerDialog(context1, "点击中心圆点确认颜色", colorListener);
		dialog.show();

	}


	public void setColor(int color) {
		System.out.println(paint);
		dmc = 2;
		paint_color = color;
		createPaint(color, paint_width, 0);
	}

	public void over() {
		Log.e("", "清除");
		bgmap = Bitmap.createBitmap(widthScreen, heightScreen,
				Config.ARGB_8888);
		bgCanvas.setBitmap(bgmap);
		savePath.clear();
		point1[0]=0;
		point1[1]=0;
		point2[0]=0;
		point2[1]=0;
		path=null;
		b=true;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (dmc == 2) {
					if (b) {
						point1[0] = x;
						point1[1] = y;
						point2[0] = x;
						point2[1] = y;
						b = false;
					}
					path = new Path();
					cur_x = x;
					cur_y = y;
					path.moveTo(cur_x, cur_y);
					dp = new DrawPath();
					dp.path = path;
					dp.paint = paint;
					dp.color = paint.getColor();
					invalidate();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (dmc == 2) {
					float dx = Math.abs(x - cur_x);
					float dy = Math.abs(cur_y - y);
					if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
						path.quadTo(cur_x, cur_y, x, y);
						cur_x = x;
						cur_y = y;
					}
					if (point1[1] < y) {
						point1[1] = y;
					}
					if (point1[0] < x) {
						point1[0] = x;
					}
					if (point2[0] > x) {
						point2[0] = x;
					}
					if (point2[1] > y) {
						point2[1] = y;
					}
					invalidate();
				} else {
					Toast.makeText(ContextAll.getContext(), "你还没有选画笔哦...", Toast.LENGTH_LONG).show();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (dmc == 2) {
					bgCanvas.drawPath(path, paint);
					savePath.add(dp);
					path = null;
					invalidate();
				}
				break;
		}

		return true;
	}

	/**
	 * 撤销的核心思想就是将画布清空， 将保存下来的Path路径最后一个移除掉， 重新将路径画在画布上面。
	 */
	public void undo() {
		bgmap = Bitmap
				.createBitmap(widthScreen, heightScreen, Config.ARGB_8888);
		bgCanvas.setBitmap(bgmap);// 重新设置画布，相当于清空画布
		// 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉...
		System.out.println("数组多少个" + savePath.size());
		if (savePath != null && savePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			savePath.remove(savePath.size() - 1);
			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				drawPath.paint.setColor(drawPath.color);
				bgCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新
		}
	}

	@Override
	public void colorChanged(int color) {

	}

	public void save() {
		FileUtils.createDirectory("/pobaby/");
		String fileUrl = Environment.getExternalStorageDirectory() + "/pobaby" +"/pobaby_sket.png";
		Log.e("222", "保存裁剪的图片"+fileUrl);
		try {
			FileOutputStream fos = new FileOutputStream(new File(fileUrl));
			int x = 0;
			int y = 0;
			if ((int) point1[0] < (int) point2[0]) {
				x = (int) point1[0]-topWidth;
			} else {
				x = (int) point2[0]-topWidth;
			}
			if ((int) point1[1] < (int) point2[1]) {
				y = (int) point1[1]-topWidth;
			} else {
				y = (int) point2[1]-topWidth;
			}
			Log.e("", "x" + point1[0]);
			Log.e("", "y" + point2[0]);
			int width = (int) (point1[0] - point2[0]) +topWidth+20;
			int height = (int) (point1[1] - point2[1]) +topWidth+20;
			cx=x;
			cy=y;
			if(x<0){
				x=0;
			}
			if(y<0){
				y=0;
			}
			Bitmap bitmap = Bitmap.createBitmap(bgmap, x, y, width, height);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
