package com.example.loveextra.microblog.Util;

import com.example.loveextra.microblog.models.Essay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by LoveExtra on 2016/7/8.
 */
public class WeChatEssayAPI {

    public static ArrayList<Essay> getEssay(String keyWord)throws IOException {
        String url="http://route.showapi.com/582-2?key="+keyWord+"&showapi_sign=53408e60373b46a0995a1843af11b44f&showapi_appid=20555";
        URL u=new URL(url);
        String date=null;
        InputStream in=u.openStream();
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
        byte b[]=out.toByteArray( );
        date=new String(b,"utf-8");
        //json 解析
        ArrayList<Essay> essayArrayList=new ArrayList<>();
        try {
            JSONObject object=new JSONObject(date);
            if(object.has("showapi_res_body")){
                JSONObject body= object.getJSONObject("showapi_res_body");
                if(body.has("pagebean")){
                    JSONObject pagebean=body.getJSONObject("pagebean");
                    if(pagebean.has("contentlist")){
                        JSONArray contentlist=pagebean.getJSONArray("contentlist");
                        for (int i=0;i<contentlist.length();i++){
                            Essay essay=new Essay();
                            JSONObject es=contentlist.getJSONObject(i);
                            if(es.has("id")){
                                essay.setId(es.getString("id"));
                            }
                            if(es.has("typeName")){
                                essay.setTypeName(es.getString("typeName"));
                            }
                            if(es.has("title")){
                                essay.setTitle(es.getString("title"));
                            }
                            if(es.has("contentImg")){
                                essay.setContentImg(es.getString("contentImg"));
                            }
                            if(es.has("userLogo")){
                                essay.setUserLogo(es.getString("userLogo"));
                            }
                            if(es.has("userName")){
                                essay.setUserName(es.getString("userName"));
                            }
                            if(es.has("date")){
                                essay.setDate(es.getString("date"));
                            }
                            if(es.has("typeId")){
                                essay.setTypeId(es.getString("typeId"));
                            }
                            if(es.has("url")){
                                essay.setUrl(es.getString("url"));
                            }
                            if(es.has("weixinNum")){
                                essay.setWeixinNum(es.getString("weixinNum"));
                            }
                            if(es.has("userLogo_code")){
                                essay.setUserLogo_code(es.getString("userLogo_code"));
                            }
                            essayArrayList.add(essay);
                        }
                        return essayArrayList;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}


