//https://stackoverflow.com/questions/32473804/how-to-get-the-position-of-cardview-item-in-recyclerview/33027953#33027953
package com.rallygeologico;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import MenuRallies.Rally;

public class RallyList extends AppCompatActivity {

    //Variables
    private ArrayList<Rally> rallies_descargados = new ArrayList<Rally>();
    private ArrayList<Rally> rallies_sin_descargar = new ArrayList<Rally>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;
    private ImageButton boton_menu;


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

        mDynamicListAdapter = new DynamicListAdapter(RallyList.this);
        mLayoutManager = new LinearLayoutManager(RallyList.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mDynamicListAdapter);

        //Obtenemos la referencia a los controles
        //boton_menu = (ImageButton) findViewById(R.id.boton_menu);

        //Asociamos los menús contextuales a los controles
        //registerForContextMenu(boton_menu);

    }

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

    private class DynamicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int FIRST_LIST_ITEM_VIEW = 2;
        private static final int FIRST_LIST_HEADER_VIEW = 3;
        private static final int SECOND_LIST_ITEM_VIEW = 4;
        private static final int SECOND_LIST_HEADER_VIEW = 5;

        private Context context;

        public DynamicListAdapter(Context context) {
            this.context = context;
        }

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
            private ImageButton boton_menu_rally;


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
                boton_menu_rally = (ImageButton) itemView.findViewById(R.id.boton_menu);
            }

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
            }

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
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(view.getContext(), view);
                        popup.inflate(R.menu.menu_rally_descargado);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Toast.makeText(boton_menu_rally.getContext(), "DO SOME STUFF HERE", Toast.LENGTH_LONG).show();
                                return true;
                            }
                        });
                        popup.show();
                    }
                });

            }

        }

        public class FooterViewHolder extends ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class RallyDescargadoViewHolder extends ViewHolder {
            private ImageButton boton_menu_rally2;

            public RallyDescargadoViewHolder(View itemView) {
                super(itemView);
                boton_menu_rally2 = (ImageButton) itemView.findViewById(R.id.boton_menu);
            }
        }

        private class RallySinDescargarViewHolder extends ViewHolder {
            public RallySinDescargarViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class TituloLista1 extends ViewHolder {
            public TituloLista1(View itemView) {
                super(itemView);
            }
        }

        private class TituloLista2 extends ViewHolder {
            public TituloLista2(View itemView) {
                super(itemView);
            }
        }

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
                    vh.boton_menu_rally2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //creating a popup menu
                            PopupMenu popup = new PopupMenu(context, vh.boton_menu_rally2);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.menu_rally_descargado);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.jugar:
                                            //handle menu1 click
                                            break;
                                        case R.id.eliminar:
                                            //handle menu2 click
                                            break;
                                        case R.id.subir_resultados:
                                            //handle menu3 click
                                            break;
                                    }
                                    return false;
                                }
                            });
                            //displaying the popup
                            popup.show();
                        }
                    });
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

    public void onDeleteClick(View v) {

        AlertDialog.Builder alert_builder = new AlertDialog.Builder(v.getContext());
        alert_builder.setMessage("¿Seguro que quiere eliminar el rally?").setTitle("Eliminar Rally");
        alert_builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        alert_builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog alert = alert_builder.create();
        alert.show();
    }

    public void downloadClick(View v) {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(v.getContext());
        alert_builder.setMessage("¿Seguro que quiere desargar el rally?").setTitle("¿Desea descargar el rally con id: "+" ?");
        alert_builder.setPositiveButton("Descargar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        alert_builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog alert = alert_builder.create();
        alert.show();
    }

    public void menuClick(final View v){
        //creating a popup menu
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        //inflating menu from xml resource
        popup.inflate(R.menu.menu_rally_descargado);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.jugar:
                        //handle menu1 click
                        break;
                    case R.id.eliminar:
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(v.getContext());
                        alert_builder.setMessage("¿Seguro que quiere eliminar el rally?").setTitle("¿Desea eliminar el rally con id: "+" ?");
                        alert_builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                            }
                        });
                        alert_builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
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
