package com.example.loveextra.microblog.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveextra.microblog.models.Status;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.legacy.StatusesAPI;
import com.example.loveextra.microblog.models.ErrorInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class PublishIdeaActivity extends Activity implements View.OnClickListener,View.OnLongClickListener{


    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;


    private TextView bnCancel;
    private Button bnSend;
    private EditText editText;
    private ImageView bnImage,bnAt,bnTrend,bnEmotion,bnMore,display;
    private ImageButton bndelete;


    private static final String TAG = "123";


    private Bitmap tempBitmap;


    private static final String IMAGE_UNSPECIFIED = "image/*";


    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 4;




    public void init(){
        bnCancel=(TextView)findViewById(R.id.btn_publish_idea_navigation_cancel);
        bnSend=(Button)findViewById(R.id.btn_publish_idea_navigation_send);
        bndelete=(ImageButton)findViewById(R.id.btn_publish_idea_delete);
        editText=(EditText)findViewById(R.id.edit_publish_idea);
        bnImage=(ImageView)findViewById(R.id.btn_publish_idea_tool_image);
        bnAt=(ImageView)findViewById(R.id.btn_publish_idea_tool_at);
        bnTrend=(ImageView)findViewById(R.id.btn_publish_idea_tool_trend);
        bnEmotion=(ImageView)findViewById(R.id.btn_publish_idea_tool_emotion);
        bnMore=(ImageView)findViewById(R.id.btn_publish_idea_tool_more);
        display=(ImageView)findViewById(R.id.display_publish_idea);


        bnCancel.setOnClickListener(this);
        bnSend.setOnClickListener(this);
        bndelete.setOnClickListener(this);
        editText.addTextChangedListener(editlis);


        bnImage.setOnClickListener(this);
        bnAt.setOnClickListener(this);
        bnTrend.setOnClickListener(this);
        bnEmotion.setOnClickListener(this);
        bnMore.setOnClickListener(this);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constant.APP_KEY, mAccessToken);

        display.setOnLongClickListener(this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_idea);
        init();

    }






    private String getEdit(){
        String str=editText.getText().toString();
        return str;
    }


    private TextWatcher editlis=new TextWatcher() {
        private CharSequence wordNum;//记录输入的字数
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(getEdit().length()>0||tempBitmap!=null){
                bnSend.setEnabled(true);
            }else {
                bnSend.setEnabled(false);
            }
        }
    };



    private void toChooseImage(){
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_publis_idea_image);
        dialog.setTitle("请选择一张图片");
        ImageButton bnCamera=(ImageButton)dialog.findViewById(R.id.btn_image_from_camera);
        ImageButton bnFile=(ImageButton)dialog.findViewById(R.id.btn_image_from_file);
        bnCamera.setOnClickListener(this);
        bnFile.setOnClickListener(this);
        dialog.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {


                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                Log.e(TAG, "开始压缩");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
                Log.e(TAG, "压缩完成");
//                 将Bitmap设定到ImageView

                tempBitmap=bitmap;
                display.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(PublishIdeaActivity.this, "发送一送微博成功, id = " + status.id, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PublishIdeaActivity.this, response, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(PublishIdeaActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_publish_idea_navigation_cancel:
                finish();
                break;
            case R.id.btn_publish_idea_navigation_send:
                if(tempBitmap!=null){       //如果选中了图片，则发带图片微博
                    mStatusesAPI.upload(getEdit(),tempBitmap,null,null,mListener);
                    tempBitmap=null;
                }else {         //否则发文字微博
                    mStatusesAPI.update(getEdit(), null, null, mListener);
                }
                break;
            case R.id.btn_publish_idea_delete:
                display.setImageResource(R.mipmap.welcome_android_slogan);
                tempBitmap=null;
                bndelete.setVisibility(View.GONE);
                break;
            case R.id.btn_publish_idea_tool_image:
                toChooseImage();
                break;
            case R.id.btn_publish_idea_tool_at:

                break;
            case R.id.btn_publish_idea_tool_trend:

                break;
            case R.id.btn_publish_idea_tool_emotion:

                break;
            case R.id.btn_publish_idea_tool_more:

                break;

            //对话框button监听
            case R.id.btn_image_from_file:

                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);

                break;
            case R.id.btn_image_from_camera:

                Log.i(TAG, "相机");
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                        getExternalStorageDirectory(), "temp.jpg")));
                startActivityForResult(intent, CAMERA_REQUEST_CODE);

                break;


            default:
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.ACTION_DOWN:
                if (getEdit().length()>0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("当前正在编辑，退出将丢失");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.display_publish_idea:
                bndelete.setVisibility(View.VISIBLE);
                Log.i(TAG, "长按");
                break;
            default:
                break;
        }
        return true;
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
