package com.example.loveextra.microblog.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.loveextra.microblog.Adapter.ListStatusAdapter;
import com.example.loveextra.microblog.Adapter.NineGridListAdapter;
import com.example.loveextra.microblog.models.Status;
import com.example.loveextra.microblog.OpenAPI.StatusesAPI;
import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.AccessTokenKeeper;
import com.example.loveextra.microblog.Util.Constant;
import com.example.loveextra.microblog.models.StatusList;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.LinkedList;

/**
 * Created by LoveExtra on 2016/6/25.
 */
public class HomePageFragment extends Fragment implements View.OnClickListener{

    final int UPDATE=1;
    final int LOAD=2;

    String imageUrl;

    Oauth2AccessToken token;
    RequestQueue mQueue;
    private StatusesAPI statusesAPI;

    private View view;      //homepageFragment的整个布局控件
    private Context context;
    private UserNameFisrtListAdapter userNameadapter;

    private ImageButton bnFriendAttention,bnRadar;
    private RadioButton rdUsername;
    private PopupWindow pop;

    private MaterialRefreshLayout materialRefreshLayout;
    private ListView listView;
//    NineGridListAdapter nineAdapter;
    private ListStatusAdapter adapter;

    private StatusList statuses;
    private LinkedList<Status> tempStatusesList;        //缓存区，保存所有微博
    private LinkedList<Status> statusesList;          //缓存微博list，设置长度为7


    private final String[] usernameFisrtList={
            "首页","好友圈","群微博"
    };

   public static HomePageFragment newInstance(){
       HomePageFragment homePageFragment= new HomePageFragment();
       Bundle bundle=new Bundle();
       return homePageFragment;
   }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getActivity();
        view=inflater.inflate(R.layout.fragment_homepage,container,false);
        init();         //初始化activity

        return view;
    }

    private void init(){        //初始化控件
        Log.i("123","进入主页");

        materialRefreshLayout=(MaterialRefreshLayout) view.findViewById(R.id.material_refresh_homepage);
        listView=(ListView) view.findViewById(R.id.list_status_homepage);
        bnFriendAttention=(ImageButton)view.findViewById(R.id.btn_homepage_navigation_friendattion);
        rdUsername=(RadioButton) view.findViewById(R.id.btn_homepage_navigation_username);
        bnRadar=(ImageButton)view.findViewById(R.id.btn_homepage_navigation_radar);

        bnFriendAttention.setOnClickListener(this);
        bnRadar.setOnClickListener(this);
        rdUsername.setOnCheckedChangeListener(rdlis);

        statusesList=new LinkedList<>();

        token = AccessTokenKeeper.readAccessToken(context);      //获取token

        statusesAPI=new StatusesAPI(context, Constant.APP_KEY,token);       //获取接口类对象

        mQueue = Volley.newRequestQueue(context);

        materialRefreshLayout.setLoadMore(true);
        materialRefreshLayout.finishRefreshLoadMore();
        materialRefreshLayout.setMaterialRefreshListener(mlis);

        statusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);         //获取数据，通知监听
    }

    @Override
    public void onStart() {
        super.onStart();
        statusesList=new LinkedList<>();            //使用LinkedList对上拉加载和下拉刷新的数据填充数据更快
        updateStatus();          //进入fragment，默认刷新微博
    }


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
            statusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);         //获取数据，通知监听
        }

        @Override
        public void onfinish() {
            Toast.makeText(context, "finish", Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "暂未实现上拉加载更多，敬请期待", Toast.LENGTH_LONG).show();
        }
    };


    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                updateStatus();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }




    //用户名点击按钮radiobutton监听
    private CompoundButton.OnCheckedChangeListener rdlis=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.btn_homepage_navigation_username:
                    if(isChecked) {
                        popupUsernameWindow();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateStatus(){
        statusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);         //获取数据，通知监听
    }

    private void createAdapter(){
//        nineAdapter=new NineGridListAdapter(context,statusesList);


        adapter=new ListStatusAdapter(context,statusesList);
        Log.i("123","创建适配");
        listView.setAdapter(adapter);
//        listView.setAdapter(nineAdapter);
        Log.i("123","列表适配完毕");

    }






    private void popupUsernameWindow(){
        View viewPop=LayoutInflater.from(context).inflate(R.layout.popupwindow_layout_homepage_username,null);
        ListView firstList= (ListView) viewPop.findViewById(R.id.listview_homepage_username_homepage_friend_group);
        userNameadapter=new UserNameFisrtListAdapter();
        firstList.setAdapter(userNameadapter);
        firstList.setOnItemClickListener(listLis);
        pop=new PopupWindow(viewPop,rdUsername.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT,false);
        pop.setAnimationStyle(R.style.PopupAnimation);
        pop.update();
        pop.showAsDropDown(rdUsername);

    }


    private AdapterView.OnItemClickListener listLis=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    updateStatus();                     //刷新微博数据
                    Toast.makeText(context,usernameFisrtList[position],Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context,usernameFisrtList[position],Toast.LENGTH_SHORT).show();
                    break;
            }
            rdUsername.setChecked(false);
            pop.dismiss();
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_homepage_navigation_friendattion:
                Toast.makeText(context,"特别关注",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_homepage_navigation_radar:
                Toast.makeText(context,"雷达",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    class UserNameFisrtListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return usernameFisrtList.length;
        }

        @Override
        public Object getItem(int position) {
            return usernameFisrtList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                LayoutInflater inflater=LayoutInflater.from(context);
                convertView=inflater.inflate(R.layout.item_homepage_username_first,null);
                viewHolder=new ViewHolder();
                viewHolder.title= (TextView) convertView.findViewById(R.id.tv_homepage_username_first_list_item);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(usernameFisrtList[position]);
            return convertView;
        }

        class ViewHolder{
            TextView title;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private RequestListener mListener=new RequestListener() {
        @Override
        public void onComplete(String s) {
            Log.i("123",s.toString());
            if (!TextUtils.isEmpty(s)) {
                if (s.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    statuses = StatusList.parse(s);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(context, "获取微博信息流成功,条数: " + statuses.statusList.size(), Toast.LENGTH_LONG).show();
                    }

                    statusesList=statuses.statusList;


//                    if(statusesList.size()==0) {        //如果缓存为0，则将获取的10条微博显示
//                        for (Status st:statuses.statusList){
//                            statusesList.add(st);
//                        }
//                    }else {                     //否则
//                        int m=-1;
//                        for (int i=0;i<statuses.statusList.size();i++) {        //依次查获取的10条数据
//                            if (statuses.statusList.get(i).id.equals(statusesList.getFirst())) {     //如果从第i条信息与list的最后一条相同
//                                m=i;
//                            }
//                            if(m>0&&m<statuses.statusList.size()){                     //
//                                for (int j=m-1;j>=0;j--){
//                                    statusesList.addFirst(statuses.statusList.get(j));
//                                }
//                            }
//                        }
//                    }

                    Log.i("123","微博获取成功");

                    createAdapter();            //创建适配器
                }else {
                    Toast.makeText(context,s, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

            Log.i("123","微博获取异常"+e.getMessage());
        }
    };


}
