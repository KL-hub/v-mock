package com.vmock.biz.util;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Slf4j
public class HttpClinetUtil {
    private static int SOCKET_TIME_OUT = 60*1000;        //传输间隔超时
    private static int CONNECT_TIME_OUT = 60*1000;        //链接建立超时

    /**
     * @author Yanzm
     * @param url 请求路径
     * @param jsonString Json格式的请求参数
     * @return
     */
    public static String doPost(String url,String jsonString,String headerArray){
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultStr = null;
        HttpPost post = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIME_OUT)
                .setConnectTimeout(CONNECT_TIME_OUT).build();
        post.setConfig(requestConfig);

        try {
            post.addHeader("Accept","application/json");
            if(StringUtils.isNotBlank(headerArray)){
                JSONArray jsonArray = new JSONArray(headerArray);
                for (Object o : jsonArray) {
                    JSONObject jsonObject = new JSONObject(o);
                    post.addHeader(String.valueOf(jsonObject.get("key")),String.valueOf(jsonObject.get("val")));
                }
            }
            StringEntity entity = new StringEntity(jsonString);

            post.setEntity(entity);
            response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (StringUtils.isNotBlank(resultStr)) {
                    return resultStr;
                }else {
                    log.info("请求响应正常，返回的数据为空");
                    return resultStr;
                }
            }else {
                log.error("响应异常，状态码为：{}",response.getStatusLine().getStatusCode());
                return StringUtils.EMPTY;
            }
        } catch (Exception e) {
            log.error("httpClient 接口调用异常");
            e.printStackTrace();
        }finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("httpClient response 关闭异常");
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error("httpClient 关闭异常");
                    e.printStackTrace();
                }
            }
        }
        return resultStr;
    }

    /**
     * @author Yanzm
     * @param  url 请求路径
     * @return
     */
    public static String doGet(String url){
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultStr = null;
        HttpGet get = new HttpGet(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIME_OUT)
                .setConnectTimeout(CONNECT_TIME_OUT).build();
        get.setConfig(requestConfig);

        try {
            get.addHeader("Accept","application/json");

            response = httpClient.execute(get);
            log.info("Connect to {}",url);
            if (response.getStatusLine().getStatusCode() == 200) {
                resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (StringUtils.isNotBlank(resultStr)) {
                    return resultStr;
                }else {
                    log.info("请求响应正常，返回的数据为空");
                    return resultStr;
                }
            }else {
                log.error("响应异常，状态码为：{}",response.getStatusLine().getStatusCode());
                return StringUtils.EMPTY;
            }
        } catch (Exception e) {
            log.error("httpClient 接口调用异常");
            e.printStackTrace();
        }finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("httpClient response 关闭异常");
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error("httpClient 关闭异常");
                    e.printStackTrace();
                }
            }
        }
        return resultStr;
    }

    public static void setSocketTimeOut(int timeOut) {
        SOCKET_TIME_OUT = timeOut;
    }

    public static void setConnectTimeOut(int timeOut) {
        CONNECT_TIME_OUT = timeOut;
    }
}
