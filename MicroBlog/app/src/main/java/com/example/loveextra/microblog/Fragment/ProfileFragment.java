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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveextra.microblog.Activity.AccountInfoActivity;
import com.example.loveextra.microblog.Activity.MicroBlogActivity;
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

import java.io.IOException;

/**
 * Created by LoveExtra on 2016/6/28.
 */
public class ProfileFragment extends Fragment {

    View view;
    Context context;

    TextView bnAddFriend,bnSetting,tvAccountName,tvBeWrite,tvLocation,tvMicroBlogs,tvAttentions,tvFans;
    RelativeLayout bnProtectedWarning,bnMicroBlogs,bnAttentions,bnFans;
    LinearLayout bnAccountInfo;
    ImageView portraitAccount,levelAccount;
    private String portraitUrl;


    public static ProfileFragment newInstance(){
        ProfileFragment profileFragment=new ProfileFragment();
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getActivity();
        view=inflater.inflate(R.layout.fragment_profile,container,false);
        init();
        getAccountInfo();
        return view;
    }

    public void init(){
        bnAddFriend=(TextView)view.findViewById(R.id.btn_profile_navigation_add_friend);
        bnSetting=(TextView)view.findViewById(R.id.btn_profile_navigation_setting);
        tvAccountName=(TextView)view.findViewById(R.id.username_profile_account);
        tvBeWrite=(TextView)view.findViewById(R.id.bewrite_profile_account);
        tvLocation=(TextView)view.findViewById(R.id.none_profile_account);
        tvMicroBlogs=(TextView)view.findViewById(R.id.amount_micro_blog_profile);
        tvAttentions=(TextView) view.findViewById(R.id.amount_attention_profile);
        tvFans=(TextView)view.findViewById(R.id.amount_fans_profile);
        bnProtectedWarning=(RelativeLayout)view.findViewById(R.id.btn_profile_protect_warning);
        bnMicroBlogs=(RelativeLayout)view.findViewById(R.id.btn_profile_microblogs);
        bnAttentions=(RelativeLayout)view.findViewById(R.id.btn_profile_attentions);
        bnFans=(RelativeLayout)view.findViewById(R.id.btn_profile_fans);
        bnAccountInfo=(LinearLayout)view.findViewById(R.id.btn_profile_account_info);
        portraitAccount=(ImageView)view.findViewById(R.id.portrait_profile_account);
        levelAccount=(ImageView)view.findViewById(R.id.level_profile_account);

        bnAddFriend.setOnClickListener(bnlis);
        bnSetting.setOnClickListener(bnlis);
        bnProtectedWarning.setOnClickListener(bnlis);
        bnMicroBlogs.setOnClickListener(bnlis);
        bnAttentions.setOnClickListener(bnlis);
        bnFans.setOnClickListener(bnlis);
        bnAccountInfo.setOnClickListener(bnlis);

    }

    @Override
    public void onStart() {
        super.onStart();



    }

    private void getAccountInfo(){
        Oauth2AccessToken token= AccessTokenKeeper.readAccessToken(context);
        long uid = Long.parseLong(token.getUid());
        UsersAPI usersAPI=new UsersAPI(context,Constant.APP_KEY,token);
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
                    tvAccountName.setText(user.screen_name);
                    tvBeWrite.setText(user.description);
                    tvLocation.setText(user.location);
                    tvMicroBlogs.setText(user.statuses_count+"");
                    tvAttentions.setText(user.friends_count+"");
                    tvFans.setText(user.followers_count+"");
                    portraitUrl=user.profile_image_url;
                    ProfileNetBitmapAsyTask task=new ProfileNetBitmapAsyTask();
                    task.execute(portraitUrl);
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








    private View.OnClickListener bnlis=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_profile_navigation_add_friend:        //添加朋友
//                    toAddFriend();
                    break;
                case R.id.btn_profile_navigation_setting:       //设置
//                    toSetting();
                    break;
                case R.id.btn_profile_protect_warning:          //账号保护警告
//                    toProtectAccount();
                    break;
                case R.id.btn_profile_microblogs:          //微博数

                    break;
                case R.id.btn_profile_attentions:          //关注数
//                    toViewAttion();
                    break;
                case R.id.btn_profile_fans:               //粉丝数
//                    toViewFans();
                    break;
                case R.id.btn_profile_account_info:           //个人账户
                    Intent toAccountInfo=new Intent(context, AccountInfoActivity.class);
                    startActivity(toAccountInfo);
                    break;

            }
        }
    };


    class ProfileNetBitmapAsyTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap portraitBitMap=null;
            try {
                portraitBitMap=NetUtil.downLoadBitMap(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return portraitBitMap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            portraitAccount.setImageBitmap(bitmap);
        }
    }

}
