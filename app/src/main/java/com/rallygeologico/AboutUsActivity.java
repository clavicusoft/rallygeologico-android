package com.rallygeologico;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Clase que contiene la informacion de las instituciones asociadas
 */
public class AboutUsActivity extends AppCompatActivity {

    TextView cicg;
    TextView ecci;
    TextView acg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        cicg = findViewById(R.id.cicg_text);
        cicg.setPaintFlags(cicg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        cicg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWebActivity("http://www.cicg.ucr.ac.cr/");
            }
        });

        ecci = findViewById(R.id.ecci_text);
        ecci.setPaintFlags(ecci.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ecci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWebActivity("https://www.ecci.ucr.ac.cr/");
            }
        });

        acg = findViewById(R.id.acg_text);
        acg.setPaintFlags(acg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        acg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWebActivity("https://www.acguanacaste.ac.cr/");
            }
        });
    }

    public void setWebActivity(String url) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("URL", url);
        startActivity(intent);
    }
}
