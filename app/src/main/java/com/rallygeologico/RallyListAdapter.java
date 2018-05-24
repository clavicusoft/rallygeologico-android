package com.rallygeologico;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import SqlDatabase.LocalDB;
import SqlEntities.Rally;

public class RallyListAdapter extends ArrayAdapter<Rally> {
    public RallyListAdapter(Context context, ArrayList<Rally> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Rally rally = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rally_user_item, parent, false);
        }
        // Lookup view for data population
        TextView tv_nombre = (TextView) convertView.findViewById(R.id.nombre_rally_usuario);
        TextView tv_puntos = (TextView) convertView.findViewById(R.id.puntos_rally_usuario);
        TextView tv_estado = (TextView) convertView.findViewById(R.id.estado_rally_usuario);
        // Populate the data into the template view using the data object
        tv_nombre.setText(rally.getName());
        tv_puntos.setText(String.valueOf(rally.getPointsAwarded()));
        tv_estado.setText(String.valueOf(rally.getRallyId()));
        // Return the completed view to render on screen
        return convertView;
    }
}