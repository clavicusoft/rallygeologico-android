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

public class DownloadTask {

    private static final String TAG = "Download Task";
    private final String folderPrincipal = "RallyGeologico";
    private Context context;
    private int fileType;
    private String downloadUrl = "";
    private String fileName = "";

    public DownloadTask(Context context, int fileType, String fileName, String downloadUrl) {
        this.context = context;
        this.fileType = fileType;
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File directorio;
        File archivo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode() + " " + c.getResponseMessage());
                }

                FileManager fm = new FileManager();
                if (fm.hayAlmacenamientoExterno()) {
                    switch(fileType){
                        //Texto
                        case 0:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "textos");
                            archivo = new File(directorio, fileName + ".txt");
                            break;
                        //Imagen
                        case 1:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "imagenes");
                            archivo = new File(directorio, fileName + ".png");
                            break;
                        //Audio
                        case 2:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "audios");
                            archivo = new File(directorio, fileName + ".mp3");
                            break;
                        //Video
                        case 3:
                            directorio = new File(Environment.getExternalStorageDirectory() + "/" + folderPrincipal, "videos");
                            archivo = new File(directorio, fileName + ".mp4");
                            break;
                    }
                } else {
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

                FileOutputStream fos = new FileOutputStream(archivo);//Get OutputStream for NewFile Location
                InputStream is = c.getInputStream();//Get InputStream for connection
                byte[] buffer = new byte[4096];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);// Escribir el nuevo archivo
                }
                // Cerrar las conexiones despues de escribir el archivo
                fos.close();
                is.close();
            } catch (Exception e) {
                //Read exception if something went wrong
                e.printStackTrace();
                archivo = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }
            return null;
        }
    }
}