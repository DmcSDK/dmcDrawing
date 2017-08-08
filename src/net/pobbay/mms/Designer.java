package net.pobbay.mms;

import java.util.List;
import java.util.Random;

import net.pobbay.service.Res;
import net.pobbay.service.Sms;
import net.pobbay.service.SmsParser;
import net.pobbay.util.AppContext;
import net.pobbay.util.DeleteFile;
import net.pobbay.view.Drawing;
import net.pobbay.view.Panel;
import net.pobbay.view.newPanel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;




public class Designer extends FragmentActivity implements OnClickListener {
	private static final int REQ_CODE_IMAGEPICKER_BG = 0x1001;
	private static final int REQ_CODE_IMAGEPICKER_ROLE = 0x1002;
	private newPanel panel;
	private List<Sms> smsList;
	public boolean b_showBanner = false;

	private Bitmap sketMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn_clear).setOnClickListener(this);
		findViewById(R.id.btn_bg).setOnClickListener(this);
		findViewById(R.id.btn_text).setOnClickListener(this);
		findViewById(R.id.btn_role).setOnClickListener(this);
		findViewById(R.id.btn_preview).setOnClickListener(this);
		findViewById(R.id.btn_sket).setOnClickListener(this);
		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);
		Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title));
		textView.setText(R.string.title_main);
		textView.setTypeface(typeface);
		panel = new newPanel(this);

		((ViewGroup) findViewById(R.id.contener)).addView(panel,
				new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
		panel.invalidate();
		
		smsList = new SmsParser().parse(this);
	
	}


	@Override
	protected void onDestroy() {
		panel.dispose();
		DeleteFile.delete();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bg: {
			Intent intent = new Intent(this, ImagePicker.class);
			intent.putExtra("path", "0");
			startActivityForResult(intent,REQ_CODE_IMAGEPICKER_BG);
			break;
		}

		case R.id.btn_role: {
			Intent intent = new Intent(this, ImagePicker.class);
			intent.putExtra("path","1");
			startActivityForResult(intent,REQ_CODE_IMAGEPICKER_ROLE);
			break;
		}

		case R.id.btn_clear: {
			panel.clear();
			break;
		}

		case R.id.btn_sket: {
			panel.snapshot();
			Drawing.setPreviewBitmap(panel.cacheBitmap);
			Intent intent = new Intent(this, Drawing.class);
			startActivityForResult(intent, 5555);
			break;
		}

		case R.id.btn_preview: {
			panel.snapshot();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					panel.save();
				}
			};
			Thread thread = new Thread(runnable);
			thread.start();
			Previewer.setPreviewBitmap(panel.cacheBitmap);
			startActivity(new Intent(this, Previewer.class));
			break;
		}

		case R.id.btn_text: {
			showTextDialog();
			break;
		}

		case R.id.btn_titlebar_left:
			finish();
			startActivity(new Intent(this, HomePage.class));
			break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			startActivity(new Intent(this, HomePage.class));
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("222","resultCode:"+resultCode);
		System.gc();
		switch (requestCode) {
			case  5555:
				int x=data.getIntExtra("X", 0);
				int y=data.getIntExtra("Y", 0);
				sketMap = BitmapFactory.decodeFile( Environment.getExternalStorageDirectory() + "/pobaby" +"/pobaby_sket.png");
				Log.e("222","sketMap.getHeight():"+sketMap.getHeight());
				panel.setSketBitmap(sketMap,x,y);
			break;

		case REQ_CODE_IMAGEPICKER_BG:
			if (resultCode == RESULT_OK && data != null) {
				if(data.getStringExtra("gallery")==null){
					panel.setBgBitmap(data.getStringExtra("path"),"");
				}else{
				    panel.setBgBitmap(data.getStringExtra("path"),data.getStringExtra("gallery"));
				}
			}
			break;

		case REQ_CODE_IMAGEPICKER_ROLE:
			if (resultCode == RESULT_OK && data != null) {
				if(data.getStringExtra("gallery")==null){
					panel.setRoleBitmap(data.getStringExtra("path"),"");
				}else{
					panel.setRoleBitmap(data.getStringExtra("path"),data.getStringExtra("gallery"));
				}
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	// TODO ��ʾ�ı���ʾ��
	private void showTextDialog() {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		DialogFragment f = new TextDialogFragment();
		// f.setArguments(args);
		f.show(ft, "dialog");
	}


	public static class TextDialogFragment extends DialogFragment implements
			OnClickListener, OnSeekBarChangeListener {
		private EditText editText;
		private SeekBar seekBar;
		private String defaultString = "";

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// if(getArguments() != null)
			// defaultString = getArguments().getString("text");
			newPanel panel = ((Designer) getActivity()).panel;
			defaultString = panel.getLabelText();
			setStyle(DialogFragment.STYLE_NO_FRAME, 0);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.dialog, container, false);

			final int[] ids = { R.id.font0, R.id.font1, R.id.font2,
					R.id.color0, R.id.color1, R.id.color2, R.id.color3,
					R.id.color4, R.id.color5, R.id.btn_left, R.id.btn_right,
					R.id.btn_close };

			for (int id : ids) {
				view.findViewById(id).setOnClickListener(this);
			}
			editText = (EditText) view.findViewById(R.id.editText1);
			editText.setTypeface(Typeface.DEFAULT);
			editText.setText(defaultString);
			seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
			seekBar.setOnSeekBarChangeListener(this);
			return view;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {                                                                                                                                                                                                                      
			case R.id.btn_close:
				dismiss();
				break;

			case R.id.btn_left: {
				String text = editText.getText().toString();

				if (text.length() <= 0) {
					dismiss();
					return;
				}

				newPanel panel = ((Designer) getActivity()).panel;
				panel.getLabel().set(text, editText.getTypeface(),
						editText.getTextColors().getDefaultColor(),
						editText.getTextSize());
				dismiss();
				break;
			}

			case R.id.btn_right: {
				editText.setText("hello");
				break;
			}

			case R.id.font0:
				editText.setTypeface(Typeface.DEFAULT);
				break;
			case R.id.font1:
				Typeface wawaTypeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/font1.ttf");
				editText.setTypeface(wawaTypeface);
				break;
			case R.id.font2:
				Typeface pingheTypeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/font2.ttf");
				editText.setTypeface(pingheTypeface);
				break;

			case R.id.color0:
				editText.setTextColor(Color.WHITE);
				break;
			case R.id.color1:
				editText.setTextColor(Color.BLUE);
				break;
			case R.id.color2:
				editText.setTextColor(Color.GREEN);
				break;
			case R.id.color3:
				editText.setTextColor(Color.RED);
				break;
			case R.id.color4:
				editText.setTextColor(Color.YELLOW);
				break;
			case R.id.color5:
				editText.setTextColor(Color.BLACK);
				break;
			}
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 + progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	}
}
