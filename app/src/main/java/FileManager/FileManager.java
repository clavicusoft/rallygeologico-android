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

    private final String folderPrincipal = "RallyGeologico";

    public FileManager(){
    }

    /* Checks if external storage is available for read and write */
    public boolean hayAlmacenamientoExterno() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /*************** Cargar archivos de almacenamiento interno ***************/

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
