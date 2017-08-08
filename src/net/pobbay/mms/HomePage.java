package net.pobbay.mms;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import net.pobbay.util.ScreenMetricsSingler;
import net.pobbay.view.ChoicenessListActivity;
import net.pobbay.view.GifListActivity;
import net.pobbay.view.MoreActivity;


/**
 * ������
 * 
 * @author yu
 * 
 */
public class HomePage extends Activity implements OnClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		setTitle(getResources().getString(R.string.app_name));
		initView();
	}

   	void	initView(){
		ImageView imageView=(ImageView)findViewById(R.id.imageView);
		ViewGroup.LayoutParams layoutParams=imageView.getLayoutParams();
		int h= (int)(ScreenMetricsSingler.getInstance().screenHeight/3.2);
		layoutParams.height=h;
		layoutParams.width=(int)(h*1.16);
		imageView.setLayoutParams(layoutParams);
		Button btn_create=(Button)findViewById(R.id.btn_create);
		Button btn_choiceness=(Button)findViewById(R.id.btn_choiceness);
		Button btn_gif=(Button)findViewById(R.id.btn_gif);
		Button more=(Button)findViewById(R.id.more);

		ViewGroup.LayoutParams btn_createlayoutParams=btn_create.getLayoutParams();
		int w= (int)(ScreenMetricsSingler.getInstance().screenWidth/3.7);
		btn_createlayoutParams.height=w;
		btn_createlayoutParams.width=w;
		btn_create.setLayoutParams(btn_createlayoutParams);

		ViewGroup.LayoutParams btn_choicenesslayoutParams=btn_choiceness.getLayoutParams();
		btn_choicenesslayoutParams.height=w;
		btn_choicenesslayoutParams.width=w;
		btn_choiceness.setLayoutParams(btn_choicenesslayoutParams);

		ViewGroup.LayoutParams btn_giflayoutParams=btn_gif.getLayoutParams();
		btn_giflayoutParams.height=w;
		btn_giflayoutParams.width=w;
		btn_gif.setLayoutParams(btn_giflayoutParams);

		ViewGroup.LayoutParams morelayoutParams=more.getLayoutParams();
		morelayoutParams.height=w;
		morelayoutParams.width=w;
		more.setLayoutParams(morelayoutParams);


		btn_create.setOnClickListener(this);
		btn_choiceness.setOnClickListener(this);
		btn_gif.setOnClickListener(this);
		more.setOnClickListener(this);
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create:
			startActivity(new Intent(this, Designer.class));
			finish();
			break;
		case R.id.btn_choiceness:
			Toast.makeText(this,"喵喵喵~~~不给好吃的就不给看",Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_gif:
			startActivity(new Intent(this, GifListActivity.class));
			finish();
			break;
		case R.id.more:
			Toast.makeText(this,"汪汪汪~~~不给好吃的就不给看",Toast.LENGTH_LONG).show();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		System.gc();
		super.onDestroy();
	}
}
