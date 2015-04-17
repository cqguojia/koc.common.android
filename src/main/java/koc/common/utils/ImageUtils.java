package koc.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;

public class ImageUtils {

	public static String saveBitmap(Bitmap mBitmap, String Path, String Name,
			CompressFormat Format, int quality) {
		// 判断并创建图像存储路径
		if (!FileUtils.createDirectory(Path)) {
			return null;
		}
		// 保存图像为高质量png
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(new File(Path, Name));
			mBitmap.compress(Format, quality, fOut);
			fOut.flush();
			fOut.close();
			return Path + Name;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getRequest(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		if (conn.getResponseCode() == 200) {
			return conn.getInputStream();
		}
		return null;
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		if(inStream == null)
		{
			return null;
		}
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(i, "src");
		return d;
	}

	public static Drawable getDrawableFromUrl(String url) throws Exception {
		return Drawable.createFromStream(getRequest(url), null);
	}

	public static Bitmap getBitmapFromUrl(String url) throws Exception {
		byte[] bytes = getBytesFromUrl(url);
		return byteToBitmap(bytes, true);
	}

	public static Bitmap getRoundBitmapFromUrl(String url, int pixels)
			throws Exception {
		byte[] bytes = getBytesFromUrl(url);
		Bitmap bitmap = byteToBitmap(bytes, true);
		return toRoundCorner(bitmap, pixels);
	}

	public static Drawable geRoundDrawableFromUrl(String url, int pixels)
			throws Exception {
		byte[] bytes = getBytesFromUrl(url);
		BitmapDrawable bitmapDrawable = (BitmapDrawable) byteToDrawable(bytes);
		return toRoundCorner(bitmapDrawable, pixels);
	}

	public static byte[] getBytesFromUrl(String url) throws Exception {
		return readInputStream(getRequest(url));
	}

	public static Bitmap byteToBitmap(byte[] byteArray, boolean minSampleSize) {
		return byteToBitmap(byteArray, -1, -1, true);
	}

	public static Bitmap byteToBitmap(byte[] byteArray, int minWidth,
			int minHeight, boolean minSampleSize) {
		if (byteArray == null) {
			return null;
		}
		if (byteArray.length <= 0) {
			return null;
		}
		Bitmap bitmap;
		int scale = 1;
		if (minSampleSize) {
			if (byteArray.length < 400 * 1024) {
				scale = 1;
			} else if (byteArray.length < 800 * 1024) {
				scale = 2;
			} else if (byteArray.length < 1200 * 1024) {
				scale = 3;
			} else if (byteArray.length < 1600 * 1024) {
				scale = 4;
			} else if (byteArray.length < 2000 * 1024) {
				scale = 5;
			} else if (byteArray.length < 2400 * 1024) {
				scale = 6;
			} else if (byteArray.length < 2800 * 1024) {
				scale = 7;
			} else if (byteArray.length < 3200 * 1024) {
				scale = 8;
			} else if (byteArray.length < 3600 * 1024) {
				scale = 9;
			} else {
				scale = 10;
			}
		}
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inSampleSize = scale;
		if (minWidth <= 0 && minHeight <= 0) {
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
					byteArray.length, o);
		} else {
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			while (minWidth > 0 || minHeight > 0) {
				if ((minWidth > 0 && (width_tmp / (scale + 1)) < minWidth)
						|| (minHeight > 0 && (height_tmp / (scale + 1)) < minHeight)) {
					break;
				}
				scale++;
			}
			o.inSampleSize = scale;
			o.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
					byteArray.length, o);
		}
		return bitmap;
	}

	public static Bitmap fileToBitmap(File f, int minWidth, int minHeight,
			boolean minSampleSize) {
		try {
			Bitmap bitmap;
			int scale = 1;
			if (minSampleSize) {
				if (f.length() < 400 * 1024) {
					scale = 1;
				} else if (f.length() < 800 * 1024) {
					scale = 2;
				} else if (f.length() < 1200 * 1024) {
					scale = 3;
				} else if (f.length() < 1600 * 1024) {
					scale = 4;
				} else if (f.length() < 2000 * 1024) {
					scale = 5;
				} else if (f.length() < 2400 * 1024) {
					scale = 6;
				} else if (f.length() < 2800 * 1024) {
					scale = 7;
				} else if (f.length() < 3200 * 1024) {
					scale = 8;
				} else if (f.length() < 3600 * 1024) {
					scale = 9;
				} else {
					scale = 10;
				}
			}
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inSampleSize = scale;
			if (minWidth <= 0 && minHeight <= 0) {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
						null, o);
			} else {
				o.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(new FileInputStream(f), null, o);
				int width_tmp = o.outWidth, height_tmp = o.outHeight;
				while (minWidth > 0 || minHeight > 0) {
					if ((minWidth > 0 && (width_tmp / (scale + 1)) < minWidth)
							|| (minHeight > 0 && (height_tmp / (scale + 1)) < minHeight)) {
						break;
					}
					scale++;
				}
				o.inSampleSize = scale;
				o.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
						null, o);
			}
			return bitmap;
		} catch (Exception e) {
		}
		return null;
	}

	public static Bitmap UriToBitmap(Context context, Uri uri, int maxWidth,
			int maxHeight, boolean minSampleSize) {
		String strRealPath = FileUtils.getRealPath(context, uri);
		return PathToBitmap(strRealPath, maxWidth, maxHeight, minSampleSize);
	}

	public static Bitmap PathToBitmap(String Path, int maxWidth, int maxHeight,
			boolean minSampleSize) {
		File file = new File(Path);
		if (!file.exists()) {
			Path = Path.replaceFirst("/mnt/", "/");
			file = new File(Path);
		}
		return fileToBitmap(file, maxWidth, maxHeight, minSampleSize);
	}

	public static Drawable byteToDrawable(byte[] byteArray) {
		ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
		return Drawable.createFromStream(ins, null);
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
								: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 图片缩放
	 */
	public static Bitmap resizeImage(Bitmap bmp, int width, int height) {
		if (width <= 0 && height <= 0) {
			return bmp;
		}
		if (height <= 0) {
			height = (int) (bmp.getHeight() * (width / (float) bmp.getWidth()));
		} else if (width <= 0) {
			width = (int) (bmp.getWidth() * (height / (float) bmp.getHeight()));
		}
		return ThumbnailUtils.extractThumbnail(bmp, width, height);
	}

	/**
	 * 图片去色,返回灰度图片
	 * 
	 * @param bmpOriginal
	 *            传入的图片
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 去色同时加圆角
	 * 
	 * @param bmpOriginal
	 *            原图
	 * @param pixels
	 *            圆角弧度
	 * @return 修改后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
		return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	}

	/**
	 * 把图片变成圆角
	 * 
	 * @param bitmap
	 *            需要修改的图片
	 * @param pixels
	 *            圆角的弧度
	 * @return 圆角图片
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 使圆角功能支持BitampDrawable
	 * 
	 * @param bitmapDrawable
	 * @param pixels
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
			int pixels) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}

	/**
	 * 图片旋转
	 * 
	 * @param bmp
	 * @param degree
	 * @return
	 */
	public static Bitmap postRotateBitamp(Bitmap bmp, float degree) {
		// 获得Bitmap的高和宽
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		// 产生resize后的Bitmap对象
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		return resizeBmp;
	}

	// 图片翻转
	public static Bitmap reverseBitmap(Bitmap bmp, int flag) {
		float[] floats = null;
		switch (flag) {
		case 0: // 水平反转
			floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
			break;
		case 1: // 垂直反转
			floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
			break;
		}

		if (floats != null) {
			Matrix matrix = new Matrix();
			matrix.setValues(floats);
			return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), matrix, true);
		}

		return bmp;
	}

	/**
	 * 底片效果
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap film(Bitmap bmp) {
		// RGBA的最大值
		final int MAX_VALUE = 255;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = MAX_VALUE - pixR;
				newG = MAX_VALUE - pixG;
				newB = MAX_VALUE - pixB;

				newR = Math.min(MAX_VALUE, Math.max(0, newR));
				newG = Math.min(MAX_VALUE, Math.max(0, newG));
				newB = Math.min(MAX_VALUE, Math.max(0, newB));

				pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 光照效果
	 * 
	 * @param bmp
	 *            光照中心x坐标
	 * @param centerX
	 *            光照中心要坐标
	 * @param centerY
	 * @param strength
	 *            光照强度 100~150
	 * @return
	 */
	public static Bitmap sunshine(Bitmap bmp, int centerX, int centerY,
			float strength) {
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;
		int radius = Math.min(centerX, centerY);

		if (strength < 100 || strength > 15) {
			strength = 150f;
		}

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(
						centerX - k, 2));
				if (distance < radius * radius) {
					// 按照距离大小计算增加的光照值
					int result = (int) (strength * (1.0 - Math.sqrt(distance)
							/ radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public static Bitmap oldRemeber(Bitmap bmp) {
		// 速度测试
		// long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR,
						newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		// long end = System.currentTimeMillis();
		// Log.e("may", "used time=" + (end - start));
		return bitmap;
	}
}