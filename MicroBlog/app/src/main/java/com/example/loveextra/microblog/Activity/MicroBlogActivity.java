package com.example.loveextra.microblog.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.example.loveextra.microblog.Adapter.ListStatusAdapter;
import com.example.loveextra.microblog.Adapter.ListStatusSelfAdapter;
import com.example.loveextra.microblog.Adapter.NineGridListAdapter;
import com.example.loveextra.microblog.OpenAPI.StatusesAPI;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.models.Status;
import com.example.loveextra.microblog.models.StatusList;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.LinkedList;
import java.util.List;

public class MicroBlogActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    private StatusesAPI mStatusesAPI;
    ImageButton bnBack;
    ImageButton bnMenu;
    MaterialRefreshLayout refreshLayout;
    ListView listMicroblog;

    ListStatusSelfAdapter adapter;

    private LinkedList<Status> mLinkedList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_blog);
        init();
    }

    public void init(){
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mStatusesAPI = new StatusesAPI(this, Constant.APP_KEY, mAccessToken);
        bnBack=(ImageButton)findViewById(R.id.btn_microblog_navigation_back);
        bnMenu=(ImageButton)findViewById(R.id.btn_microblog_navigation_menu);

        refreshLayout=(MaterialRefreshLayout)findViewById(R.id.material_refresh_microblog);
        listMicroblog=(ListView)findViewById(R.id.list_status_microblog);

        bnBack.setOnClickListener(this);
        bnMenu.setOnClickListener(this);

        mStatusesAPI.friendsTimeline(0L,0L,10,1,false,0,true,mListener);
        Log.i("123","获取我的微博");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_microblog_navigation_back:
                finish();
                break;
            case R.id.btn_microblog_navigation_menu:
                //弹出菜单
                break;
            default:
                break;
        }
    }

    private void setAdapter(){
        adapter=new ListStatusSelfAdapter(MicroBlogActivity.this,mLinkedList);
        Log.i("123","创建适配");
        listMicroblog.setAdapter(adapter);
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            if (!TextUtils.isEmpty(s)) {
                Log.i("123","Json解析微博");
                if (s.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(s);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(MicroBlogActivity.this, "获取微博信息流成功,条数: " + statuses.statusList.size(), Toast.LENGTH_LONG).show();
                    }
                    mLinkedList = statuses.statusList;
                    setAdapter();            //创建适配器
                } else {
                    Toast.makeText(MicroBlogActivity.this, "获取失败", Toast.LENGTH_LONG).show();
                }
            }else {
                Log.i("123","未获取到微博");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(MicroBlogActivity.this, "评论获取失败", Toast.LENGTH_LONG).show();
        }
    };
}
