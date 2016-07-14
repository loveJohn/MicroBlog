package com.example.loveextra.microblog.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveextra.microblog.Activity.Game2048Activity;
import com.example.loveextra.microblog.Activity.NoteActivity;
import com.example.loveextra.microblog.Activity.PublishIdeaActivity;
import com.example.loveextra.microblog.OpenAPI.UsersAPI;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.Util.NetUtil;
import com.example.loveextra.microblog.models.ErrorInfo;
import com.example.loveextra.microblog.models.User;
import com.example.loveextra.microblog.models.WeatherManager;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by LoveExtra on 2016/6/29.
 */
public class AddFragment extends Fragment {

    private View view;
    Context context;
    String data;


    private WeatherManager weatherManager;
    private User account;

    private TextView tvDay,tvMonthYear,tvWeek,tvLocationWeather;
    private ImageView imAd,imWeather;
    private LinearLayout bnIdea,bnImage,bnHeadline,bnSign,bnComments,bnMore;
    private GridLayout bnInfoWeather;


    public static AddFragment newInstance(){
        AddFragment addFragment=new AddFragment();
        Bundle bundle=new Bundle();
        return addFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getActivity();
        view=inflater.inflate(R.layout.fragment_add,container,false);
        init();
        updateInfo();
        return view;
    }

    public void init(){
        bnInfoWeather=(GridLayout)view.findViewById(R.id.time_weather_info_add);
        bnIdea=(LinearLayout)view.findViewById(R.id.bn_idea_add);
        bnImage=(LinearLayout)view.findViewById(R.id.bn_image_add);
        bnHeadline=(LinearLayout)view.findViewById(R.id.bn_headline_add);
        bnSign=(LinearLayout)view.findViewById(R.id.bn_sign_add);
        bnComments=(LinearLayout)view.findViewById(R.id.bn_comments_add);
        bnMore=(LinearLayout)view.findViewById(R.id.bn_more_add);

        tvDay=(TextView)view.findViewById(R.id.day_add_calendar);
        tvMonthYear=(TextView)view.findViewById(R.id.month_year_add_calendar);
        tvWeek=(TextView)view.findViewById(R.id.week_add_calendar);
        tvLocationWeather=(TextView)view.findViewById(R.id.location_weather_add_calendar);

        imAd=(ImageView)view.findViewById(R.id.ad_image_add);
        imWeather=(ImageView)view.findViewById(R.id.im_weather_add_calendar);

        bnInfoWeather.setOnClickListener(bnlis);
        bnIdea.setOnClickListener(bnlis);
        bnImage.setOnClickListener(bnlis);
        bnHeadline.setOnClickListener(bnlis);
        bnSign.setOnClickListener(bnlis);
        bnComments.setOnClickListener(bnlis);
        bnMore.setOnClickListener(bnlis);
        imAd.setOnClickListener(bnlis);

    }

    private void updateInfo(){
        Oauth2AccessToken token= AccessTokenKeeper.readAccessToken(context);
        long uid = Long.parseLong(token.getUid());
        UsersAPI usersAPI=new UsersAPI(context, Constant.APP_KEY,token);
        usersAPI.show(uid, mListener);                //通过ID获取用户信息
    }


    //设定监听，得到用户信息
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                    account=user;
                    getInfo();
                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Log.i("123",info.toString());
        }
    };

    private void getInfo(){
        new WeatherTask().execute();      //开异步任务

    }

    class WeatherTask extends AsyncTask<Void,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap=null;
            try {
                getWeatherInfo(jLocation(account.location));
                Log.i("123","获取到天气信息,开始json解析");
                Log.i("123",getData().toString());
                setWeatherManager(NetUtil.jsonWeather(getData()));
                Log.i("123","json解析完成");
                bitmap=NetUtil.downLoadBitMap(weatherManager.getWeatherPicture());       //可以将bitmap设置天气图片
                Log.i("123","天气图片获取成功");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //设置天气图片
            Log.i("123","设置界面");
//
            tvLocationWeather.setText(account.location+":"+getWeatherManager().getWeather()+" "+getWeatherManager().getTemperature()+"度");
            Calendar calendar=Calendar.getInstance();
            tvDay.setText(calendar.get(Calendar.DAY_OF_MONTH)+"");
            tvWeek.setText(changeWeekday(calendar.get(Calendar.DAY_OF_WEEK)));
            tvMonthYear.setText((calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR));
            imWeather.setImageBitmap(bitmap);
        }
    }


    private String jLocation(String str){
        String[] lo=str.split(" ");
        return lo[lo.length-1];
    }



    private void getWeatherInfo(String area) throws Exception {
        URL u=new URL("https://route.showapi.com/9-2?area="+area+"&showapi_appid=20619&showapi_sign=cc54de6717124368b93c79b8939b9a04");
        Log.i("123",u.toString());
        HttpURLConnection conn= (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream in=conn.getInputStream();
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            byte buf[]=new byte[1024];
            int read = 0;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
        }  finally {
            if (in != null) {
                in.close();
            }
        }
        setData(out.toString());
        out.close();
    }



    private String changeWeekday(int w){
        String str="";
        switch (w){
            case 1:
                str="星期一";
                break;
            case 2:
                str="星期二";
                break;
            case 3:
                str="星期三";
                break;
            case 4:
                str="星期四";
                break;
            case 5:
                str="星期五";
                break;
            case 6:
                str="星期六";
                break;
            case 7:
                str="星期天";
                break;
            default:
                break;
        }
        return str;
    }

    private View.OnClickListener bnlis=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.time_weather_info_add:
                    Toast.makeText(context,"天气预报",Toast.LENGTH_SHORT).show();
//                    toWeather();
                    break;
                case R.id.bn_idea_add:
                    Toast.makeText(context,"写微博",Toast.LENGTH_SHORT).show();
                    Intent ideaIntent=new Intent(context, PublishIdeaActivity.class);
                    startActivity(ideaIntent);
//                    toIdeaStatus();
                    break;
                case R.id.bn_image_add:
                    Toast.makeText(context,"发布照片",Toast.LENGTH_SHORT).show();
//                    toPublishImage();
                    break;
                case R.id.bn_headline_add:
                    toHeadline();       //游戏
                    break;
                case R.id.bn_sign_add:
//                    toSign();
                    break;
                case R.id.bn_comments_add:
                    toComments();           //便签
                    break;
                case R.id.bn_more_add:
//                    toMore();
                    break;
                case R.id.ad_image_add:
//                    toAd();
                    break;
                default:
                    break;
            }
        }
    };

    private void toHeadline(){
        Intent toGameIntent=new Intent(context, Game2048Activity.class);
        context.startActivity(toGameIntent);
    }

    private void toComments(){
        Intent toNoteIntent=new Intent(context, NoteActivity.class);
        context.startActivity(toNoteIntent);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public WeatherManager getWeatherManager() {
        return weatherManager;
    }

    public void setWeatherManager(WeatherManager weatherManager) {
        this.weatherManager = weatherManager;
    }
}
