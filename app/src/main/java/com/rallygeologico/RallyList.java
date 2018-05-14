//https://stackoverflow.com/questions/32473804/how-to-get-the-position-of-cardview-item-in-recyclerview/33027953#33027953
package com.rallygeologico;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import SqlEntities.Rally;

/**
 * Clase para manejar la pantalla con una lista de rallies
 * Created by Pablo Madrigal on 20/04/2018.
 */
public class RallyList extends AppCompatActivity {

    //Variables
    private ArrayList<Rally> rallies_descargados = new ArrayList<Rally>();
    private ArrayList<Rally> rallies_sin_descargar = new ArrayList<Rally>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;

    /**
     *
     * @param savedInstanceState Estado del programa
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rallylist);

        //Initialize the RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.mi_lista);

        //Set the Layout Manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Get the data
        initializeData();

        mDynamicListAdapter = new DynamicListAdapter();
        mLayoutManager = new LinearLayoutManager(RallyList.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mDynamicListAdapter);


    }

    /**
     * Permite inicializar la informacion de los rallies
     */
    private void initializeData() {
        //Get the resources from the XML file
        String[] lista_rallies_descargados = getResources().getStringArray(R.array.rally_titles_Descargados);
        String[] lista_rallies_sin_descargar = getResources().getStringArray(R.array.rally_titles_NoDescargados);
        String[] rallyInfo = getResources().getStringArray(R.array.rally_info);

        //Clear the existing data (to avoid duplication)
        rallies_descargados.clear();
        rallies_sin_descargar.clear();

        //Create the ArrayList of Sports objects with the titles and information about each rally
        for(int i=0;i<lista_rallies_descargados.length;i++){
            int memoria = 10 + (int)(Math.random() * 50);
            rallies_descargados.add(new Rally(i+0,lista_rallies_descargados[i],rallyInfo[i],i,"hola", "Este rally utiliza:"+memoria+"Mb",false));
        }

        for(int i=0;i<lista_rallies_sin_descargar.length;i++){
            int memoria = 10 + (int)(Math.random() * 50);
            rallies_sin_descargar.add(new Rally(i+10,lista_rallies_sin_descargar[i],rallyInfo[i],i,"hola", "Este rally pesa :"+memoria+"Mb",false));
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
                final String memoria_rally = rallies_descargados.get(pos).getMemoryUsage();
                final String id_rally = "" + rallies_descargados.get(pos).getRallyId();

                nombre_rally_descargado.setText(nombre_rally);
                memoria_rally_descargado.setText(memoria_rally);
                id_rally_sin_descargar.setText(id_rally);
                boton_menu_rally.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Administra lo que sucede cuando se le da click al boton del menu
                     * @param view La vista a la cual se le acaba de dar un click
                     */
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
                        downloadClick(view,getLayoutPosition());
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
                        Toast.makeText(view.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
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
    public void downloadClick(View v, int position) {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
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
                // User clicked OK button
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
    public void menuClick(final View v){
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
                    case R.id.jugar:
                        /*Intent intent = new Intent(v.getContext(), ActivityMap.class);
                        startActivity(intent);*/
                        break;
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
                                // Se elimina el Rally
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
                    case R.id.subir_resultados:
                        //Intent intent = new Intent(v.getContext(),InformacionRally.class);
                        //startActivity(intent);
                        break;
                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }
}
