package com.example.baseweather.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamHandler {
    /***
     * 将流转换为string
     * @param inputStream 输入流
     * @return 结果
     * @throws IOException IO报错
     */
    public static String stream2string(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null){
            builder.append(line);
        }

        return builder.toString();
    }
}
