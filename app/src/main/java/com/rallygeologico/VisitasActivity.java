package com.rallygeologico;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import FileManager.FileManager;

/**
 * Esta clase se encarga de mostrar la informacion asociada a un punto que ha sido tocado en el mapa
 * */

public class VisitasActivity extends AppCompatActivity implements terminoImagen.OnFragmentInteractionListener{

    String nombreImagen;
    String tipo;
    String nombre;
    String numero;
    String distancia;
    String geopuntos;
    String informacion;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),3);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_termino);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FileManager fm = new FileManager();


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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {

            Fragment fragment = null;
            Bundle args = new Bundle();
            switch (sectionNumber) {
                case 1:
                    fragment = new terminoImagen();
                    args = new Bundle();
                    args.putString("IMAGE_NAME", "Rally 3");
                    fragment.setArguments(args);
                    break;
                case 2:
                    fragment = new terminoImagen();
                    args = new Bundle();
                    args.putString("IMAGE_NAME", "Rally 1");
                    fragment.setArguments(args);
                    break;
                case 3:
                    fragment = new terminoImagen();
                    args = new Bundle();
                    args.putString("IMAGE_NAME", "Rally 3");
                    fragment.setArguments(args);
                    break;
            }
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_media_activity, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        int numberOfTerms;

        public SectionsPagerAdapter(FragmentManager fm, int terms) {
            super(fm);
            numberOfTerms = terms;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        /*Mostrar el size de terminos*/
        @Override
        public int getCount() {
            // Show 3 total pages.
            return numberOfTerms;
        }
    }

}
