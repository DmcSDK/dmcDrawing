package net.pobbay.util;

import java.io.File;

import android.os.Environment;

public class DeleteFile {
	public static void delete() {
		File f = new File(Environment.getExternalStorageDirectory().toString()
				+ "/android/data/pobaby_sket.png");
		if (f.exists()) {
			f.delete();
		}
	}
}