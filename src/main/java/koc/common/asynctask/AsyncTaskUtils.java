package koc.common.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 异步操作工具类
 * 
 * @ClassName: ActivityUtils
 * @author 姜涛
 * @version 1.0 2012-1-16 下午11:02:26
 */
public class AsyncTaskUtils {

	/**
	 * 封装的asynctask方法，此方法没有进度框.
	 * 
	 * @param pCallEarliest
	 *            运行于主线程，最先执行此方法.
	 * @param mCallable
	 *            运行于异步线程,第二执行此方法.
	 * @param mCallback
	 *            运行于主线程,最后执行此方法.
	 */
	public static <T> void doAsync(final CallEarliest<T> pCallEarliest,
			final Callable<T> pCallable, final Callback<T> pCallback) {

		new AsyncTask<Void, Void, T>() {

			/**
			 * 首先运行此方法,运行于主线程
			 */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				try {
					pCallEarliest.onCallEarliest();
				} catch (Exception e) {
					Log.e("error", e.toString());
				}
			}

			/**
			 * 第二步执行这个方法，这个方法运行在异步线程中
			 */
			@Override
			protected T doInBackground(Void... params) {

				try {
					return pCallable.call();
				} catch (Exception e) {
					Log.e("error", e.toString());
				}
				return null;
			}

			/**
			 * 第三步执行这个方法，运行于主线程
			 */
			protected void onPostExecute(T result) {
				pCallback.onCallback(result);
			}
		}.execute((Void[]) null);
	}

	/**
	 * 封装的asynctask方法，此方法拥有进度对话框，并支持定义样式.
	 * 
	 * @param pContext
	 *            上下文
	 * @param styleID
	 *            对话框样式
	 *            ProgressDialog.STYLE_HORIZONTAL|ProgressDialog.STYLE_SPINNER
	 * @param pTitle
	 *            标题
	 * @param pMessage
	 *            内容
	 * @param pCallEarliest
	 *            运行于主线程，最先执行此方法.
	 * @param progressCallable
	 *            运行于异步线程,用于传递对话框进度.
	 * @param pCallback
	 *            运行于主线程,最后执行此方法.
	 */
	public static <T> void doProgressAsync(final Context pContext,
			final int styleID, final String pTitle, final String pMessage,
			final CallEarliest<T> pCallEarliest,
			final ProgressCallable<T> progressCallable,
			final Callback<T> pCallback) {

		new AsyncTask<Void, Void, T>() {

			private ProgressDialog mProgressDialog;

			/**
			 * 首先运行此方法,运行于主线程
			 */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

				mProgressDialog = new ProgressDialog(pContext);
				mProgressDialog.setProgressStyle(styleID);
				mProgressDialog.setTitle(pTitle);
				mProgressDialog.setMessage(pMessage);
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.show();
				try {
					pCallEarliest.onCallEarliest();
				} catch (Exception e) {
					Log.e("error", e.toString());
				}
			}

			/**
			 * 第二步执行这个方法，这个方法运行在异步线程中
			 */
			@Override
			protected T doInBackground(Void... params) {
				try {
					return progressCallable.call(new IProgressListener() {

						@Override
						public void onProgressChanged(int pProgress) {
							onProgressUpdate(pProgress);
						}
					});
				} catch (Exception e) {
					Log.e("error", e.toString());
				}

				return null;
			}

			/**
			 * 更新进度框
			 */
			protected void onProgressUpdate(final Integer... values) {
				mProgressDialog.setProgress(values[0]);
			};

			/**
			 * 第三步执行这个方法，运行于主线程
			 */
			protected void onPostExecute(T result) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
				pCallback.onCallback(result);

			}

		}.execute((Void[]) null);

	}

	/**
	 * 封装的asynctask方法，此方法拥有进度对话框，并支持定义样式.
	 * 
	 * @param pContext
	 *            上下文
	 * @param styleID
	 *            对话框样式
	 *            ProgressDialog.STYLE_HORIZONTAL|ProgressDialog.STYLE_SPINNER
	 * @param pTitle
	 *            标题,资源id
	 * @param pMessage
	 *            内容,资源id
	 * @param pCallEarliest
	 *            运行于主线程，最先执行此方法.
	 * @param progressCallable
	 *            运行于异步线程,用于传递对话框进度.
	 * @param pCallback
	 *            运行于主线程,最后执行此方法.
	 */
	public static <T> void doProgressAsync(final Context pContext,
			final int styleID, final int pTitleResId, final int pMessageResId,
			final CallEarliest<T> pCallEarliest,
			final ProgressCallable<T> progressCallable,
			final Callback<T> pCallback) {
		AsyncTaskUtils.doProgressAsync(pContext, styleID,
				pContext.getString(pTitleResId),
				pContext.getString(pMessageResId), pCallEarliest,
				progressCallable, pCallback);
	}

}


//使用方法
//public class SimpleAsyncTaskActivity extends BaseActivity {
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.doProgressAsync(this, ProgressDialog.STYLE_HORIZONTAL,
//				R.string.app_name, R.string.app_name, new CallEarliest<Void>() {
//					@Override
//					public void onCallEarliest() throws Exception {
//					}
//				}, new ProgressCallable<Void>() {
//					@Override
//					public Void call(IProgressListener pProgressListener)
//							throws Exception {
//						for (int i = 0; i < 100; i++) {
//							Thread.sleep(200);
//							pProgressListener.onProgressChanged(i);
//						}
//						return null;
//					}
//				}, new Callback<Void>() {
//					@Override
//					public void onCallback(Void pCallbackValue) {
//					}
//				});
//	}
//}