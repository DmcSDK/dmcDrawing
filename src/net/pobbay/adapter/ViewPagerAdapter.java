package net.pobbay.adapter;

import java.util.List;  

import net.pobbay.mms.R;
import android.app.Activity;  
import android.content.Context;  
import android.content.Intent;  
import android.content.SharedPreferences;  
import android.content.SharedPreferences.Editor;  
import android.net.Uri;
import android.os.Parcelable;  
import android.support.v4.view.PagerAdapter;  
import android.support.v4.view.ViewPager;  
import android.util.Log;
import android.view.View;  
import android.view.View.OnClickListener;  

import android.widget.Button;
import android.widget.ImageView;  
public class ViewPagerAdapter extends PagerAdapter {

    private List<View> views;  
    private Activity activity;  
    private static final String SHAREDPREFERENCES_NAME = "first_pref";  
    public ViewPagerAdapter(List<View> views, Activity activity) {  
        this.views = views;  
        this.activity = activity;  
    }  
  
    // ����arg1λ�õĽ���  
    @Override  
    public void destroyItem(View arg0, int arg1, Object arg2) {  
        ((ViewPager) arg0).removeView(views.get(arg1));  
    }  
  
    @Override  
    public void finishUpdate(View arg0) {  
    }  
  
    // ��õ�ǰ������  
    @Override  
    public int getCount() {  
        if (views != null) {  
            return views.size();  
        }  
        return 0;  
    }  
  
    // ��ʼ��arg1λ�õĽ���  
    @Override  
    public Object instantiateItem(View arg0, int arg1) {  
        ((ViewPager) arg0).addView(views.get(arg1), 0);  
        if (arg1 == 0) {  
       
        	Button game1=(Button)arg0.findViewById(R.id.game1);
        	game1.setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                 
                  Uri  uri = Uri.parse("http://zhushou.360.cn/detail/index/soft_id/761273?recrefer=SE_D_");
                  Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
                  activity.startActivity(intent);
                }  
  
            });  
        	Button game2=(Button)arg0.findViewById(R.id.game2);
        	game2.setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                	 Uri  uri = Uri.parse("http://wap.pobaby.net/");
                     Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
                     activity.startActivity(intent);
                }  
  
            });  
        	Button game3=(Button)arg0.findViewById(R.id.game3);
        	game3.setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                	 Uri  uri = Uri.parse("http://wap.pobaby.net/");
                     Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
                     activity.startActivity(intent);
                }  
  
            });  
        	Button game4=(Button)arg0.findViewById(R.id.game4);
        	game4.setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                	 Uri  uri = Uri.parse("http://wap.pobaby.net/");
                     Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
                     activity.startActivity(intent);
                }  
  
            });  
            
        }  
        return views.get(arg1);  
    }  
  
    private void goHome() {  
        // ��ת  
   //     Intent intent = new Intent(activity, MainActivity.class);  
    //    activity.startActivity(intent);  
    //    activity.finish();  
    }  
    /** 
     *  
     * method desc�������Ѿ��������ˣ��´����������ٴ����� 
     */  
    private void setGuided() {  
        SharedPreferences preferences = activity.getSharedPreferences(  
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);  
        Editor editor = preferences.edit();  
        // ��������  
        editor.putBoolean("isFirstIn", false);  
        // �ύ�޸�  
        editor.commit();  
    }  
  
    // �ж��Ƿ��ɶ������ɽ���  
    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return (arg0 == arg1);  
    }  
  
    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
    }  
  
    @Override  
    public Parcelable saveState() {  
        return null;  
    }  
  
    @Override  
    public void startUpdate(View arg0) {  
    }  
}
