package com.rallygeologico;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import SqlDatabase.LocalDB;
import SqlEntities.Activity;
import SqlEntities.Rally;
import SqlEntities.Site;
import SqlEntities.User;

import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Clase para manejar la pantalla principal del juego y solicitar los permisos de memoria externa y uso del GPS
 */
public class GameActivity extends AppCompatActivity implements OnItemSelectedListener {

    private static final int SOLICITUD_TODOS=100;
    LocalDB db;
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar appbar;
    Spinner spinner;
    ArrayAdapter<Rally> dataAdapter;
    ArrayList<Rally> rallies;

    ImageView imgRally;
    TextView nombreRally;
    TextView descRally;
    TextView sitesRally;
    Bundle mBundle;
    String seleccionado;

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
        db = new LocalDB(this);

        seleccionado="-1"; //Rally default
        //Inicia las misma variables que en el login para controlar si el usuario desea salir de la sesion
        /*gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        fbLoginManager = LoginManager.getInstance();
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        fbSignIn = enableButtons && profile != null;
        googleSignIn = account != null;*/

        //Busca el boton de jugar en la vista y le asigna una funcion
        // de click para cambiar a la actividad del mapa
        View myLayout = findViewById( R.id.content);
        imgRally = myLayout.findViewById( R.id.iv_rally_game_screen);
        nombreRally = myLayout.findViewById( R.id.name_rally_game_screen);
        descRally = myLayout.findViewById( R.id.description_rally_game_screen);
        sitesRally = myLayout.findViewById( R.id.sites_rally_game_screen);

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

        //Dropdown para cargar los rallies descargados
        spinner = (Spinner) myLayout.findViewById(R.id.spinner_rallies);
        spinner.setOnItemSelectedListener(this);
        spinner.setPrompt("Seleccione un rally");

        rallies = db.selectAllDownloadedRallies();
        dataAdapter = new ArrayAdapter<Rally>(this, R.layout.rally_spinner_item, rallies){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                String colorHex;
                if(position%2 == 1) {
                    // Set the item background color
                    colorHex = "#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.Gris_2) & 0x00ffffff);
                    tv.setBackgroundColor(Color.parseColor(colorHex));
                }
                else {
                    // Set the alternate item background color
                    colorHex = "#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.Gris_3) & 0x00ffffff);
                    tv.setBackgroundColor(Color.parseColor(colorHex));
                }
                return view;
            }
        };
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.rally_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

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
                                setHowToPlayScreen();
                                break;
                            case R.id.menu_info:
                                setAboutUsScreen();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        Rally item = (Rally) parent.getItemAtPosition(position);
        mBundle = new Bundle();
        // Showing selected spinner item

        String prueba=item.toString();
        String seleccTockenizer="";

        /*Agarrar el id del rally*/
        StringTokenizer st = new StringTokenizer(prueba);
        while (st.hasMoreTokens()) {
            seleccTockenizer=st.nextToken();
        }
        seleccionado= seleccTockenizer;
        Toast.makeText(parent.getContext(), "Seleccionado: " + seleccionado, Toast.LENGTH_LONG).show();
        nombreRally.setText(item.getName());
        descRally.setText(item.getDescription());
        mBundle.putInt("rallyId", item.getRallyId());
        List<Site> sitios = db.selectAllSitesFromRally(item.getRallyId());
        Iterator iterator = sitios.iterator();
        String sitesList = "";
        while(iterator.hasNext()){
            Site sitio = (Site) iterator.next();
            sitesList = sitesList + sitio.getSiteName() + "\n";
        }
        sitesRally.setText(sitesList);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Toast.makeText(this, "No ha descargado ningún rally", Toast.LENGTH_LONG).show();
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
    private boolean solicitarPermisos() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA}, SOLICITUD_TODOS);
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
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
     * Comienza la actividad de sobre nosotros
     */
    public void setAboutUsScreen() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    /**
     * Comienza la actividad de como jugar
     */
    public void setHowToPlayScreen() {
        Intent intent = new Intent(this, HowToPlayActivity.class);
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
        if ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            if(!seleccionado.equals("-1")) {
                Intent intent = new Intent(this, ActivityMap.class);
                intent.putExtra("ID",seleccionado);
                startActivity(intent);
            }
            else {
                Toast.makeText(this,"Seleccione un Rally",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this,"Active el GPS",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Maneja el cierre de sesion, dependiendo de con cual api esta conectada
     */
    public void manejarCierreSesion(){
        /*if(fbSignIn) {
            fbLoginManager.logOut();
        } else if(googleSignIn) {
            mGoogleSignInClient.signOut();
        }*/
        User user = db.selectLoggedUser();
        user.setLogged(false);
        db.updateUser(user);
        setStartScreen();
    }

    /**
     * Muestra el activity de inicio del juego
     */
    public void setStartScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Si se presiona el boton de atras, se devuelve a la pantalla principal
     */
    @Override
    public void onBackPressed(){
        setStartScreen();
    }

    /**
     * Muestra alerta de confirmacion cuando el usuario desea hacer logout
     */
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

    @Override
    public void onResume(){
        super.onResume();
        rallies = db.selectAllDownloadedRallies();
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart(){
        super.onStart();
        rallies = db.selectAllDownloadedRallies();
        dataAdapter.notifyDataSetChanged();
    }

}
