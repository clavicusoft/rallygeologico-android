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

    /**
     *
     * @param context Contexto de la actividad
     * @param users Lista de usuarios
     */
    public RallyListAdapter(Context context, ArrayList<Rally> users) {
        super(context, 0, users);
    }

    /**
     * Recibe los elementos y los carga como elementos de la lista
     * @param position Posicion donde carga el item correspondiente
     * @param convertView Vista en donde cargar el adaptador
     * @param parent Vista padre
     * @return Devuelve la vista con el adaptador cargado
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtiene los datos del item correspondiente a esta posicion
        Rally rally = getItem(position);
        // Revisa si se usa una vista, sino se creo una nueva
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rally_user_item, parent, false);
        }
        // Busca los elementos en donde cargar la informacion
        TextView tv_nombre = (TextView) convertView.findViewById(R.id.nombre_rally_usuario);
        TextView tv_puntos = (TextView) convertView.findViewById(R.id.puntos_rally_usuario);
        TextView tv_estado = (TextView) convertView.findViewById(R.id.estado_rally_usuario);
        // Carga los datos usando el tipo d e objeto correspondiete
        tv_nombre.setText(rally.getName());
        tv_puntos.setText(String.valueOf(rally.getPointsAwarded()));
        tv_estado.setText(String.valueOf(rally.getRallyId()));

        return convertView;
    }
}