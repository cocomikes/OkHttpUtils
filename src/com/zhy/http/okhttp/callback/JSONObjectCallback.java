/**
 * 
 */
package com.zhy.http.okhttp.callback;

import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author mikes
 * @version 2016-8-16 下午2:47:41
 */
public abstract class JSONObjectCallback extends Callback<JSONObject>{
	@Override
	public JSONObject parseNetworkResponse(Response response) throws Exception {
		try {
			return new JSONObject(response.body().toString());
		} catch(JSONException e){
			e.printStackTrace();
			return null;
		}
	}
}
