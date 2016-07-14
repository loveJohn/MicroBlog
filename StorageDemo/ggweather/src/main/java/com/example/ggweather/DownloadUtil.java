package com.example.ggweather;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by LoveExtra on 2016/6/16.
 */
public class DownloadUtil {
    public static byte[] download(String srcUrl) {
        HttpURLConnection conn=null;
        byte[] date=null;
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        URL url = null;
        try {
            url = new URL(srcUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);    //延迟时间
            conn.connect();
            //code    2**请求成功   3**重定向   4***请求错误  5***服务
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                byte[] buff=new byte[1024];
                int len;
                while ((len=is.read(buff))!=-1){
                    os.write(buff,0,len);

                }
                is.close();
            }
            date=os.toByteArray();
            os.close();
        } catch (MalformedURLException e) {
            Log.i("123","aaaa");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.i("123","bbbb");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("123","cccc");
            e.printStackTrace();
        }
        return date;
    }


}
