package net.pobbay.view;

import net.pobbay.mms.R;
import net.pobbay.util.PaintWidth;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class Drawing extends FragmentActivity {
	private ViewGroup g;
	private Panel2 panel2;
	private Pane3 pane3;
	private static Bitmap previewBitmap;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_main2);
		g = (ViewGroup) findViewById(R.id.contener);
		pane3 = new Pane3(this, previewBitmap);
		panel2 = new Panel2(this, previewBitmap);
		g.addView(pane3, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		g.addView(panel2, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title2));
		textView.setText(R.string.title_main2);
		textView.setTypeface(typeface);
		MyButton button = (MyButton) findViewById(R.id.paint);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog dialog = new PaintWidth(Drawing.this, panel2);
				dialog.show();
			}
		});
		MyButton back = (MyButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panel2.back();
			}
		});

		MyButton eraser = (MyButton) findViewById(R.id.eraser);
		eraser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				panel2.eraser();
			}
		});

		MyButton over = (MyButton) findViewById(R.id.over);
		over.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panel2.over();
			}
		});

		MyButton color = (MyButton) findViewById(R.id.color);
		color.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panel2.color(Drawing.this);
			}
		});
		MyButton ok = (MyButton) findViewById(R.id.btn_oksave);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				panel2.save();
				Intent data = new Intent();
				data.putExtra("X", panel2.cx);
				data.putExtra("Y", panel2.cy);
				setResult(5555, data);
				finish();
			}
		});
		MyButton backUp = (MyButton) findViewById(R.id.btn_titlebar_left2);
		backUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		panel2.color(Drawing.this);
	}

	public static void setPreviewBitmap(Bitmap previewBitmap) {
		Drawing.previewBitmap = previewBitmap;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}
