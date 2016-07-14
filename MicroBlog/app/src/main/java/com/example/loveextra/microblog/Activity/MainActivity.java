package com.example.loveextra.microblog.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.loveextra.microblog.Fragment.AddFragment;
import com.example.loveextra.microblog.Fragment.DiscoverFragment;
import com.example.loveextra.microblog.Fragment.HomePageFragment;
import com.example.loveextra.microblog.Fragment.MessageFragment;
import com.example.loveextra.microblog.Fragment.ProfileFragment;
import com.example.loveextra.microblog.R;


public class MainActivity extends Activity {

    private Fragment currentFragment;
    private FragmentManager fm;


    private HomePageFragment homePageFragment;
    private MessageFragment messageFragment;
    private DiscoverFragment discoverFragment;
    private ProfileFragment profileFragment;
    private AddFragment addFragment;


    private RadioButton rdMainPageHomepage,rdMainPageMessage,rdMainPageDiscover,rdMainPageProfile;
    private ImageButton rdMainPageAdd;
    private LinearLayout bnMainpageMenu;
    private RelativeLayout bnMainpageClose;
    private RadioGroup rdGroup;


    public void init(){
        Log.i("123","创建main");
        rdMainPageHomepage=(RadioButton)findViewById(R.id.btn_mainpage_homepage);
        rdMainPageAdd=(ImageButton)findViewById(R.id.btn_mainpage_add);
        bnMainpageMenu=(LinearLayout)findViewById(R.id.btn_mainpage_menu);
        bnMainpageClose=(RelativeLayout)findViewById(R.id.btn_mainpage_close);
        rdGroup=(RadioGroup)findViewById(R.id.rgroup_mainpage);

        rdGroup.setOnCheckedChangeListener(grouplis);       //对group监听

        rdMainPageAdd.setOnClickListener(bnlis);
        bnMainpageClose.setOnClickListener(bnlis);

        fm=getFragmentManager();            //获取fragment管理者
        homePageFragment=new HomePageFragment().newInstance();
        messageFragment=new MessageFragment().newInstance();
        discoverFragment=new DiscoverFragment().newInstance();
        profileFragment=new ProfileFragment().newInstance();
        addFragment=new AddFragment().newInstance();


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("123","设置默认");
        switchFragment(homePageFragment);       //设置默认fragment
        Log.i("123","设置默认");
        bnMainpageClose.setVisibility(View.GONE);
        bnMainpageMenu.setVisibility(View.VISIBLE);
        rdMainPageHomepage.setChecked(true);

    }


    private RadioGroup.OnCheckedChangeListener grouplis=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.btn_mainpage_homepage:
                    if(currentFragment==homePageFragment){

                    }else {
                        switchFragment(homePageFragment);
                    }

                    Log.i("123","调用home");
                    break;
                case R.id.btn_mainpage_message:
                    switchFragment(messageFragment);
                    Log.i("123","调用mess");
                    break;
                case R.id.btn_mainpage_discover:
                    switchFragment(discoverFragment);
                    Log.i("123","调用dis");
                    break;
                case R.id.btn_mainpage_profile:
                    switchFragment(profileFragment);
                    Log.i("123","调用pro");
                    break;
                default:
                    break;
            }
        }
    };


    private View.OnClickListener bnlis=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_mainpage_add:
                    bnMainpageMenu.setVisibility(View.GONE);
                    bnMainpageClose.setVisibility(View.VISIBLE);
                    switchAddFragment(addFragment);        //跳转add
                    break;
                case R.id.btn_mainpage_close:
                    bnMainpageClose.setVisibility(View.GONE);
                    bnMainpageMenu.setVisibility(View.VISIBLE);
                    switchFragment(currentFragment);        //跳转add
                default:
                    break;
            }
        }
    };




    private void switchFragment(Fragment to){
        Log.i("123","调用");
        FragmentTransaction transaction=fm.beginTransaction();      //重新实例
        currentFragment=to;
        transaction.replace(R.id.fragment_content,to);
        transaction.commit();
    }

    private void switchAddFragment(Fragment to){
        FragmentTransaction transaction=fm.beginTransaction();      //重新实例
        transaction.replace(R.id.fragment_content,to);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("123","destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("123","pause");
    }
}
