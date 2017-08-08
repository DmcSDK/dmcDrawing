package net.pobbay.view;

import net.pobbay.gui.Label;
import net.pobbay.gui.Widget;
import net.pobbay.mms.R;
import net.pobbay.service.MyUtils;
import net.pobbay.util.ContextAll;
import net.pobbay.util.DeleteFile;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
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
public class newPanel extends ImageView {
	private AssetManager asset;
	private Bitmap loadBitmap;
	private Bitmap httpbitmap;
	private Bitmap httprbitmap;
	private String bitmappath;
	private String gg;
	private Bitmap roleBitmap;
	private Context mcontext;
	public Bitmap cacheBitmap;
	private Bitmap sketBitmap;
	private Canvas cacheCanvas;
	private DrawFilter filter = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG); //
	MyUtils myUtils=new MyUtils();
	private Role roles[] = null;
	private Role oldRoles[] = null;
	private Role oldRoles2[] = null;
	private Point lastpoint = null;
	private int mnum = 0;
	private int[] layoutid;
	private int[] oldLayoutid;
	private int[] oldLayoutid2;
	private int pic;
	private boolean roleToch;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	int mode = NONE;
	float oldDist = 1f;
	float oldRotation = 0;
	PointF start = new PointF();
	PointF mid = new PointF();
	PointF centre = new PointF();
	private float newDist;
	private float rotation;
	private boolean go = true;
	private boolean go1 = false;

	private Label label;
	private Bitmap bgBitmap;
	private boolean loadingOK;
	Matrix matrix = new Matrix();
	Matrix matrix1 = new Matrix();
	Matrix savedMatrix = new Matrix();

	ColorMatrix colorMatrix = new ColorMatrix();
	
	private int mLastMotionX, mLastMotionY;
	// 是否移动了
	private boolean isMoved;
	// 是否释放了
	private boolean isReleased;
	// 计数器，防止多次点击导致最后一次形成longpress的时间变短
	private int mCounter;
	// 长按的runnable
	private Runnable mLongPressRunnable;
	// 移动的阈值

	private static final int TOUCH_SLOP = 20;
	float[] array = new float[] { 0.35f, 0.65f, 0.05f, 0, 50, 0.35f, 0.65f,
			0.05f, 0, 50, 0.35f, 0.65f, 0.05f, 0, 50, 0, 0, 0, 1, 20 };
	Paint paint = new Paint();

	public newPanel(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		label = new Label(this);
		asset = context.getAssets();
		colorMatrix.set(array);
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		this.mcontext = context;
		loadBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.load);
		mLongPressRunnable = new Runnable() {
			@Override
			public void run() {
				mCounter--;
				// 计数器大于0，说明当前执行的Runnable不是最后一次down产生的。
				if (mCounter > 0 || isReleased || isMoved || pic == -1)
					return;
				roles[pic].delete = true;
				invalidate();
				showdeleteRole();
			}
		};
	}

	// 终极控制方法
	public void control(int count) {
		int jk = 0;
		oldRoles = roles;
		roles = new Role[count];
		for (int i = 0; i < count - 1; i++) {
			if (oldRoles != null) {
				roles[i] = oldRoles[i];
			}
		}
		oldLayoutid = layoutid;
		layoutid = new int[count];
		for (int i = count - 1; i >= 0; i--) {
			if (oldLayoutid != null) {
				if (jk == 0) {
					layoutid[jk] = i;
				} else {
					layoutid[jk] = oldLayoutid[jk - 1];
				}
			}
			jk++;
		}
	}
	//设置人物
	public void setRoleBitmap(String path, String gallery) {
		bitmappath = path;
		gg = gallery;
		loadingOK = true;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				
				httprbitmap = myUtils.getBitmap2(asset, bitmappath, gg);
				
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
		invalidate();
	}

	public void setRole(Bitmap httprbitmap2) {
		mnum++;
		loadingOK = false;
		Role role = new Role(this);
		control(mnum);
		roleBitmap = httprbitmap2;
		role.bitmap = roleBitmap;
		role.imageWidth = roleBitmap.getWidth();
		role.imageHeight = roleBitmap.getHeight();
		role.paint = paint;
		role.matrix.postTranslate(0, 0);
		roles[mnum - 1] = role;
		invalidate();
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
	//删除
	public void showdeleteRole() {
		new AlertDialog.Builder(mcontext)
				.setTitle("删除")
				// 设置标题
				.setMessage("确定要删除指定图片?")
				// 设置提示消息
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {// 设置确定的按键
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do something
								deleteRole();
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {// 设置取消按键
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do something
								dialog.dismiss();
							}
						}).setCancelable(false)// 设置按返回键是否响应返回，这是是不响应
				.show();// 显示
	}

	public void deleteRole() {
		mnum--;
		int count = roles.length - 1;
		int jk = 0;
		oldRoles = roles;
		oldRoles2 = roles;
		roles = new Role[count];

		for (int i = 0; i < mnum + 1; i++) {
			if (i < mnum) {
				if (i >= pic) {
					oldRoles2[i] = oldRoles[i + 1];
				}
			}
		}
		for (int i = 0; i < count; i++) {
			if (oldRoles2 != null) {
				roles[i] = oldRoles2[i];
			}
		}
		oldLayoutid = layoutid;
		oldLayoutid2 = layoutid;
		layoutid = new int[count];
		int lindex = 0;
		for (int i = 0; i < mnum + 1; i++) {
			if (oldLayoutid[i] == pic) {
				lindex = i;
			}
		}

		for (int i = 0; i < mnum + 1; i++) {
			if (i < mnum) {
				if (i >= lindex) {
					oldLayoutid2[i] = oldLayoutid[i + 1];
				}
				if (oldLayoutid2[i] > pic) {
					oldLayoutid2[i] = oldLayoutid[i + 1] - 1;
				}
			}
		}

		for (int i = 0; i < count; i++) {
			layoutid[i] = oldLayoutid2[i];
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(filter);
		if (bgBitmap != null) {
			canvas.drawBitmap(bgBitmap, 0, 0, null);
		}

		if (mnum > 0 && go) {
			for (int i = 0; i < mnum; i++) {
				roles[i].draw(canvas);
			}
		}
		if (mnum > 0 && go1 == true) {
			for (int i = mnum - 1; i >= 0; i--) {
				roles[layoutid[i]].draw(canvas);
			}
		}
		label.draw(canvas);
		
		if (loadingOK) {
			canvas.drawBitmap(loadBitmap, (getWidth()-loadBitmap.getWidth())/2, 0, null);
		}
	}

	private Widget toucherWidget = null;
	//事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float fx = event.getX();
		float fy = event.getY();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (label.testHit(fx - label.getX(), fy - label.getY())) {
				label.touchDown(fx - label.getX(), fy - label.getY());
				toucherWidget = label;
			} else {
				mode = DRAG;
				pic = getTouchIndex(event.getX(), event.getY());
				Log.e("", "点击的是" + pic);
				if (pic == -1) {
					roleToch = false;
				} else {
					go = false;
					go1 = true;
					topOne(pic);
					roleToch = true;
					savedMatrix.set(roles[pic].matrix);
					lastpoint = new Point((int) event.getX(),
							(int) event.getY());
					// 自定义长按事件
					mLastMotionX = (int) event.getX();
					mLastMotionY = (int) event.getY();
					mCounter++;
					isReleased = false;
					isMoved = false;
					postDelayed(mLongPressRunnable, 2000);// 按下 3秒后调用线程
				}
			}
			return true;
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			pic = getTouchIndex(event.getX(1), event.getY(1));
			if (pic != -1) {
				topOne(pic);
				float s = spacing(event);
				if (s != 0) {
					oldDist = s;
				}
				float r = rotation(event);
				if (r != 0) {
					oldRotation = r;
				}
				centre = getImgaexy(pic);
				mode = ZOOM;
				midPoint(mid, event);
				savedMatrix.set(roles[pic].matrix);
			}
			return true;
		}

		case MotionEvent.ACTION_MOVE: {
			if (toucherWidget == label) {
				label.touchDrag(fx - label.getX(), fy - label.getY());
				return true;
			} else {
				if (roleToch && mode == DRAG && pic != -1) {
					roleMove((int) event.getX(), (int) event.getY());
					// 自定义长按
					if (Math.abs(mLastMotionX - (int) event.getX()) > TOUCH_SLOP
							|| Math.abs(mLastMotionY - (int) event.getY()) > TOUCH_SLOP) {
						// 移动超过阈值，则表示移动了
						isMoved = true;
						roles[pic].delete = false;
					}
				}
				if (roleToch && mode == ZOOM && pic != -1) {
					isMoved = true;
					midPoint(start, event);
					roleZoom(event);
				}
				return true;
			}
		}

		case MotionEvent.ACTION_POINTER_UP:
			// 动作完成
			mode = NONE;
			break;
		case MotionEvent.ACTION_UP: {
			if (toucherWidget == label) {
				label.touchUp(fx - label.getX(), fy - label.getY());
				toucherWidget = null;
				return true;
			}else{
			roleToch = false;
			mode = NONE;
			// 释放了
			if (pic != -1) {
				roles[pic].delete = false;
				isReleased = true;
			}
			return true;
			}
		}
		}
		return true;
	}

	
	public void roleZoom(MotionEvent event) {
		matrix1.set(savedMatrix);
		float s = spacing(event);
		if (s != 0) {
			newDist = s;
		}
		float r = rotation(event);
		if (r != 0) {
			rotation = r - oldRotation;
		}
		float scale = (newDist / oldDist);
		roles[pic].Rotate = rotation;
		matrix1.postScale(scale, scale, mid.x, mid.y);//
		matrix1.postRotate(rotation, mid.x, mid.y);//
		roles[pic].matrix.set(matrix1);
		invalidate();
	}

	
	public void roleMove(int xr, int yr) {
		matrix1.set(savedMatrix);
		int x = xr - lastpoint.x;
		int y = yr - lastpoint.y;
		matrix1.postTranslate(x, y);
		roles[pic].matrix.set(matrix1);
		invalidate();
	}

	public PointF getImgaexy(int pic) {
		PointF point = new PointF();
		float x = (int) (roles[pic].imageWidth * roles[pic].Scale / 2)
				+ roles[pic].move_x;
		float y = (int) (roles[pic].imageHeight * roles[pic].Scale / 2)
				+ roles[pic].move_y;
		point.x = x;
		point.y = y;
		return point;
	}

	// TODO 浮动层
	public void topOne(int pic2) {
		int nowpicindex = 0;
		for (int i = 0; i < mnum; i++) {
			if (layoutid[i] == pic2) {
				nowpicindex = i;
			}
		}

		if (layoutid[0] == pic2) {
			
		} else {
			int[] top = new int[nowpicindex + 1];
			for (int i = 0; i < nowpicindex; i++) {
				top[i] = layoutid[i];
				Log.e("", "top" + top[i]);
			}
			layoutid[0] = pic2;
			int j = 0;
			for (int i = 1; i <= nowpicindex; i++) {

				layoutid[i] = top[j];
				j++;
			}
		}

	}

	public int getTouchIndex(Float x, Float y) {
		for (int j = 0; j < mnum; j++) {
			float[] px = givePointBeforTransform(x, y, roles[layoutid[j]]);
			if (px[0] > 0 && px[0] <= roles[layoutid[j]].imageWidth
					&& px[1] > 0 && px[1] <= roles[layoutid[j]].imageHeight) {

				return layoutid[j];
			}
		}
		return -1;
	}

	// 矩阵逆运算
	private float[] givePointBeforTransform(Float x, Float y, Role imageParams) {
		Matrix inverseMatrix = new Matrix();
		imageParams.matrix.invert(inverseMatrix);
		float[] values = new float[9];
		inverseMatrix.getValues(values);
		float x1 = values[0] * x + values[1] * y + values[2];
		float y1 = values[3] * x + values[4] * y + values[5];
		return new float[] { x1, y1 };
	}

	// 触碰两点间距离
	private float spacing(MotionEvent event) {
		float x = 0;
		float y = 0;
		try {
			x = event.getX(0) - event.getX(1);
			y = event.getY(0) - event.getY(1);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FloatMath.sqrt(x * x + y * y);
	}

	// 取手势中心点
	private void midPoint(PointF point, MotionEvent event) {
		float x = 0;
		float y = 0;
		try {
			x = event.getX(0) + event.getX(1);
			y = event.getY(0) + event.getY(1);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		point.set(x / 2, y / 2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event) {
		double radians = 0;
		try {
			double delta_x = (event.getX(0) - event.getX(1));
			double delta_y = (event.getY(0) - event.getY(1));
			radians = Math.atan2(delta_y, delta_x);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (float) Math.toDegrees(radians);
	}

	// TODO 合成被操作的图片样式
	public void setTouchStyle(boolean now) {

	}

	private class Role {
		public float Scale = 1.0f;
		public float Rotate = 0;
		public Matrix matrix = new Matrix();
		public int imageWidth = 0;
		public int imageHeight = 0;
		public int TrueWidth = 0;
		public int TrueHeight = 0;
		public int move_x = 0;
		public int move_y = 0;
		public Bitmap bitmap;
		public Bitmap onTouchBitmap;
		public boolean delete;
		public Paint paint;

		public Role(View view) {

		}

		public void draw(Canvas canvas) {
			if (bitmap != null && delete) {
				canvas.drawBitmap(bitmap, matrix, paint);
			}
			if (bitmap != null && delete == false)
				canvas.drawBitmap(bitmap, matrix, null);
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void clear() {
		mnum=0;
		DeleteFile.delete();
		if (bgBitmap != null) {
			bgBitmap.recycle();
			bgBitmap = null;
		}
		if(roles!=null){
			roles=null;
			layoutid=null;
		}
		myUtils.recycleBitmap();
		label.set("", null, 0, 0);
		invalidate();
	}

	public void snapshot() {
		// TODO Auto-generated method stub
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
		if (mnum > 0 && go) {
			for (int i = 0; i < mnum; i++) {
				roles[i].draw(cacheCanvas);
			}
		}
		if (go1 == true) {
			for (int i = mnum - 1; i >= 0; i--) {
				roles[layoutid[i]].draw(cacheCanvas);
			}
		}
		float f = label.getTextSize();
		label.setTextSize(f);
		label.draw(cacheCanvas);
	}

	public void save() {
		// TODO Auto-generated method stub

	}

	public void setSketBitmap(Bitmap sketMap,int x,int y) {
		mnum++;
		Role role = new Role(this);
		control(mnum);
		roleBitmap = sketMap;
		role.bitmap = roleBitmap;
		role.imageWidth = roleBitmap.getWidth();
		role.imageHeight = roleBitmap.getHeight();
		role.paint = paint;
		role.matrix.preTranslate(x, y);
		roles[mnum - 1] = role;
		invalidate();
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
				
				httpbitmap = myUtils.getBitmap(asset, bitmappath, gg);
				
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
		invalidate();
	}

	public void setErr() {
		Toast.makeText(ContextAll.getContext(), "呜呜呜.下载失败了.我已经很努力了...",
				Toast.LENGTH_LONG).show();
		loadingOK = false;
		invalidate();
	}

	public String getLabelText() {
		// TODO Auto-generated method stub
		return null;
	}

	public Label getLabel() {
		return label;
	}

}
