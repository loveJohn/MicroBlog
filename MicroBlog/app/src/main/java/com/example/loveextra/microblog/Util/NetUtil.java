package com.example.loveextra.microblog.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.loveextra.microblog.models.WeatherManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by LoveExtra on 2016/6/25.
 */
public class NetUtil {


    /**
     * 网络连接是否可用
     */

    public static boolean isConnnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (null != networkInfo) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    Log.e("123", "Net is connected");
                    return true;
                }

            }
        }
        Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
        return false;
    }



    public static String getRequest(Context context,String urlStr){
        if(!isConnnected(context)){
            return null;
        }
        InputStream in = null;
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000*10);         //请求延时等待时间
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                in = conn.getInputStream();
                int len = -1;
                byte[] buff = new byte[1024];
                while((len = in.read(buff)) != -1){
                    dataOut.write(buff, 0, len);
                }
            }else{
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            //关闭流
            if (in != null ) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( dataOut != null){
                try {
                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataOut.toString();
    }


    public static String postRequest(String url){

        return null;
    }



    //根据图片url返回BitMap
    public static Bitmap downLoadBitMap(String urlStr) throws IOException {
        byte[] date=null;
        InputStream is=null;
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            URL url=new URL(urlStr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            //设置通用选项
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.connect();
            //code2 请求成功 code3重定向 code4请求错误 code5服务器错误
            if(conn.getResponseCode()==200){
                is=conn.getInputStream();
                byte [] buffer=new byte[1024];
                int len;
                while((len=is.read(buffer))!=-1){
                    out.write(buffer,0,len);
                }
            }
            Log.i("123","数据请求成功");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                is.close();
            }
            if(out!=null){
                out.close();
            }
        }
        date=out.toByteArray();
        if(date!=null){
            Bitmap bm= BitmapFactory.decodeByteArray(date,0,date.length);
            return bm;
        }else{
            Log.i("123","未获取到数据");
            return null;
        }
    }






    public static WeatherManager jsonWeather(String s){

        WeatherManager weatherManager=new WeatherManager();

        try {
            JSONObject object=new JSONObject(s);
            if(object.has("showapi_res_body")){
                JSONObject body= object.getJSONObject("showapi_res_body");
                if(body.has("cityInfo")) {
                    JSONObject cityInfo=body.getJSONObject("cityInfo");
                    if (cityInfo.has("c3")) {
                        weatherManager.setArea(cityInfo.getString("c3"));
                    }
                    if (cityInfo.has("c7")) {
                        weatherManager.setProvinceOfArea(cityInfo.getString("c7"));
                    }
                    if (cityInfo.has("c12")) {
                        weatherManager.setPostcodeOfArea(cityInfo.getString("c12"));
                    }
                }
                if(body.has("now")){
                    JSONObject now=body.getJSONObject("now");
                    if(now.has("weather")){
                        weatherManager.setWeather(now.getString("weather"));
                    }
                    if(now.has("weather_pic")){
                        weatherManager.setWeatherPicture(now.getString("weather_pic"));
                    }
                    if(now.has("temperature_time")){
                        weatherManager.setTimeOfData(now.getString("temperature_time"));
                    }
                    if(now.has("temperature")){
                        weatherManager.setTemperature(now.getString("temperature"));
                    }
                    if(now.has("wind_direction")){
                        weatherManager.setWindDirection(now.getString("wind_direction"));
                    }
                    if(now.has("wind_power")){
                        weatherManager.setWindPower(now.getString("wind_power"));
                    }
                    if(now.has("aqi")){
                        weatherManager.setExponentOfAir(now.getString("aqi"));
                    }
                    if(now.has("sd")){
                        weatherManager.setHumidityOfAir(now.getString("sd"));
                    }
                    if(now.has("aqiDetail")){
                        JSONObject aqiDetail=now.getJSONObject("aqiDetail");
                        if(aqiDetail.has("quality")){
                            weatherManager.setQualityOfAir(aqiDetail.getString("quality"));
                        }
                        if(aqiDetail.has("primary_pollutant")){
                            weatherManager.setPrimaryPollution(aqiDetail.getString("primary_pollutant"));
                        }
                        if(aqiDetail.has("pm2_5")){
                            weatherManager.setPM2_5(aqiDetail.getString("pm2_5"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherManager;
    }

}
