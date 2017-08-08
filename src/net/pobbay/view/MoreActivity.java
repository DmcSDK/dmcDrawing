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
    // 底部小点图片  
    private ImageView[] dots;  
  
    // 记录当前选中位置  
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
	        // 初始化引导图片列表  
	        views.add(inflater.inflate(R.layout.m1, null));  
	        views.add(inflater.inflate(R.layout.m2, null));  
	        views.add(inflater.inflate(R.layout.m3, null));  
	        views.add(inflater.inflate(R.layout.m4, null));  
	        views.add(inflater.inflate(R.layout.m5, null));  
	        views.add(inflater.inflate(R.layout.m6, null));  
	        // 初始化Adapter  
	        vpAdapter = new ViewPagerAdapter(views, this);   
	        vp = (ViewPager) findViewById(R.id.pager);  
	        vp.setAdapter(vpAdapter);  
	        // 绑定回调  
	        vp.setOnPageChangeListener(this);  
	    }  
	  
	    private void initDots() {  
	        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);  
	  
	        dots = new ImageView[views.size()];  	  
	        // 循环取得小点图片  
	        for (int i = 0; i < views.size(); i++) {  
	            dots[i] = (ImageView) ll.getChildAt(i);  
	            dots[i].setEnabled(true);// 都设为灰色  
	        }  
	        currentIndex = 0;  
	        dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态  
	    }
	    private void setCurrentDot(int position) {  
	    	dots[currentIndex].setImageResource(R.drawable.di); 
	        dots[position].setImageResource(R.drawable.dmc); 
	        currentIndex = position;  
	    }  
	  
	    // 当滑动状态改变时调用  
	    @Override  
	    public void onPageScrollStateChanged(int arg0) {  
	    }  
	  
	    // 当当前页面被滑动时调用  
	    @Override  
	    public void onPageScrolled(int arg0, float arg1, int arg2) {  
	    }  
	  
	    // 当新的页面被选中时调用  
	    @Override  
	    public void onPageSelected(int arg0) {  
	        // 设置底部小点选中状态  
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
