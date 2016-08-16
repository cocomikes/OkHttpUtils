/**
 * 
 */
package com.zhy.http.okhttp.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import com.zhy.http.okhttp.request.DeleteRequest;
import com.zhy.http.okhttp.request.GetRequest;
import com.zhy.http.okhttp.request.RequestCall;

/**
 * @author mikes
 * @version 2016-8-16 下午3:09:01
 */
public class DeleteBuilder extends OkHttpRequestBuilder {
	@Override
	public RequestCall build() {
		if (params != null) {
			url = appendParams(url, params);
		}

		return new DeleteRequest(url, tag, params, headers).build();
	}

	private String appendParams(String url, Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		sb.append(url + "?");
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				sb.append(key).append("=").append(params.get(key)).append("&");
			}
		}

		sb = sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public DeleteBuilder url(String url) {
		this.url = url;
		return this;
	}

	@Override
	public DeleteBuilder tag(Object tag) {
		this.tag = tag;
		return this;
	}

	@Override
	public DeleteBuilder params(Map<String, String> params) {
		this.params = params;
		return this;
	}

	@Override
	public DeleteBuilder addParams(String key, String val) {
		if (this.params == null) {
			params = new LinkedHashMap<>();
		}
		params.put(key, val);
		return this;
	}

	@Override
	public DeleteBuilder headers(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	@Override
	public DeleteBuilder addHeader(String key, String val) {
		if (this.headers == null) {
			headers = new LinkedHashMap<>();
		}
		headers.put(key, val);
		return this;
	}

}
