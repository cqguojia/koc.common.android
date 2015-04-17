package koc.common.utils;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
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

public class HttpUtils {

	public static JSONObject GetJson(final String Url,
			final int timeoutConnection, final int timeoutSocket,
			final ContentValues ParamList) {
		String serverResponse = null;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				(timeoutConnection > 0 ? timeoutConnection * 1000 : 5000));
		HttpConnectionParams.setSoTimeout(httpParameters,
				(timeoutSocket > 0 ? timeoutSocket * 1000 : 15000));
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(Url);
		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			if (ParamList != null) {
				
				String strKey;
				Set<Entry<String, Object>> s = ParamList.valueSet();
		        for (Entry<String, Object> entry : s) {
		        	strKey = entry.getKey();
		        	if (strKey.toLowerCase().startsWith("file|")) {
						entity.addPart(strKey.substring(5), new FileBody(
								new File(ParamList.getAsString(strKey))));
					} else {
						entity.addPart(
								strKey,
								new StringBody(EscapeUtils.escape(ParamList
										.getAsString(strKey))));
					}
		        }
				
		        /* 改成上面的方法
				Iterator<String> it;
				String strKey;
				it = ParamList.keySet().iterator();
				while (it.hasNext()) {
					strKey = it.next();
					if (strKey.toLowerCase().startsWith("file|")) {
						entity.addPart(strKey.substring(5), new FileBody(
								new File(ParamList.getAsString(strKey))));
					} else {
						entity.addPart(
								strKey,
								new StringBody(EscapeUtils.escape(ParamList
										.getAsString(strKey))));
					}
				}
				*/
			}
			// Send it
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return StringUtils.ToJSONObject(serverResponse);
	}
}
