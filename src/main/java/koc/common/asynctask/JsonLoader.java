package koc.common.asynctask;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class JsonLoader extends AsyncTask<String, Integer, JSONObject> {
	private ProgressDialog Loading;
	private CallBackListener mListener;
	private String strType = "";
	private boolean boolLoading = false;
	private String strUrl = null;

	public JsonLoader(CallBackListener listener, String type, boolean loading,
			Context context) {
		mListener = listener;
		strType = type;
		if (loading) {
			boolLoading = true;
			Loading = new ProgressDialog(context);
			Loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			Loading.setMessage("数据载入中，请稍候...");
		}
	}

	public JsonLoader(CallBackListener listener, String type) {
		mListener = listener;
		strType = type;
	}

	public JsonLoader(CallBackListener listener) {
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		if (boolLoading) {
			Loading.show();
		}
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		if(null == strUrl){
			strUrl = params[0];
		}
		//return GetJsonObject(params[0]);
		return GetJsonObject(strUrl);
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (boolLoading) {
			Loading.dismiss();
		}
		mListener.AsyncTask_JsonLoader_Callback(result, strType);
	}

	private String GetString(String urlString) {
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		HttpGet get = new HttpGet(urlString);
		try {
			HttpResponse response = client.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private JSONObject GetJsonObject(String urlString) {
		try {
			String strValue = GetString(urlString);
			strValue = strValue.substring(strValue.indexOf("{")).replace(
					"\r\n", "\n");
			return new JSONObject(strValue);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public interface CallBackListener {
		public void AsyncTask_JsonLoader_Callback(JSONObject json, String type);
	}
	
	public void setUrl(String url){
		strUrl = url;
	}
}