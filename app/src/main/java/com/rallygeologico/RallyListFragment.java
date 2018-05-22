package com.rallygeologico;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import SqlDatabase.LocalDB;

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

       /* // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
        LocalDB.LocalDBHelper handler = new LocalDB.LocalDBHelper(getContext());
        // Get access to the underlying writeable database
        SQLiteDatabase db = handler.getWritableDatabase();
        // Query for items from the database and get a cursor back
        Cursor cursor = db.rawQuery("SELECT  * FROM todo_items", null);
        // Setup cursor adapter using cursor from last step
        RallyListAdapter adapter = new RallyListAdapter(getContext(), cursor);
        // Attach cursor adapter to the ListView
        listaRallies.setAdapter(adapter);*/
    }
}