/**
 * 
 */
package com.zhy.http.okhttp.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;

/**
 * @author mikes
 * @version 2016-8-16 下午3:10:18
 */
public class DeleteRequest extends OkHttpRequest{

	/**
	 * @param url
	 * @param tag
	 * @param params
	 * @param headers
	 */
	public DeleteRequest(String url, Object tag, Map<String, String> params,
			Map<String, String> headers) {
		super(url, tag, params, headers);
	}

	@Override
	protected RequestBody buildRequestBody() {
		return null;
	}

	@Override
	protected Request buildRequest(Builder builder, RequestBody requestBody) {
		return builder.delete().build();
	}

}
