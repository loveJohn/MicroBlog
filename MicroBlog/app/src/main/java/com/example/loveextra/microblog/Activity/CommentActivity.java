package com.example.loveextra.microblog.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.loveextra.microblog.OpenAPI.CommentsAPI;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.Util.Tools;
import com.example.loveextra.microblog.models.Comment;
import com.example.loveextra.microblog.models.CommentList;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.List;


//对微博进行评论

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton bnBack;
    private ImageButton bnMenu, bnSend;
    private TextView textNumber;
    private EditText editText;
    private ListView listViewComment;
    private int num=140;            //输入框字数限制

    private Intent intent;
    private Long statuID;

    private LayoutInflater inflater;

    private List<Comment> commentList;

    private RequestQueue mQueue;
    private ImageLoader imageLoader;

    LruCache<String, Bitmap> mNetCache;

    private CommentListAdapter adapter;
    private MaterialRefreshLayout materialRefreshComment;

    private int maxSize = 10 * 1024 * 1024;

    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    private CommentsAPI mCommentsAPI;


    public void init() {

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mCommentsAPI = new CommentsAPI(this, Constant.APP_KEY, mAccessToken);
        intent = getIntent();
        statuID = Long.parseLong(intent.getStringExtra("statusId"));
        mQueue = Volley.newRequestQueue(this);

        textNumber= (TextView) findViewById(R.id.tv_text_number_comment);
        bnBack = (ImageButton) findViewById(R.id.btn_comment_navigation_back);
        bnMenu = (ImageButton) findViewById(R.id.btn_comment_navigation_menu);
        bnSend = (ImageButton) findViewById(R.id.btn_comment_send);
        editText = (EditText) findViewById(R.id.edit_comment_activity);
        listViewComment=(ListView)findViewById(R.id.list_comment);
        materialRefreshComment= (MaterialRefreshLayout) findViewById(R.id.material_refresh_comment);
        materialRefreshComment.setLoadMore(true);
        materialRefreshComment.finishRefreshLoadMore();
        materialRefreshComment.setMaterialRefreshListener(mlis);

        bnBack.setOnClickListener(this);
        bnMenu.setOnClickListener(this);
        bnSend.setOnClickListener(this);
        editText.addTextChangedListener(telis);

        inflater = LayoutInflater.from(this);

        mNetCache=new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

        imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {

            @Override
            public void putBitmap(String url, Bitmap bitmap) {      //设置缓存区
                if(getBitmap(url)==null){
                    mNetCache.put(url,bitmap);
                }
            }
            @Override
            public Bitmap getBitmap(String url) {           //从缓存区中取图
                return mNetCache.get(url);
            }
        });



        //默认进去显示全部人的评论

        mCommentsAPI.show(statuID,0L,0L,50,1,0,mListener);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();

    }


    private void showAttention(){
//        mCommentsAPI.show(statuID,0L,0L,50,1,1,mListener);
    }

    private TextWatcher telis=new TextWatcher() {
        private CharSequence wordNum;//记录输入的字数
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            wordNum= s;//实时记录输入的字数
        }

        @Override
        public void afterTextChanged(Editable s) {
            int number = num - s.length();
            //TextView显示剩余字数
            textNumber.setText("" + number);
            selectionStart=editText.getSelectionStart();
            selectionEnd = editText.getSelectionEnd();
            if (wordNum.length() > num) {
                //删除多余输入的字（不会显示出来）
                s.delete(selectionStart - 1, selectionEnd);
                int tempSelection = selectionEnd;
                editText.setText(s);
                editText.setSelection(tempSelection);//设置光标在最后
            }
        }
    };

    private MaterialRefreshListener mlis=new MaterialRefreshListener() {


        @Override
        public void onRefresh( final MaterialRefreshLayout materialRefreshLayout) {
            materialRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //异步任务
                    materialRefreshLayout.finishRefresh();

                }
            }, 2000);
            //下拉刷新
            mCommentsAPI.show(statuID,0L,0L,50,1,0,mListener);
        }

        @Override
        public void onfinish() {
            Toast.makeText(CommentActivity.this, "finish", Toast.LENGTH_LONG).show();
        }


        @Override
        public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
            materialRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //异步任务
                    materialRefreshLayout.finishRefreshLoadMore();

                }
            }, 1000);
            Toast.makeText(CommentActivity.this, "暂未实现上拉加载更多，敬请期待", Toast.LENGTH_LONG).show();
        }

    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment_navigation_back:
                finish();
                break;
            case R.id.btn_comment_navigation_menu:
                //弹出菜单
                break;
            case R.id.btn_comment_send:
                send();
                //发表评论
                break;
            default:
                break;
        }
    }

    //设置列表
    private void setList(){
        adapter=new CommentListAdapter();
        listViewComment.setAdapter(adapter);
    }

    private void send(){
        mCommentsAPI.create(getEdit(),statuID,true,creatLis);
    }


    private String getEdit() {
        return editText.getText().toString();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }





    class CommentListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater.inflate(R.layout.item_comments, parent, false);
                holder = new ViewHolder();
                holder.user_image = (ImageView) convertView.findViewById(R.id.portrait_comment_status);
                holder.user_name = (TextView) convertView.findViewById(R.id.user_name_comment_status);
                holder.user_level= (ImageView) convertView.findViewById(R.id.account_level_comment_status);
                holder.like= (CheckBox) convertView.findViewById(R.id.like_comment);
                holder.time= (TextView) convertView.findViewById(R.id.time_comment_status);
                holder.commentContent= (TextView) convertView.findViewById(R.id.text_comment_content);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            Comment comment=commentList.get(position);

            ImageLoader.ImageListener listener = imageLoader.getImageListener(holder.user_image, R.mipmap.l_xin, R.drawable.banner_default);      //获取头像
            imageLoader.get(comment.user.profile_image_url,listener);

            holder.user_name.setText(comment.user.name);
            holder.time.setText(Tools.recodeTime(comment.created_at));
            if(comment.reply_comment!=null&&comment.reply_comment.text.length()>0){
                Comment re=comment.reply_comment;
                holder.commentContent.setText(comment.text+"\n\t\t@"+re.user.name+":"+re.text);
            }else {
                holder.commentContent.setText(comment.text);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView user_image, user_level;
            TextView user_name, time, commentContent;
            CheckBox like;
        }
    }

    private RequestListener mListener=new RequestListener() {
        @Override
        public void onComplete(String s) {
            if(!TextUtils.isEmpty(s)){
                CommentList comments = CommentList.parse(s);
                if(comments != null && comments.total_number > 0){
                    Toast.makeText(CommentActivity.this,
                            "获取评论成功, 条数: " + comments.commentList.size(),
                            Toast.LENGTH_LONG).show();
                    commentList=comments.commentList;
                    setList();          //数据获取成功，初始化适配器
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(CommentActivity.this,"评论获取失败",
                    Toast.LENGTH_LONG).show();
        }
    };

    private RequestListener creatLis=new RequestListener() {
        @Override
        public void onComplete(String s) {
            if(!TextUtils.isEmpty(s)){
                Toast.makeText(CommentActivity.this,"评论成功",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    };

}
