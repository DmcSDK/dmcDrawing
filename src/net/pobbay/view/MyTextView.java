package net.pobbay.view;

import net.pobbay.service.Res;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(Res.wawaTypeface);
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(Res.wawaTypeface);
	}

	public MyTextView(Context context) {
		super(context);
		setTypeface(Res.wawaTypeface);
	}

}
