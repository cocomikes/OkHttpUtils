/**
 * 
 */
package com.zhy.http.okhttp.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import com.zhy.http.okhttp.request.PutRequest;
import com.zhy.http.okhttp.request.RequestCall;

/**
 * @author mikes
 * @version 2016-8-16 下午2:51:24
 */
public class PutBuilder extends OkHttpRequestBuilder {
	@Override
	public PutBuilder url(String url) {
		this.url = url;
		return this;
	}

	@Override
	public PutBuilder tag(Object tag) {
		this.tag = tag;
		return this;
	}

	@Override
	public PutBuilder params(Map<String, String> params) {
		this.params = params;
		return this;
	}

	@Override
	public PutBuilder addParams(String key, String val) {
		if (this.params == null) {
			params = new LinkedHashMap<>();
		}
		params.put(key, val);
		return this;
	}

	@Override
	public PutBuilder headers(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	@Override
	public PutBuilder addHeader(String key, String val) {
		if (this.headers == null) {
			headers = new LinkedHashMap<>();
		}
		headers.put(key, val);
		return this;
	}

	@Override
	public RequestCall build() {
        return new PutRequest(url, tag, params, headers).build();
	}

}
