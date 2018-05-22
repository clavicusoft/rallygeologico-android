package com.rallygeologico;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import FileManager.FileManager;

/**
 * Clase para manejar la pantalla del perfil del usuario
 */
public class ProfileActivity extends AppCompatActivity {

    ImageView fotoPerfil;
    ImageView fotoFondo;
    ImageView editar;
    TextView nombreUsuario;
    TextView lugar;
    TextView recorridos;
    TextView recorridosTotal;
    TextView puntos;
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

        fotoFondo = findViewById(R.id.header_cover_image);
        fotoPerfil = findViewById(R.id.profile);
        editar = findViewById(R.id.edit);
        nombreUsuario = findViewById(R.id.nombre);
        lugar = findViewById(R.id.ubicacion);
        recorridos = findViewById(R.id.rallies);
        puntos = findViewById(R.id.points);
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

        // Si se esta conectado a Facebook se carga la informacion obtenida en login
        boolean conectado = AccessToken.getCurrentAccessToken() != null;
        Profile perfil = Profile.getCurrentProfile();
        if (conectado && perfil != null) {
            //new ImageLoader(fotoPerfil).execute(perfil.getProfilePictureUri(200, 200).toString());
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            if (object != null) {
                                Log.d("Request", object.toString());
                                String name = JSONParser.getName(object);
                                nombreUsuario.setText(name);
                                String home = JSONParser.getHometown(object);
                                lugar.setText(home);
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,hometown");
            request.setParameters(parameters);
            request.executeAsync();
        }
        if (fm.hayAlmacenamientoExterno()) {
            fm.cargarImagenAlmacenamientoExterno("fotoPerfil", fotoPerfil);
        }
    }

}
