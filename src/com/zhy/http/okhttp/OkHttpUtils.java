package com.zhy.http.okhttp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.builder.DeleteBuilder;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.builder.PutBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.cookie.SimpleCookieJar;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.request.RequestCall;

/**
 * Created by zhy on 15/8/17.
 */
public class OkHttpUtils
{
    public static final String TAG = "OkHttpUtils";
    public static final long DEFAULT_MILLISECONDS = 10000;
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private SimpleCookieJar mCookieJar;
    private static final String PICASSO_CACHE = "image-cache";
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 30 * 1024 * 1024; // 30MB
    
    private boolean debug;
    private String tag;
    
    private Context mContext;

    
    private OkHttpUtils()
    {
    	this(null);
    }
    
    private OkHttpUtils(Context context)
    {
    	this.mContext = context;
    	mDelivery = new Handler(Looper.getMainLooper());
    	mCookieJar = new SimpleCookieJar();
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //cookie enabled
        okHttpClientBuilder.cookieJar(mCookieJar);
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
                return true;
            }
        });
        mOkHttpClient = okHttpClientBuilder.build();
    }
    
    private static File createDefaultCacheDir(Context context,String cacheDirectory) {
    	if (TextUtils.isEmpty(cacheDirectory)){
    		cacheDirectory = PICASSO_CACHE;
    	}
        File cache = new File(context.getApplicationContext().getCacheDir(), cacheDirectory);
        if (!cache.exists()) {
          //noinspection ResultOfMethodCallIgnored
          cache.mkdirs();
        }
        return cache;
   }
    
    public void setOkHttpCache(String cacheDirectory,int cacheSize){
    	if (mContext == null){
    		throw new RuntimeException("must invoke OkHttpUtils init first.");
    	}
    	getOkHttpClient().newBuilder().cache(new Cache(createDefaultCacheDir(mContext,cacheDirectory), cacheSize == 0 ? MAX_DISK_CACHE_SIZE : cacheSize));
    }
    
    public OkHttpUtils debug(String tag)
    {
        debug = true;
        this.tag = tag;
        return this;
    }
    
    public static void init(Context context){
    	if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                	mInstance = new OkHttpUtils(context);
                }
            }
        }
    }

    public static OkHttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery()
    {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }
    
    public SimpleCookieJar getCookieJar(){
    	return mCookieJar;
    }
    
    public List<Cookie> getCookies()
    {
    	return mCookieJar.getCookies();
    }

    public static GetBuilder get()
    {
        return new GetBuilder();
    }

    public static PostStringBuilder postString()
    {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile()
    {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post()
    {
        return new PostFormBuilder();
    }
    
    public static DeleteBuilder delete(){
    	return new DeleteBuilder();
    }
    
    public static PutBuilder put(){
    	return new PutBuilder();
    }

    public void execute(final RequestCall requestCall, Callback callback)
    {
        if (debug)
        {
            if(TextUtils.isEmpty(tag))
            {
                tag = TAG;
            }
            Log.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new okhttp3.Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(final Call call, final Response response)
            {
                if (response.code() >= 400 && response.code() <= 599)
                {
                    try
                    {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    return;
                }

                try
                {
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (Exception e)
                {
                    sendFailResultCallback(call, e, finalCallback);
                }

            }
        });
    }


    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback)
    {
        if (callback == null) return;

        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onError(call, e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback)
    {
        if (callback == null) return;
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag)
    {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
    }


    public void setCertificates(InputStream... certificates)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null))
                .build();
    }


    public void setConnectTimeout(int timeout, TimeUnit units)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(timeout, units)
                .build();
    }
}

