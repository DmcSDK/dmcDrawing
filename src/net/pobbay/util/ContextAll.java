package net.pobbay.util;

import android.app.Application;

public class ContextAll extends Application {
	private static ContextAll contextAll;

	public static ContextAll getContext() {
		return contextAll;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		contextAll = this;
	}
}
