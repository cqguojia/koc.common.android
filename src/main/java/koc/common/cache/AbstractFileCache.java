package koc.common.cache;

import java.io.File;

import koc.common.utils.FileUtils;

import android.content.Context;
import android.util.Log;

public abstract class AbstractFileCache {
	private String dirString;
	private static final String TAG = "KOC.AbstractFileCache";

	public AbstractFileCache(Context context) {

		dirString = getCacheDir();
		boolean ret = FileUtils.createDirectory(dirString);
		Log.i(TAG, "FileHelper.createDirectory:" + dirString + ", ret = " + ret);
	}

	public File getFile(String url) {
		File f = new File(getSavePath(url));
		return f;
	}

	public abstract String getSavePath(String url);

	public abstract String getCacheDir();

	public void clear() {
		FileUtils.deleteDirectory(dirString);
	}

}
