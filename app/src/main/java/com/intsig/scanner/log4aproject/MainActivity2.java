package com.intsig.scanner.log4aproject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity2.this, "toast: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        List<EditTextAdapter.ItemBean> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            EditTextAdapter.ItemBean itemTest = new EditTextAdapter.ItemBean();
            itemTest.setText("第"+i + "项");
            list.add(itemTest);
        }
        EditTextAdapter adapter = new EditTextAdapter( list,this);
        listView.setAdapter(adapter);

    }
}