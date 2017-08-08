package net.pobbay.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;

public class Pane3 extends View {
	private Context context;
	private Bitmap bgmap;
	private Canvas bgCanvas;
	DisplayMetrics dm;
	private Bitmap previewBitmap;
	int widthScreen;
	int heightScreen;
	private boolean a;

	public Pane3(Context context, Bitmap previewBitmap) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		widthScreen = dm.widthPixels;
		heightScreen = dm.heightPixels;
		this.previewBitmap = previewBitmap;
		bgmap = Bitmap.createBitmap(previewBitmap);
		bgCanvas = new Canvas(bgmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bgmap, 0, 0, null);
	}

}
