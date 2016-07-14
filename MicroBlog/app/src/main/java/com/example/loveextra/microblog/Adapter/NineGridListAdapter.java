package com.example.loveextra.microblog.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.loveextra.microblog.Activity.CommentActivity;
import com.example.loveextra.microblog.CustomView.NineGridTestLayout;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.Tools;
import com.example.loveextra.microblog.models.Status;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by LoveExtra on 2016/7/2.
 */
public class NineGridListAdapter extends BaseAdapter{


    private Context mContext;
    private RequestQueue mQueue;
    ImageLoader imageLoader;
    private LinkedList<Status> mLinkedList;
    protected LayoutInflater inflater;

    public NineGridListAdapter(Context context,LinkedList<Status> list) {
        mContext = context;
        mLinkedList=list;
        mQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });
        inflater = LayoutInflater.from(mContext);
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.item_status_list, parent, false);
            holder = new ViewHolder();
            holder.layout = (NineGridTestLayout) convertView.findViewById(R.id.layout_nine_grid);
            holder.portrait=(ImageView)convertView.findViewById(R.id.portrait_homepage_status);
            holder.level=(ImageView)convertView.findViewById(R.id.account_level_homepage_status);
            holder.userName=(TextView)convertView.findViewById(R.id.user_name_homepage_status);
            holder.time=(TextView)convertView.findViewById(R.id.time_homepage_status);
            holder.shareDes=(TextView)convertView.findViewById(R.id.share_text_homepage_status);
            holder.statusDes=(TextView)convertView.findViewById(R.id.content_homepage_status);
            holder.shares=(TextView)convertView.findViewById(R.id.btn_homepage_share);
            holder.comments=(TextView)convertView.findViewById(R.id.btn_homepage_comment);
            holder.likes=(CheckBox)convertView.findViewById(R.id.btn_homepage_like);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.layout.setIsShowAll(false);
        holder.layout.setSpacing(5);


        if(mLinkedList.get(position).pic_urls!=null&&mLinkedList.get(position).pic_urls.size()!=0) {
            holder.layout.setUrlList(changeUrlString(mLinkedList.get(position).pic_urls));
            //此处获取微博图片
        }else{
            holder.layout.removeAllViews();
        }
        Log.i("123","获取图片");
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.portrait,
                R.mipmap.l_xin, R.drawable.banner_default);
        Log.i("123","微博条数"+mLinkedList.size());
//        Log.i("123",mLinkedList.get(position).user.profile_image_url);
        imageLoader.get(mLinkedList.get(position).user.profile_image_url,listener);        //获取头像
        holder.userName.setText(mLinkedList.get(position).user.name);
        holder.time.setText(Tools.subTime(mLinkedList.get(position).created_at)+"    来自"+Tools.codeSource(mLinkedList.get(position).source));
        holder.shareDes.setText(mLinkedList.get(position).text);

        if(mLinkedList.get(position).retweeted_status!=null){
            holder.shareDes.setText(mLinkedList.get(position).retweeted_status.text);
        }

        holder.shares.setText(mLinkedList.get(position).reposts_count+"");
        holder.comments.setText(mLinkedList.get(position).comments_count+"");
        holder.likes.setText(mLinkedList.get(position).attitudes_count+"");

        //设置按钮监听
        holder.shareDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"分享",Toast.LENGTH_SHORT).show();

            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"评论",Toast.LENGTH_SHORT).show();
                Intent toCommentIntent=new Intent(mContext, CommentActivity.class);
                mContext.startActivity(toCommentIntent);

            }
        });
        holder.likes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }
            }
        });

        Log.i("123","适配成功");

        return convertView;
    }








    private CompoundButton.OnCheckedChangeListener boxlis=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.btn_homepage_like:
                    if(isChecked){

                    }else {

                    }
            }
        }
    };

    private class ViewHolder {
        NineGridTestLayout layout=new NineGridTestLayout(mContext);
        ImageView portrait,level;
        TextView userName,time,shareDes,statusDes,shares,comments;
        CheckBox likes;


    }

    private String getMiddlePicture(String str){            //将图片地址换成中等清晰图
        return str.replace("thumbnail","bmiddle");

    }

    private String getLargePicture(String str){            //将图片地址换成中等清晰图
        return str.replace("thumbnail","large");

    }


    private ArrayList<String> changeUrlString(ArrayList<String> list){
        ArrayList<String> temp=new ArrayList<>();
        String tempStr=null;
        for (int i=0;i<list.size();i++){
            tempStr=getMiddlePicture(list.get(i));
            temp.add(tempStr);
        }
        return temp;
    }

    private int getListSize(LinkedList<Status> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }


}
