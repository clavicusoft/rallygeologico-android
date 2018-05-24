package com.rallygeologico;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
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

import java.util.List;


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

    Marker me;

    //Dialogo
    Dialog especialDialog;

    Button botonobservar;
    TextView botoncerrar;

    //Lugares
    int numeroVisitados;
    int numeroNoVisitados;
    int numeroEspeciales;

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

        //Copia el folder
        CopyFolder.copyAssets(this);

        setContentView(R.layout.activity_maps);

        Toast.makeText(this,"Cargando el mapa",Toast.LENGTH_LONG).show();

        //Actualiza el cuadrado del mapa para generar un rango de validas

       // arribaIzquierda=new GeoPoint(10.0804,-84.3530);
        arribaDerecha=new GeoPoint(11.1124, -85.2827);
        abajoIzquierda=new GeoPoint(  10.6775, -86.0264);

        lastKnown=false;

        especialDialog=new Dialog(this);

        /*Inicializa contadores*/
         numeroVisitados=0;
         numeroNoVisitados=0;
         numeroEspeciales=0;

        mapView = (MapView) findViewById(R.id.mapview);

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(false);


        //Inicializa el controlador
        mc = (MapController) mapView.getController();

        mc.setZoom(14);


        mapView.setTileSource(new XYTileSource("tiles", 10, 15, 256, ".png", new String[0]));

        crearBorde();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //Pruebas
        addMarker(new GeoPoint(10.8643, -85.6947),1,"1"); //No visitado
        addMarker(new GeoPoint(11.0356, -85.5849),2,"2"); //Visitado

        try {
            Location ultimo = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (ultimo != null ) {
                if (estaAdentro(ultimo)) {
                    center = new GeoPoint(ultimo.getLatitude(), ultimo.getLongitude());
                    mc.animateTo(center);
                    addMarker(center, 0, "Ultima Ubicacion Registrada");
                    lastKnown = true;

                verificarPuntos();
                }
            }

            if (lastKnown==false)//Centro el mapa y digo que busco una ubicación

            {
                mc.animateTo( boundingBox.getCenter());
                Toast.makeText(this,"Buscando localizacion...",Toast.LENGTH_SHORT).show();

            }


        }

        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
        }




        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, this);
        }
        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
        }

        /*Programar botones*/


       botonLocalizacion= findViewById( R.id.mylocation);
        botonLocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastKnown)
                {mc.animateTo(center);
                }
                else {
                    Toast.makeText(getApplicationContext(),"No podemos encontrar tu ubicación",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"Realidad Aumentada en Trabajo",Toast.LENGTH_SHORT).show();

            }
        });

        botonQR= findViewById( R.id.botonqr);
        botonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Codigo QR en Trabajo",Toast.LENGTH_SHORT).show();

            }
        });



    }

    public void removeMarker(GeoPoint loc)
    {
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

    public void addMarker(GeoPoint loc,int ite,String titulo)
    {
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

        if (location != null)
        {
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

              //  verificarPuntos();

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
     * Se dispara cuando se toca un marcador
     * Agarra datos de la base de datos local asociadas al punto y se las pasa a otro activity (VisitasActivity) para mostrar la pantalla informativa sobre el punto.
     * Como aun no esta implementada la base, se tuvo que inventar los datos
     * @param punto Punto del cual deseo informacion
     * @param tipo Estado del punto, si es visitado, no visitado o especial
     * */

    public void informacionMarcador(GeoPoint punto, String tipo)
    {
        Intent i = new Intent(this, VisitasActivity.class);

        String Distancia;

        if (center!=null)
        {Distancia=String.format("%.1f",center.distanceToAsDouble(punto)) +" metros de distancia";}

        else
            {Distancia="No estas dentro del mapa de juego";}

        // if (Condicion con la base de Datos)//Visitado
        switch (Integer.parseInt(tipo))
        {
            case 1:
                i.putExtra("Tipo","No Visitado");
                i.putExtra("Imagen","volcan");
                i.putExtra("Nombre","Volcan Arenal");
                i.putExtra("Numero","Punto #1");
                i.putExtra("Distancia",Distancia);
                break;
            case 2:
                i.putExtra("Tipo","Visitado");
                i.putExtra("Imagen","playa");
                i.putExtra("Nombre","Playa Carrillo");
                i.putExtra("Numero","Punto #2");
                i.putExtra("Distancia",Distancia);
                i.putExtra("Geopuntos","Valor: 50 Petrocoins");
                i.putExtra("Informacion","Carrillo esta repleta de palmeras y arena blanca. La costa por lo general nunca esta muy concurrida, lo que la convierte en un lugar perfecto para relajarse y escapar de los dias estresantes en el trabajo. Al tiempo que usted se relaja, puede disfrutar de impresionantes paisajes de las exuberantes montanas que se dibujan en la distancia y que hacen las veces de un hermoso marco para la playa.");
                break;
            case 3:
                i.putExtra("Tipo","Especial");
                i.putExtra("Imagen","rio");
                i.putExtra("Nombre","Rio Agrio");
                i.putExtra("Numero","Punto Extra");
                i.putExtra("Distancia",Distancia);
                i.putExtra("Geopuntos","Valor: 100 Petrocoins");
                i.putExtra("Informacion","Catarata Rio Agrio y las Pozas Celestes se han convertido en uno de los principales atractivos turisticos del canton de Sarchi, ya no solo es conocido por la artesania sino tambien por la majestuosa naturaleza en esta zona del pais");
                break;
        }

        startActivity(i);//Inicia la actividad

    }

    public boolean estaAdentro(Location location)
    {
        if((abajoIzquierda.getLongitude()<location.getLongitude()  && location.getLongitude()<arribaDerecha.getLongitude()) && (arribaDerecha.getLatitude()>location.getLatitude()  && location.getLatitude()>abajoIzquierda.getLatitude()))
        {
            return true;
        }
        else {
            return false;
        }
    }

    public void crearBorde()
    {  boundingBox=new BoundingBox(arribaDerecha.getLatitude(),arribaDerecha.getLongitude(),abajoIzquierda.getLatitude(),abajoIzquierda.getLongitude());
        mapView.setMinZoomLevel(12.7);
        mapView.setMaxZoomLevel(20.0);
        mapView.setScrollableAreaLimitDouble(boundingBox);}

     public void verificarPuntos()
     {

         try{
             locationManager.removeUpdates(this);
         }
         catch(SecurityException e){
             Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
         }

         //Verificar eliminarlo
         double lat = 10.8333;
         double lon =  -85.3885;
         verificarEspecial(lat,lon);

         lat=10.8643;
         lon=-85.6947;

         verificarNoVisitados(lat,lon);

         try{
             locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, this);
         }
         catch(SecurityException e){
             Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_SHORT).show();
         }


     }

     public void verificarEspecial(double lat,double lon)
     {
         final GeoPoint esp=new GeoPoint(lat,lon);

         if (center.distanceToAsDouble(new GeoPoint(lat,lon))<=10000.0) //Menor o igual a un km
         {
             Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
             v.vibrate(3000);

             addMarker(esp,3,"3"); //Especial

             especialDialog.setContentView(R.layout.alertaespecial);

             /*Llenar el activity*/

             ImageView imagen= especialDialog.findViewById(R.id.iv_alerta_imagen);

             imagen.setImageResource(getResources().getIdentifier( "dorado", "drawable", getPackageName()));


             TextView secreto= especialDialog.findViewById( R.id.tv_alerta_secreto);
             secreto.setText("¡Has encontrado un secreto!");

             TextView valor= especialDialog.findViewById( R.id.tv_alerta_valor);
             valor.setText("Playa Carrillo: 20 Petrocoins");

             TextView especial= especialDialog.findViewById( R.id.tv_alerta_especial);
             especial.setText(Integer.toString(numeroEspeciales));

             TextView visitados= especialDialog.findViewById( R.id.tv_alerta_visitados);
             visitados.setText(Integer.toString(numeroVisitados));

             TextView novisitados= especialDialog.findViewById( R.id.tv_alerta_novisitados);
             novisitados.setText(Integer.toString(numeroNoVisitados));


             /*Asigna los botones*/

             botoncerrar= especialDialog.findViewById( R.id.btn_close);
             especialDialog.show();

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
                     mc.animateTo(esp);
                 }
             });

         }
}

    public void verificarNoVisitados( double lat, double lon)
    {
        final GeoPoint esp=new GeoPoint(10.8643, -85.6947);

        if (center.distanceToAsDouble(new GeoPoint(lat,lon))<=5000.0) //Menor o igual a un km
        {
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(3000);


            /*Quita el marcador pasado e inserta otro*/
            --numeroNoVisitados;
            removeMarker(esp);
           addMarker(esp,2,"2"); //Visitado

            especialDialog.setContentView(R.layout.alertaespecial);

            /*Llenar el activity*/

            ImageView imagen= especialDialog.findViewById(R.id.iv_alerta_imagen);

            imagen.setImageResource(getResources().getIdentifier( "visitado", "drawable", getPackageName()));


            TextView secreto= especialDialog.findViewById( R.id.tv_alerta_secreto);
            secreto.setText("¡Bienvenido!");

            TextView valor= especialDialog.findViewById( R.id.tv_alerta_valor);
            valor.setText("Playa Carrillo: 20 Petrocoins");

            TextView especial= especialDialog.findViewById( R.id.tv_alerta_especial);
            especial.setText(Integer.toString(numeroEspeciales));

            TextView visitados= especialDialog.findViewById( R.id.tv_alerta_visitados);
            visitados.setText(Integer.toString(numeroVisitados));

            TextView novisitados= especialDialog.findViewById( R.id.tv_alerta_novisitados);
            novisitados.setText(Integer.toString(numeroNoVisitados));


            /*Asigna los botones*/

            botoncerrar= especialDialog.findViewById( R.id.btn_close);
            especialDialog.show();

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
                    mc.animateTo(esp);
                }
            });

        }
    }

}