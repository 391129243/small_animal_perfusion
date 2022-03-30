package com.gidi.bio_console.utils;

import java.io.File;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


import android.text.TextUtils;


public class OkHttpUtil {  
    private static final String CHARSET_NAME = "UTF-8";  
    //懒汉式  
    private static OkHttpClient mOkHttpClient;  
    //缓存路径  
    private static final String FilePath = "OKHTTPCacheFile";  
    //缓存大小  
    private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB  
    //链接缓存时间    单位秒  
    private static final int conectTimeout = 10;  
    //写入缓存时间    单位秒  
    private static final int writeTimeout = 10;  
    //读取缓存时间    单位秒  
    private static final int readTimeout = 30;  
      
      
    private OkHttpUtil(){}  
    //初始化  
    public static void initOkHttpUtil(){  
        if (mOkHttpClient == null){  
            synchronized (OkHttpUtil.class) {  
                if (mOkHttpClient == null){  
                    File cacheDirectory = new File(FilePath);  
                    Cache cache = new Cache(cacheDirectory, cacheSize);  
                    mOkHttpClient = new OkHttpClient().newBuilder()                      		
                            .cache(cache)       //最好不要更改cache目录  
                            .connectTimeout(conectTimeout, TimeUnit.SECONDS)//设置相应时间  
                            .writeTimeout(writeTimeout, TimeUnit.SECONDS)  
                            .readTimeout(readTimeout, TimeUnit.SECONDS)  
                            .build();  
                }  
            }  
        }  
    }  
      
      
      
    //TODO 克隆一个client，更改链接时间等参数设置  
    public static OkHttpClient OkHttpClientClone(int conTime, int WriteTime, int ReadTime){  
        if (mOkHttpClient == null){  
            initOkHttpUtil();  
        }  
        OkHttpClient build = mOkHttpClient.newBuilder()  
                                .connectTimeout(conTime, TimeUnit.SECONDS)  
                                .writeTimeout(WriteTime, TimeUnit.SECONDS)  
                                .readTimeout(ReadTime, TimeUnit.SECONDS)  
                                .build();  
          
        return build;  
    }  
      
      
      
      
      
      
    /** 
     * 异步请求数据，有回调参数 
     * @param url 
     * @param callback 
     */  
    public static void get(String url, Callback callback){  
        Request request = new Request.Builder()  
                .url(url)  
                .build();  
        if (mOkHttpClient == null){  
            initOkHttpUtil();  
        }  
        Call newCall = mOkHttpClient.newCall(request);  
        newCall.enqueue(callback);  
    }  
      
    /** 
     * url参数较多时，可以使用map来拼接参数 
     * @param url 
     * @param headerMap 
     * @param callback 
     */  
    public static void get(String url, Map<String, String> params, Callback callback){  
        String urls = buildParams(url, params);  
        get(urls, callback);  
    }  
      
      
    /** 
     * 带header的get请求 如果url参数比较多，可以传递map，直观点 
     * @param urls 
     * @param params 
     * @param headerMap 
     * @param callback 
     */  
    public static void get(String urls, Map<String, String> params, Map<String, String> headerMap, Callback callback){  
        String url = buildParams(urls, params);  
        Request.Builder builder = new Request.Builder();  
        for (String str : headerMap.keySet()) {  
            builder.addHeader(str, headerMap.get(str));  
        }  
        Request request = builder  
                .url(url)  
                .build();  
        if (mOkHttpClient == null){  
            initOkHttpUtil();  
        }  
        Call newCall = mOkHttpClient.newCall(request);  
        newCall.enqueue(callback);  
    }  
      
      
      
      
      
    /** 
     * 简单的post请求 
     * @param url 
     * @param params 
     * @param callback 
     */  
    public static void post(String url, Map<String, String> params, Callback callback){  
        okhttp3.FormBody.Builder builder = new FormBody.Builder();  
        for (String key : params.keySet()) {  
            builder.add(key, params.get(key));  
        }  
        RequestBody body = builder.build();  
        Request requeset = new Request.Builder()  
                            .url(url)  
                            .post(body)  
                            .build();  
        if (mOkHttpClient == null){  
            initOkHttpUtil();  
        }  
        Call newCall = mOkHttpClient.newCall(requeset);  
        newCall.enqueue(callback);  
          
    }  
      
      
    /** 
     * 提交文件 
     * @param url 
     * @param file 
     * @param callback 
     */  
    public void postFile(String url, File file, Callback callback){  
        final MediaType MEDIA_TYPE_MARKDOWN  
          = MediaType.parse("text/x-markdown; charset=utf-8");  
        Request request = new Request.Builder()  
            .url(url)  
            .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))  
            .build();  
        if (mOkHttpClient == null){  
            initOkHttpUtil();  
        }  
        Call newCall = mOkHttpClient.newCall(request);  
        newCall.enqueue(callback);  
    }  
      
      
    /** 
     * 拼接url参数 
     * @param url 
     * @param params 
     * @return 
     */  
    public static String buildParams(String url, Map<String, String> params) {  
        if (TextUtils.isEmpty(url)) {  
            return url;  
        }  
        if (params == null || params.size() == 0) {  
            return url;  
        }  
        url += "?";  
        Set<String> keySet = params.keySet();  
        Iterator<String> itr = keySet.iterator();  
        while (itr.hasNext()) {  
            String key = itr.next();  
            url += key + "=" + params.get(key) + "&";  
        }  
        url = url.substring(0, url.length() - 1);  
        return url;  
    }  
      
      
      
      
    public static void cancelAllCall(){  
        if (mOkHttpClient == null){  
            return;  
        }  
    }     
  

 
    public static OkHttpClient getUnsafeOkHttpClient() {  
        try {  
            // Create a trust manager that does not validate certificate chains  
            final TrustManager[] trustAllCerts = new TrustManager[] {  
                    new X509TrustManager() {  
                        @Override  
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {  
                        }  
  
                        @Override  
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {  
                        }  
  
                        @Override  
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
                            return new java.security.cert.X509Certificate[]{};  
                        }  
                    }  
            };  
  
            // Install the all-trusting trust manager  
            final SSLContext sslContext = SSLContext.getInstance("SSL");  
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());  
            // Create an ssl socket factory with our all-trusting manager  
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();  
  
            OkHttpClient.Builder builder = new OkHttpClient.Builder();  
            builder.sslSocketFactory(sslSocketFactory);  
            builder.hostnameVerifier(new HostnameVerifier() {  
                @Override  
                public boolean verify(String hostname, SSLSession session) {  
                    return true;  
                }  
            });  
  
            OkHttpClient okHttpClient = builder.build();  
            return okHttpClient;  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
}