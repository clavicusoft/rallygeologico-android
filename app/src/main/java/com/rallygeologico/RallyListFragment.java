package com.rallygeologico;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import SqlDatabase.LocalDB;
import SqlEntities.Rally;
import SqlEntities.User;

/**
 * Fragmento para manejar la lista de los logros obtenidos por el usuario
 */
public class RallyListFragment extends Fragment {

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_rally_list, container, false);
        ListView listaRallies = (ListView) v.findViewById(R.id.rallyListView);
        LocalDB db = new LocalDB(getContext());
        // Obtiene todos los rallies asignados a un usuario
        User user = db.selectLoggedUser();
        ArrayList<Rally> rallies = db.selectAllRalliesFromUser(user.getUserId());
        // Crea e adaptador para convertir el arreglo a vistas
        RallyListAdapter adapter = new RallyListAdapter(getContext(), rallies);
        // Agrega el adaptador a la list view
        listaRallies.setAdapter(adapter);
        return v;
    }
}