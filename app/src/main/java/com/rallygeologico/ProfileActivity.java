package com.rallygeologico;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;
import FileManager.FileManager;
import SlidingTab.SlidingTabLayout;
import SqlDatabase.LocalDB;
import SqlEntities.User;

/**
 * Clase para manejar la pantalla del perfil del usuario
 */
public class ProfileActivity extends AppCompatActivity {

    LocalDB db;
    ScrollView scrollView;
    ImageView fotoPerfil;
    ImageView fotoFondo;
    TextView nombreUsuario;
    TextView lugar;
    TextView recorridosTotal;
    TextView puntosTotal;
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    SlidingTabLayout tabs;

    /**
     * Cuando se crea la vista se carga la foto del usuario, su informacion personal
     * y se carga en un paginador sus logros obtenidos en el juego y rallies hechos
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new LocalDB(this);

        scrollView = (ScrollView) findViewById(R.id.sv_profile);
        scrollView.setFocusableInTouchMode(true);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

        fotoFondo = findViewById(R.id.header_cover_image);
        fotoPerfil = findViewById(R.id.profile);
        nombreUsuario = findViewById(R.id.nombre);
        lugar = findViewById(R.id.ubicacion);
        recorridosTotal = findViewById(R.id.rallyCounter);
        puntosTotal = findViewById(R.id.pointsCounter);

        // Se inicializa el paginador

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager = findViewById(R.id.pagerProfile);
        viewPager.setAdapter(pagerAdapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setViewPager(viewPager);

        FileManager fm = new FileManager();

        User user = db.selectLoggedUser();
        if(user != null){
            String name = user.getFirstName() + " " + user.getLastName();
            nombreUsuario.setText(name);
            String email = user.getEmail();
            lugar.setText(email);
            /*String points = "" + db.selectAllRalliesPointsFromUser(user.getUserId());
            puntosTotal.setText(points);
            String rallies = "" + db.selectAllRalliesCountFromUser(user.getUserId());
            recorridosTotal.setText(rallies);*/
        }

        //Si hay almacenamiento externo carga la imagen de perfil
        if (fm.hayAlmacenamientoExterno()) {
            fm.cargarImagenAlmacenamientoExterno("fotoPerfil", fotoPerfil);
        }
    }

}
