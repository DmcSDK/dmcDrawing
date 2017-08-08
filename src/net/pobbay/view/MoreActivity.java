package net.pobbay.view;

import java.util.ArrayList;
import java.util.List;

import net.pobbay.adapter.ViewPagerAdapter;
import net.pobbay.mms.HomePage;
import net.pobbay.mms.R;
import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoreActivity extends Activity implements OnPageChangeListener,OnClickListener{
	private MyButton button;
	private ViewPager vp;  
	private ViewPagerAdapter vpAdapter;  
	private List<View> views;
    // �ײ�С��ͼƬ  
    private ImageView[] dots;  
  
    // ��¼��ǰѡ��λ��  
    private int currentIndex;  
  
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		button = (MyButton) findViewById(R.id.btn_clear);
		button.setVisibility(View.INVISIBLE);
		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/font1.ttf");
		TextView textView = ((TextView) findViewById(R.id.tv_titlebar_title));
		textView.setText(R.string.title_more);
		findViewById(R.id.btn_titlebar_left).setOnClickListener(this);
		textView.setTypeface(typeface);
		initViews();
		initDots();
	}
	 private void initViews() {  
	        LayoutInflater inflater = LayoutInflater.from(this);  
	        views = new ArrayList<View>();  
	        // ��ʼ������ͼƬ�б�  
	        views.add(inflater.inflate(R.layout.m1, null));  
	        views.add(inflater.inflate(R.layout.m2, null));  
	        views.add(inflater.inflate(R.layout.m3, null));  
	        views.add(inflater.inflate(R.layout.m4, null));  
	        views.add(inflater.inflate(R.layout.m5, null));  
	        views.add(inflater.inflate(R.layout.m6, null));  
	        // ��ʼ��Adapter  
	        vpAdapter = new ViewPagerAdapter(views, this);   
	        vp = (ViewPager) findViewById(R.id.pager);  
	        vp.setAdapter(vpAdapter);  
	        // �󶨻ص�  
	        vp.setOnPageChangeListener(this);  
	    }  
	  
	    private void initDots() {  
	        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);  
	  
	        dots = new ImageView[views.size()];  	  
	        // ѭ��ȡ��С��ͼƬ  
	        for (int i = 0; i < views.size(); i++) {  
	            dots[i] = (ImageView) ll.getChildAt(i);  
	            dots[i].setEnabled(true);// ����Ϊ��ɫ  
	        }  
	        currentIndex = 0;  
	        dots[currentIndex].setEnabled(false);// ����Ϊ��ɫ����ѡ��״̬  
	    }
	    private void setCurrentDot(int position) {  
	    	dots[currentIndex].setImageResource(R.drawable.di); 
	        dots[position].setImageResource(R.drawable.dmc); 
	        currentIndex = position;  
	    }  
	  
	    // ������״̬�ı�ʱ����  
	    @Override  
	    public void onPageScrollStateChanged(int arg0) {  
	    }  
	  
	    // ����ǰҳ�汻����ʱ����  
	    @Override  
	    public void onPageScrolled(int arg0, float arg1, int arg2) {  
	    }  
	  
	    // ���µ�ҳ�汻ѡ��ʱ����  
	    @Override  
	    public void onPageSelected(int arg0) {  
	        // ���õײ�С��ѡ��״̬  
	        setCurrentDot(arg0);  
	    }

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btn_titlebar_left:
				finish();
				startActivity(new Intent(this, HomePage.class));
				break;
			}
		}  
}
