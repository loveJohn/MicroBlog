package com.example.loveextra.microblog.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.Tools;
import com.example.loveextra.microblog.models.Status;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by LoveExtra on 2016/7/2.
 */
public class CustomGridViewAdapter extends BaseAdapter {

    public static final String PIC_URL_DATA = "picUrlList";
    public static final String PIC_POSITION = "picPosition";

    private int imageWidth;
    private int imageHeight;

    private ImageView largePicture;
    private Button downloadPicture;
    private ProgressDialog proDialog;

    private Context mContext;
    private RequestQueue mQueue;
    private ImageLoader imageLoader;

    HttpHandler handler;
    BitmapUtils bitmapUtils;
    HttpUtils httpUtils;

    private String imageUrl;

    private LayoutInflater inflater;
    private ArrayList<String> mArrayList;

    public CustomGridViewAdapter(Context context){
        mContext=context;
        init();
    }


    public CustomGridViewAdapter(Context context, ArrayList<String> arrayList){
        mContext=context;
        mArrayList=arrayList;
        init();
    }

    public void init(){
        inflater = LayoutInflater.from(mContext);
        mQueue = Volley.newRequestQueue(mContext);
        bitmapUtils=new BitmapUtils(mContext);
        httpUtils=new HttpUtils();
        int arrayListSize=mArrayList.size();
        Log.i("123","进入grid适配");
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

/*        if(arrayListSize==1){
            imageWidth = metrics.widthPixels;
            imageHeight = metrics.heightPixels/2;
        }else if(arrayListSize==2){
            imageWidth = metrics.widthPixels/2;
            imageHeight = metrics.heightPixels/2;
        }else if(arrayListSize==3){
            imageWidth = metrics.widthPixels/3;
            imageHeight = metrics.heightPixels/2;
        }else if(arrayListSize>3&&arrayListSize<=6){
            imageWidth = metrics.widthPixels/3;
            imageHeight = metrics.heightPixels/4;
        }else if(arrayListSize>6){
            imageWidth = metrics.widthPixels/3;
            imageHeight = metrics.heightPixels/5;
        }*/

        imageWidth = metrics.widthPixels/3;
        imageHeight = metrics.heightPixels/5;
    }


    private View.OnClickListener downLis=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_homepage_status_picture:      //单击下载按钮，下载图片
                    //下载图片
                    if(largePicture==null){
                        Toast.makeText(mContext, "抱歉,该图片无法下载", Toast.LENGTH_SHORT).show();
                    }else if(!isSDCardCanUser()){
                        Toast.makeText(mContext, "未检测到储存卡", Toast.LENGTH_SHORT).show();
                    }else {
                        handler=httpUtils.download(imageUrl, Environment.getExternalStorageDirectory()+"/"+imageUrl.substring(imageUrl.length()-10), true, true, new RequestCallBack<File>() {

                            @Override
                            public void onStart() {
                                super.onStart();
                                proDialog=new ProgressDialog(mContext);
                                proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                proDialog.setMax(100);
                                proDialog.show();
                            }


                            @Override
                            public void onLoading(long total, long current, boolean isUploading) {
                                super.onLoading(total, current, isUploading);
                                if(isUploading){
                                    proDialog.setTitle("下载中");
                                }else {
                                    proDialog.setTitle("下载暂停");
                                }
                                proDialog.setProgress((int) (current*100/total));
                            }

                            @Override
                            public void onSuccess(ResponseInfo<File> responseInfo) {
                                Toast.makeText(mContext,"下载完成",Toast.LENGTH_SHORT).show();
                                proDialog.dismiss();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                Toast.makeText(mContext,"下载失败",Toast.LENGTH_SHORT).show();
                                proDialog.dismiss();
                            }

                        });
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private class BitmapCache implements ImageLoader.ImageCache {
        // 图片缓存，避免oom异常
        private LruCache<String, Bitmap> mNetCache;

        public BitmapCache() {          //设置图片缓存区
            int maxSize = 10 * 1024 * 1024;
            mNetCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }


        @Override
        public Bitmap getBitmap(String url) {
            Log.i("print", "get "+url);

            Bitmap bitmap = mNetCache.get(url);
            if(bitmap != null){
                Log.i("print", "width "+bitmap.getWidth());
                Log.i("print", "height "+bitmap.getHeight());
            }

            return mNetCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmp) {
            Log.i("print", "put "+url);
            if(getBitmap(url) == null){
                mNetCache.put(url, bitmp);
            }
        }

    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder holder;
        if(convertView==null||convertView.getTag()==null){
            convertView = inflater.inflate(R.layout.item_grid_view_status_list, parent, false);
            holder = new GridViewHolder();
            holder.networkImageView=(NetworkImageView)convertView.findViewById(R.id.net_image_grid_view_list_status);
            convertView.setTag(holder);
        }else {
            holder= (GridViewHolder) convertView.getTag();
        }
        NetworkImageView networkImageView=holder.networkImageView;
        ViewGroup.LayoutParams layoutParams=networkImageView.getLayoutParams();
        layoutParams.width=imageWidth;
        layoutParams.height=imageHeight;
        networkImageView.setLayoutParams(layoutParams);

        networkImageView.setErrorImageResId(R.drawable.banner_default);
        imageLoader = new ImageLoader(mQueue, new BitmapCache());
        networkImageView.setImageUrl(mArrayList.get(position),imageLoader);
        networkImageView.setOnClickListener(new OnImageClickListener(position));

        return convertView;
    }

    private class OnImageClickListener implements View.OnClickListener {

        private int currentPosition;

        public OnImageClickListener(int currentPosition) {
            this.currentPosition = currentPosition;
        }

        @Override
        public void onClick(View v) {
            imageUrl=mArrayList.get(currentPosition);
            Dialog dialog=new Dialog(mContext, R.style.dialog);
            dialog.setContentView(R.layout.dialog_homepage_picture);
            largePicture=(ImageView)dialog.findViewById(R.id.im_homepage_status_picture);
            downloadPicture=(Button)dialog.findViewById(R.id.btn_homepage_status_picture);
            downloadPicture.setOnClickListener(downLis);
            bitmapUtils.display(largePicture,imageUrl);
            dialog.setCancelable(true);
            dialog.show();
        }
    }



    class GridViewHolder{
        NetworkImageView networkImageView;

    }


    /**
     * 判断sdcard卡是否可用
     *
     * @return 布尔类型 true 可用 false 不可用
     */
    private boolean isSDCardCanUser() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
