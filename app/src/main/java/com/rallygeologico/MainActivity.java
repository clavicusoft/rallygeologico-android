package com.rallygeologico;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import SqlDatabase.LocalDB;
import SqlEntities.User;


/**
 * Clase para controlar la pantalla de inicio del juego
 */
public class MainActivity extends AppCompatActivity {

    ImageView logo;
    Button start;
    boolean fbSignIn;
    boolean googleSignIn;
    boolean enableButtons;
    boolean conectado;
    User user;
    GoogleSignInAccount account;

    /**
     * Cuando se crea la actividad se despliega un boton para comenzar la aplicacion
     * @param savedInstanceState Estado actual de la aplicacion
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalDB localDB = new LocalDB(getApplicationContext());
        localDB.prueba();
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.btn_inicio);
        logo = findViewById(R.id.iv_logoucr);

        /*account = GoogleSignIn.getLastSignedInAccount(this);
        enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();
        fbSignIn = enableButtons && profile != null;
        googleSignIn = account != null;*/
        user = localDB.selectLoggedUser();
        if(user == null){
            conectado = false;
        }else{
            conectado = true;
        }

        start.setOnClickListener(new View.OnClickListener() {
            /**
             * Se continua a la pantalla de login o del juego pero revisa si el jugador esta loggeado
             * @param view Vista del activity
             */
            @Override
            public void onClick(View view) {
                if(conectado){
                    irAJuego(view);
                    //irALogin(view);
                }else{
                    irALogin(view);
                }
            }
        });
    }

    /**
     * Inicia la actividad para el login del usuario
     * @param view Vista de la actividad
     */
    public void irALogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Inicia la actividad para jugar si ya esta loggeado
     * @param view Vista de la actividad
     */
    public void irAJuego(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

}
