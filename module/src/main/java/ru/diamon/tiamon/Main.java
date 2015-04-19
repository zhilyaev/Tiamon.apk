package ru.diamon.tiamon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ru.diamon.tiamon.util.Index;

public class Main extends Index {
    protected Intent intent_newgame,intent_about;
    private Button btn_continue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_continue = (Button) findViewById(R.id.menu_continue);
        btn_continue.setOnClickListener(view -> startActivity(intent_game));
    }

    @Override
    public void initialization() {
        super.initialization();
        intent_newgame = new Intent(this, NewGame.class);
        intent_about = new Intent(this, About.class);
    }

    @Override
    public void savePet() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (_AGE==0) { btn_continue.setEnabled(false); }
    }

    /* Переход в Активность: Новая Игра */
    public void NewGameActivity(View view){
        startActivity(intent_newgame);
    }

    /* Переход в Активность: Рекорды */
    public void RecordsActivity(View view) {
        startActivity(intent_records);
    }

    /* Переход в Активность: О Таймоне */
    public void AboutActivity(View view) {
        startActivity(intent_about);
    }

}
