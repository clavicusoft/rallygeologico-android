package com.rallygeologico;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.lang.reflect.Array;
import java.util.List;

import SqlDatabase.LocalDB;
import SqlEntities.Site;


/**
 * Clase que se encarga de desplegar el mapa y mostrar la ubicacion en tiempo real.
 * Asi como los puntos a donde se tiene que dirigir y las distincion de estas entre visitados, no visitados y especiales
 */

public class ActivityMap extends AppCompatActivity implements LocationListener {

    MapView mapView; //Mapa en el que trabajamos
    MapController mc; //Controlador del mapa
    LocationManager locationManager; //Escuchador de la ubicacion actual

    GeoPoint center; //Almacena la ubicacion del GPS
    GeoPoint arribaDerecha; //Ubicacion del vertice superior derecho del mapa
    GeoPoint abajoIzquierda;  //Ubicacion del vertice inferior izquierdo del mapa
    boolean lastKnown; //Almacena si se pudo conseguir la ultima ubiacion registrada con GPS antes de usar la aplicacion
    BoundingBox boundingBox;

    Button botonLocalizacion;
    Button botonAcercar;
    Button botonAlejar;
    Button botonCam;
    Button botonQR;
    Button botonPausa;
    Button botonFinalizar;

    Marker me;

    //Dialogo
    Dialog especialDialog;

    Button botonobservar;
    TextView botoncerrar;

    //Lugares
    int numeroVisitados;
    int numeroNoVisitados;
    int numeroEspeciales;

    LocalDB localDB;

    Site interes;
    MediaPlayer mp;

    int rallyId;

    /**
     * Se ejecuta cuando se crea la vista
     * Se establece el rango de las coordenadas posibles dentro el mapa
     * Almacena los mapas en memoria externa del celular
     * Muestra el mapa con los distintos niveles de zoom
     * Si se tiene registrada la ultima ubicacion que ha tenido por GPS la muestra en el mapa
     * Inicializa el escuchador
     * @param savedInstanceState Estado actual de la aplicacion
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        rallyId= myIntent.getIntExtra("rallyId",0);

        //Copia el folder
        CopyFolder.copyAssets(this);
        setContentView(R.layout.activity_maps);
        especialDialog=new Dialog(this);
        especialDialog.setContentView(R.layout.alertaespecial);

        //Actualiza el cuadrado del mapa para generar un rango de validas
        //arribaDerecha=new GeoPoint(10.57,-85.3);
        //abajoIzquierda=new GeoPoint(  10.50,-85.5);
        arribaDerecha=new GeoPoint(11.1,-85.5);
        abajoIzquierda=new GeoPoint(  10.75,-85.8);

        lastKnown=false;

        /*Inicializa contadores*/
        numeroVisitados=0;
        numeroNoVisitados=0;
        numeroEspeciales=0;

         /*Base de datos*/
        localDB= new LocalDB(this);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setClickable(false);
        mapView.setMultiTouchControls(false);

        crearBorde();

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(false);
        mapView.setTileSource(new XYTileSource("tiles", 13, 16, 256, ".png", new String[0]));

