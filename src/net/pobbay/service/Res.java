package net.pobbay.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Typeface;

import net.pobbay.util.AppContext;

public class Res {
	public static Typeface wawaTypeface;
	public static Typeface pingheTypeface;

	public static void load() {
		wawaTypeface = Typeface.createFromAsset(AppContext.getContextObject().getAssets(),
				"fonts/font1.ttf");
		pingheTypeface = Typeface.createFromAsset(AppContext.getContextObject().getAssets(),
				"fonts/font2.ttf");
	}

	public static void downloadSms(Context context) {
		URL url;
		InputStream is = null;
		OutputStream os = null;

		try {
			url = new URL("http://www.pobaby.net/app/smss.xml");
			URLConnection urlConnection = url.openConnection();
			is = urlConnection.getInputStream();
			os = context.openFileOutput("smss.xml", Context.MODE_PRIVATE);

			byte[] data = new byte[is.available()];
			is.read(data);
			os.write(data);

			// System.out.println(new String(data));

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void dispose() {

	}
}
