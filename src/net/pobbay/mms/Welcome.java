package net.pobbay.mms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

/**
 * 欢迎界面
 *
 * @author Yichou
 *
 */
public class Welcome extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在程序onCreate时就可以调用即可
		setContentView(R.layout.activity_logo);
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		AnimationSet animationset = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(3500);
		animationset.addAnimation(alphaAnimation);
		image.startAnimation(animationset);
		Intent mainIntent = new Intent(Welcome.this, HomePage.class);
		Welcome.this.startActivity(mainIntent);
		Welcome.this.finish();
		Log.e("getGsfAndroidId",getGsfAndroidId(this));
	}

	private static String getGsfAndroidId(Context context)
	{
		Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
		String ID_KEY = "android_id";
		String params[] = {ID_KEY};
		Cursor c = context.getContentResolver().query(URI, null, null, params, null);
		if (!c.moveToFirst() || c.getColumnCount() < 2)
			return null;
		try
		{
			return Long.toHexString(Long.parseLong(c.getString(1)));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

}
