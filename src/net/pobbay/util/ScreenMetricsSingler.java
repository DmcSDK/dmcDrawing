package net.pobbay.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


/*
 * 像素适配所需要的单例
 */
public class ScreenMetricsSingler {
	private static ScreenMetricsSingler singler = null;
	public int screenWidth;
	public int screenHeight;
	/** 计算出高度头部标题栏高度 **/
	public int titleBarHeight;
	/** 计算出高度低部标题栏高度 **/
	public int bottomHeight;

	public synchronized static ScreenMetricsSingler getInstance() {
		if (singler == null) {
			singler = new ScreenMetricsSingler();
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager mWm = (WindowManager) ContextAll.getContext().getSystemService(Context.WINDOW_SERVICE);
			mWm.getDefaultDisplay().getMetrics(dm);
			singler.screenHeight = dm.heightPixels;
			singler.screenWidth = dm.widthPixels;
			singler.titleBarHeight = (int) (singler.screenHeight * 0.068);
			singler.bottomHeight = (int) (singler.screenHeight * 0.077);
		}
		return singler;
	}
}
