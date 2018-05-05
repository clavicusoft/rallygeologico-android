package com.rallygeologico;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Clase para manejar la actividad de login con Facebook o Google
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Cuando se inicia la actividad se crea el fragmento de inicio con Facebook
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        //Carga el fragmento de FacebookLogin en la actividad
        if (fragment == null) {
            fragment = new FacebookFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
