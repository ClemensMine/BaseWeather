package com.example.baseweather.util;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHandler {
    private static final OkHttpClient client = new OkHttpClient.Builder().build();

    public interface OnResponseListener {
        void onSuccess(String responseBody);
        void onFailure(IOException e);
    }

    /***
     * 发送get请求
     * @param requestRrl 链接
     * @param headers 请求头
     * @return 网页内容
     * @throws IOException
     */
    public static String requestGETMethod(String requestRrl, Map<String, String> headers, OnResponseListener listener, Boolean async) throws IOException {
        Request build = new Request.Builder()
                .url(requestRrl)
                .headers(Headers.of(headers))
                .build();

        if (async){
            client.newCall(build).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()){
                        throw new IOException("预期外错误：" + response);
                    }
                    listener.onSuccess(response.body().string());
                }
            });
            return null;
        }else {
            return client.newCall(build).execute().body().string();
        }
    }

    /***
     * POST发送JSON数据
     * @param requestUrl 目标地址
     * @param headers 头信息
     * @param data json数据
     * @param listener 异步处理信息
     * @throws IOException IO错误
     */
    public static String sendJsonPost(String requestUrl,Map<String,String> headers, String data, OnResponseListener listener, Boolean async) throws IOException {
        RequestBody requestBody = RequestBody.create(data, MediaType.parse("application/json; charset=utf-8"));

        Request build = new Request.Builder()
                .url(requestUrl)
                .headers(Headers.of(headers))
                .post(requestBody)
                .build();

        if(async){
            client.newCall(build).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()){
                        throw new IOException("预期外错误：" + response);
                    }
                    listener.onSuccess(response.body().string());
                }
            });
            return null;
        }else {
            return client.newCall(build).execute().body().string();
        }
    }

    /***
     * 使用x-www-form方法进行post
     * @param requestUrl 目标地址
     * @param headers 头信息
     * @param data 表单数据
     * @param listener 异步回调
     * @param async 是否异步
     * @return 返回信息
     * @throws IOException IO错误
     */
    public static String sendXPost(String requestUrl, Map<String, String> headers,Map<String, String> data, OnResponseListener listener, Boolean async) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        data.forEach(builder::add);

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(builder.build())
                .build();

        if (async){
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()){
                        throw new IOException(response.toString());
                    }
                    listener.onSuccess(response.body().string());
                }
            });
            return null;
        }else {
            return client.newCall(request).execute().body().string();
        }
    }
}
