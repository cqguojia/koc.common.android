package koc.common.asynctask;

import java.io.File;
import java.util.Iterator;

import koc.common.asynctask.CustomMultipartEntity.ProgressListener;
import koc.common.utils.EscapeUtils;
import koc.common.utils.StringUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskGetJson {
	private static long totalSize;

	// ParamList.put("参数名", 路径);
	// ParamList.put("File|参数名", 路径);
	// timeoutConnection:连接超时时间(秒)
	// timeoutSocket:Socket超时时间(秒)
	public static <T> void doAsync(final String Url,
			final int timeoutConnection, final int timeoutSocket,
			final ContentValues ParamList, final CallEarliest<T> pCallEarliest,
			final Callback<JSONObject> pCallback) {
		new AsyncTask<Void, Integer, JSONObject>() {

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
			protected JSONObject doInBackground(Void... params) {
				String serverResponse = null;
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						(timeoutConnection > 0 ? timeoutConnection * 1000
								: 5000));
				HttpConnectionParams.setSoTimeout(httpParameters,
						(timeoutSocket > 0 ? timeoutSocket * 1000 : 15000));
				HttpClient httpClient = new DefaultHttpClient(httpParameters);
				HttpContext httpContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost(Url);
				try {
					CustomMultipartEntity multipartContent = new CustomMultipartEntity(
							new ProgressListener() {
								@Override
								public void transferred(long num) {
									publishProgress((int) ((num / (float) totalSize) * 100));
								}
							});
					Iterator<String> it;
					String strKey;
					it = ParamList.keySet().iterator();
					while (it.hasNext()) {
						strKey = it.next();
						if (strKey.toLowerCase().startsWith("file|")) {
							multipartContent.addPart(
									strKey.substring(5),
									new FileBody(new File(ParamList
											.getAsString(strKey))));
						} else {
							multipartContent.addPart(
									strKey,
									new StringBody(EscapeUtils.escape(ParamList
											.getAsString(strKey))));
						}
					}
					totalSize = multipartContent.getContentLength();
					// Send it
					httpPost.setEntity(multipartContent);
					HttpResponse response = httpClient.execute(httpPost,
							httpContext);
					serverResponse = EntityUtils.toString(response.getEntity());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} finally {
					httpClient.getConnectionManager().shutdown();
				}
				return StringUtils.ToJSONObject(serverResponse);
			}

			/**
			 * 第三步执行这个方法，运行于主线程
			 */
			protected void onPostExecute(JSONObject result) {
				try {
					pCallback.onCallback(result);
				} catch (Exception e) {
					Log.e("error", e.toString());
				}
			}
		}.execute();
	}

}