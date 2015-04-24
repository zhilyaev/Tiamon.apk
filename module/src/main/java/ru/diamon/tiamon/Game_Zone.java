package ru.diamon.tiamon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.diamon.tiamon.util.Kitty;

import java.util.Date;

public class Game_Zone extends Kitty {

    TextView tv_age, tv_name,tv_catbucks,tv_sleep,tv_play,tv_hangry;
    ImageButton btn_shop,btn_sleep,btn_feed,btn_play;
    ImageView ripView;
    Intent intent_shop;
    ProgressBar bar_sleep,bar_play,bar_hangry;
    WebView petView;
    Thread life,events;

    final int U = 3000; // Сложность
    final int FIRST_TIME = 5000; // 1000*60*60*12

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Инициализация вьюшек
        petView = (WebView) findViewById(R.id.PetView);
        tv_name = (TextView) findViewById(R.id.label_name);
        tv_age = (TextView) findViewById(R.id.label_age);
        tv_catbucks = (TextView) findViewById(R.id.catbucks);
        btn_shop = (ImageButton) findViewById(R.id.btn_shop);
        ripView = (ImageView) findViewById(R.id.rip);
        tv_sleep = (TextView) findViewById(R.id.TV_sleep);
        tv_play = (TextView) findViewById(R.id.TV_play);
        tv_hangry = (TextView) findViewById(R.id.TV_hangry);
        bar_sleep = (ProgressBar) findViewById(R.id.bar_sleep);
        bar_play = (ProgressBar) findViewById(R.id.bar_play);
        bar_hangry = (ProgressBar) findViewById(R.id.bar_hangry);
        btn_sleep = (ImageButton) findViewById(R.id.btn_sleep);
        btn_feed = (ImageButton) findViewById(R.id.btn_feed);
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        // Клики
        btn_sleep.setOnClickListener(view -> {
            _status_SLEEP+=random.nextInt(20);
            gifView(petView,"sleep.gif");
            savePet();
            updateLayout();
        });
        btn_feed.setOnClickListener(view -> {
            _status_HANGRY+=random.nextInt(20);
            gifView(petView,"alarm.gif");
            savePet();
            updateLayout();
        });
        btn_play.setOnClickListener(view -> {
            _status_PLAY+=random.nextInt(20);
            gifView(petView,"hihi.gif");
            savePet();
            updateLayout();
        });
        btn_shop.setOnClickListener(view -> startActivity(intent_shop));
        ripView.setOnClickListener(view -> startActivity(intent_records));
        // Определение основных потоков
        life = new Thread((Runnable) ()->{
            while (_status_HANGRY!=0 || _status_SLEEP!=0 ||_status_PLAY!=0) {
                _AGE = (new Date().getTime()-_BIRTH)/1000;
                _status_PLAY -= random.nextInt(10);
                _status_HANGRY -= random.nextInt(10);
                _status_SLEEP -= random.nextInt(10);
                // Усложнить
                if(_HARD>U){_HARD -= U;}
                // Сохранить
                savePet();
                // Обновить
                updateLayout();
                // Костыль, чтобы не спать
                if(_status_HANGRY+_status_PLAY+_status_SLEEP==0) break;
                // Уснуть
                try {Thread.sleep(_HARD);}
                catch (InterruptedException e) {System.out.println("thread error");}
            }
            // Удалить Питомца
            delPet();
        });
        // Поток Случайных Событий
        events = new Thread((Runnable) () ->{
            while(life.isAlive()){ // Пока жив , а можно было сделать внутри потока life
                if(random.nextInt(777) == 1) _MONEY+=100000;
                if(random.nextInt(100) == 5) _category_ill = true;
                // Уснуть
                try {Thread.sleep(U);}
                catch (InterruptedException e) {System.out.println("thread error");}
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_name.setText(_NAME);
        gifView(petView,"hi.gif");
        // Уже играли
        if(_LAST!=0){
            // Сумма Отниманий  = (S + 2d-4f) / (2d) * 10
            long csum = (((new Date().getTime()-_LAST)+2*U-4*FIRST_TIME) / (2*U)) * random.nextInt(10);
            _status_HANGRY -= csum;
            _status_SLEEP -= csum;
            _status_PLAY -= csum;
            savePet();
        }
        updateLayout();
        life.start(); // Я сказада, стартуем!
    }

    public void updateLayout(){
        handler.post((Runnable)()->{

            // Сменить Кнопку кормления
            if(_status_HANGRY<=15) btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.cat_hungry));
            else btn_feed.setImageDrawable(getResources().getDrawable(R.drawable.cat_food));

            tv_sleep.setText(String.valueOf(_status_SLEEP));
            tv_play.setText(String.valueOf(_status_PLAY));
            tv_hangry.setText(String.valueOf(_status_HANGRY));
            bar_sleep.setProgress(_status_SLEEP);
            bar_play.setProgress(_status_PLAY);
            bar_hangry.setProgress(_status_HANGRY);
            tv_catbucks.setText(String.valueOf(_MONEY));
            tv_age.setText(String.valueOf(_AGE));

            if(_status_HANGRY == 0 && _status_PLAY==0 && _status_SLEEP==0){
                ripView.setVisibility(View.VISIBLE);
                petView.setEnabled(false);
            }else if(_status_HANGRY+_status_PLAY+_status_SLEEP>=290){
                _MONEY+=1000;
                gifView(petView,"love.gif");
            }else if(_status_HANGRY+_status_PLAY+_status_SLEEP<=20){
                gifView(petView,"sorry.gif");
            }else{
                gifView(petView,"sorry.gif");
            }

        });
    }

    @Override
    public void loadPet() {
        super.loadPet();
        _status_HANGRY = PET.getInt("HANGRY",42+random.nextInt(42));
        _status_SLEEP  = PET.getInt("SLEEP",42+random.nextInt(42));
        _status_PLAY   = PET.getInt("PLAY",42+random.nextInt(42));

        _HARD = PET.getInt("HARD", FIRST_TIME);
        _MONEY = PET.getInt("MONEY",500);
    }

    @Override
    public void savePet() {
        // ОДЗ
        if(_status_HANGRY<0){_status_HANGRY=0;}
        if(_status_SLEEP<0){_status_SLEEP=0;}
        if(_status_PLAY<0){_status_PLAY=0;}
        if(_status_HANGRY>100){_status_HANGRY=100;}
        if(_status_SLEEP>100){_status_SLEEP=100;}
        if(_status_PLAY>100){_status_PLAY=100;}

        E = PET.edit();
        E.putInt("HANGRY",_status_HANGRY);
        E.putInt("SLEEP",_status_SLEEP);
        E.putInt("PLAY",_status_PLAY);

        E.putLong("LAST",new Date().getTime());
        E.putInt("HARD", _HARD);
        E.putInt("MONEY",_MONEY);
        E.putLong("AGE", _AGE);
        E.apply();
    }

    private void after(){
        new Thread((Runnable)()->{
            try {Thread.sleep(U);}
            catch (InterruptedException e) {System.out.println("thread error");}
            gifView(petView,"hi.gif");
        }).start();
    }

    @Override
    protected void initialization() {
        super.initialization();
    }
}
