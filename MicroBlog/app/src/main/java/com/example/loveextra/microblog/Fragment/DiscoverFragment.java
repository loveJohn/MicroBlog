package com.example.loveextra.microblog.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.loveextra.microblog.Activity.EssayActivity;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.WeChatEssayAPI;
import com.example.loveextra.microblog.models.Essay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by LoveExtra on 2016/6/27.
 */
public class DiscoverFragment extends Fragment {

    View view;
    Context context;

    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    LruCache<String, Bitmap> mNetCache;

    EditText editSearch;
    ImageButton bnVoice;
    ViewPager viewPager;

    ListView listView;

    LayoutInflater inflater;

    ArrayList<Essay> essayArrayList;

    int maxSize = 10 * 1024 * 1024;

    public static DiscoverFragment newInstance(){
        DiscoverFragment discoverFragment=new DiscoverFragment();
        return discoverFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getActivity();
        view=inflater.inflate(R.layout.fragment_discover,container,false);
        init();
        return view;
    }

    public void init(){
        essayArrayList=new ArrayList<>();
        inflater=LayoutInflater.from(context);
        editSearch= (EditText) view.findViewById(R.id.edit_discover_search);
        bnVoice= (ImageButton) view.findViewById(R.id.btn_discover_search_voice);
        listView=(ListView)view.findViewById(R.id.list_view_discover);

        editSearch.addTextChangedListener(textLis);
        bnVoice.setOnClickListener(bnLis);
        mQueue = Volley.newRequestQueue(context);

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


    private View.OnClickListener bnLis=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_discover_search_voice:
                    EssayTask task=new EssayTask();
                    task.execute(getEdit());
                    break;
                default:
                    break;
            }
        }
    };


    private AdapterView.OnItemClickListener mListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String url=essayArrayList.get(position).getUrl();
            Intent toWebEssay=new Intent(context, EssayActivity.class);
            Bundle b=new Bundle();
            b.putString("URL",url);
            toWebEssay.putExtras(b);
            startActivity(toWebEssay);
        }
    };

    class EssayTask extends AsyncTask<String,Void,ArrayList<Essay>>{

        @Override
        protected ArrayList<Essay> doInBackground(String... params) {
            ArrayList<Essay> list=new ArrayList<>();
            try {
                list=WeChatEssayAPI.getEssay(getEdit());
            } catch (IOException e) {
                e.printStackTrace();
            }
            setEssayArrayList(list);
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Essay> essays) {
            super.onPostExecute(essays);

            EssayAdapter adapter=new EssayAdapter();
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(mListener);
        }
    }

    private TextWatcher textLis=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(getEdit().length()>0){
                bnVoice.setEnabled(true);
            }else {
                bnVoice.setEnabled(false);
            }
        }
    };


    public void setEssayArrayList(ArrayList<Essay> essayArrayList) {
        this.essayArrayList = essayArrayList;
    }

    private String getEdit(){
        return editSearch.getText().toString().trim();
    }



    class EssayAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return essayArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return essayArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null||convertView.getTag()==null){
                convertView= inflater.inflate(R.layout.item_discover_list,null);
                holder=new ViewHolder();
                holder.contentImage=(ImageView)convertView.findViewById(R.id.content_image_essay_discover);
                holder.title=(TextView)convertView.findViewById(R.id.title_essay_discover);
                holder.type=(TextView)convertView.findViewById(R.id.type_discover);
                holder.gongzhonghao=(TextView)convertView.findViewById(R.id.gongzhonghao_discover);
                holder.time=(TextView)convertView.findViewById(R.id.time_discover);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            Essay essay=essayArrayList.get(position);
            ImageLoader.ImageListener listener = imageLoader.getImageListener(holder.contentImage, R.mipmap.l_xin, R.drawable.banner_default);      //获取头像
            imageLoader.get(essay.getContentImg(),listener);


            Log.i("123","适配器中"+essay.toString());
            holder.title.setText(essay.getTitle()+"");
            holder.type.setText(essay.getTypeName()+"");
            holder.gongzhonghao.setText(essay.getUserName()+"");
            holder.time.setText(essay.getDate()+"");
            return convertView;
        }

        class ViewHolder{
            ImageView contentImage;
            TextView title,type,gongzhonghao,time;
        }
    }
}
