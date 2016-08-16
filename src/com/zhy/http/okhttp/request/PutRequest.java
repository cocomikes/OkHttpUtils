/**
 * 
 */
package com.zhy.http.okhttp.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author mikes
 * @version 2016-8-16 下午2:54:31
 */
public class PutRequest extends OkHttpRequest{
	/**
	 * @param url
	 * @param tag
	 * @param params
	 * @param headers
	 */
	public PutRequest(String url, Object tag, Map<String, String> params,
			Map<String, String> headers) {
		super(url, tag, params, headers);
	}

    @Override
    protected RequestBody buildRequestBody()
    {
        FormBody.Builder builder = new FormBody.Builder();
        addParams(builder);
        return builder.build();
    }
    
    private void addParams(FormBody.Builder builder)
    {
        if (params == null || params.isEmpty())
        {
            builder.add("1", "1");
            return;
        }

        for (String key : params.keySet())
        {
            builder.add(key, params.get(key));
        }
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody)
    {
        return builder.put(requestBody).build();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        return sb.toString();
    }
}
