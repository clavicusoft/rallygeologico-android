package com.rallygeologico;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.BeyondarObjectList;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import org.osmdroid.util.Distance;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import SqlDatabase.LocalDB;
import SqlEntities.Site;

public class ActivityRealidadAumentada extends FragmentActivity implements OnClickBeyondarObjectListener, LocationListener {

    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private int ID;
    MediaPlayer mp;
    LocalDB localDB;
    LocationManager locationManager; //Escuchador de la ubicacion actual
    GeoPoint center;

    Dialog especialDialog;
    int numeroEspeciales;
    int numeroNoVisitados;
    int numeroVisitados;
    TextView botoncerrar;

    String rallyID;

    /*botones*/
    Button botonMapa;
    Button botonQR;
    Button botonInclinometro;
    Button botonInformacion;

    FragmentManager fragmentManager = getFragmentManager();
    Fragment fragmentBrujula;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        rallyID= myIntent.getStringExtra("ID");

        setContentView(R.layout.main_realidadaumentada);
        setContentView(R.layout.activity_realidadaumentada);

        /*Esconda el boton de informacion desde el inicio*/
        botonInformacion= findViewById(R.id.informacion_realidadaumentada);
        botonInformacion.setVisibility(View.GONE);
        botonInformacion.setClickable(false);

        /*Inicializar fragment de brujula*/
        fragmentBrujula = fragmentManager.findFragmentById(R.id.fragmentCompass);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.hide(fragmentBrujula);
        ft.commit();
        addShowHideListener(R.id.brujula_realidadaumentada,fragmentManager.findFragmentById(R.id.fragmentCompass));

        especialDialog=new Dialog(this);
        especialDialog.setContentView(R.layout.alertarealidadaumentada);

        localDB = new LocalDB(this);
        ID = 0;
        numeroEspeciales=0;
        numeroNoVisitados=0;
        numeroVisitados=0;

