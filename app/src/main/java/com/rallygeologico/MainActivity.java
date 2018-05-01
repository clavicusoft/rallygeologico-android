package com.rallygeologico;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import MenuRallies.Rally;
import MenuRallies.RallyAdapter;

public class MainActivity extends AppCompatActivity {

    //Variables
    private ArrayList<Rally> rallies_descargados = new ArrayList<Rally>();
    private ArrayList<Rally> rallies_sin_descargar = new ArrayList<Rally>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the RecyclerView
        recyclerView = (RecyclerView)findViewById(R.id.mi_lista);

        //Set the Layout Manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Get the data
        initializeData();

        mDynamicListAdapter = new DynamicListAdapter();
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mDynamicListAdapter);

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
            rallies_descargados.add(new Rally(i,lista_rallies_descargados[i],rallyInfo[i],i,"hola", "Este rally utiliza:"+memoria+"Mb",false));
        }

        for(int i=0;i<lista_rallies_sin_descargar.length;i++){
            int memoria = 10 + (int)(Math.random() * 50);
            rallies_sin_descargar.add(new Rally(i,lista_rallies_sin_descargar[i],rallyInfo[i],i,"hola", "Este rally pesa :"+memoria+"Mb",false));
        }
    }

    private class DynamicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int FIRST_LIST_ITEM_VIEW = 2;
        private static final int FIRST_LIST_HEADER_VIEW = 3;
        private static final int SECOND_LIST_ITEM_VIEW = 4;
        private static final int SECOND_LIST_HEADER_VIEW = 5;

        public DynamicListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            //Items de la primera lista
            private TextView nombre_rally_descargado;
            private TextView memoria_rally_descargado;

            //Items de la segunda lista
            private TextView nombre_rally_sin_descargar;

            // Element of footer view
            private TextView footerTextView;

            public ViewHolder(View itemView) {
                super(itemView);

                //Obtiene la vista de elementos de la primera lista
                nombre_rally_descargado = (TextView)itemView.findViewById(R.id.nombre_rally_descargado);
                memoria_rally_descargado = (TextView) itemView.findViewById(R.id.memoria_rally_descargado);

                //Obtiene la vista de elementos de la segunda lista
                nombre_rally_sin_descargar = (TextView)itemView.findViewById(R.id.nombre_rally_sin_descargar);

                footerTextView = (TextView) itemView.findViewById(R.id.footer);
            }

            public void bindViewSecondList(int pos){

                if (rallies_descargados == null) pos = pos - 1;
                else {
                    if (rallies_descargados.size() == 0) pos = pos - 1;
                    else pos = pos - rallies_descargados.size() - 2;
                }

                final String nombre_rally = rallies_sin_descargar.get(pos).getName();

                nombre_rally_sin_descargar.setText(nombre_rally);
            }

            public void bindViewFirstList(int pos) {
                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                final String nombre_rally = rallies_descargados.get(pos).getName();
                final String memoria_rally = rallies_descargados.get(pos).getMemoryUsage();

                nombre_rally_descargado.setText(nombre_rally);
                memoria_rally_descargado.setText(memoria_rally);
            }

            public void bindViewFooter(int pos) {
                footerTextView.setText("This is footer");
            }
        }

        public class FooterViewHolder extends ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class RallyDescargadoViewHolder extends ViewHolder{
            public RallyDescargadoViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class RallySinDescargarViewHolder extends ViewHolder{
            public RallySinDescargarViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class TituloLista1 extends ViewHolder{
            public TituloLista1(View itemView) {
                super(itemView);
            }
        }

        private class TituloLista2 extends ViewHolder{
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
                if (holder instanceof FooterViewHolder) {
                    FooterViewHolder vh = (FooterViewHolder) holder;
                    vh.bindViewFooter(position);
                } else if(holder instanceof TituloLista1){
                    TituloLista1 vh = (TituloLista1) holder;
                } else if(holder instanceof TituloLista2){
                    TituloLista2 vh = (TituloLista2) holder;
                } else if(holder instanceof RallyDescargadoViewHolder){
                    RallyDescargadoViewHolder vh = (RallyDescargadoViewHolder) holder;
                    vh.bindViewFirstList(position);
                }else if(holder instanceof RallySinDescargarViewHolder){
                    RallySinDescargarViewHolder vh = (RallySinDescargarViewHolder) holder;
                    vh.bindViewSecondList(position);
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
}