        //Inicializa el controlador
        mc = (MapController) mapView.getController();
        mc.setZoom(14);
        mc.animateTo( boundingBox.getCenter());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /*Programar botones*/
        botonLocalizacion= findViewById( R.id.mylocation);
        botonLocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastKnown)
                {
                    mc.animateTo(center);
                }
                else {
                    Toast.makeText(getApplicationContext(),"No podemos encontrar tu ubicacion",Toast.LENGTH_SHORT).show();
                }
            }
        });

        botonAcercar= findViewById( R.id.zoom_in);
        botonAcercar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mc.zoomOut();
            }
        });

        botonAlejar= findViewById( R.id.zoom_out);
        botonAlejar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mc.zoomIn();;
            }
        });

        botonCam= findViewById( R.id.botonCamara);
        botonCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irRealidadAumentada();
            }
        });

        botonQR= findViewById( R.id.botonqr);
        botonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irQR();
            }
        });

        botonPausa= findViewById( R.id.pausa);
        botonPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metodoPausa();
            }
        });

        botonFinalizar = findViewById(R.id.btn_stop);
        botonFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizarRally();
            }
        });

        /*Anade puntos*/
        insertarPuntos();
        try{
            if(center!=null)
            {verificarPuntos();}
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5, this);
        }
        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Se encarga de pausar el juego cuando el usuario lo desee
     * */
    public void metodoPausa() {

        new AlertDialog.Builder(this)
                .setTitle("Pausar rally")
                .setMessage("¿Seguro que desea pausar el rally? Posteriormente puede reanudarlo conservando su progreso.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        volveraListaRallies();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void finalizarRally() {
        new AlertDialog.Builder(this)
                .setTitle("Finalizar rally")
                .setMessage("¿Seguro que desea finalizar el rally sin haber visitado todos los puntos?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        guardarCompetencia();
                        volveraListaRallies();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    public void guardarCompetencia(){

    }

    /**
     * Vuelve al activity de juego, donde se selecciona el rally
     * */

    public void volveraListaRallies(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * Inicia la realidad aumentada, envia el id del rally seleccionado por el usuario
     * */
    public void irRealidadAumentada() {
        Intent intent = new Intent(this,ActivityRealidadAumentada.class);
        String id = "" + rallyId;
        intent.putExtra("rallyId",id);
        startActivity(intent);
    }

    public void irQR()
    {

        Intent intent = new Intent(this,ActivityQR.class);
        startActivity(intent);
    }

    /**
     * Remueve un marcador del mapa
     * @param loc  ubicacion donde se encuentra el marcador a elminar
     */
    public void removeMarker(GeoPoint loc) {
        int i=0;
        boolean noEncontrado=true;
        while (i<mapView.getOverlays().size() && noEncontrado)
        { Marker compare = (Marker) mapView.getOverlays().get(i);
            if (compare.getPosition().getLatitude()==loc.getLatitude() && compare.getPosition().getLongitude()==loc.getLongitude())
            {mapView.getOverlays().remove(i);
                noEncontrado=false;}
            i++;
        }
    }

    /**
     * Pone un marcador en el mapa con su debido titulo, dependiendo del numero del iterador de cluster
     * se identifica que tipo es y con esto se le asigna un icono diferente y que al tocarlo genere una accion
     * @param loc Localizacion donde se desea poner el marcador
     * @param ite Numero de cluster que se le da al marcador
     * @param titulo Titulo que se le asocia al marcador
     */
    public void addMarker(GeoPoint loc,int ite,String titulo) {
        Marker marker=new Marker(mapView);
        marker.setPosition(loc);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        marker.setTitle(titulo);
        if (ite==0) {
            marker.setIcon(getResources().getDrawable(R.drawable.currentlocation));
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                /**
                 * Metodo que se dispara al tocar un marcador
                 * Llama a un metodo que despliega la informacion
                 * @param marker marcador que fue tocado
                 * @param mapView mapa donde se encuentra el marcador
                 * */
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    return true;
                }
            });
            me=marker;
        }
        else {
            if (ite==1) {
                marker.setIcon(getResources().getDrawable(R.drawable.novisitado));
                ++numeroNoVisitados;
            }
            if  (ite==2){
                marker.setIcon(getResources().getDrawable(R.drawable.visitado));
                ++numeroVisitados;

            }
            if  (ite==3) {
                marker.setIcon(getResources().getDrawable(R.drawable.dorado));
                ++numeroEspeciales;
            }
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                /**
                 * Metodo que se dispara al tocar un marcador
                 * Llama a un metodo que despliega la informacion
                 * @param marker marcador que fue tocado
                 * @param mapView mapa donde se encuentra el marcador
                 * */
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    informacionMarcador(marker.getPosition(),marker.getTitle());
                    return true;
                }
            });
        }
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    /**
     * Meotodo que se dispara cuando hay una nueva localizacion
     * Si la nueva actualizacion se encuentra dentro del rango elimina la anterior y agrega la nueva al mapa
     * @param location localizacion nueva
     * */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (estaAdentro(location)) {
                GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                if (lastKnown) {
                    mapView.getOverlays().remove(me);
                    center=newLocation;
                    addMarker(newLocation, 0, "Aca estoy");
                }
                else {
                    lastKnown=true;
                    center=newLocation;
                    addMarker(newLocation, 0, "Aca estoy");
                    mc.animateTo(center);
                }
                verificarPuntos();
            }

        }
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
        Toast.makeText(this,"Se activo la ubicacion",Toast.LENGTH_SHORT).show();
    }

    /**
     * Se dispara cuando se desactiva el proveedor
     * @param s Proveedor
     * */
    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this,"Se desconecto la ubicacion",Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * Se dispara cuando se toca un marcador
     * Agarra datos de la base de datos local asociadas al punto y se las pasa a otro activity (VisitasActivity) para mostrar la pantalla informativa sobre el punto.
     * Como aun no esta implementada la base, se tuvo que inventar los datos
     * @param punto Punto del cual deseo informacion
     * @param tipo Estado del punto, si es visitado, no visitado o especial
     */
    public void informacionMarcador(GeoPoint punto, String tipo) {
        boolean noEncontre=true;
        List <Site> sites= localDB.selectAllSitesFromRally(rallyId);
        int ite=0;
        while (ite<sites.size() && noEncontre) {
            double lat=Double.parseDouble(sites.get(ite).getLatitud());
            double lon=Double.parseDouble(sites.get(ite).getLongitud());
            if (lat==punto.getLatitude() && lon==punto.getLongitude()) {
                interes=sites.get(ite);
                noEncontre=false;
            }
            ite++;
        }
        Intent i = new Intent(this, VisitasActivity.class);
        String Distancia;
        if (center!=null) {
            Distancia=String.format("%.1f",center.distanceToAsDouble(punto)) +" metros de distancia";
        } else {
            Distancia="No estas dentro del mapa de juego";
        }
        // if (Condicion con la base de Datos)//Visitado
        switch (Integer.parseInt(tipo)) {
            case 1:
                i.putExtra("Tipo","No Visitado");
                i.putExtra("Imagen","sitio"+interes.getSiteId());
                i.putExtra("Nombre",interes.getSiteName());
                i.putExtra("Numero","Punto #"+interes.getSiteId());
                i.putExtra("Distancia",Distancia);
                break;
            case 2:
                i.putExtra("Tipo","Visitado");
                i.putExtra("Imagen","sitio"+interes.getSiteId());
                i.putExtra("Nombre",interes.getSiteName());
                i.putExtra("Numero","Punto #"+interes.getSiteId());
                i.putExtra("Distancia",Distancia);
                i.putExtra("Geopuntos","Valor: "+interes.getSiteTotalPoints()+ " Petrocoins");
                i.putExtra("Informacion",interes.getSiteDescription());
                break;
            case 3:
                i.putExtra("Tipo","Especial");
                i.putExtra("Imagen","sitio"+interes.getSiteId());
                i.putExtra("Nombre",interes.getSiteName());
                i.putExtra("Numero","Punto #"+interes.getSiteId());
                i.putExtra("Distancia",Distancia);
                i.putExtra("Geopuntos","Valor: "+interes.getSiteTotalPoints()+ " Petrocoins");
                i.putExtra("Informacion",interes.getSiteDescription());
                break;
        }
        startActivity(i);//Inicia la actividad
    }

    /**
     * Verifica si la localizacion se encuentra dentro del area de juego
     * @param location localizacion que se chequea si esta adentro
     */
    public boolean estaAdentro(Location location) {
        if((abajoIzquierda.getLongitude()<location.getLongitude()  && location.getLongitude()<arribaDerecha.getLongitude()) && (arribaDerecha.getLatitude()>location.getLatitude()  && location.getLatitude()>abajoIzquierda.getLatitude())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Establece el borde del area de juego para que no se observe lo gris,
     * tambien se establece el nivel minimo y maximo de zoom
     * */
    public void crearBorde() {
        boundingBox=new BoundingBox(arribaDerecha.getLatitude(),arribaDerecha.getLongitude(),abajoIzquierda.getLatitude(),abajoIzquierda.getLongitude());
        mapView.setMinZoomLevel(13.5);
        mapView.setMaxZoomLevel(20.0);
        mapView.setScrollableAreaLimitDouble(boundingBox);
    }

    /**
     * Este metodo se encarga agarrar todos los sitios de la base de datos y chequear si estamos cerca de un no visitado o uno especial
     * */
    public void verificarPuntos() {
        /*Recorro los markers*/
        List <Site> sites= localDB.selectAllSitesFromRally(rallyId);
        int activosonido=0;
        for (int ite=0;ite<sites.size();ite++) {
            double lat=Double.parseDouble(sites.get(ite).getLatitud());
            double lon=Double.parseDouble(sites.get(ite).getLongitud());
            if (sites.get(ite).getStatus()==4 && center.distanceToAsDouble(new GeoPoint(lat,lon))<=50.0) {
                /*Contador de puntos*/
                localDB.updatePointsAwarded(sites.get(ite).getSiteId());

                localDB.updateSiteVisit(sites.get(ite).getSiteId(),3);
                verificarEspecial(lat,lon,sites.get(ite).getSiteName(),Integer.toString(sites.get(ite).getSiteTotalPoints()));
                activosonido=1;
            }
            if (sites.get(ite).getStatus()==1 && center.distanceToAsDouble(new GeoPoint(lat,lon))<=1000.0) {
                /*Contador de puntos*/
                localDB.updatePointsAwarded(sites.get(ite).getSiteId());

                localDB.updateSiteVisit(sites.get(ite).getSiteId(),2);

                /*Actualizo la vista*/
                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                v.vibrate(3000);

                /*Quita el marcador pasado e inserta otro*/
                final GeoPoint esp=new GeoPoint(lat,lon);
                --numeroNoVisitados;
                removeMarker(esp);
                addMarker(esp,2,"2"); //Visitado

                /*Muestro la notificacion o termino*/
                if(numeroNoVisitados==0) {
                    visiteTodos();
                } else {
                    activosonido=2;
                    verificarNoVisitados(lat,lon,sites.get(ite).getSiteName(),Integer.toString(sites.get(ite).getSiteTotalPoints()));
                }
            }
        }
        if(activosonido==1) {//Sonido de Alerta
            mp = MediaPlayer.create(this,R.raw.alertadesonido);
            mp.start();
        }
        if(activosonido==2) {//Sonido normal
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
    public void verificarEspecial(double lat,double lon,String nombre, String petrocoins) {
        final GeoPoint esp=new GeoPoint(lat,lon);
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(3000);
        addMarker(esp,3,"3"); //Especial

        /*Llenar el activity*/
        ImageView imagen= especialDialog.findViewById(R.id.iv_alerta_imagen);
        imagen.setImageResource(getResources().getIdentifier( "dorado", "drawable", getPackageName()));

        TextView secreto= especialDialog.findViewById( R.id.tv_alerta_secreto);
        secreto.setText("¡Has encontrado un secreto!");

        TextView nom= especialDialog.findViewById( R.id.tv_alerta_nombre);
        nom.setText(nombre);

        TextView valor= especialDialog.findViewById( R.id.tv_alerta_valor);
        valor.setText(petrocoins+ " Petrocoins");

        TextView especial= especialDialog.findViewById( R.id.tv_alerta_especial);
        especial.setText(Integer.toString(numeroEspeciales));

        TextView visitados= especialDialog.findViewById( R.id.tv_alerta_visitados);
        visitados.setText(Integer.toString(numeroVisitados));

        TextView novisitados= especialDialog.findViewById( R.id.tv_alerta_novisitados);
        novisitados.setText(Integer.toString(numeroNoVisitados));

        /*Asigna los botones*/
        botoncerrar= especialDialog.findViewById( R.id.btn_close);
        botoncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                especialDialog.hide();
            }
        });

        botonobservar= especialDialog.findViewById( R.id.btn_observar);
        botonobservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                especialDialog.hide();
                mc.setZoom(15);
                mc.animateTo(esp);
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
        ImageView imagen= especialDialog.findViewById(R.id.iv_alerta_imagen);
        imagen.setImageResource(getResources().getIdentifier( "visitado", "drawable", getPackageName()));

        TextView secreto= especialDialog.findViewById( R.id.tv_alerta_secreto);
        secreto.setText("Bienvenido!");

        TextView nom= especialDialog.findViewById( R.id.tv_alerta_nombre);
        nom.setText(nombre);

        TextView valor= especialDialog.findViewById( R.id.tv_alerta_valor);
        valor.setText(petrocoins+ " Petrocoins");

        TextView especial= especialDialog.findViewById( R.id.tv_alerta_especial);
        especial.setText(Integer.toString(numeroEspeciales));

        TextView visitados= especialDialog.findViewById( R.id.tv_alerta_visitados);
        visitados.setText(Integer.toString(numeroVisitados));

        TextView novisitados= especialDialog.findViewById( R.id.tv_alerta_novisitados);
        novisitados.setText(Integer.toString(numeroNoVisitados));

        /*Asigna los botones*/
        botoncerrar= especialDialog.findViewById( R.id.btn_close);
        botoncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                especialDialog.hide();
            }
        });

        botonobservar= especialDialog.findViewById( R.id.btn_observar);
        botonobservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                especialDialog.hide();
                mc.setZoom(15);
                mc.animateTo(esp);
            }
        });

        especialDialog.show();
    }

    /**
     * Este metodo se encarga de poner los marcadores en el mapa, con los sitios recibidos en la base de datos
     * */
    private void insertarPuntos() {
        List <Site> sites= localDB.selectAllSitesFromRally(rallyId);

        for (int i=0; i<sites.size();i++) {

            Location nuevo = new Location("dummyprovider");
            nuevo.setLatitude(Double.parseDouble(sites.get(i).getLatitud()));
            nuevo.setLongitude(Double.parseDouble(sites.get(i).getLongitud()));

            if (estaAdentro(nuevo)) {
                if (sites.get(i).getStatus() == 1) {
                    addMarker(new GeoPoint(nuevo.getLatitude(), nuevo.getLongitude()), 1, "1");
                }
                if (sites.get(i).getStatus() == 2) {
                    addMarker(new GeoPoint(nuevo.getLatitude(), nuevo.getLongitude()), 2, "2");
                }
                if (sites.get(i).getStatus()==3){
                    addMarker(new GeoPoint(nuevo.getLatitude(), nuevo.getLongitude()), 3, "3");
                }
            }
        }
    }


    /**
     * Indica al usuario cuando ya ha visitado todos los sitios de un rally
     * */
    public void visiteTodos() {
        Intent intent = new Intent(this, finishRallyActivity.class);
        startActivity(intent);
    }


   /**
    * Cuando viene de otro activity se actualiza los sitios segun la base de datos
    * */

   @Override
   protected void  onStart()
   {
       super.onStart();
       mapView.getOverlays().clear();
       numeroNoVisitados=0;
       numeroVisitados=0;
       numeroEspeciales=0;
       insertarPuntos();
       try{
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5, this);
       }
       catch(SecurityException e){
           Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
       }

   }



    @Override
    protected void onStop()
    {
        super.onStop();
        try{
            locationManager.removeUpdates(this);
        }
        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
        }
    }

}