package com.example.loveextra.microblog.CustomView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.ImageLoaderUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.List;

public class NineGridTestLayout extends NineGridLayout implements View.OnClickListener{

    ImageView largePicture;
    Button downloadPicture;
    String imageUrl;
    ProgressDialog proDialog;

    HttpHandler handler;
    BitmapUtils bitmapUtils;
    HttpUtils httpUtils;


    protected static final int MAX_W_H_RATIO = 3;
    Context mContext;

    public NineGridTestLayout(Context context) {
        super(context);
        mContext=context;
        bitmapUtils=new BitmapUtils(context);
        httpUtils=new HttpUtils();
        ViewUtils.inject(this);
    }

    public NineGridTestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        bitmapUtils=new BitmapUtils(context);
        httpUtils=new HttpUtils();
        ViewUtils.inject(this);
    }

    @Override
    protected boolean displayOneImage(final RatioImageView imageView, String url, final int parentWidth) {


        ImageLoaderUtil.playImage(mContext, imageView, url, ImageLoaderUtil.getPhotoImageOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                int newW;
                int newH;
                if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
                    newW = parentWidth / 2;
                    newH = newW * 5 / 3;
                } else if (h < w) {//h:w = 2:3
                    newW = parentWidth * 2 / 3;
                    newH = newW * 2 / 3;
                } else {//newH:h = newW :w
                    newW = parentWidth / 2;
                    newH = h * newW / w;
                }
                setOneImageLayoutParams(imageView, newW, newH);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        return false;
    }

    @Override
    protected void displayImage(RatioImageView imageView, String url) {
        ImageLoaderUtil.getImageLoader(mContext).displayImage(url, imageView, ImageLoaderUtil.getPhotoImageOption());
    }

    private String getLargePicture(String str){            //将图片地址换成中等清晰图
        return str.replace("bmiddle","large");
    }
    @Override
    protected void onClickImage(int i, String url, List<String> urlList) {
//        Toast.makeText(mContext, "点击了图片" + url, Toast.LENGTH_SHORT).show();
        imageUrl=getLargePicture(url);      //图片地址

        //创建对话框
        Dialog dialog=new Dialog(mContext, R.style.dialog);
        dialog.setContentView(R.layout.dialog_homepage_picture);
        largePicture=(ImageView)dialog.findViewById(R.id.im_homepage_status_picture);
        downloadPicture=(Button)dialog.findViewById(R.id.btn_homepage_status_picture);
        downloadPicture.setOnClickListener(this);
        bitmapUtils.display(largePicture,imageUrl);
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_homepage_status_picture:
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


    /**
     * 判断sdcard卡是否可用
     *
     * @return 布尔类型 true 可用 false 不可用
     */
    private boolean isSDCardCanUser() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
