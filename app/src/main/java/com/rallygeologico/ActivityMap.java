package com.rallygeologico;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


public class ActivityMap extends AppCompatActivity implements LocationListener {

    MapView mapView;
    MapController mc;
    LocationManager locationManager;

    GeoPoint center;
    GeoPoint arribaIzquierda;
    GeoPoint arribaDerecha;
    GeoPoint abajoIzquierda;
    int numberMarker; //El 0 siempre es para el actual
    boolean lastKnown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Actualiza el cuadrado

        arribaIzquierda=new GeoPoint(10.0804,-84.3530);
        arribaDerecha=new GeoPoint(10.0804, -83.7028);
        abajoIzquierda=new GeoPoint(  9.912, -84.3530);





        numberMarker=1; //El 0 siempre es la ubicacion real
        lastKnown=false;

        mapView = (MapView) findViewById(R.id.mapview);

        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(false);

        //Copia el folder
        CopyFolder.copyAssets(this);

        //Inicializa el controlador
        mc = (MapController) mapView.getController();

        mc.setZoom(18);

        //Carga los tiles
        mapView.setTileSource(new XYTileSource("tiles", 10, 18, 256, ".png", new String[0]));

       // obtenerDistancia(9.93407, -84.05657);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            Location ultimo = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(ultimo != null)
            {if ((arribaIzquierda.getLongitude()<ultimo.getLongitude()  && ultimo.getLongitude()<arribaDerecha.getLongitude()) && (arribaIzquierda.getLatitude()>ultimo.getLatitude()  && ultimo.getLatitude()>abajoIzquierda.getLatitude())  ){
                center = new GeoPoint(ultimo.getLatitude(),ultimo.getLongitude());
                mc.animateTo(center);
                addMarker(center,0,"Ultima Ubicacion Registrada");
                lastKnown=true;
            }
            }

        }
        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_LONG).show();
        }


        //Pruebas
        addMarker(new GeoPoint(9.93409, -84.05638),1,"1"); //No visitado
        addMarker(new GeoPoint(9.9172, -84.04664),2,"2"); //Visitado
        addMarker(new GeoPoint(  9.93203, -84.05242),3,"3"); //Especial

        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
        }
        catch(SecurityException e){
            Toast.makeText(this,"No pedi el permiso bien",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public void addMarker(GeoPoint loc,int ite,String titulo)
    {
        Marker marker=new Marker(mapView);
        marker.setPosition(loc);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        marker.setTitle(titulo);
        if (ite==0) {
            marker.setIcon(getResources().getDrawable(R.drawable.me));
        }
        else {
            if (ite==1) {
                marker.setIcon(getResources().getDrawable(R.drawable.novisitado));
            }
            if  (ite==2){
                marker.setIcon(getResources().getDrawable(R.drawable.visitado));
            }
            if  (ite==3) {
                marker.setIcon(getResources().getDrawable(R.drawable.dorado));
            }
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    informacionMarcador(marker.getPosition(),marker.getTitle()); //Cambiar getTittle para manejar con Base de datos
                    return true;
                }
            });

        }

       // mapView.getOverlays().clear();
        mapView.getOverlays().add(ite,marker);
        mapView.invalidate();
    }

    public void removeMarker(int ite)
    {
        Marker marker=new Marker(mapView);
        mapView.getOverlays().remove(ite);
        mapView.invalidate();
    }

    public void visiteElMarker(GeoPoint loc)
    {
        Marker marker=new Marker(mapView);
        marker.setPosition(loc);
      //  marker.setIcon(Drawable.createFromPath("@drawable/visitadoMarker"));
    }

    public void obtenerDistancia(double lat, double lon)
    {
        GeoPoint nuevoPunto = new GeoPoint(lat,lon);
        //addMarker(nuevoPunto);
        Toast.makeText(this,"Distancia al punto: "+Double.toString(center.distanceToAsDouble(nuevoPunto)),Toast.LENGTH_LONG).show();
    }

    /*Metodos del location listener*/

    @Override
    public void onLocationChanged(Location location) {

        if (location != null)
        {
            if((arribaIzquierda.getLongitude()<location.getLongitude()  && location.getLongitude()<arribaDerecha.getLongitude()) && (arribaIzquierda.getLatitude()>location.getLatitude()  && location.getLatitude()>abajoIzquierda.getLatitude()))
                {
                GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                if (lastKnown) {
                    removeMarker(0);
                }
                addMarker(newLocation, 0, "Aca estoy");
            }
        }
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
/*
    //Ahorro de energía
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (locationManager!=null)
        {
            locationManager.removeUpdates(this);
        }
    }
    */

    public void informacionMarcador(GeoPoint punto, String tipo)
    {
        Intent i = new Intent(this, VisitasActivity.class);

        // if (Condicion con la base de Datos)//Visitado
        switch (Integer.parseInt(tipo))
        {
            case 1:
                i.putExtra("Tipo","No Visitado");
                i.putExtra("Imagen","volcan");
                i.putExtra("Nombre","Volcan Arenal");
                i.putExtra("Numero","Punto #1");
                i.putExtra("Distancia",String.format("%.1f",center.distanceToAsDouble(punto))+" metros de distancia");
                break;
            case 2:
                i.putExtra("Tipo","Visitado");
                i.putExtra("Imagen","playa");
                i.putExtra("Nombre","Playa Carrillo");
                i.putExtra("Numero","Punto #2");
                i.putExtra("Distancia",String.format("%.1f",center.distanceToAsDouble(punto))+" metros de distancia");
                i.putExtra("Geopuntos","Valor: 50 Geopuntos");
                i.putExtra("Informacion","Carrillo esta repleta de palmeras y arena blanca. La costa por lo general nunca está muy concurrida, lo que la convierte en un lugar perfecto para relajarse y escapar de los días estresantes en el trabajo. Al tiempo que usted se relaja, puede disfrutar de impresionantes paisajes de las exuberantes montañas que se dibujan en la distancia y que hacen las veces de un hermoso marco para la playa.");
                break;
            case 3:
                i.putExtra("Tipo","Especial");
                i.putExtra("Imagen","rio");
                i.putExtra("Nombre","Rio Agrio");
                i.putExtra("Numero","Punto Extra");
                i.putExtra("Distancia",String.format("%.1f",center.distanceToAsDouble(punto))+" metros de distancia");
                i.putExtra("Geopuntos","Valor: 100 Geopuntos");
                i.putExtra("Informacion","Catarata Río Agrio y las Pozas Celestes se han convertido en uno de los principales atractivos turísticos del cantón de Sarchí, ya no solo es conocido por la artesanía sino también por la majestuosa naturaleza en esta zona del país");
                break;
        }

        startActivity(i);

    } //Inicia la actividad

}