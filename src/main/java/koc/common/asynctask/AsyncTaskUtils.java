package koc.common.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * �첽����������
 * 
 * @ClassName: ActivityUtils
 * @author ����
 * @version 1.0 2012-1-16 ����11:02:26
 */
public class AsyncTaskUtils {

	/**
	 * ��װ��asynctask�������˷���û�н��ȿ�.
	 * 
	 * @param pCallEarliest
	 *            ���������̣߳�����ִ�д˷���.
	 * @param mCallable
	 *            �������첽�߳�,�ڶ�ִ�д˷���.
	 * @param mCallback
	 *            ���������߳�,���ִ�д˷���.
	 */
	public static <T> void doAsync(final CallEarliest<T> pCallEarliest,
			final Callable<T> pCallable, final Callback<T> pCallback) {

		new AsyncTask<Void, Void, T>() {

			/**
			 * �������д˷���,���������߳�
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
			 * �ڶ���ִ�������������������������첽�߳���
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
			 * ������ִ��������������������߳�
			 */
			protected void onPostExecute(T result) {
				pCallback.onCallback(result);
			}
		}.execute((Void[]) null);
	}

	/**
	 * ��װ��asynctask�������˷���ӵ�н��ȶԻ��򣬲�֧�ֶ�����ʽ.
	 * 
	 * @param pContext
	 *            ������
	 * @param styleID
	 *            �Ի�����ʽ
	 *            ProgressDialog.STYLE_HORIZONTAL|ProgressDialog.STYLE_SPINNER
	 * @param pTitle
	 *            ����
	 * @param pMessage
	 *            ����
	 * @param pCallEarliest
	 *            ���������̣߳�����ִ�д˷���.
	 * @param progressCallable
	 *            �������첽�߳�,���ڴ��ݶԻ������.
	 * @param pCallback
	 *            ���������߳�,���ִ�д˷���.
	 */
	public static <T> void doProgressAsync(final Context pContext,
			final int styleID, final String pTitle, final String pMessage,
			final CallEarliest<T> pCallEarliest,
			final ProgressCallable<T> progressCallable,
			final Callback<T> pCallback) {

		new AsyncTask<Void, Void, T>() {

			private ProgressDialog mProgressDialog;

			/**
			 * �������д˷���,���������߳�
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
			 * �ڶ���ִ�������������������������첽�߳���
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
			 * ���½��ȿ�
			 */
			protected void onProgressUpdate(final Integer... values) {
				mProgressDialog.setProgress(values[0]);
			};

			/**
			 * ������ִ��������������������߳�
			 */
			protected void onPostExecute(T result) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
				pCallback.onCallback(result);

			}

		}.execute((Void[]) null);

	}

	/**
	 * ��װ��asynctask�������˷���ӵ�н��ȶԻ��򣬲�֧�ֶ�����ʽ.
	 * 
	 * @param pContext
	 *            ������
	 * @param styleID
	 *            �Ի�����ʽ
	 *            ProgressDialog.STYLE_HORIZONTAL|ProgressDialog.STYLE_SPINNER
	 * @param pTitle
	 *            ����,��Դid
	 * @param pMessage
	 *            ����,��Դid
	 * @param pCallEarliest
	 *            ���������̣߳�����ִ�д˷���.
	 * @param progressCallable
	 *            �������첽�߳�,���ڴ��ݶԻ������.
	 * @param pCallback
	 *            ���������߳�,���ִ�д˷���.
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


//ʹ�÷���
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