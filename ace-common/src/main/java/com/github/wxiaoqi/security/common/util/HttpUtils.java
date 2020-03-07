package com.github.wxiaoqi.security.common.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.github.wxiaoqi.security.common.task.CallbackMsg;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    protected final static Logger log = LogManager.getLogger(HttpUtils.class);
    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String PUT = "PUT";
    public final static String DELETE = "DELETE";
    public final static String PATCH = "PATCH";

    private final static String UTF8 = "UTF-8";
    // private final static String GBK = "GBK";

    private final static String DEFAULT_CHARSET = UTF8;
    private final static String DEFAULT_METHOD = GET;
    private final static String DEFAULT_MEDIA_TYPE = "application/json; charset=utf-8";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final static boolean DEFAULT_LOG = false;

    private static SSLContext sc = null;

    private static HttpUtils httpUtils = new HttpUtils();


    static {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        } };

        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true)
            .connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)).readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS).sslSocketFactory(sc.getSocketFactory()).build();

    /**
     * GET请求
     *
     * @param url
     *            URL地址
     * @return
     */
    public static String get(String url) {
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        return execute(okHttp);
    }

    public static String asynget(String url) {
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        return asynexecute(okHttp);
    }

    /**
     * GET请求
     *
     * @param url
     *            URL地址
     * @return
     */
    public static String get(String url, String charset) {
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        okHttp.setResponseCharset(charset);
        return execute(okHttp);
    }

    public static String asynget(String url, String charset) {
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        okHttp.setResponseCharset(charset);
        return asynexecute(okHttp);
    }

    /**
     * 带查询参数的GET查询
     *
     * @param url
     *            URL地址
     * @param queryMap
     *            查询参数
     * @return
     */
    public static String get(String url, Map<String, Object> queryMap) {
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        okHttp.setQueryMap(queryMap);
        return execute(okHttp);
    }

    public static String asynget(String url, Map<String, Object> queryMap) {
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        okHttp.setQueryMap(queryMap);
        return asynexecute(okHttp);
    }

    /**
     * 带查询参数的GET查询
     *
     * @param url
     *            URL地址
     * @param queryMap
     *            查询参数
     * @return
     */
    public static String get(String url, Map<String, Object> queryMap, String charset) {
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        okHttp.setQueryMap(queryMap);
        okHttp.setResponseCharset(charset);
        return execute(okHttp);
    }

    public static String asynget(String url, Map<String, Object> queryMap, String charset) {
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(GET);
        okHttp.setQueryMap(queryMap);
        okHttp.setResponseCharset(charset);
        return asynexecute(okHttp);
    }

    /**
     * POST application/json
     *
     * @param url
     * @param obj
     * @return
     */
    public static String postJson(String url, Object obj) {
        System.out.println(url);
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(POST);
        okHttp.setData(JSON.toJSONString(obj));
        okHttp.setMediaType(DEFAULT_MEDIA_TYPE);
        System.out.println(okHttp.getData());
        return execute(okHttp);
    }

    public static String postWithHeader(String url, Object obj,Map<String,String> headerMap) {
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(POST);
        if(okHttp.getQueryMap()!=null) {
            okHttp.getQueryMap().clear();
        }
        if(okHttp.getHeaderMap()!=null) {
            okHttp.getHeaderMap().clear();
        }
        okHttp.setHeaderMap(headerMap);
        okHttp.setData(JSON.toJSONString(obj));
        return execute(okHttp);
    }



    /**
     * POST application/json
     *
     * @param url
     * @param obj
     * @return
     */
    public static String postJsonString(String url, String obj) {
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(POST);
        okHttp.setData(obj);
        okHttp.setMediaType(DEFAULT_MEDIA_TYPE);
        return execute(okHttp);
    }

    public static String asynpostJson(String url, Object obj) {
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(POST);
        okHttp.setData(JSON.toJSONString(obj));
        okHttp.setMediaType(DEFAULT_MEDIA_TYPE);
        return asynexecute(okHttp);
    }

    /**
     * POST application/x-www-form-urlencoded
     *
     * @param url
     * @param formMap
     * @return
     */
    public static String postForm(String url, Map<String, Object> formMap) {
        String data = "";
        if (MapUtils.isNotEmpty(formMap)) {
            data = formMap.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
        }
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(POST);
        okHttp.setData(data);
        okHttp.setMediaType(CONTENT_TYPE);
        return execute(okHttp);
    }

    public static String asynpostForm(String url, Map<String, Object> formMap) {
        String data = "";
        if (MapUtils.isNotEmpty(formMap)) {
            data = formMap.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
        }
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(POST);
        okHttp.setData(data);
        okHttp.setMediaType(CONTENT_TYPE);
        return asynexecute(okHttp);
    }

    private static String post(String url, String data, String mediaType, String charset) {
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(url);
        okHttp.setMethod(POST);
        okHttp.setData(data);
        okHttp.setMediaType(mediaType);
        okHttp.setResponseCharset(charset);
        return execute(okHttp);
    }

    /**
     * 通知商户到账
     *
     * @param url
     * @return
     */
    public static Response postNotify(CallbackMsg obj) {
        HttpUtils httpUtils=new HttpUtils();
        OkHttp okHttp = httpUtils.new OkHttp();
        okHttp.setUrl(obj.getCallbackUrl());
        okHttp.setMethod(POST);
        okHttp.setData(obj.getNotifyStr());
        okHttp.setMediaType(DEFAULT_MEDIA_TYPE);
        return notify(okHttp);
    }

    /**
     * 通知
     */
    private static Response notify(OkHttp okHttp) {
        if (StringUtils.isBlank(okHttp.requestCharset)) {
            okHttp.requestCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.responseCharset)) {
            okHttp.responseCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.method)) {
            okHttp.method = DEFAULT_METHOD;
        }
        if (StringUtils.isBlank(okHttp.mediaType)) {
            okHttp.mediaType = DEFAULT_MEDIA_TYPE;
        }
        if (okHttp.requestLog) {// 记录请求日志
            log.info(okHttp.toString());
        }

        String url = okHttp.url;

        Request.Builder builder = new Request.Builder();

        if (MapUtils.isNotEmpty(okHttp.queryMap)) {
            String queryParams = okHttp.queryMap.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
            url = String.format("%s%s%s", url, url.contains("?") ? "&" : "?", queryParams);
        }
        builder.url(url);

        if (MapUtils.isNotEmpty(okHttp.headerMap)) {
            okHttp.headerMap.forEach(builder::addHeader);
        }

        String method = okHttp.method.toUpperCase();
        String mediaType = String.format("%s;charset=%s", okHttp.mediaType, okHttp.requestCharset);

        if (StringUtils.equals(method, GET)) {
            builder.get();
        } else if (ArrayUtils.contains(new String[] { POST, PUT, DELETE, PATCH }, method)) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), okHttp.data);
            builder.method(method, requestBody);
        } else {
            log.error(String.format("http method:%s not support!", method));
        }

        //	String result = "";
        try {
            Response response = client.newCall(builder.build()).execute();
//			byte[] bytes = response.body().bytes();
//			String result = new String(bytes, okHttp.responseCharset);
//			if (okHttp.responseLog) {// 记录返回日志
//				log.info(String.format("Got response->%s", result));
//			}
//			if (response.code() != 200) {
//				JSONObject requestData=JSON.parseObject(okHttp.getData());
//				if(requestData.containsKey("notifyId")) {
//					Integer notifyTimes=(Integer) CacheUtil.getCache().get("orderNo");
//					if(notifyTimes==null) {
//						CacheUtil.getCache().set("orderNo",1);
//					}else if(notifyTimes<=5) {
//						CacheUtil.getCache().set("orderNo",notifyTimes+1);
//					}else {
//						return "error";
//					}
//					notify(okHttp);
//				}
//			}
//			byte[] bytes = response.body().bytes();
//			result = new String(bytes, okHttp.responseCharset);
//			if (okHttp.responseLog) {// 记录返回日志
//				log.info(String.format("Got response->%s", result));
//			}
            return response;
        } catch (Exception e) {
            log.error(okHttp.toString(), e);
            return null;
        }
    }





    /**
     * 通用执行方法
     */
    private static String execute(OkHttp okHttp) {
        if (StringUtils.isBlank(okHttp.requestCharset)) {
            okHttp.requestCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.responseCharset)) {
            okHttp.responseCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.method)) {
            okHttp.method = DEFAULT_METHOD;
        }
        if (StringUtils.isBlank(okHttp.mediaType)) {
            okHttp.mediaType = DEFAULT_MEDIA_TYPE;
        }
        if (okHttp.requestLog) {// 记录请求日志
            log.info(okHttp.toString());
        }

        String url = okHttp.url;

        Request.Builder builder = new Request.Builder();

        if (MapUtils.isNotEmpty(okHttp.queryMap)) {
            String queryParams = okHttp.queryMap.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
            url = String.format("%s%s%s", url, url.contains("?") ? "&" : "?", queryParams);
        }
        builder.url(url);

        if (MapUtils.isNotEmpty(okHttp.headerMap)) {
            okHttp.headerMap.forEach(builder::addHeader);
        }

        String method = okHttp.method.toUpperCase();
        String mediaType = String.format("%s;charset=%s", okHttp.mediaType, okHttp.requestCharset);

        if (StringUtils.equals(method, GET)) {
            builder.get();
        } else if (ArrayUtils.contains(new String[] { POST, PUT, DELETE, PATCH }, method)) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), okHttp.data);
            builder.method(method, requestBody);
        } else {
            log.error(String.format("http method:%s not support!", method));
        }

        String result = "";
        try {
            Response response = client.newCall(builder.build()).execute();
            byte[] bytes = response.body().bytes();
            result = new String(bytes, okHttp.responseCharset);
            if (okHttp.responseLog) {// 记录返回日志
                log.info(String.format("Got response->%s", result));
            }
        } catch (Exception e) {
            log.error(okHttp.toString(), e);
            return "error";
        }
        return result;
    }

    /**
     * 通用执行方法
     */
    private static String asynexecute(OkHttp okHttp) {
        if (StringUtils.isBlank(okHttp.requestCharset)) {
            okHttp.requestCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.responseCharset)) {
            okHttp.responseCharset = DEFAULT_CHARSET;
        }
        if (StringUtils.isBlank(okHttp.method)) {
            okHttp.method = DEFAULT_METHOD;
        }
        if (StringUtils.isBlank(okHttp.mediaType)) {
            okHttp.mediaType = DEFAULT_MEDIA_TYPE;
        }
        if (okHttp.requestLog) {// 记录请求日志
            log.info(okHttp.toString());
        }

        String url = okHttp.url;

        Request.Builder builder = new Request.Builder();

        if (MapUtils.isNotEmpty(okHttp.queryMap)) {
            String queryParams = okHttp.queryMap.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
            url = String.format("%s%s%s", url, url.contains("?") ? "&" : "?", queryParams);
        }
        builder.url(url);

        if (MapUtils.isNotEmpty(okHttp.headerMap)) {
            okHttp.headerMap.forEach(builder::addHeader);
        }

        String method = okHttp.method.toUpperCase();
        String mediaType = String.format("%s;charset=%s", okHttp.mediaType, okHttp.requestCharset);

        if (StringUtils.equals(method, GET)) {
            builder.get();
        } else if (ArrayUtils.contains(new String[] { POST, PUT, DELETE, PATCH }, method)) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType), okHttp.data);
            builder.method(method, requestBody);
        } else {
            log.error(String.format("http method:%s not support!", method));
        }

        String result = "";
        try {
            client.newCall(builder.build()).enqueue(new Callback() {

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null)
                        response.body().close();
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    log.error("调用okhttp服务异步请求异常" + e);
                }
            });
        } catch (Exception e) {
            log.error(okHttp.toString(), e);
        }
        return result;
    }

    class OkHttp {
        private String url;
        private String method = DEFAULT_METHOD;
        private String data;
        private String mediaType = DEFAULT_MEDIA_TYPE;
        private Map<String, Object> queryMap;
        private Map<String, String> headerMap;
        private String requestCharset = DEFAULT_CHARSET;
        private boolean requestLog = DEFAULT_LOG;

        private String responseCharset = DEFAULT_CHARSET;
        private boolean responseLog = DEFAULT_LOG;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public Map<String, Object> getQueryMap() {
            return queryMap;
        }

        public void setQueryMap(Map<String, Object> queryMap) {
            this.queryMap = queryMap;
        }

        public Map<String, String> getHeaderMap() {
            return headerMap;
        }

        public void setHeaderMap(Map<String, String> headerMap) {
            this.headerMap = headerMap;
        }

        public String getRequestCharset() {
            return requestCharset;
        }

        public void setRequestCharset(String requestCharset) {
            this.requestCharset = requestCharset;
        }

        public boolean isRequestLog() {
            return requestLog;
        }

        public void setRequestLog(boolean requestLog) {
            this.requestLog = requestLog;
        }

        public String getResponseCharset() {
            return responseCharset;
        }

        public void setResponseCharset(String responseCharset) {
            this.responseCharset = responseCharset;
        }

        public boolean isResponseLog() {
            return responseLog;
        }

        public void setResponseLog(boolean responseLog) {
            this.responseLog = responseLog;
        }

    }
}
