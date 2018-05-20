package com.rallygeologico;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import FileManager.FileManager;

/**
 * Clase para cargar la imagen de perfil de Facebook del usuario
 * */
public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;
    String name;
    Context context;

    /**
     * Constructor que inicializa la imagen
     * @param bmImage
     */
    public ImageLoader(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    public ImageLoader(String name, Context context) {
        this.name = name;
        this.context = context;
    }

    /**
     * Tarea asincrona para cargar la imagen de perfil de facebook desde url
     * */
    protected Bitmap doInBackground(String... uri) {
        String url = uri[0];
        Bitmap image = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Carga la imagen despues de obtenerla en el metodo anterior
     * @param result resultado obtenido de la url
     */
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
            bmImage.setImageBitmap(resized);
        }
    }
}