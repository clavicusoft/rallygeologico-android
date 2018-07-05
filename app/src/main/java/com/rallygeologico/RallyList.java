//https://stackoverflow.com/questions/32473804/how-to-get-the-position-of-cardview-item-in-recyclerview/33027953#33027953
package com.rallygeologico;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import FileManager.DownloadTask;
import SqlDatabase.LocalDB;
import SqlEntities.Activity;
import SqlEntities.Competition;
import SqlEntities.Rally;
import SqlEntities.Site;
import SqlEntities.Term;
import SqlEntities.User;


/**
 * Clase para manejar la pantalla con una lista de rallies
 * Created by Pablo Madrigal on 20/04/2018.
 */
public class RallyList extends AppCompatActivity {

    //Variables
    private ArrayList<Rally> rallies_descargados = new ArrayList<Rally>();
    private ArrayList<Rally> rallies_sin_descargar = new ArrayList<Rally>();

    Toolbar toolbar;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;

    LocalDB db;

    /**
     *
     * @param savedInstanceState Estado del programa
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rallylist);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rally Geológico");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialize the RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.mi_lista);

        //Set the Layout Manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the View
        View myLayout = findViewById( R.id.content);

        //Get the data
        db  = new LocalDB(this.getApplicationContext());
        initializeData();

        mDynamicListAdapter = new DynamicListAdapter();
        mLayoutManager = new LinearLayoutManager(RallyList.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mDynamicListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
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
            case R.id.action_refresh:
                // desgarga la informacion de los rallies
                actualizarRallies();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Descarga la informacion de los rallies en los que esta inscrito el usuario
     */
    public void actualizarRallies(){
        User user = db.selectLoggedUser();
        if(tieneConexionInternet()){
            String userId = "" + user.getUserId();
            String resultado = obtenerRallies(userId);
            if (resultado.equalsIgnoreCase("null")) {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Atención")
                        .setMessage("No se ha inscrito en ningún rally. ¿Desea continuar a la página web para inscribirse?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                setWebActivity();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                String toParse = "";
                for (int i = 1; i < resultado.length()-1; i++){
                    toParse += resultado.charAt(i);
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(toParse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jsonObject != null){
                    Competition competition = JSONParser.getCompetition(jsonObject);
                    //Rally rally = JSONParser.getRally(jsonObject);
                    Rally rally = competition.getRally();
                    String url = "http://www.rallygeologico.ucr.ac.cr" + rally.getImageURL();
                    new DownloadTask(this, 1, rally.getName(), url);
                    long id = db.insertRally(rally);
                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle("Descarga")
                            .setMessage("Se han descargado los rallies con éxito.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
                initializeData();
                mDynamicListAdapter.notifyDataSetChanged();
            }
        } else {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Debe conectarse a internet para actualizar los rallies en los que se ha inscrito.")
                    .setPositiveButton("Ok", null)
                    .show();
        }
        mDynamicListAdapter.notifyDataSetChanged();
    }

    /**
     * Descarga todos los sitios asociados a un rally y los guarda en la base de datos local
     * @param rallyId
     */
    public void descargarSitiosRally(int rallyId){
        if(tieneConexionInternet()){
            String resultado = obtenerSitios(rallyId);
            if (resultado.equalsIgnoreCase("null")) {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Atención")
                        .setMessage("Este rally aún no tiene sitios asignados.")
                        .setPositiveButton("Ok", null)
                        .show();
            } else {
                String toParse = "";
                for (int i = 0; i < resultado.length(); i++){
                    toParse += resultado.charAt(i);
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(toParse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jsonObject != null){
                    List<Site> listaSitios = JSONParser.getSitesFromRally(jsonObject);
                    List<Site> listaSitiosNuevos = new LinkedList<>();
                    for(Site site : listaSitios){
                        site = descargarActividadesYTerminosSitio(site);
                        listaSitiosNuevos.add(site);
                    }
                    String id = "" + rallyId;
                    Rally rally = db.selectRallyFromId(id);
                    rally.setSites(listaSitiosNuevos);
                    db.updateRally(rally);
                }
            }
        } else {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Debe conectarse a internet para descargar los sitios.")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    public Site descargarActividadesYTerminosSitio(Site sitio){
        Site site = sitio;
        if(tieneConexionInternet()){
            String resultado = obtenerActividades(site.getSiteId());
            if (resultado.equalsIgnoreCase("null")) {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Atención")
                        .setMessage("Este rally aún no tiene sitios asignados.")
                        .setPositiveButton("Ok", null)
                        .show();
            } else {
                String toParse = "";
                for (int i = 0; i < resultado.length(); i++){
                    toParse += resultado.charAt(i);
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(toParse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jsonObject != null){
                    List<Activity> listaActividades = JSONParser.getActivitiesFromSite(jsonObject);
                    site.setActivityList(listaActividades);
                }
            }

            resultado = obtenerTerminos(site.getSiteId());
            if (resultado.equalsIgnoreCase("null")) {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Atención")
                        .setMessage("Este rally aún no tiene sitios asignados.")
                        .setPositiveButton("Ok", null)
                        .show();
            } else {
                String toParse = "";
                for (int i = 0; i < resultado.length(); i++){
                    toParse += resultado.charAt(i);
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(toParse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jsonObject != null){
                    List<Term> listaTerminos = JSONParser.getTermsFromSite(jsonObject);
                    site.setTermList(listaTerminos);
                }
            }
        } else {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Debe conectarse a internet para descargar las actividades.")
                    .setPositiveButton("Ok", null)
                    .show();
        }
        return site;
    }

    /**
     * Revisa si el dispositivo tiene acceso a internet
     *
     * @return Verdadero si esta conectado, sino falso
     */
    public boolean tieneConexionInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Obtiene un json en forma de string de la base de datos remota con un rally
     * @param userId id del rally
     * @return
     */
    public String obtenerRallies(String userId) {
        String resultado = "JORGE";
        String idConsultaValor = "1";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        resultado = backgroundWorker.doInBackground(idConsultaValor, userId);
        return resultado;
    }

    /**
     * Obtiene un json en forma de string de la base de datos remota con los sitios de un rally
     * @param id id del rally
     * @return
     */
    public String obtenerSitios(int id) {
        String resultado = "JORGE";
        String rallyId = "" + id;
        String idConsultaValor = "2";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        resultado = backgroundWorker.doInBackground(idConsultaValor, rallyId);
        return resultado;
    }

    public String obtenerActividades(int id) {
        String resultado = "JORGE";
        String siteId = "" + id;
        String idConsultaValor = "3";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        resultado = backgroundWorker.doInBackground(idConsultaValor, siteId);
        return resultado;
    }

    public String obtenerTerminos(int id) {
        String resultado = "JORGE";
        String siteId = "" + id;
        String idConsultaValor = "4";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        resultado = backgroundWorker.doInBackground(idConsultaValor, siteId);
        return resultado;
    }

    /**
     * Permite inicializar la informacion de los rallies
     */
    private void initializeData() {
        List<Rally> rallyListTemp = db.selectAllRallies();
        for(int i = 0; i < rallyListTemp.size(); i++){
            if(rallyListTemp.get(i).getIsDownloaded()) {
                rallies_descargados.add(rallyListTemp.get(i));
            }
            else{
                rallies_sin_descargar.add(rallyListTemp.get(i));
            }
        }
    }

    /**
     * Crea la clase para el adaptador dinamico basandose en un RecyclerView
     */
    private class DynamicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

        private static final int FOOTER_VIEW = 1;
        private static final int FIRST_LIST_ITEM_VIEW = 2;
        private static final int FIRST_LIST_HEADER_VIEW = 3;
        private static final int SECOND_LIST_ITEM_VIEW = 4;
        private static final int SECOND_LIST_HEADER_VIEW = 5;

        /**
         * Constructor del adaptador
         */
        public DynamicListAdapter() {
        }

        /**
         * Administra lo que sucede cuando se le da click a una vista dada
         * @param view vista a la cual se le presiono
         */
        @Override
        public void onClick(View view) {

        }

        /**
         * Crea la clase que se va a encargar de "sostener" los elementos que vamos a ver
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            //Items invisibles
            private TextView id_rally_descargado;
            private TextView id_rally_sin_descargar;

            //Items de la primera lista
            private TextView nombre_rally_descargado;
            private TextView memoria_rally_descargado;

            //Items de la segunda lista
            private TextView nombre_rally_sin_descargar;

            // Element of footer view
            private TextView footerTextView;

            //Boton del menu
            private Button boton_menu_rally;
            private Button boton_descargar;

            /**
             * Constructor del ViewHolder
             * @param itemView Vista con la que se esta trabajando actualmente
             */
            public ViewHolder(View itemView) {
                super(itemView);

                //Obtiene la vista de elementos de la primera lista
                id_rally_descargado = (TextView) itemView.findViewById(R.id.id_rally_descargado);
                nombre_rally_descargado = (TextView) itemView.findViewById(R.id.nombre_rally_descargado);
                memoria_rally_descargado = (TextView) itemView.findViewById(R.id.memoria_rally_descargado);

                //Obtiene la vista de elementos de la segunda lista
                id_rally_sin_descargar = (TextView) itemView.findViewById(R.id.id_rally_no_descargado);
                nombre_rally_sin_descargar = (TextView) itemView.findViewById(R.id.nombre_rally_sin_descargar);

                footerTextView = (TextView) itemView.findViewById(R.id.footer);

                //Boton de menu para los rallies descargados
                boton_menu_rally = (Button) itemView.findViewById(R.id.boton_menu);
                boton_descargar = (Button) itemView.findViewById(R.id.boton_descargar);

            }

            /**
             * Llenamos las vistas correspondientes a la lista de rallies descargados
             * @param pos posicion de la vista con la que estamos trabajando
             */
            public void bindViewFirstList(int pos) {
                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                final String nombre_rally = rallies_descargados.get(pos).getName();
                final String id_rally = "" + rallies_descargados.get(pos).getRallyId();

                nombre_rally_descargado.setText(nombre_rally);
                id_rally_descargado.setText(id_rally);
                boton_menu_rally.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Administra lo que sucede cuando se le da click al boton del menu
                     * @param view La vista a la cual se le acaba de dar un click
                     */
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(view.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
                        menuClick(view,getLayoutPosition());
                    }
                });
            }

            /**
             * Llenamos las vistas correspondientes a la lista de rallies sin descargar
             * @param pos posicion de la vista con la que estamos trabajando
             */
            public void bindViewSecondList(int pos) {
                if (rallies_descargados == null) pos = pos - 1;
                else {
                    if (rallies_descargados.size() == 0) pos = pos - 1;
                    else pos = pos - rallies_descargados.size() - 2;
                }

                final String nombre_rally = rallies_sin_descargar.get(pos).getName();
                final String id_rally = "" + rallies_sin_descargar.get(pos).getRallyId();

                nombre_rally_sin_descargar.setText(nombre_rally);
                id_rally_sin_descargar.setText(id_rally);
                boton_descargar.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Administra lo que sucede cuando se le da click al boton de descargar un rally
                     * @param view La vista a la cual se le acaba de dar un click
                     */
                    @Override
                    public void onClick(View view) {

                        //Toast.makeText(view.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
                        downloadClick(view,getLayoutPosition());
                    }
                });
            }
        }

        /**
         * Crea la clase que se va a encargar de "sostener" los elementos del pie de pagina
         */
        public class FooterViewHolder extends ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        /**
         * * Crea la clase que se va a encargar de "sostener" los elementos de la lista con rallies descargados
         */
        private class RallyDescargadoViewHolder extends ViewHolder {
            public RallyDescargadoViewHolder(View itemView) {
                super(itemView);
            }
        }

        /**
         * * * Crea la clase que se va a encargar de "sostener" los elementos de la lista con rallies sin descargar
         */
        private class RallySinDescargarViewHolder extends ViewHolder {
            public RallySinDescargarViewHolder(View itemView) {
                super(itemView);
            }
        }

        /**
         * * * Crea la clase que se va a encargar de "sostener" los elementos del titulo de la lista 1
         */
        private class TituloLista1 extends ViewHolder {
            public TituloLista1(View itemView) {
                super(itemView);
            }
        }

        /**
         * * * * Crea la clase que se va a encargar de "sostener" los elementos del titulo de la lista 2
         */
        private class TituloLista2 extends ViewHolder {
            public TituloLista2(View itemView) {
                super(itemView);
            }
        }

        /**
         * Lo que sucede cuando se crea el RecyclerView encargado de "colocar" cada elemento en su posicion especifica de esa manera podemos ordenar las 2 listas
         * Dependiendo del tipo de vista llama al ViewHolder correspondiente, ya sea para titulos de listas o elementos de las listas en si
         * @param parent referecia a la vista que "almacena" el recyclerview
         * @param viewType tipo de vista con la que estamos trabajando
         * @return devuelve el Recyclerview con la vista que estabamos trabajando
         */
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer, parent, false);
                FooterViewHolder vh = new FooterViewHolder(v);
                return vh;
            } else if (viewType == FIRST_LIST_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rallies_descargados, parent, false);
                RallyDescargadoViewHolder vh = new RallyDescargadoViewHolder(v);

                return vh;

            } else if (viewType == FIRST_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nombre_lista1, parent, false);
                TituloLista1 vh = new TituloLista1(v);
                return vh;

            } else if (viewType == SECOND_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nombre_lista2, parent, false);
                TituloLista2 vh = new TituloLista2(v);
                return vh;

            } else {
                // SECOND_LIST_ITEM_VIEW
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rallies_sin_descargar, parent, false);
                RallySinDescargarViewHolder vh = new RallySinDescargarViewHolder(v);
                return vh;
            }
        }

        /**
         * Es el metodo encargado de "llenar" el RecyclerView con la informacion
         * Como tenemos un metodo que hace eso para cada tipo de vista, entonces este metodo llama al metodo correspondiente dependiendo del tipo
         * @param holder El holder con el que estamos trabajando actualmente
         * @param position Posicion a la que pertenece ese holder
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try{
                 if(holder instanceof TituloLista1){
                    TituloLista1 vh = (TituloLista1) holder;
                } else if(holder instanceof TituloLista2){
                    TituloLista2 vh = (TituloLista2) holder;
                } else if(holder instanceof RallyDescargadoViewHolder){
                    final RallyDescargadoViewHolder vh = (RallyDescargadoViewHolder) holder;
                    vh.bindViewFirstList(position);
                    //holder.itemView.setTag(1,position);
                }else if(holder instanceof RallySinDescargarViewHolder){
                    RallySinDescargarViewHolder vh = (RallySinDescargarViewHolder) holder;
                    vh.bindViewSecondList(position);
                    //holder.itemView.setTag(1,position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Obtiene el numero total de elementos en el RecyclerView incluyendo los titulos de las listas
         * @return
         */
        @Override
        public int getItemCount() {
            int cantidad_rallies_descargados = 0;
            int cantidad_rallies_sin_descargar = 0;
            int total = 0;
            if(rallies_descargados==null && rallies_sin_descargar==null) return 0;
            if(rallies_sin_descargar != null)
                cantidad_rallies_sin_descargar = rallies_sin_descargar.size();
            if(rallies_descargados!=null)
                cantidad_rallies_descargados = rallies_descargados.size();
            if(cantidad_rallies_descargados>0) {
                total += cantidad_rallies_descargados;
                total++;
            }
            if(cantidad_rallies_sin_descargar>0){
                total += cantidad_rallies_sin_descargar;
                total++;
            }
            return total;
        }

        /**
         * Devuelve el tipo de vista de acuerdo con la posicion
         * @param position posicion de la vista que queremos analizar
         * @return el tipo de vista
         */
        @Override
        public int getItemViewType(int position) {

            int firstListSize = 0;
            int secondListSize = 0;

            if (rallies_sin_descargar == null && rallies_descargados == null)
                return super.getItemViewType(position);

            if (rallies_sin_descargar != null)
                secondListSize = rallies_sin_descargar.size();
            if (rallies_descargados != null)
                firstListSize = rallies_descargados.size();

            if (secondListSize > 0 && firstListSize > 0) {
                if (position == 0) return FIRST_LIST_HEADER_VIEW;
                else if (position == firstListSize + 1)
                    return SECOND_LIST_HEADER_VIEW;
                else if (position > firstListSize + 1)
                    return SECOND_LIST_ITEM_VIEW;
                else return FIRST_LIST_ITEM_VIEW;

            } else if (secondListSize > 0 && firstListSize == 0) {
                if (position == 0) return SECOND_LIST_HEADER_VIEW;
                else return SECOND_LIST_ITEM_VIEW;

            } else if (secondListSize == 0 && firstListSize > 0) {
                if (position == 0) return FIRST_LIST_HEADER_VIEW;
                else return FIRST_LIST_ITEM_VIEW;
            }

            return super.getItemViewType(position);
        }
    }

    /**
     * Maneja lo que sucede cuando presionamos el boton de descargar
     * @param v La vista que acabamos de presionar
     * @param position Posicion de la vista en el recycler view
     */
    public void downloadClick(final View v, final int position) {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        final LocalDB db = new LocalDB(this.getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        long megAvailable = bytesAvailable / (1024 * 1024);
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(v.getContext());
        alert_builder.setMessage("Seguro que quiere desargar el rally, Usted cuenta con "+megAvailable+" MB disponibles.").setTitle("Desea descargar el rally con id: "+" ");
        alert_builder.setPositiveButton("Descargar", new DialogInterface.OnClickListener() {
            /**
             * maneja que sucede cuando se presiona el boton de "ok" en el cuadro de dialogo
             * @param dialog Cuadro de dialogo
             * @param id identificador unico
             */
            public void onClick(DialogInterface dialog, int id) {
                int rallyid = changeRallyState(position);// User clicked OK button
                descargarSitiosRally(rallyid);
                List<Site> siteList = db.selectAllSitesFromRally(rallyid);
            }
        });
        alert_builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            /**
             * maneja que sucede cuando se presiona el boton de "ok" en el cuadro de dialogo
             * @param dialog Cuadro de dialogo
             * @param id identificador unico
             */
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog alert = alert_builder.create();
        alert.show();
    }

    /**
     * Maneja lo que sucede cuando presionamos el menu
     * @param v La vista que acabamos de presionar
     */
    public void menuClick(final View v, final int position){
        //creating a popup menu
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        //inflating menu from xml resource
        popup.inflate(R.menu.menu_rally_descargado);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            /**
             * Maneja todo lo interno a las opciones del menu
             * @param item perteneciente al menu
             * @return si se logro el correcto funcionamiento del menu
             */
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.eliminar:
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(v.getContext());
                        alert_builder.setMessage("Seguro que quiere eliminar el rally").setTitle("Desea eliminar el rally con id: "+" ");
                        alert_builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            /**
                             * Maneja lo que sucede cuando se presiona la opcion de eliminar en el menu
                             * @param dialog interfaz de dialogo que se esta utilizando
                             * @param id id unico para identificar el item del menu
                             */
                            public void onClick(DialogInterface dialog, int id) {
                                changeRallyState(position);// Se elimina el Rally
                            }
                        });
                        alert_builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            /**
                             * Maneja lo que sucede cuando se presiona la opcion de Cancelar en el menu
                             * @param dialog interfaz de dialogo que se esta utilizando
                             * @param id id unico para identificar el item del menu
                             */
                            public void onClick(DialogInterface dialog, int id) {
                                // No hace nada por que el usuario cancelo la instruccion
                            }
                        });
                        AlertDialog alert = alert_builder.create();
                        alert.show();
                        break;
                    case R.id.resultados:
                        displayResultsview();
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }

    public void displayResultsview(){
        Intent intent = new Intent(this,finishRallyActivity.class);
        startActivity(intent);
    }


    public int changeRallyState(int position){
        position --; //Le quitamos el encabezado de la primera lista que siempre tiene la posicion 0
        Rally rallyTemp;
        int rallyId = 0;
        if(position > rallies_descargados.size()){ //Esta en la segunda lista
            position--;//Le quitamos el encabezado de la segunda lista
            position = position - rallies_descargados.size(); //Encontramos la posicion en la segunda lista
            rallyTemp = rallies_sin_descargar.get(position);
            rallyId = rallyTemp.getRallyId();
            rallies_sin_descargar.remove(position);
            rallyTemp.setDownloaded(true);
            rallies_descargados.add(rallyTemp);
        }
        else if(rallies_descargados.size()>0){
            rallyTemp = rallies_descargados.get(position);
            rallyId = rallyTemp.getRallyId();
            rallies_descargados.remove(position);
            rallyTemp.setDownloaded(false);
            rallies_sin_descargar.add(rallyTemp);
        }
        else{
            rallyTemp = rallies_sin_descargar.get(position);
            rallyId = rallyTemp.getRallyId();
            rallies_sin_descargar.remove(position);
            rallyTemp.setDownloaded(true);
            rallies_descargados.add(rallyTemp);
        }
        db.updateRally(rallyTemp);
        mDynamicListAdapter.notifyDataSetChanged();
        return rallyId;
    }

    public void updateRallyList(View view){
        mDynamicListAdapter.notifyDataSetChanged();
    }

    public void setWebActivity() {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("URL", "http://rallygeologico.ucr.ac.cr");
        startActivity(intent);
    }


}
