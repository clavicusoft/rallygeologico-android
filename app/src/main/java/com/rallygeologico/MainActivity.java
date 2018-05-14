package com.rallygeologico;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import SqlDatabase.LocalDB;

/**
 * Clase para controlar la pantalla de inicio del juego
 */
public class MainActivity extends AppCompatActivity {

    ImageView logo;
    Button start;

    /**
     * Cuando se crea la actividad se despliega un boton para comenzar la aplicacion
     * @param savedInstanceState Estado actual de la aplicacion
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalDB localDB = new LocalDB(getApplicationContext());
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.btn_inicio);
        logo = findViewById(R.id.iv_inicio);
    }

    /**
     * Inicia la actividad para el login del usuario
     * @param view Vista de la actividad
     */
    public void setLoginScreen(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