        /*Ubicacion*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5, this);
        } catch (SecurityException e) {
            Toast.makeText(this, "No pedi el permiso bien", Toast.LENGTH_SHORT).show();
        }

        /*Fin de ubicación*/
        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        mWorld = new World(this);
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);
        crearmundo();
        mBeyondarFragment.setMaxDistanceToRender(500);  //Distancia donde queremos que se observen los objetos
        mBeyondarFragment.setDistanceFactor(6);
        //  mBeyondarFragment.setPullCloserDistance (500);

        /*Funcion a los botones*/
        botonInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInformacionActivity();
            }
        });

        botonMapa= findViewById( R.id.mapa_realidadaumentada);
        botonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMapActivity();
            }
        });

        botonInclinometro= findViewById( R.id.inclinometro_realidadaumentada);
        botonInclinometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInclinometroActivity();
            }
        });

    }

    public void setMapActivity() {
       onBackPressed();
    }

    void addShowHideListener(int buttonId, final Fragment fragment) {
        final Button button = (Button) findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                if (fragment.isHidden()) {
                    ft.show(fragment);
                } else {
                    ft.hide(fragment);
                }
                ft.commit();
            }
        });
    }

    public void setQRActivity() {
        Toast.makeText(this,"Llamar al activity inclinometro",Toast.LENGTH_SHORT).show();
    }

    public void setInclinometroActivity() {
        Toast.makeText(this,"Llamar al activity inclinometro",Toast.LENGTH_SHORT).show();
    }

    public void setInformacionActivity() {
        Toast.makeText(this,"Multimedia en proceso",Toast.LENGTH_SHORT).show();
    }

    private void crearmundo() {
        crearGeobjetos();
        // Finally we add the Wold data in to the fragment
        mBeyondarFragment.setWorld(mWorld);
    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        if (center != null) {
            Toast.makeText(this, beyondarObjects.get(0).getName() + ": " + String.format("%.1f", beyondarObjects.get(0).getDistanceFromUser()) + " metros", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
        }
    }

    public void crearGeobjetos() {
        List<Site> sites = localDB.selectAllSitesFromRally(Integer.parseInt(rallyID));
        if (sites.size() == 0) {
            //localDB.prueba();
            sites = localDB.selectAllSitesFromRally(Integer.parseInt(rallyID));
        }
        for (int i = 0; i < sites.size(); i++) {
            Location nuevo = new Location("dummyprovider");
            nuevo.setLatitude(Double.parseDouble(sites.get(i).getLatitud()));
            nuevo.setLongitude(Double.parseDouble(sites.get(i).getLongitud()));
            crearObjeto(nuevo.getLatitude(), nuevo.getLongitude(), sites.get(i).getStatus(), sites.get(i).getSiteName());
        }
    }

    public void crearObjeto(double lat, double lon, int tipo, String Name) {
        if (tipo != 4) {//Si no es el especial
            ++ID;
            GeoObject go1 = new GeoObject(Long.valueOf(ID));
            go1.setGeoPosition(lat, lon);
            if (tipo == 1) {
                go1.setImageResource(R.drawable.novisitado);
                ++numeroNoVisitados;
            }
            if (tipo == 2) {
                go1.setImageResource(R.drawable.visitado);
                ++numeroVisitados;
            }
            if (tipo == 3) {
                go1.setImageResource(R.drawable.dorado);
                ++numeroEspeciales;
            }
            go1.setName(Name);
            mWorld.addBeyondarObject(go1);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        center = new GeoPoint(location.getLatitude(), location.getLongitude());
        mWorld.setGeoPosition(location.getLatitude(), location.getLongitude());
        verificarPuntos();
        verificarInformacion();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public void verificarInformacion(){
        boolean noEncontre=true;
        List<Site> sites = localDB.selectAllSitesFromRally(Integer.parseInt(rallyID));
        int ite=0;
        while ( ite < sites.size() && noEncontre) {
            double lat = Double.parseDouble(sites.get(ite).getLatitud());
            double lon = Double.parseDouble(sites.get(ite).getLongitud());
            if (center.distanceToAsDouble(new GeoPoint(lat, lon)) <= 1000.0) {
                botonInformacion.setVisibility(View.VISIBLE);
                botonInformacion.setClickable(true);
                noEncontre=false;
            }
            ite++;
        }
        if (noEncontre) {
            botonInformacion.setVisibility(View.GONE);
            botonInformacion.setClickable(false);
         }
    }

    public void verificarPuntos() {
        int activoSonido=0;
        /*Recorro los markers*/
        List<Site> sites = localDB.selectAllSitesFromRally(Integer.parseInt(rallyID));
        for (int ite = 0; ite < sites.size(); ite++) {
            double lat = Double.parseDouble(sites.get(ite).getLatitud());
            double lon = Double.parseDouble(sites.get(ite).getLongitud());
            if (sites.get(ite).getStatus() == 4 && center.distanceToAsDouble(new GeoPoint(lat, lon)) <= 50.0) {
                localDB.updateSiteVisit(sites.get(ite).getSiteId(), 3);
                verificarEspecial(lat, lon, sites.get(ite).getSiteName(), Integer.toString(sites.get(ite).getSiteTotalPoints()));
            activoSonido=1;
            }
            if (sites.get(ite).getStatus() == 1 && center.distanceToAsDouble(new GeoPoint(lat, lon)) <= 20.0) {
                localDB.updateSiteVisit(sites.get(ite).getSiteId(), 2);
                 /*Actualizo la vista*/
                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                v.vibrate(3000);
                /*Quita el marcador pasado e inserta otro*/
                mWorld.clearWorld();
                numeroNoVisitados=0;
                numeroVisitados=0;
                numeroEspeciales=0;
                crearGeobjetos();
                /*Muestro la notificacion o termino*/
                if(numeroNoVisitados==0) {
                   visiteTodos();
                }
                else {
                    activoSonido=2;
                    verificarNoVisitados(lat, lon, sites.get(ite).getSiteName(), Integer.toString(sites.get(ite).getSiteTotalPoints()));
                }
            }
        }
        if(activoSonido==1) {//Sonido de Alerta
            mp = MediaPlayer.create(this,R.raw.alertadesonido);
            mp.start();
        }
        if(activoSonido==2) {//Sonido normal
            mp = MediaPlayer.create(this,R.raw.alertadesonidonormal);
            mp.start();
        }
    }

    public void verificarEspecial(double lat, double lon, String nombre, String petrocoins) {
        final GeoPoint esp = new GeoPoint(lat, lon);
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(3000);
        crearObjeto(lat, lon, 3, nombre);

        /*Llenar el activity*/
        ImageView imagen = especialDialog.findViewById(R.id.iv_alerta_imagen2);
        imagen.setImageResource(getResources().getIdentifier("dorado", "drawable", getPackageName()));

        TextView secreto = especialDialog.findViewById(R.id.tv_alerta_secreto2);
        secreto.setText("Has encontrado un secreto!");

        TextView valor = especialDialog.findViewById(R.id.tv_alerta_valor2);
        valor.setText(petrocoins + " Petrocoins");

        TextView especial = especialDialog.findViewById(R.id.tv_alerta_especial2);
        especial.setText(Integer.toString(numeroEspeciales));

        TextView visitados = especialDialog.findViewById(R.id.tv_alerta_visitados2);
        visitados.setText(Integer.toString(numeroVisitados));

        TextView novisitados = especialDialog.findViewById(R.id.tv_alerta_novisitados2);
        novisitados.setText(Integer.toString(numeroNoVisitados));

        /*Asigna los botones*/
        botoncerrar= especialDialog.findViewById( R.id.btn_close2);
        botoncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                especialDialog.hide();
            }
         });
        especialDialog.show();
    }

    public void verificarNoVisitados( double lat, double lon,String nombre, String petrocoins) {
        final GeoPoint esp=new GeoPoint(lat, lon);

        /*Llenar el activity*/
        ImageView imagen= especialDialog.findViewById(R.id.iv_alerta_imagen2);
        imagen.setImageResource(getResources().getIdentifier( "visitado", "drawable", getPackageName()));

        TextView secreto= especialDialog.findViewById( R.id.tv_alerta_secreto2);
        secreto.setText("Bienvenido!");

        TextView valor= especialDialog.findViewById( R.id.tv_alerta_valor2);
        valor.setText(petrocoins+ " Petrocoins");

        TextView especial= especialDialog.findViewById( R.id.tv_alerta_especial2);
        especial.setText(Integer.toString(numeroEspeciales));

        TextView visitados= especialDialog.findViewById( R.id.tv_alerta_visitados2);
        visitados.setText(Integer.toString(numeroVisitados));

        TextView novisitados= especialDialog.findViewById( R.id.tv_alerta_novisitados2);
        novisitados.setText(Integer.toString(numeroNoVisitados));

        /*Asigna los botones*/
        botoncerrar= especialDialog.findViewById( R.id.btn_close2);
        botoncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                especialDialog.hide();
            }
        });
        especialDialog.show();
    }

    public void visiteTodos() {
        Toast.makeText(this,"Visite todos los puntos",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWorld.clearWorld();
        numeroNoVisitados=0;
        numeroVisitados=0;
        numeroEspeciales=0;
        crearGeobjetos();
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5, this);
        }
        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            locationManager.removeUpdates(this);
        }
        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
        }
    }
}