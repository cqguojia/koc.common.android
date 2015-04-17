package koc.common.cache;

import koc.common.utils.CommonUtils;
import android.content.Context;

public class FileCache extends AbstractFileCache {

	public FileCache(Context context) {
		super(context);
	}

	@Override
	public String getSavePath(String url) {
		String filename = String.valueOf(url.hashCode());
		return getCacheDir() + filename;
	}

	@Override
	public String getCacheDir() {

		if (CommonUtils.hasSDCard()) {
			return CommonUtils.getRootFilePath() + "koc.cache/files/";
		} else {
			return CommonUtils.getRootFilePath() + "koc.cache/files";
		}
	}

}
