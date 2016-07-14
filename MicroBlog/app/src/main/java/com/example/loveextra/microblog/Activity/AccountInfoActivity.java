package com.example.loveextra.microblog.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveextra.microblog.OpenAPI.LogoutAPI;
import com.example.loveextra.microblog.OpenAPI.UsersAPI;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.Util.NetUtil;
import com.example.loveextra.microblog.models.ErrorInfo;
import com.example.loveextra.microblog.models.User;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AccountInfoActivity extends AppCompatActivity {

    private TextView name,id,sex,location,description;
    private ImageView portrait;
    private Button logout;

    private String portraitUrl;

    Oauth2AccessToken token;
    UsersAPI usersAPI;

    public void init(){
        name=(TextView)findViewById(R.id.account_info_name);
        id=(TextView)findViewById(R.id.account_info_id);
        sex=(TextView)findViewById(R.id.account_info_sex);
        location=(TextView)findViewById(R.id.account_info_location);
        description=(TextView)findViewById(R.id.account_info_description);
        portrait=(ImageView)findViewById(R.id.account_info_portrait);
        logout=(Button)findViewById(R.id.btn_unload_account_info);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutAPI(AccountInfoActivity.this, Constant.APP_KEY,
                        AccessTokenKeeper.readAccessToken(AccountInfoActivity.this)).logout(new LogOutRequestListener());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        token= AccessTokenKeeper.readAccessToken(this);
        long uid = Long.parseLong(token.getUid());
        usersAPI=new UsersAPI(this, Constant.APP_KEY,token);
        init();
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
                    name.setText(user.screen_name);
                    description.setText("个性签名:"+user.description);
                    location.setText("所在地:"+user.location);
                    portraitUrl=user.profile_image_url;         //获取到头像照片的url
                    id.setText("用户ID:"+user.id);
                    sex.setText("性别:"+getSex(user.gender));
                    ProfileNetBitmapAsyTask task=new ProfileNetBitmapAsyTask();
                    task.execute(portraitUrl);
                } else {
                    Toast.makeText(AccountInfoActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Log.i("123",info.toString());
        }
    };


    private String getSex(String s){
        if(s.equals("m")){
            return "男";
        }else {
            return "女";
        }
    }

    class ProfileNetBitmapAsyTask extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap portraitBitMap=null;
            try {
                portraitBitMap= NetUtil.downLoadBitMap(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return portraitBitMap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            portrait.setImageBitmap(bitmap);
        }
    }


    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(AccountInfoActivity.this);
                        Toast.makeText(AccountInfoActivity.this,"已退出登录",Toast.LENGTH_SHORT).show();
                        Intent toAuthorizeIntent=new Intent(AccountInfoActivity.this,AccountInfoActivity.class);
                        startActivity(toAuthorizeIntent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(AccountInfoActivity.this,"注销失败",Toast.LENGTH_SHORT).show();
        }
    }

}
