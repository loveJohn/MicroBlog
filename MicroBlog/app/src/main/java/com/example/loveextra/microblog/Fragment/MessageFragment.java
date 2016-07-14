package com.example.loveextra.microblog.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveextra.microblog.Activity.AtMeActivity;
import com.example.loveextra.microblog.R;

/**
 * Created by LoveExtra on 2016/6/26.
 */
public class MessageFragment extends Fragment implements View.OnClickListener{

    Context context;
    View view;

    TextView bnFindGroup;
    ImageButton bnNewChat;
    EditText editSearch;
    RelativeLayout bnAtMe,bnComments,bnGood;
    ListView listViewMessage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public static MessageFragment newInstance(){
        MessageFragment messageFragment = new MessageFragment();
        Bundle b = new Bundle();
        return messageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getActivity();
        view=inflater.inflate(R.layout.fragment_message,container,false);
        init();
        return view;
    }


    private void init(){        //初始化控件
        bnFindGroup= (TextView) view.findViewById(R.id.btn_message_navigation_findgroup);
        bnNewChat=(ImageButton)view.findViewById(R.id.btn_message_navigation_new_chat);
        editSearch=(EditText)view.findViewById(R.id.edit_message_search);
        bnAtMe= (RelativeLayout) view.findViewById(R.id.btn_message_contents_at_me);
        bnComments= (RelativeLayout) view.findViewById(R.id.btn_message_contents_comments);
        bnGood= (RelativeLayout) view.findViewById(R.id.btn_message_contents_good);
        listViewMessage= (ListView) view.findViewById(R.id.list_view_message);
        bnFindGroup.setOnClickListener(this);
        bnAtMe.setOnClickListener(this);
        bnComments.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_message_navigation_findgroup:
                Toast.makeText(context,"找群",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_message_contents_at_me:
                Intent toAtMeIntent=new Intent(context, AtMeActivity.class);
                context.startActivity(toAtMeIntent);
                break;
            case R.id.btn_message_contents_comments:
                break;
            case R.id.btn_message_contents_good:
                break;
            default:
                break;
        }
    }
}
