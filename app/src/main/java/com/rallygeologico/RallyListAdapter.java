package com.rallygeologico;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RallyListAdapter extends CursorAdapter {

    public RallyListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.rally_user_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tv_nombre = (TextView) view.findViewById(R.id.nombre_rally_usuario);
        TextView tv_puntos = (TextView) view.findViewById(R.id.puntos_rally_usuario);
        TextView tv_estado = (TextView) view.findViewById(R.id.estado_rally_usuario);

        // Extract properties from cursor
        String nombre = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int puntos = cursor.getInt(cursor.getColumnIndexOrThrow("points"));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow("state"));

        // Populate fields with extracted properties
        tv_nombre.setText(nombre);
        tv_puntos.setText(String.valueOf(puntos));
        tv_estado.setText(estado);
    }
}