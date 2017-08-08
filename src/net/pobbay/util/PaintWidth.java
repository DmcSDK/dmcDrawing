package net.pobbay.util;

import net.pobbay.mms.R;
import net.pobbay.view.MyButton;
import net.pobbay.view.Panel2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PaintWidth extends AlertDialog implements android.view.View.OnClickListener {
	private SeekBar seekBar;
	private MyButton button;
	private Panel2 p;
	Context mContext;
	public PaintWidth(Context context, Panel2 panel2) {
		super(context);
		mContext=context;
		this.p = panel2;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paint_size);
		seekBar = (SeekBar) findViewById(R.id.seekBar2);
		DisplayMetrics dm=new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		ViewGroup.LayoutParams layoutParams=seekBar.getLayoutParams();
		layoutParams.width=(int)(dm.widthPixels*0.8);
		seekBar.setLayoutParams(layoutParams);
		button = (MyButton) findViewById(R.id.paintwidth);
		button.setOnClickListener(this);


	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.paintwidth:
				p.goDraw(seekBar.getProgress());
				dismiss();
				break;

			default:
				break;
		}
	}
}
