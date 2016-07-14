package com.example.loveextra.microblog.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loveextra.microblog.CustomView.Game2048Layout;
import com.example.loveextra.microblog.R;


public class Game2048Activity extends Activity implements Game2048Layout.OnGame2048Listener {


    private Game2048Layout mGame2048Layout;

    private TextView mScore;

    public void findView(){
        mScore = (TextView) findViewById(R.id.id_score);
        mGame2048Layout = (Game2048Layout)findViewById(R.id.lay_gamepage);
        mGame2048Layout.setOnGame2048Listener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findView();
    }

    @Override
    public void onScoreChange(int score) {
        mScore.setText("SCORE: " + score);
    }

    @Override
    public void onGameOver() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("小样，你终于狗带了！嚯嚯嚯").setMessage("当前分数 " + mScore.getText());

        builder.setPositiveButton("重来又如何", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGame2048Layout.restart();
            }
        });
        builder.setNegativeButton("GG", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onVictory(boolean isVictory) {
        if(isVictory){
            Dialog dialog=new Dialog(this);
            dialog.setContentView(R.layout.game_end_view);
            ImageView imageVictory= (ImageView) dialog.findViewById(R.id.image_victory);
            ImageView textVictory=(ImageView) dialog.findViewById(R.id.image_text_victory);
            Button bnAgain= (Button) dialog.findViewById(R.id.bn_again);
            Animation animImage= AnimationUtils.loadAnimation(this,R.anim.victory_scale);
            Animation animText= AnimationUtils.loadAnimation(this,R.anim.victory_rotate);
            bnAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGame2048Layout.restart();
                }
            });
            imageVictory.startAnimation(animImage);
            textVictory.startAnimation(animText);
            dialog.show();
        }
    }
}
