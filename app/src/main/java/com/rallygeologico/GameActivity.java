package com.rallygeologico;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Clase para manejar la pantalla principal del juego y solicitar los permisos de memoria externa y uso del GPS
 */
public class GameActivity extends AppCompatActivity {

    private static final int SOLICITUD_TODOS=100;
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar appbar;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    LoginManager fbLoginManager;
    boolean fbSignIn;
    boolean googleSignIn;

    /**
     * Se ejecuta cuando se crea la vista
     * Verifica si se tienen los permisos y si no los tiene los solicita
     * Habilita el boton hasta que haya chequeado los permisos
     * @param savedInstanceState Estado actual de la aplicacion
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        fbLoginManager = LoginManager.getInstance();
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        fbSignIn = enableButtons && profile != null;
        googleSignIn = account != null;

        //Busca el boton de jugar en la vista y le asigna una funcion
        // de click para cambiar a la actividad del mapa
        View myLayout = findViewById( R.id.content);
        Button botonMapa= myLayout.findViewById( R.id.btnMap);
        botonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMapScreen();
            }
        });
        botonMapa.setEnabled(solicitarPermisos());

        //Crea la barra superior de herramientas con una opcion para las opciones
        appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Vista para poder desplegar el menu de opciones de manera lateral
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navview);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    /**
                     * Indica la instruccion a ejecutar despues de dar click en
                     * alguna de las opciones del menu
                     * @param menuItem Item del menu seleccionado
                     * @return verdadero siempre que no haya error y luego cierra
                     * el menu desplegable
                     */
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_perfil:
                                setProfileScreen();
                                break;
                            case R.id.menu_rally:
                                setRallyListScreen();
                                break;
                            case R.id.menu_puntuacion:
                                break;
                            case R.id.menu_ajustes:
                                Log.i("NavigationView", "Pulsada opcion 1");
                                break;
                            case R.id.menu_info:
                                Log.i("NavigationView", "Pulsada opcion 2");
                                break;
                            case R.id.menu_salir:
                                showAlertLogout();
                                break;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }

    /**
     * Controla que se abra el menu de navegacion lateral cuando se oprime
     * el boton de opciones en la barra superior de herramientas
     * @param item Opcion seleccionada en el menu
     * @return booleano cuando se completa la operacion super
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Solicita permisos al usuario si la version de android es superior a 6
     * @return Booleano despues de solicitar los permisos
     */
    private boolean solicitarPermisos()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions(this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        SOLICITUD_TODOS);
            }
        }
        return true;
    }

    /**
     * Se dispara al solicitar los permisos al usuario
     * Si este aun no los tiene se los solicita
     * @param requestCode Codigo de solicitud
     * @param permissions Permisos solicitados
     * @param grantResults Permisos asignados
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SOLICITUD_TODOS:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                }
                return;
        }
    }

    /**
     * Comienza la actividad del perfil del usuario
     */
    public void setProfileScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Comienza la actividad de la lista de rallies
     */
    public void setRallyListScreen() {
        Intent intent = new Intent(this, RallyList.class);
        startActivity(intent);
    }

    /**
     * Comienza la actividad del mapa del juego
     */
    public void setMapScreen() {
        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)))
        {
            Intent intent = new Intent(this, ActivityMap.class);
            startActivity(intent);
        }
        else {Toast.makeText(this,"Active el GPS",Toast.LENGTH_SHORT).show();}
    }

    public void manejarCierreSesion(){
        if(fbSignIn) {
            fbLoginManager.logOut();
        } else if(googleSignIn) {
            mGoogleSignInClient.signOut();
        }
        setStartScreen();
    }

    public void setStartScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        if(fbSignIn || googleSignIn) {
            setStartScreen();
        }
    }

    private void showAlertLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que desea salir?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        manejarCierreSesion();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
