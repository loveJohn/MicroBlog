package com.example.loveextra.microblog.Activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.loveextra.microblog.Adapter.ListStatusAdapter;
import com.example.loveextra.microblog.Adapter.NineGridListAdapter;
import com.example.loveextra.microblog.OpenAPI.CommentsAPI;
import com.example.loveextra.microblog.OpenAPI.StatusesAPI;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.Util.Tools;
import com.example.loveextra.microblog.models.Comment;
import com.example.loveextra.microblog.models.CommentList;
import com.example.loveextra.microblog.models.Status;
import com.example.loveextra.microblog.models.StatusList;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class AtMeActivity extends AppCompatActivity {

    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    private CommentsAPI mCommentsAPI;

//    private StatusesAPI mStatusesAPI;

    private ListView listView;
    private CommentListAdapter adapter;

    private LayoutInflater inflater;

    private MaterialRefreshLayout materialRefreshComment;

    private ArrayList<Comment> commentArrayList;

    LruCache<String, Bitmap> mNetCache;

    private RequestQueue mQueue;
    private ImageLoader imageLoader;

    private int maxSize = 10 * 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_me);
        init();


    }

    private void init() {
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mCommentsAPI = new CommentsAPI(this, Constant.APP_KEY, mAccessToken);


        listView = (ListView) findViewById(R.id.list_at_me);
        materialRefreshComment = (MaterialRefreshLayout) findViewById(R.id.material_refresh_at_me_comment);
        materialRefreshComment.setLoadMore(true);
        materialRefreshComment.finishRefreshLoadMore();
        materialRefreshComment.setMaterialRefreshListener(mlis);
        mQueue = Volley.newRequestQueue(this);

        inflater = LayoutInflater.from(this);


        commentArrayList=new ArrayList<>();

        mNetCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

        imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {

            @Override
            public void putBitmap(String url, Bitmap bitmap) {      //设置缓存区
                if (getBitmap(url) == null) {
                    mNetCache.put(url, bitmap);
                }
            }

            @Override
            public Bitmap getBitmap(String url) {           //从缓存区中取图
                return mNetCache.get(url);
            }
        });

        mCommentsAPI.toME(0L, 0L, 10, 1, CommentsAPI.AUTHOR_FILTER_ALL, CommentsAPI.SRC_FILTER_ALL, mListener);
    }



    private void setAdapter() {
        adapter=new CommentListAdapter();
        listView.setAdapter(adapter);
    }


    private MaterialRefreshListener mlis = new MaterialRefreshListener() {


        @Override
        public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
            materialRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //异步任务
                    materialRefreshLayout.finishRefresh();

                }
            }, 2000);
            //下拉刷新
            mCommentsAPI.toME(0L, 0L, 10, 1, CommentsAPI.AUTHOR_FILTER_ALL, CommentsAPI.SRC_FILTER_ALL, mListener);
        }

        @Override
        public void onfinish() {
            Toast.makeText(AtMeActivity.this, "finish", Toast.LENGTH_LONG).show();
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
            Toast.makeText(AtMeActivity.this, "暂未实现上拉加载更多，敬请期待", Toast.LENGTH_LONG).show();
        }

    };


    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                CommentList comments = CommentList.parse(response);

                Log.i("123","comments"+comments.commentList.size());
                if(comments != null && comments.total_number > 0){
                    Toast.makeText(AtMeActivity.this,
                            "获取评论成功, 条数: " + comments.commentList.size(),
                            Toast.LENGTH_LONG).show();
                    setCommentArrayList(comments.commentList);
                    setAdapter();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            e.printStackTrace();
        }
    };


    class CommentListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commentArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater.inflate(R.layout.item_comments, null);
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
            Comment comment=commentArrayList.get(position);

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

    public void setCommentArrayList(ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }
}

