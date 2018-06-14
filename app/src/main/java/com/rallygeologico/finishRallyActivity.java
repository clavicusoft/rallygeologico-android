package com.rallygeologico;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class finishRallyActivity extends AppCompatActivity {

    Button btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finishrally);

        btnPublish= findViewById( R.id.btnPublish);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             publicarresultados();
            }
        });

    }

    public void publicarresultados(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }



}
