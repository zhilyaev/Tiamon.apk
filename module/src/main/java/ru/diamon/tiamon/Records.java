package ru.diamon.tiamon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Records extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        // получаем экземпляр элемента ListView
        ListView lv = (ListView) findViewById(R.id.listView);

        final String[] catnames = new String[] {
                "Orange", "Bars", "Murzik", "Simba"
        };
        // используем адаптер данных
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,	android.R.layout.simple_list_item_1, catnames);
        lv.setAdapter(adapter);


    }

    @Override // В главное меню
    public void onBackPressed() {
        startActivity(new Intent(this, Main.class));
    }
}
