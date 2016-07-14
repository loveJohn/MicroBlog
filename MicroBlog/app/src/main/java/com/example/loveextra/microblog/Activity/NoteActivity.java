package com.example.loveextra.microblog.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.loveextra.microblog.R;
import com.example.loveextra.microblog.Util.DBHelper;
import com.example.loveextra.microblog.Util.DBUtils;
import com.example.loveextra.microblog.models.Notes;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    private DBHelper helper;
    private SQLiteDatabase db;

    private ImageButton bnEditNote;
    private RadioButton bnSaveNote;
    private EditText editNote;
    private ListView listViewNote;
    private NoteAdapter noteAdapter;

    private int notePosition=-1;

    private List<Notes> notesList;


    public void init(){
        helper=new DBHelper(this,DBHelper.DATA_MB,null,DBHelper.VERSION);
        db=helper.getReadableDatabase();
        bnEditNote=(ImageButton)findViewById(R.id.note_bn_edit);
        bnSaveNote=(RadioButton)findViewById(R.id.note_bn_save);
        editNote=(EditText)findViewById(R.id.note_edit);
        listViewNote=(ListView)findViewById(R.id.note_listview);

        notesList= DBUtils.queryNotes(NoteActivity.this,db);

        editNote.addTextChangedListener(editLis);
        bnEditNote.setOnClickListener(lise);
        bnSaveNote.setOnCheckedChangeListener(savalis);
        noteAdapter=new NoteAdapter();

        listViewNote.setAdapter(noteAdapter);
        listViewNote.setOnItemClickListener(noteli);
        listViewNote.setOnItemLongClickListener(notelis);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        init();
    }

    private View.OnClickListener lise=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.note_bn_edit:
                    toEditNote();
                    break;
                default:
                    break;
            }
        }
    };


    private TextWatcher editLis=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(editNote.getText().toString().trim().length()>0){
                bnSaveNote.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(editNote.getText().toString().trim().length()>0){
                bnSaveNote.setVisibility(View.VISIBLE);
            }
        }
    };



    private CompoundButton.OnCheckedChangeListener savalis=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.note_bn_save:
                    if(isChecked){
                        saveNote();
                        buttonView.setChecked(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };



    private void toEditNote(){
        bnEditNote.setVisibility(View.GONE);
        editNote.setVisibility(View.VISIBLE);
    }

    //保存笔记
    private void saveNote(){
        if(notePosition==-1){
            Notes notes=new Notes();
            notes.setNoContent(getEditOfNote());
            notes.setNoDateStamp(getCurrentTime());
            DBUtils.insertNotes(NoteActivity.this,db,notes);
            Toast.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
            notesList= DBUtils.queryNotes(this,db);
            noteAdapter.notifyDataSetChanged();
            editNote.setText("");
        }else {
            Notes notes=notesList.get(notePosition);
            notes.setNoContent(getEditOfNote());
            notes.setNoDateStamp(getCurrentTime());
            DBUtils.updateNotes(NoteActivity.this,db,notes);
            Toast.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
            editNote.setText("");
            notesList= DBUtils.queryNotes(this,db);
            noteAdapter.notifyDataSetChanged();
            notePosition=-1;
        }
    }

    //获取当前时间
    public String getCurrentTime(){
        Calendar calendar=Calendar.getInstance();
        DecimalFormat tf=new DecimalFormat("00");
        String hour=tf.format(calendar.get(Calendar.HOUR_OF_DAY));
        String minute=tf.format(calendar.get(Calendar.MINUTE));
        String time=calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" "+hour+":"+minute;
        return time;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!editNote.getText().toString().trim().equals("")){
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("当前正在编辑，退出将丢失");
                dialog.setCancelable(false);
                dialog.setPositiveButton("退出",new DialogInterface.OnClickListener(){

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
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getEditOfNote(){
        return editNote.getText().toString().trim();
    }

    private AdapterView.OnItemClickListener noteli=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toEditNote();
            editNote.setText(notesList.get(position).getNoContent());
        }
    };


    private AdapterView.OnItemLongClickListener notelis=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder=new AlertDialog.Builder(NoteActivity.this);
            builder.setTitle("确认删除？");
            builder.setCancelable(false);
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Notes notes=notesList.get(position);
                    DBUtils.deleteNotes(NoteActivity.this,db,notes);
                    notesList= DBUtils.queryNotes(NoteActivity.this,db);
                    noteAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return false;
        }
    };

    //适配器
    class NoteAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return notesList.size();
        }

        @Override
        public Object getItem(int position) {
            return notesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BlueNoteViewHolder blueNoteViewHolder;
            PinkNoteViewHolder pinkNoteViewHolder;
            GreyNoteViewHolder greyNoteViewHolder;

            View blueView=null;
            View pinkView=null;
            View greyView=null;

            if(position%3==0){
                if(blueView==null){
                    blueView=getLayoutInflater().inflate(R.layout.item_note_french_blue,null);
                    blueNoteViewHolder=new BlueNoteViewHolder();
                    blueNoteViewHolder.noteContent=(TextView)blueView.findViewById(R.id.item_tv_note_content_blue);
                    blueNoteViewHolder.noteDateStamp=(TextView)blueView.findViewById(R.id.item_tv_note_datestamp_blue);
                    blueView.setTag(blueNoteViewHolder);
                }else {
                    blueNoteViewHolder= (BlueNoteViewHolder) blueView.getTag();
                }
                blueNoteViewHolder.noteContent.setText(notesList.get(position).getNoContent());
                blueNoteViewHolder.noteDateStamp.setText(notesList.get(position).getNoDateStamp());
                return blueView;

            }else if(position%3==1){
                if(pinkView==null){
                    pinkView=getLayoutInflater().inflate(R.layout.item_note_french_pink,null);
                    pinkNoteViewHolder=new PinkNoteViewHolder();
                    pinkNoteViewHolder.noteContent=(TextView)pinkView.findViewById(R.id.item_tv_note_content_pink);
                    pinkNoteViewHolder.noteDateStamp=(TextView)pinkView.findViewById(R.id.item_tv_note_datestamp_pink);
                    pinkView.setTag(pinkNoteViewHolder);
                }else {
                    pinkNoteViewHolder= (PinkNoteViewHolder) pinkView.getTag();
                }
                pinkNoteViewHolder.noteContent.setText(notesList.get(position).getNoContent());
                pinkNoteViewHolder.noteDateStamp.setText(notesList.get(position).getNoDateStamp());
                return pinkView;

            }else if(position%3==2){
                if(greyView==null){
                    greyView=getLayoutInflater().inflate(R.layout.item_note_sliver_grey,null);
                    greyNoteViewHolder=new GreyNoteViewHolder();
                    greyNoteViewHolder.noteContent=(TextView)greyView.findViewById(R.id.item_tv_note_content_grey);
                    greyNoteViewHolder.noteDateStamp=(TextView)greyView.findViewById(R.id.item_tv_note_datestamp_grey);
                    greyView.setTag(greyNoteViewHolder);
                }else {
                    greyNoteViewHolder= (GreyNoteViewHolder) greyView.getTag();
                }
                greyNoteViewHolder.noteContent.setText(notesList.get(position).getNoContent());
                greyNoteViewHolder.noteDateStamp.setText(notesList.get(position).getNoDateStamp());
                return greyView;
            }
            return null;
        }

        class BlueNoteViewHolder{
            TextView noteContent;
            TextView noteDateStamp;
        }

        class PinkNoteViewHolder{
            TextView noteContent;
            TextView noteDateStamp;
        }

        class GreyNoteViewHolder{
            TextView noteContent;
            TextView noteDateStamp;
        }
    }
}
