package com.rallygeologico;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Fragmento para manejar la lista de los logros obtenidos por el usuario
 */
public class RallyListFragment extends Fragment {

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_rally_list, container, false);
        ListView listaRallies = (ListView) v.findViewById(R.id.rallyListView);
        return v;
    }
}