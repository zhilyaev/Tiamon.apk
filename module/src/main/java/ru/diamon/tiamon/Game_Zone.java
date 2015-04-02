package ru.diamon.tiamon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class Game_Zone extends Index {

    TextView TV_age, TV_name,TV_catbucks,tv_sleep,tv_play,tv_hangry;
    ImageButton btn_shop;
    ImageView ripView;
    Intent intent_shop;
    Handler head_sleep,head_play,head_hangry,h;
    ProgressBar bar_sleep,bar_play,bar_hangry;
    WebView petView;
    Thread life;
    boolean die = false;

    static Random random;
    final int U = 3000;
    int _status_HANGRY,_status_SLEEP,_status_PLAY;
    //protected final int FIRST_TIME = 1000*60*60*12;
    final int FIRST_TIME = 12000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        random = new Random();

        petView = (WebView) findViewById(R.id.PetView);
        TV_name = (TextView) findViewById(R.id.label_name);
        TV_age = (TextView) findViewById(R.id.label_age);
        TV_catbucks = (TextView) findViewById(R.id.catbucks);
        btn_shop = (ImageButton) findViewById(R.id.btn_shop);
        ripView = (ImageView) findViewById(R.id.rip);
        tv_sleep = (TextView) findViewById(R.id.TV_sleep);
        tv_play = (TextView) findViewById(R.id.TV_play);
        tv_hangry = (TextView) findViewById(R.id.TV_hangry);
        bar_sleep = (ProgressBar) findViewById(R.id.bar_sleep);
        bar_play = (ProgressBar) findViewById(R.id.bar_play);
        bar_hangry = (ProgressBar) findViewById(R.id.bar_hangry);

        head_sleep = new Handler() {
            public void handleMessage(android.os.Message msg) {
                tv_sleep.setText(String.valueOf(msg.what));
            }
        };
        head_play = new Handler() {
            public void handleMessage(android.os.Message msg) {
                tv_play.setText(String.valueOf(msg.what));
            }
        };
        head_hangry = new Handler() {
            public void handleMessage(android.os.Message msg) {
                tv_hangry.setText(String.valueOf(msg.what));
            }
        };
        btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent_shop);
            }
        });

        life = new Thread((Runnable) ()->{

            do {
                try {Thread.sleep(1000);}
                catch (InterruptedException e) {System.out.println("thread error");}

                _status_PLAY -= random.nextInt(10);
                _status_HANGRY -= random.nextInt(10);
                _status_SLEEP -= random.nextInt(10);

                upStatus();

            } while (!die);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!_VIRGIN){
            // Вместо цикла
            // Сумма Отниманий  = (S + 2d-4f) / (2d) * 5
            long csum = (((new Date().getTime()-_LAST)+2*U-4*FIRST_TIME) / (2*U)) * random.nextInt(5);

            _status_HANGRY -= csum;
            _status_SLEEP -= csum;
            _status_PLAY -= csum;
        }

        upStatus();

        TV_name.setText(_NAME);
        TV_age.setText(String.valueOf(_AGE));
        TV_catbucks.setText(String.valueOf(_MONEY));

        life.start();

    }

    @Override
    public void loadPet() {
        super.loadPet();
        _status_HANGRY = PET.getInt("HANGRY",0);
        _status_SLEEP = PET.getInt("SLEEP",0);
        _status_PLAY = PET.getInt("PLAY",0);
    }

    public void upStatus(){

        if(_status_HANGRY<0){_status_HANGRY=0;}
        if(_status_SLEEP<0){_status_SLEEP=0;}
        if(_status_PLAY<0){_status_PLAY=0;}

        if(_status_HANGRY>100){_status_HANGRY=100;}
        if(_status_SLEEP>100){_status_SLEEP=100;}
        if(_status_PLAY>100){_status_PLAY=100;}

        head_hangry.sendEmptyMessage(_status_HANGRY);
        head_sleep.sendEmptyMessage(_status_SLEEP);
        head_play.sendEmptyMessage(_status_PLAY);

        bar_sleep.setProgress(_status_SLEEP);
        bar_play.setProgress(_status_PLAY);
        bar_hangry.setProgress(_status_HANGRY);

        if(_status_HANGRY<=0 ||_status_SLEEP<=0 ||_status_PLAY<=0){
            die=true;
        }
    }
}
