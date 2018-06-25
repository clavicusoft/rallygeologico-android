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


/**
 * Clase que se encarga de desplegar la camara y crea objetos de la realidad aumentada segun la base de datos
 * Actualiza la ubicacion actual del jugador por medio de GPS y verifica si esta cerca para notificarle la visita de un punto
 */

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
    Fragment fragmentInclinometro;


    /**
     * Recibe el id del rally selecccionado por el jugador
     * Muestra la vista la camara
     * Crea el mundo de realidad aumentada segun la base de datos
     * Inicializa el escuchador de la ubicacion actual
     * @param savedInstanceState Estado actual de la aplicacion
     */


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        rallyID= "" + myIntent.getIntExtra("rallyId",0);

        setContentView(R.layout.main_realidadaumentada);
        setContentView(R.layout.activity_realidadaumentada);

        /*Esconda el boton de informacion desde el inicio*/
        botonInformacion= findViewById(R.id.informacion_realidadaumentada);
        botonInformacion.setVisibility(View.GONE);
        botonInformacion.setClickable(false);

        /*Inicializar fragment de brujula*/
        fragmentBrujula = fragmentManager.findFragmentById(R.id.fragmentCompass);
        fragmentInclinometro = fragmentManager.findFragmentById(R.id.fragmentSensors);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.hide(fragmentBrujula);
        ft.hide(fragmentInclinometro);
        ft.commit();
        addShowHideListener(R.id.inclinometro_realidadaumentada,fragmentManager.findFragmentById(R.id.fragmentSensors));
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
            if(center!=null)
            {verificarPuntos();}
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5, this);
        } catch (SecurityException e) {
            Toast.makeText(this, "No pedi el permiso bien", Toast.LENGTH_SHORT).show();
        }

        /*Fin de ubicacion*/
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
    }

    /**
     * Vuelve a la actividad del mapa
     * */
    public void setMapActivity() {
       onBackPressed();
    }

    /**
     * Muestra y esconde la brujula y el inclinometro
     * @param buttonId boton victima que se debe esconde o mostrar
     * @param fragment fragmento utilizado
     * */
   public void addShowHideListener(int buttonId, final Fragment fragment) {
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

    /**
     * Llama a la actividad del QR
     * */
    public void setQRActivity() {
        Toast.makeText(this,"Llamar al activity QR",Toast.LENGTH_SHORT).show();
    }

    public void setInformacionActivity() {
        Toast.makeText(this,"Multimedia en proceso",Toast.LENGTH_SHORT).show();
    }

    /**
    *Llama al metodo que crea los objetos y despues setea el mundo
     * */
    private void crearmundo() {
        crearGeobjetos();
        // Finally we add the Wold data in to the fragment
        mBeyondarFragment.setWorld(mWorld);
    }
    /**
     * Metodo que se dispara al tocar un objeto de la realidad aumentada
     * Muestra el nombre del sitio y la distancia a la que se encuentra
     * @param beyondarObjects lista de lo objetos de la realidad aumentada creados
     */
    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        // The first element in the array belongs to the closest BeyondarObject
        if (center != null) {
            Toast.makeText(this, beyondarObjects.get(0).getName() + ": " + String.format("%.1f", beyondarObjects.get(0).getDistanceFromUser()) + " metros", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, beyondarObjects.get(0).getName(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  Consulta la base de datos y crea los objetos de la realidad aumentada
     * Segun el rally id recibido en el activity
     * */
    public void crearGeobjetos() {
        List<Site> sites = localDB.selectAllSitesFromRally(Integer.parseInt(rallyID));
        if (sites.size() == 0) {
            sites = localDB.selectAllSitesFromRally(Integer.parseInt(rallyID));
        }
        for (int i = 0; i < sites.size(); i++) {
            Location nuevo = new Location("dummyprovider");
            nuevo.setLatitude(Double.parseDouble(sites.get(i).getLatitud()));
            nuevo.setLongitude(Double.parseDouble(sites.get(i).getLongitud()));
            crearObjeto(nuevo.getLatitude(), nuevo.getLongitude(), sites.get(i).getStatus(), sites.get(i).getSiteName());
        }
    }

    /**
     * Crea un objeto de la realidad aumentada
     * Segun el estado de este, se muestra una imagen diferente y el nombre que lo identifica
     * @param lat latitud del sitio
     * @param lon Longitud del sitio
     * @param tipo Status del sitio (visitado, no visitado, especial no visitado, especial visitado)
     * */
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


    /**
    * Metodo que se dispara al tener una nueva localizacion del jugador
     * Verifica si se esta cerca de un punto para notificarlo y posteriormente muestra el boton para obtener mas informacion del sitio
     * */
    @Override
    public void onLocationChanged(Location location) {
        center = new GeoPoint(location.getLatitude(), location.getLongitude());
        mWorld.setGeoPosition(location.getLatitude(), location.getLongitude());
        verificarPuntos();
        verificarInformacion();
    }

    /**
     * Se dispara cuando hay un cambio de estado del proveedor
     * @param s Proveedor
     * @param i Estado en el que se encuentra
     * @param bundle Estado de la aplicacion
     * */
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    /**
     * Se dispara cuando se activa el proveedor
     * @param s Proveedor
     * */
    @Override
    public void onProviderEnabled(String s) {
    }
    /**
     * Se dispara cuando se desactiva el proveedor
     * @param s Proveedor
     * */
    @Override
    public void onProviderDisabled(String s) {
    }

    /**
     * Consulta la base de datos los sitios y verifica si esta en el rango para mostrar el boton que despliega la informacion
     * */
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

    /**
     * Este metodo se encarga agarrar todos los sitios de la base de datos y chequear si estamos cerca de un no visitado o uno especial
     * */
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

    /**
     * Despliega un mensaje al usuario indicando que encontro un punto especial
     * @param lat latitud del punto encontrado
     * @param lon Longitud del punto encontrado
     * @param nombre Nombre del punto encontrado
     * @param petrocoins valor del punto en petrocoins para el usuario
     *
     * */
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


    /**
     * Despliega un mensaje al usuario indicando que visito un punto
     * @param lat latitud del punto encontrado
     * @param lon Longitud del punto encontrado
     * @param nombre Nombre del punto encontrado
     * @param petrocoins valor del punto en petrocoins
     *
     * */
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

    /**
     * Indica al usuario cuando ya ha visitado todos los sitios de un rally
     * */
    public void visiteTodos() {
        Toast.makeText(this,"Visite todos los puntos",Toast.LENGTH_SHORT).show();
    }

    /**
     * Cuando viene de otro activity se actualiza los sitios segun la base de datos
     * */
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
    /**
     * Cuando me paso de activity se deja de escuchar la ubicacion
     * */
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