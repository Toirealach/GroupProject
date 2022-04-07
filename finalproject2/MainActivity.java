package com.example.finalproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends MapsActivity {
    private static Button on1,on2,on3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        on1=(Button) findViewById(R.id.GREEN);
        on2=(Button) findViewById(R.id.button2);
        on3=(Button) findViewById(R.id.button3);

        on1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on1 process1 = new on1();
                process1.execute();

            }
        });

        on2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on3 process1 = new on3();
                process1.execute();

            }
        });

        on3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on2 process1 = new on2();
                process1.execute();

            }
        });
    }
}