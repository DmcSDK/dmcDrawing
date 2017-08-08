package net.pobbay.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class MyButton extends ImageView {
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			// this.setColorFilter(0x808080, Mode.ADD);
			this.setColorFilter(0x80808080);
			ScaleAnimation animation = new ScaleAnimation(1, 1.1f, 1, 1.1f,
					getWidth() / 2, getHeight() / 2);
			animation.setDuration(200);
			animation.setFillAfter(true);
			this.startAnimation(animation);

			super.onTouchEvent(event);

			return true;
		}

		case MotionEvent.ACTION_UP: {
			Animation animation = new ScaleAnimation(1.1f, 1, 1.1f, 1,
					getWidth() / 2, getHeight() / 2);
			animation.setDuration(200);
			animation.setFillAfter(true);
			this.startAnimation(animation);
			this.clearColorFilter();
			break;
		}
		}

		return super.onTouchEvent(event);
	}
}
