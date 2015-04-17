package koc.common.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koc.common.utils.ImageUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageLoader {
	private static final String TAG = "KOC.ImageLoader";
	private AbstractFileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池
	private ExecutorService executorService;

	private boolean mFadeInBitmap;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// 最主要的方法
	public void DisplayImage(boolean FadeIn, String url, ImageView imageView,
			int ImageVisibility, ProgressBar progressBar,
			boolean isLoadOnlyFromCache, int minWidth, int minHeight,
			boolean minSampleSize, int screenWidth) {
		if (url == null) {
			return;
		}
		imageViews.put(imageView, url);
		// 先从内存缓存中查找
		imageView.setImageBitmap(null);
		imageView.setVisibility(ImageVisibility);

		Bitmap bitmap = MemoryCache.get(getCacheName(url, minWidth, minHeight,
				minSampleSize));
		mFadeInBitmap = FadeIn;
		if (bitmap != null) {
			mFadeInBitmap = false;
			setBitmap(imageView, progressBar, screenWidth, bitmap);
			return;
		}
		if (progressBar != null) {
			progressBar.setProgress(0);
			progressBar.setVisibility(View.VISIBLE);
		}
		// if (!isLoadOnlyFromCache) {
		// // 若没有的话则开启新线程加载图片
		// queuePhoto(url, imageView, progressBar, minWidth, minHeight,
		// minSampleSize, screenWidth);
		// }

		queuePhoto(isLoadOnlyFromCache, url, imageView, progressBar, minWidth,
				minHeight, minSampleSize, screenWidth);
	}

	private void setBitmap(ImageView imageView, ProgressBar progressBar,
			int screenWidth, Bitmap bitmap) {
		// photoToLoad.screenWidth -1:不控制 | 0:按控件控制 | >0精确控制
		// --------显示渐显--------
		if (mFadeInBitmap) {
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(bitmap) });

			imageView.setImageDrawable(td);
			td.startTransition(300);
		} else {
			imageView.setImageBitmap(bitmap);
		}
		// --------显示渐显结束------

		if (screenWidth >= 0) {
			ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
			if (screenWidth == 0) {
				screenWidth = layoutParams.width;
			}
			layoutParams.height = (int) (bitmap.getHeight() * screenWidth / (bitmap
					.getWidth() * 1.0));
			imageView.setLayoutParams(layoutParams);
		}

		// imageView.setImageBitmap(bitmap);
		imageView.setVisibility(View.VISIBLE);
		// bitmap.recycle();
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
	}

	private void queuePhoto(boolean isLoadOnlyFromCache, String url,
			ImageView imageView, ProgressBar progressBar, int minWidth,
			int minHeight, boolean minSampleSize, int screenWidth) {
		PhotoToLoad p = new PhotoToLoad(isLoadOnlyFromCache, url, imageView,
				progressBar, minWidth, minHeight, minSampleSize, screenWidth);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(PhotoToLoad photoToLoad) {

		Bitmap b = null;

		// 本地图片
		if (photoToLoad.url.startsWith("/")) {
			return ImageUtils.PathToBitmap(photoToLoad.url,
					photoToLoad.minWidth, photoToLoad.minHeight, true);
		}

		File f = fileCache.getFile(photoToLoad.url);

		// 先从文件缓存中查找是否有
		if (f != null && f.exists()) {
			b = ImageUtils.fileToBitmap(f, photoToLoad.minWidth,
					photoToLoad.minHeight, true);
		}
		if (b != null) {
			return b;
		}

		if (photoToLoad.isLoadOnlyFromCache) {
			return null;
		}

		// 最后从指定的url中下载图片
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(photoToLoad.url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);

			int size = conn.getContentLength();
			int readsize = 0;

			final int buffer_size = 1024;
			try {
				byte[] bytes = new byte[buffer_size];
				for (;;) {
					int count = is.read(bytes, 0, buffer_size);
					if (count == -1) {
						if (photoToLoad.progressBar != null) {
							photoToLoad.progressBar.setProgress(100);
						}
						break;
					}
					readsize += count;
					if (photoToLoad.progressBar != null) {
						photoToLoad.progressBar.setProgress(readsize * 100
								/ size);
					}
					os.write(bytes, 0, count);
				}
			} catch (Exception ex) {
				Log.e(TAG, "CopyStream catch Exception...");
			}

			CopyStream(is, os);

			os.close();

			bitmap = ImageUtils.fileToBitmap(f, photoToLoad.minWidth,
					photoToLoad.minHeight, true);
			return bitmap;
		} catch (Exception ex) {
			Log.e(TAG,
					"getBitmap catch Exception...\nmessage = "
							+ ex.getMessage());
			return null;
		}
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public ProgressBar progressBar;
		public boolean minSampleSize, isLoadOnlyFromCache;
		public int minWidth, minHeight, screenWidth;

		public PhotoToLoad(boolean isLoadOnlyFromCache, String u, ImageView i,
				ProgressBar progressBar, int minWidth, int minHeight,
				boolean minSampleSize, int screenWidth) {
			this.isLoadOnlyFromCache = isLoadOnlyFromCache;
			this.url = u;
			this.imageView = i;
			this.progressBar = progressBar;
			this.minWidth = minWidth;
			this.minHeight = minHeight;
			this.screenWidth = screenWidth;
			this.minSampleSize = minSampleSize;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad);
			MemoryCache.put(
					getCacheName(photoToLoad.url, photoToLoad.minWidth,
							photoToLoad.minHeight, photoToLoad.minSampleSize),
					bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 更新的操作放在UI线程中
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)) {
				return;
			}
			if (bitmap != null) {
				setBitmap(photoToLoad.imageView, photoToLoad.progressBar,
						photoToLoad.screenWidth, bitmap);
			}
		}
	}

	public void clearCache() {
		// MemoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e(TAG, "CopyStream catch Exception...");
		}
	}

	private String getCacheName(String url, int minWidth, int minHeight,
			boolean minSampleSize) {
		return url.hashCode() + "|" + minWidth + "|" + minHeight + "|"
				+ minSampleSize;
	}
}