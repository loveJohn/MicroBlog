package com.example.loveextra.microblog.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.loveextra.microblog.Activity.CommentActivity;
import com.example.loveextra.microblog.CustomView.CustomGridView;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.Util.Tools;
import com.example.loveextra.microblog.legacy.FavoritesAPI;
import com.example.loveextra.microblog.models.Status;
import com.example.loveextra.microblog.models.User;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by LoveExtra on 2016/7/8.
 */
public class ListStatusSelfAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<Status> mLinkedList;
    LruCache<String, Bitmap> mNetCache;

    private RequestQueue mQueue;
    private ImageLoader imageLoader;

    Oauth2AccessToken token;
    FavoritesAPI favoritesAPI;

    private LayoutInflater inflater;

    private int selectPosition=-1;
    private long selectStatusId=-1;

    CheckBox collect;
    CheckBox attention;

    int maxSize = 10 * 1024 * 1024;

    public ListStatusSelfAdapter(Context context){
        mContext=context;
        init();
    }




    public ListStatusSelfAdapter(Context context,LinkedList<Status>  linkedList){
        mContext=context;
        mLinkedList=linkedList;
        init();
    }

    public void init(){
        inflater = LayoutInflater.from(mContext);
        mQueue = Volley.newRequestQueue(mContext);

        token= AccessTokenKeeper.readAccessToken(mContext);

        Log.i("123","进入list适配");
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
    }




    @Override
    public int getCount() {
        return getListSize(mLinkedList);
    }

    @Override
    public Object getItem(int position) {
        return mLinkedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        setSelectPosition(position);


        ListViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.item_status_list, parent, false);
            holder = new ListViewHolder();
            holder.portrait=(ImageView)convertView.findViewById(R.id.portrait_homepage_status);
            holder.level=(ImageView)convertView.findViewById(R.id.account_level_homepage_status);
            holder.menu=(ImageButton) convertView.findViewById(R.id.img_btn_content_menu);
            holder.userName=(TextView)convertView.findViewById(R.id.user_name_homepage_status);
            holder.time=(TextView)convertView.findViewById(R.id.time_homepage_status);
            holder.shareDes=(TextView)convertView.findViewById(R.id.share_text_homepage_status);
            holder.statusDes=(TextView)convertView.findViewById(R.id.content_homepage_status);
            holder.shares=(TextView)convertView.findViewById(R.id.btn_homepage_share);
            holder.comments=(TextView)convertView.findViewById(R.id.btn_homepage_comment);
            holder.likes=(CheckBox)convertView.findViewById(R.id.btn_homepage_like);
            holder.gridView=(CustomGridView)convertView.findViewById(R.id.grid_view_status_list);
            convertView.setTag(holder);
            ((ViewGroup)convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        } else {
            holder = (ListViewHolder) convertView.getTag();
        }
        final Status itemStatus=mLinkedList.get(position);
        User itemUser=itemStatus.user;
        if(itemUser==null){
            Log.i("123","itemUser为空");
        }
        long sId= Long.parseLong(itemStatus.id);
        setSelectStatusId(sId);


        ImageLoader.ImageListener listener = imageLoader.getImageListener(holder.portrait, R.mipmap.l_xin, R.drawable.banner_default);      //获取头像
//        imageLoader.get(itemUser.profile_image_url,listener);

        holder.userName.setText(itemUser.name);         //用户名
        holder.time.setText(Tools.subTime(itemStatus.created_at)+"    来自"+Tools.codeSource(itemStatus.source));     //时间戳
        holder.shareDes.setText(itemStatus.text);       //微博正文

        holder.menu.setOnClickListener(bnLis);

        if(itemStatus.retweeted_status!=null){
            holder.shareDes.setText("@"+itemStatus.retweeted_status.user.name+":"+itemStatus.retweeted_status.text);
        }
        holder.shares.setText(itemStatus.reposts_count+"");
        holder.comments.setText(itemStatus.comments_count+"");
        holder.likes.setText(itemStatus.attitudes_count+"");

        holder.shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent toShareIntent=new Intent(mContext,ShareActivity.class);
//                mContext.startActivity(toShareIntent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCommentIntent = new Intent(mContext, CommentActivity.class);
                toCommentIntent.putExtra("statusId", itemStatus.id);
                mContext.startActivity(toCommentIntent);
            }
        });

        holder.likes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //点赞
//                    buttonView.setText(itemStatus.attitudes_count+1+"");
                }else {
                    //取消点赞
//                    buttonView.setText(itemStatus.attitudes_count+"");
                }
            }
        });

        ArrayList<String> middleImageUrl= new ArrayList<String>();
        if(itemStatus.pic_urls!=null&&itemStatus.pic_urls.size()!=0){       //如果图片地址组不为空
            for (int i = 0; i < itemStatus.pic_urls.size(); i++) {
                String picUrl = itemStatus.pic_urls.get(i);
                String midPicUrl = picUrl.replace("thumbnail","bmiddle");
                middleImageUrl.add(midPicUrl);
            }

        }

        CustomGridViewAdapter adapter=new CustomGridViewAdapter(mContext,middleImageUrl);

        holder.gridView.setAdapter(adapter);     //创建GridView 适配器

        return convertView;
    }

    class ListViewHolder {
        ImageView portrait,level;
        ImageButton menu;
        TextView userName,time,shareDes,statusDes,shares,comments;
        CheckBox likes;
        CustomGridView gridView;

    }


    private View.OnClickListener bnLis=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_btn_content_menu:
                    popDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener checkLis=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.btn_homepage_content_menu_dialog_collect:
                    if(isChecked){
                        collect.setText("取消收藏");
                        favoritesAPI=new FavoritesAPI(mContext, Constant.APP_KEY,token);
                        favoritesAPI.create(getSelectStatusId(),mListener);
                    }else {
                        collect.setText("收藏");
                        favoritesAPI=new FavoritesAPI(mContext, Constant.APP_KEY,token);
                        favoritesAPI.destroy(getSelectStatusId(),mListener);
                    }
                    break;
                case R.id.btn_homepage_content_menu_dialog_attention:

                    break;
                default:
                    break;
            }
        }
    };



    private void popDialog(){
        AlertDialog.Builder builer = new AlertDialog.Builder(mContext);
        //判断
        builer.setItems(new String[]{"收藏", "关注"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:

                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        });

        builer.create().show();


    }

    private int getListSize(LinkedList<Status> mLinkedList){
        if(mLinkedList==null||mLinkedList.size()==0){
            return 0;
        }else {
            return mLinkedList.size();
        }
    }


    public LinkedList<Status> getmLinkedList() {
        return mLinkedList;
    }

    public void setmLinkedList(LinkedList<Status> mLinkedList) {
        this.mLinkedList = mLinkedList;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public long getSelectStatusId() {
        return selectStatusId;
    }

    public void setSelectStatusId(long selectStatusId) {
        this.selectStatusId = selectStatusId;
    }

    private RequestListener mListener=new RequestListener() {
        @Override
        public void onComplete(String s) {
            if (!TextUtils.isEmpty(s)) {
                Log.i("123",s.toString());
            }else {
                Toast.makeText(mContext,"操作失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.i("123","操作失败".toString());
        }
    };
}
