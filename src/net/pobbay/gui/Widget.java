package net.pobbay.gui;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;

public abstract class Widget {
	protected float px, py;
	protected float width, height;
	protected View view;
	protected Matrix matrix;

	public Widget(View view) {
		this.view = view;

	}

	public float getX() {

		return px;
	}

	public float getY() {
		return py;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	/**
	 * 设置图片的位置
	 *
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		Log.e("", "设置XY" + x);
		this.px = x;
		this.py = y;
	}

	public void setSize(float w, float h) {
		this.width = w;
		this.height = h;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
		// view.invalidate();
	}

	public abstract void draw(Canvas canvas);

	public boolean testHit(float x, float y) {
		Log.e("", "测试字的XY是" + x);
		if (x < 0 || y < 0 || x > width || y > height)
			return false;

		return true;
	}

	boolean touched;
	float lastX, lastY;

	public boolean touchDown(float x, float y) {
		touched = true;
		// 转换为父系坐标
		lastX = x + px;
		lastY = y + py;
		draged = false;
		return true;
	}

	boolean draged = false;

	public void touchDrag(float x, float y) {
		// 转换为父系坐标
		x += px;
		y += py;

		// if(Math.abs(x-lastX) > 5 && Math.abs(y-lastY) > 5){
		this.px += x - lastX;
		this.py += y - lastY;
		Log.e("", "---touchDrag" + this.px);
		lastX = x;
		lastY = y;
		draged = true;
		view.invalidate();
		// }
	}

	public void touchUp(float x, float y) {
		if (!draged) { // 没拖拽 响应点击事件

		}
	}

}