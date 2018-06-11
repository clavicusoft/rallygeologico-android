package FileManager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Maneja las descargas de archivos desde una direccion url
 */
public class DownloadTask {

    private static final String TAG = "Download Task";
    private final String folderPrincipal = "RallyGeologico";
    private Context context;
    private int fileType;
    private String downloadUrl = "";
    private String fileName = "";

    /**
     * Constructor que ejecuta la tarea de descargar la foto de manera asincrona
     * @param context contexto ce la actividad
     * @param fileType entero que indica el tipo de archivo a descargar
     * @param fileName nombre con que el archivo se guarda en el dispositivo
     * @param downloadUrl direccion url de donde se descarga
     */
    public DownloadTask(Context context, int fileType, String fileName, String downloadUrl) {
        this.context = context;
        this.fileType = fileType;
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
        // Empieza la tarea de descarga
        new DownloadingTask().execute();
    }

    /**
     * Clase que se encarga de la descarga asincrona en si de los archivos
     */
    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File directorio;
        File archivo;

        /**
         * Se ejecuta antes de la tarea
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Se ejecuta despues de la tarea
         * @param result resultado de la tarea
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        /**
         * Ejecucion de la tarea de descarga que posteriormente guarda en un archivo en el dispositivo
         * @param arg0
         * @return
         */
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Crea el url de descarga
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Abre la coneccion url
                c.setRequestMethod("GET");//Pone el metodo de solicitud en GET para solicitar informacion
                c.connect();//conecta el url a la conexion
                // Escribe en el log si la conexion no es aceptada
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode() + " " + c.getResponseMessage());
                }
                //Revisa si hay almacenamiento externo disponible para lectura/escritura
                FileManager fm = new FileManager();
                //Si hay escribe sobre una carpeta correspondiente a cada tipo de archivo
                if (fm.hayAlmacenamientoExterno()) {
                    switch(fileType){
                        //Texto
                        case 0:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "textos");
                            if (!directorio.exists()) {
                                directorio.mkdirs();
                            }
                            archivo = new File(directorio, fileName + ".txt");
                            break;
                        //Imagen
                        case 1:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "imagenes");
                            if (!directorio.exists()) {
                                directorio.mkdirs();
                            }
                            archivo = new File(directorio, fileName + ".png");
                            break;
                        //Audio
                        case 2:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "audios");
                            if (!directorio.exists()) {
                                directorio.mkdirs();
                            }
                            archivo = new File(directorio, fileName + ".mp3");
                            break;
                        //Video
                        case 3:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "videos");
                            if (!directorio.exists()) {
                                directorio.mkdirs();
                            }
                            archivo = new File(directorio, fileName + ".mp4");
                            break;
                    }
                }
                //Si no hay, hace lo mismo pero sobre el almacenamiento interno
                else {
                    switch(fileType){
                        //Texto
                        case 0:
                            directorio = context.getDir("textos", Context.MODE_PRIVATE);
                            archivo = new File(directorio, fileName + ".txt");
                            break;
                        //Imagen
                        case 1:
                            directorio = context.getDir("imagenes", Context.MODE_PRIVATE);
                            archivo = new File(directorio, fileName + ".png");
                            break;
                        //Audio
                        case 2:
                            directorio = context.getDir("audios", Context.MODE_PRIVATE);
                            archivo = new File(directorio, fileName + ".mp3");
                            break;
                        //Video
                        case 3:
                            directorio = context.getDir("videos", Context.MODE_PRIVATE);
                            archivo = new File(directorio, fileName + ".mp4");
                            break;
                    }
                }
                FileOutputStream fos = new FileOutputStream(archivo); //Obtiene el flujo de salida para escribir en el archivo
                InputStream is = c.getInputStream();//Obtiene el flujo de entrada para leer de la conexion
                byte[] buffer = new byte[4096];// Determina el tipo de buffer
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);// Escribe el nuevo archivo
                }
                // Cierra las conexiones despues de escribir el archivo
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                archivo = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }
            return null;
        }
    }
}