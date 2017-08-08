package net.pobbay.gui;

import java.util.ArrayList;
import java.util.List;

import net.pobbay.mms.R;
import net.pobbay.util.ContextAll;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;

/**
 * 文字的显示
 *
 * @author Yichou
 *
 */
public class Label extends Widget {
	private String text;
	private String[] lines;
	private Paint paint;

	public Label(View view) {
		super(view);
		setPosition(10, 10);
	}

	public void set(String text, Typeface typeface, int color, float size) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(typeface);
		paint.setColor(color);
		paint.setTextSize(size);
		setText(text);
	}

	public void setTypeface(Typeface t) {
		paint.setTypeface(t);
	}

	public void setTextColor(int c) {
		paint.setColor(c);
	}

	public void setTextSize(float s) {
		if (paint == null) {

		} else {
			paint.setTextSize(s);
		}
	}

	public String getText() {
		return text;
	}

	// TODO 文字是否换行方法
	public void testsss() {
		float showw = view.getResources().getDimensionPixelSize(
				R.dimen.edit_width);

		List<String> list = new ArrayList<String>(10);
		char[] strs = text.toCharArray();

		float fw, tw = 0;
		int start = 0;
		int count = 0;
		for (int i = 0; i < strs.length; i++) {
			boolean newline = false;

			if (strs[i] == '\r') { // 跳过 \r\n
				i++;
				count += 2;

				newline = true;
			} else if (strs[i] == '\n') { // 跳过 \n
				newline = true;
				count++;
			} else {
				fw = paint.measureText(strs, i, 1);
				if (tw + fw > showw) { // 换行
					newline = true;
				} else {
					tw += fw;
					count++;
				}
			}

			if (newline) {
				list.add(String.copyValueOf(strs, start, count));
				tw = 0;
				start += count;
				count = 0;
			}
		}

		if (count > 0) {
			list.add(String.copyValueOf(strs, start, count));
		}

		lines = (String[]) list.toArray(new String[list.size()]);
	}

	Rect rect = new Rect();

	public void setText(String s) {
		text = s;

		testsss();
		// lines = text.split("\n");
		//
		int maxW = 0;
		int totalH = 0;
		for (String str : lines) {
			paint.getTextBounds(str, 0, str.length(), rect);
			if (rect.width() > maxW)
				maxW = rect.width();
			totalH += rect.height();
		}

		setSize(maxW, totalH);
		view.invalidate();
	}

	public float getTextSize() {
		if (paint == null) {

			return 0;
		}
		return paint.getTextSize();
	}

	@Override
	public void draw(Canvas canvas) {
		float dy = py;
		if (lines != null) {
			for (String str : lines) {
				// paint.sets
				paint.getTextBounds(str, 0, str.length(), rect);
				dy += rect.height();
				canvas.drawText(str, px, dy, paint);
			}
		}
	}
}