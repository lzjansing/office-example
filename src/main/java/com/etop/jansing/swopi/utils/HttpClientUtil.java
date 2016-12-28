package com.etop.jansing.swopi.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient工具类
 * Created by liweimin on 2015/6/10.
 */
public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static final String CHARSET = "UTF-8";
    public static final String BROWSER_FIREFOX = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0";
    public static final String BROWSER_CHROME = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.75 Safari/537.36";
    public static final int CONNECT_TIMEOUT = 180000;
    public static final int SOCKET_TIMEOUT = 90000;

    private CloseableHttpClient httpClient;
    private BasicHeader header;
    private HttpHost httpHost;

    public HttpClientUtil(boolean isSSL, BasicHeader header) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        initHttpClient(isSSL, config, header);
    }

    public HttpClientUtil(boolean isSSL, BasicHeader header, int timeout) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
        initHttpClient(isSSL, config, header);
    }

    /**
     * 使用代理请求
     *
     * @param isSSL
     * @param header
     * @param ip
     * @param port
     */
    public HttpClientUtil(boolean isSSL, BasicHeader header, String ip, int port) {
        httpHost = new HttpHost(ip, port);
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).setProxy(httpHost).build();
        initHttpClient(isSSL, config, header);
    }

    /**
     * 初始化HttpClient
     *
     * @param isSSL
     * @param config
     * @param header
     */
    private void initHttpClient(boolean isSSL, RequestConfig config, BasicHeader header) {
        if (isSSL) {

        } else {
            this.httpClient = HttpClientBuilder.create().setUserAgent(BROWSER_FIREFOX).setDefaultRequestConfig(config).build();
        }

        this.header = header;
    }

    public String doGet(String url, Map<String, String> params) throws Exception {
        return doGet(url, params, CHARSET);
    }

    public byte[] doGetImage(String url, Map<String, String> params) throws Exception {
        return doGetImage(url, params, CHARSET);
    }

    public InputStream doGetImageForInputStream(String url, Map<String, String> params) throws Exception {
        return doGetImageForInputStream(url, params, CHARSET);
    }

    public String doPost(String url, Map<String, String> params) throws Exception {
        return doPost(url, params, CHARSET);
    }

    /**
     * GET请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 编码
     * @return 网页内容
     * @throws Exception
     */
    public String doGet(String url, Map<String, String> params, String charset) throws Exception {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }

            HttpGet httpGet = new HttpGet(url);
            if (header != null) {
                httpGet.addHeader(header);
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * GET请求图片字节
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 编码
     * @return 图片字节
     * @throws Exception
     */
    public byte[] doGetImage(String url, Map<String, String> params, String charset) throws Exception {
        byte[] data = null;

        if (StringUtils.isBlank(url)) {
            return null;
        }

        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }

            HttpGet httpGet = new HttpGet(url);
            if (header != null) {
                httpGet.addHeader(header);
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream is = entity.getContent();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                data = baos.toByteArray();
            }
            EntityUtils.consume(entity);
            response.close();
            return data;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * GET请求图片字节流
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 编码
     * @return 图片字节
     * @throws Exception
     */
    public InputStream doGetImageForInputStream(String url, Map<String, String> params, String charset) throws Exception {
        InputStream in = null;

        if (StringUtils.isBlank(url)) {
            return null;
        }

        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }

            HttpGet httpGet = new HttpGet(url);
            if (header != null) {
                httpGet.addHeader(header);
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
            }
            return in;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * POST请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 编码
     * @return 网页内容
     * @throws Exception
     */
    public String doPost(String url, Map<String, String> params, String charset) throws Exception {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }

            HttpPost httpPost = new HttpPost(url);
            if (header != null) {
                httpPost.addHeader(header);
            }
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static String encodeParams(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            try {
                return "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
            } catch (IOException e) {
                ;
            }
        }
        return "";
    }

    public static String getLocalServerPath(HttpServletRequest req) {
        String requestURL = req.getRequestURL().toString();
        String servletPath = req.getServletPath();
        return requestURL.substring(0, requestURL.indexOf(servletPath));
    }
}
