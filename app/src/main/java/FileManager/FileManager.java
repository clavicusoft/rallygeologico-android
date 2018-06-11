package FileManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {

    // Nombre de la carpeta donde s guardan los contenidos multimedia del juego
    private final String folderPrincipal = "RallyGeologico";

    /**
     * Constructor vacio
     */
    public FileManager(){
    }

    /**
     * Revisa si hay almacenamiento externo disponible para llectura y escritura
     * @return Verdadero si hay almacenamiento externo y permisos, falso de manera contraria
     */
    public boolean hayAlmacenamientoExterno() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /*************** Cargar archivos de almacenamiento interno ***************/

    /**
     * Carga una imagen en la vista indicada desde almacenamiento interno que se indica con el nombre
     * @param path ruta del archivo
     * @param nombre nombre del archivo
     * @param v vista conde carga la imagen
     * @param destination id del elemento donde carga la imagen
     */
    public void cargarImagenAlmacenamientoInterno(String path, String nombre, View v, int destination) {
        try {
            File f = new File(path, nombre + ".png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView) v.findViewById(destination);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*************** Cargar archivos de almacenamiento externo ***************/

    /**
     * Carga una imagen en la vista indicada desde almacenamiento externo que se indica con el nombre
     * @param nombre Nombre de la imagen
     * @param iv Vista donde carga la imagen
     */
    public void cargarImagenAlmacenamientoExterno(String nombre, ImageView iv) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "imagenes");
            File f = new File(dir, nombre + ".png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = iv;
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
