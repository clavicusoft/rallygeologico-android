package com.rallygeologico;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class VisitasActivity extends AppCompatActivity {

    String nombreImagen;
    String tipo;
    String nombre;
    String numero;
    String distancia;
     String geopuntos;
     String informacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitas);

        Intent myIntent = getIntent(); // gets the previously created intent

        tipo= myIntent.getStringExtra("Tipo");
        nombreImagen= myIntent.getStringExtra("Imagen");
        nombre = myIntent.getStringExtra("Nombre");
         numero = myIntent.getStringExtra("Numero");
         distancia = myIntent.getStringExtra("Distancia");

         if (!tipo.equals("No Visitado")) {
             geopuntos = myIntent.getStringExtra("Geopuntos");
             informacion = myIntent.getStringExtra("Informacion");
         }

        ImageView imagenNoVisitado= (ImageView) findViewById(R.id.imagenPunto);

        imagenNoVisitado.setImageResource(getResources().getIdentifier( nombreImagen, "drawable", getPackageName()));


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
                secretoNoVisitado.setText("¡Ya lo visitaste!");
            }
            else //Es especial
    {
        secretoNoVisitado.setText("¡Has encontrado un secreto!");

    }
        }

    }
}
