package com.rallygeologico;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Esta clase se encarga de mostrar la informacion asociada a un punto que ha sido tocado en el mapa
 * */

public class VisitasActivity extends AppCompatActivity {

    String nombreImagen;
    String tipo;
    String nombre;
    String numero;
    String distancia;
    String geopuntos;
    String informacion;

    Toolbar toolbar;

    /**
     * Se ejecuta cuando se crea la vista
     * Despliega la informacion sobre el punto.
     * El layout que despliega la informacion es dinamico y depende de lo que recibamos en el intent.
     * @param savedInstanceState Estado actual de la aplicacion
     *
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitas);

/*Toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rally Geológico");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent myIntent = getIntent(); // gets the previously created intent

        tipo= myIntent.getStringExtra("Tipo");
        nombreImagen=  myIntent.getStringExtra("Imagen");
        nombre = myIntent.getStringExtra("Nombre");
        numero = myIntent.getStringExtra("Numero");
        distancia = myIntent.getStringExtra("Distancia");

        if (!tipo.equals("No Visitado")) {
            geopuntos = myIntent.getStringExtra("Geopuntos");
            informacion = myIntent.getStringExtra("Informacion");
        }

        ImageView imagenNoVisitado= (ImageView)     findViewById(R.id.imagenPunto);

        Resources res = getResources();

        int resourceId = res.getIdentifier( nombreImagen, "drawable", getPackageName() );

        imagenNoVisitado.setImageResource( resourceId );


        TextView nombrePuntoNoVisitado=(TextView) findViewById(R.id.nombrePunto);
        nombrePuntoNoVisitado.setText(nombre);

        TextView numeroPuntoNoVisitado=(TextView) findViewById(R.id.numeroPunto);
        numeroPuntoNoVisitado.setText(numero);

        TextView distanciaNoVisitado=(TextView) findViewById(R.id.distanciaPunto);
        distanciaNoVisitado.setText(distancia);

        TextView geopuntosNoVisitado = (TextView) findViewById(R.id.geopuntosPunto);
        TextView informacionNoVisitado = (TextView) findViewById(R.id.informacionPunto);
        TextView secretoNoVisitado = (TextView) findViewById(R.id.secretoPunto);


        if (tipo.equals("No Visitado")) {
            geopuntosNoVisitado.setVisibility(View.GONE);
            informacionNoVisitado.setVisibility(View.GONE);
            secretoNoVisitado.setVisibility(View.GONE);

        }

        else{
            geopuntosNoVisitado.setVisibility(View.VISIBLE);
            informacionNoVisitado.setVisibility(View.VISIBLE);
            secretoNoVisitado.setVisibility(View.VISIBLE);

            geopuntosNoVisitado.setText(geopuntos);
            informacionNoVisitado.setText(informacion);


            if (tipo.equals("Visitado")) //Ya lo visite pero no es especial
            {
                secretoNoVisitado.setText("Ya lo visitaste");
            }
            else //Es especial
            {
                secretoNoVisitado.setText("Secreto");
            }
        }

    }

    /**
     * Acciones a realizar cuando se clickea un boton de la barra superior de ayuda
     * @param item opcion seleccionada
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // vuelve a la pantalla anterior
                onBackPressed();
                break;
        }
        return true;
    }


}
