package SqlDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.List;

import SqlEntities.Activity;
import SqlEntities.Competition;
import SqlEntities.Multimedia;
import SqlEntities.Rally;
import SqlEntities.Site;
import SqlEntities.Term;
import SqlEntities.User;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class LocalDB{

    private SQLiteDatabase database;
    private LocalDBHelper localDBHelper;

    /**
     * Constructor de la base de datos
     *
     * @param context contexto general de la aplicación
     */
    public LocalDB(Context context) {
        localDBHelper = new LocalDBHelper(context);
        database = localDBHelper.getWritableDatabase();
    }

    /**
     * Metodo para ingresar un usuario a la base de datos local
     * @param user que desea ser ingresado
     * @return cantidad de filas afectadas
     */
    public long insertUser(User user){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.UserEntry.COLUMN_NAME_USERNAME,user.getUsername());
        values.put(DBContract.UserEntry.COLUMN_NAME_EMAIL, user.getEmail());
        values.put(DBContract.UserEntry.COLUMN_NAME_FACEBOOKID, user.getFacebookId());
        values.put(DBContract.UserEntry.COLUMN_NAME_FIRSTNAME, user.getFirstName());
        values.put(DBContract.UserEntry.COLUMN_NAME_GOOGLEID, user.getGoogleId());
        values.put(DBContract.UserEntry.COLUMN_NAME_ISLOGGED, user.isLogged());
        values.put(DBContract.UserEntry.COLUMN_NAME_LASTNAME, user.getLastName());
        values.put(DBContract.UserEntry.COLUMN_NAME_PHOTOURL, user.getPhotoUrl());
        values.put(DBContract.UserEntry.COLUMN_NAME_USERID, user.getUserId());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.UserEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Metodo para ingresar una competencia a la base de datos local
     * @param competition que desea ser ingresado
     * @return cantidad de filas afectadas
     */
    public long insertCompetition(Competition competition){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_COMPETITIONID,competition.getCompetitionId());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_ACTIVE, competition.isActive());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_STARTINGDATE, competition.getStartingDate().toString());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_FINISHINGDATE, competition.getFinichingDate().toString());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_ISPUBLIC, competition.isPublic());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_NAME, competition.getName());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_TOTALPOINTS, competition.getTotalPoints());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_RALLYID, competition.getRally().getRallyId());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.CompetitionEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Metodo para ingresar un rally a la base de datos local
     * @param rally que desea ser ingresado
     * @return cantidad de filas afectadas
     */
    public long insertRally(Rally rally){
        /**
         * Como en SQLite no exite el boolean tenemos que pasar de "true" and "false" a 1 y 0
         */
        int rallyDownloaded = 0;
        if(rally.getIsDownloaded())
            rallyDownloaded = 1;

        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.RallyEntry.COLUMN_NAME_RALLYID, rally.getRallyId());
        values.put(DBContract.RallyEntry.COLUMN_NAME_NAME, rally.getName());
        values.put(DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION, rally.getDescription());
        values.put(DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD, rallyDownloaded);
        values.put(DBContract.RallyEntry.COLUMN_NAME_IMAGEURL, rally.getImageURL());
        values.put(DBContract.RallyEntry.COLUMN_NAME_MEMORYUSAGE, rally.getMemoryUsage());
        values.put(DBContract.RallyEntry.COLUMN_NAME_POINTSAWARDED, rally.getPointsAwarded());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.RallyEntry.TABLE_NAME,
                null,
                values
        );

        //Revisa si hay que crear relaciones entre las tablas
        if(rally.getSites().size()>0){
            for(int i = 0; i < rally.getSites().size(); i++) {
                try {
                    newRowId+=this.insertSite(rally.getSites().get(i));
                } catch (android.database.sqlite.SQLiteException e) {}
                try {
                    newRowId+=this.insertRally_Site(rally.getRallyId(),rally.getSites().get(i).getSiteId());
                } catch (android.database.sqlite.SQLiteException e) {}
            }
        }
        return newRowId;
    }

    /**
     * Metodo para ingresar una multimedia a la base de datos local
     * @param multimedia que desea ser ingresado
     * @return cantidad de filas afectadas
     */
    public long insertMultimedia(Multimedia multimedia){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.MultimediaEntry.COLUMN_NAME_ID, multimedia.getMultimediaId());
        values.put(DBContract.MultimediaEntry.COLUMN_NAME_TYPE, multimedia.getMultimediaType());
        values.put(DBContract.MultimediaEntry.COLUMN_NAME_URL, multimedia.getMultimediaURL());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.MultimediaEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Metodo para ingresar una multimedia a la base de datos local
     * @param activity que desea ser ingresado
     */
    public long insertActivity(Activity activity){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.ActivityEntry.COLUMN_NAME_ID, activity.getActivityId());
        values.put(DBContract.ActivityEntry.COLUMN_NAME_TYPE, activity.getGetActivityType());
        values.put(DBContract.ActivityEntry.COLUMN_NAME_POINTS, activity.getActivityPoints());
        values.put(DBContract.ActivityEntry.COLUMN_NAME_STATUS, activity.getActivityStatus());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.ActivityEntry.TABLE_NAME,
                null,
                values
        );

        //Revisa si hay que crear relaciones entre las tablas
        if(activity.getActivityMultimediaList().size()>0){
            for(int i = 0; i < activity.getActivityMultimediaList().size(); i++) {
                try {
                    newRowId+=this.insertMultimedia(activity.getActivityMultimediaList().get(i));
                } catch (android.database.sqlite.SQLiteException e) {}
                try {
                    newRowId+=this.insertMultimedia_Activity(activity.getActivityMultimediaList().get(i).getMultimediaId(),activity.getActivityId());
                } catch (android.database.sqlite.SQLiteException e) {}
            }
        }
        return newRowId;
    }

    /**
     * Metodo para ingresar un Site a la base de datos local
     * @param site que desea ser ingresado
     */
    public long insertSite(Site site){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.SiteEntry.COLUMN_NAME_ID, site.getSiteId());
        values.put(DBContract.SiteEntry.COLUMN_NAME_NAME, site.getSiteName());
        values.put(DBContract.SiteEntry.COLUMN_NAME_POINTSAWARDED, site.getSitePointsAwarded());
        values.put(DBContract.SiteEntry.COLUMN_NAME_STATUS, site.getStatus());
        values.put(DBContract.SiteEntry.COLUMN_NAME_DESCRIPTION, site.getSiteDescription());
        values.put(DBContract.SiteEntry.COLUMN_NAME_LATITUD, site.getLatitud());
        values.put(DBContract.SiteEntry.COLUMN_NAME_LONGITUD, site.getLongitud());
        values.put(DBContract.SiteEntry.COLUMN_NAME_TOTALPOINTS, site.getSiteTotalPoints());
        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.SiteEntry.TABLE_NAME,
                null,
                values
        );

        if(site.getActivityList().size()>0){
            for(int i = 0; i < site.getActivityList().size(); i++) {
                try {
                    this.insertActivity(site.getActivityList().get(i));
                } catch (android.database.sqlite.SQLiteException e) {}
                try {
                    this.insertActivity_Site(site.getSiteId(),site.getActivityList().get(i).getActivityId());
                } catch (android.database.sqlite.SQLiteException e) {}
            }
        }
        if(site.getTermList().size()>0){
            for(int i = 0; i < site.getTermList().size(); i++) {
                try {
                    newRowId += this.insertTerm(site.getTermList().get(i));
                } catch (android.database.sqlite.SQLiteException e) {}
                try {
                    newRowId += this.insertTerm_Site(site.getSiteId(),site.getTermList().get(i).getTermId());
                } catch (android.database.sqlite.SQLiteException e) {}
            }
        }
        return newRowId;
    }

    /**
     * Metodo para ingresar un termino a la base de datos local
     * @param term que desea ser ingresado
     */
    public long insertTerm(Term term){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.TermEntry.COLUMN_NAME_ID,term.getTermId());
        values.put(DBContract.TermEntry.COLUMN_NAME_NAME, term.getTermName());
        values.put(DBContract.TermEntry.COLUMN_NAME_DESCRIPTION, term.getTermDescription());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.TermEntry.TABLE_NAME,
                null,
                values
        );

        //Revisa si hay que crear relaciones entre las tablas
        if(term.getTermMultimediaList().size()>0){
            for(int i = 0; i < term.getTermMultimediaList().size(); i++) {
                try {
                    newRowId += this.insertMultimedia(term.getTermMultimediaList().get(i));
                } catch (android.database.sqlite.SQLiteException e) {}
                try {
                    newRowId += this.insertMultimedia_Term(term.getTermMultimediaList().get(i).getMultimediaId(),term.getTermId());
                } catch (android.database.sqlite.SQLiteException e) {}
            }
        }
        return newRowId;
    }

    /**
     * Crea la relacion entre una actividad y el sitio relacionado
     * @param userId identificador del usuario
     * @param competitionId identificador de la competencia
     * @return filas modificadas
     */
    public long insertUser_Competition(int userId, int competitionId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.User_CompetitionEntry.COLUMN_NAME_USERID, userId);
        values.put(DBContract.User_CompetitionEntry.COLUMN_NAME_ID, competitionId);


        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.User_CompetitionEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Crea la relacion entre una actividad y el sitio relacionado
     * @param rallyId identificador del termino
     * @param SiteId identificador de la actividad
     * @return filas modificadas
     */
    public long insertRally_Site(int rallyId, int SiteId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.Rally_SiteEntry.COLUMN_NAME_RallyID, rallyId);
        values.put(DBContract.Rally_SiteEntry.COLUMN_NAME_SITEID, SiteId);


        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.Rally_SiteEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Crea la relacion entre una actividad y el sitio relacionado
     * @param TermId identificador del termino
     * @param siteId identificador del sitio
     * @return filas modificadas
     */
    public long insertTerm_Site(int siteId, int TermId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.Term_SiteEntry.COLUMN_NAME_SITEID, siteId);
        values.put(DBContract.Term_SiteEntry.COLUMN_NAME_TermID, TermId);


        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.Term_SiteEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Crea la relacion entre una actividad y el sitio relacionado
     * @param siteId identificador del sitio
     * @param activityId identificador de la actividad
     * @return filas modificadas
     */
    public long insertActivity_Site(int siteId, int activityId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.Activity_SiteEntry.COLUMN_NAME_SITEID, siteId);
        values.put(DBContract.Activity_SiteEntry.COLUMN_NAME_ACTIVITYID, activityId);


        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.Activity_SiteEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Crea la relacion entre una actividad y el multimedia relacionado
     * @param multimediaId identificador del contenido multimedia
     * @param activityId identificador de la actividad
     * @return filas modificadas
     */
    public long insertMultimedia_Activity(int multimediaId, int activityId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.Multimedia_ActivityEntry.COLUMN_NAME_MULTIMEDIAID, multimediaId);
        values.put(DBContract.Multimedia_ActivityEntry.COLUMN_NAME_ACTIVITYID, activityId);


        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.Multimedia_ActivityEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Crea la relacion entre una actividad y el multimedia relacionado
     * @param multimediaId identificador del contenido multimedia
     * @param termId identificador del termino
     * @return filas modificadas
     */
    public long insertMultimedia_Term(int multimediaId, int termId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.Multimedia_TermEntry.COLUMN_NAME_MULTIMEDIAID, multimediaId);
        values.put(DBContract.Multimedia_TermEntry.COLUMN_NAME_TermID, termId);


        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.Multimedia_TermEntry.TABLE_NAME,
                null,
                values
        );
        return newRowId;
    }

    /**
     * Metodo para haer pruebas en la base de datos
     */
    public void prueba(){
        database.execSQL("delete from "+ DBContract.RallyEntry.TABLE_NAME);
        database.execSQL("delete from "+ DBContract.UserEntry.TABLE_NAME);
        User user1 = new User("1","Face1","Google1","Usuario 1","Pablo ","Madrigal"," Correo 1","Foto 1",false);
        User user2 = new User("2","Face2","Google2","Usuario 2","Marco ","Madrigal"," Correo 2","Foto 2",false);
        long prueba1 = this.insertUser(user1);
        long prueba2 = this.insertUser(user2);
        Rally rally1 = new Rally(1,"rally 1","Descripcion 1", 3,"https://www.google.com/logos/doodles/2013/qixi_festival__chilseok_-2009005-hp.jpg","Utiliza 34Mb",false);
        Rally rally2 = new Rally(2,"rally 2","Descripcion 2", 6,"https://www.google.com/logos/2012/montessori-hp.jpg","Utiliza 55Mb ",true);
        this.insertRally(rally1);
        this.insertRally(rally2);
        long prueba5 = 0;
    }

    /**
     * Metodo para recuperar todos los usuarios de la base de datos
     * @return un cursor con los datos de la base de datos
     */
    public Cursor selectAllUsers(){
        /**
         * Se describen las columnas que va a devolver la consulta
         */
        String[] columnas = {
                DBContract.UserEntry.COLUMN_NAME_USERID,
                DBContract.UserEntry.COLUMN_NAME_USERNAME,
                DBContract.UserEntry.COLUMN_NAME_FIRSTNAME,
                DBContract.UserEntry.COLUMN_NAME_LASTNAME,
                DBContract.UserEntry.COLUMN_NAME_EMAIL,
                DBContract.UserEntry.COLUMN_NAME_GOOGLEID,
                DBContract.UserEntry.COLUMN_NAME_FACEBOOKID,
                DBContract.UserEntry.COLUMN_NAME_PHOTOURL,
                DBContract.UserEntry.COLUMN_NAME_ISLOGGED
        };

        /**
         * Filtro que se utiliza para la clausula WHERE "id" = 1
         */
        String selection = DBContract.UserEntry.COLUMN_NAME_USERID + " = ?";
        String[] selectionArgs = { "1" };

        /**
         *  Como queremos que esten ordenados los resultados
         */
        String sortOrder =
                DBContract.UserEntry.COLUMN_NAME_USERNAME + " DESC";

        /**
         * La consulta en si
         */
        Cursor cursor = database.query(
                DBContract.UserEntry.TABLE_NAME,    // La tabla en la que se hace la consulta
                columnas,                           // El arreglo de las columnas que queremos que devuelva
                null,                          // Las columnas para el WHERE
                null,                      // Los valores para cada una de las columnas
                null,                       // La agrupación de las filas
                null,                        // El parametro HAVING para agrupar las filas
                sortOrder                          // The sort order
        );
        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public ArrayList<Rally> selectAllRallies(){
        /**
         * Se describen las columnas que va a devolver la consulta
         */
        String[] columnas = {
                DBContract.RallyEntry.COLUMN_NAME_RALLYID,
                DBContract.RallyEntry.COLUMN_NAME_MEMORYUSAGE,
                DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD,
                DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION,
                DBContract.RallyEntry.COLUMN_NAME_IMAGEURL,
                DBContract.RallyEntry.COLUMN_NAME_POINTSAWARDED,
                DBContract.RallyEntry.COLUMN_NAME_NAME
        };

        /**
         *  Como queremos que esten ordenados los resultados
         */
        String sortOrder =
                DBContract.RallyEntry.COLUMN_NAME_NAME + " DESC";

        /**
         * La consulta en si
         */
        Cursor cursor = database.query(
                DBContract.RallyEntry.TABLE_NAME,    // La tabla en la que se hace la consulta
                columnas,                           // El arreglo de las columnas que queremos que devuelva
                null,                          // Las columnas para el WHERE
                null,                      // Los valores para cada una de las columnas
                null,                       // La agrupación de las filas
                null,                        // El parametro HAVING para agrupar las filas
                sortOrder                          // The sort order
        );
        if(cursor != null)
            cursor.moveToFirst();

        ArrayList<Rally> mArrayList = new ArrayList<Rally>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            int index;
            Rally rally = new Rally();

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_RALLYID);
            int rallyId = cursor.getInt(index);
            rally.setRallyId(rallyId);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_MEMORYUSAGE);
            String memoryUsage = cursor.getString(index);
            rally.setMemoryUsage(memoryUsage);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD);
            int temporatl = cursor.getInt(index);
            boolean download = temporatl>0;
            rally.setDownloaded(download);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION);
            String description = cursor.getString(index);
            rally.setDescription(description);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_IMAGEURL);
            String imageURL = cursor.getString(index);
            rally.setImageURL(imageURL);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_POINTSAWARDED);
            int points = cursor.getInt(index);
            rally.setPointsAwarded(points);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_NAME);
            String name = cursor.getString(index);
            rally.setName(name);

            mArrayList.add(rally);
        }
        return mArrayList;
    }


    /**
     * Metodo para devolver todos los rallies asociados a un usuario
     * @param userId Identificador del usuario del cual deseo obtener los rallies
     * @return una lista con los rallies asociados al usuario
     */
    public List<Rally> selectAllRalliesFromUser(int userId){
        String rawQuery = "Select * FROM " + DBContract.RallyEntry.TABLE_NAME +
                " INNER JOIN " + DBContract.CompetitionEntry.TABLE_NAME +
                " ON " + DBContract.RallyEntry.COLUMN_NAME_RALLYID + " = " + DBContract.CompetitionEntry.COLUMN_NAME_RALLYID +
                " INNER JOIN " + DBContract.User_CompetitionEntry.TABLE_NAME +
                " ON " + DBContract.User_CompetitionEntry.COLUMN_NAME_ID + " = " + DBContract.CompetitionEntry.COLUMN_NAME_COMPETITIONID +
                " WHERE " + DBContract.User_CompetitionEntry.COLUMN_NAME_USERID + " = " + userId;

        Cursor cursor = database.rawQuery(rawQuery,null);
        if(cursor != null)
            cursor.moveToFirst();

        List<Rally> rallyList = new ArrayList<Rally>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            int index;
            Rally rally = new Rally();

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_RALLYID);
            int rallyId = cursor.getInt(index);
            rally.setRallyId(rallyId);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_MEMORYUSAGE);
            String memoryUsage = cursor.getString(index);
            rally.setMemoryUsage(memoryUsage);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD);
            int temporatl = cursor.getInt(index);
            boolean download = temporatl>0;
            rally.setDownloaded(download);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION);
            String description = cursor.getString(index);
            rally.setDescription(description);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_IMAGEURL);
            String imageURL = cursor.getString(index);
            rally.setImageURL(imageURL);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_POINTSAWARDED);
            int points = cursor.getInt(index);
            rally.setPointsAwarded(points);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_NAME);
            String name = cursor.getString(index);
            rally.setName(name);

            rallyList.add(rally);
        }

        return rallyList;
    }

    /**
     * Metodo para actualizar el estatus de un sitio en la base de datos local
     * @param siteId identificador del sitio a modificar
     * @param newStatus nuevo estatus para el sitio seleccionado
     * @return cantidad de filas modificadas, devuelve un -1 si ocurrio un error
     */
    public int updateSiteVisit(int siteId, int newStatus){
        ContentValues values = new ContentValues();
        values.put(DBContract.SiteEntry.COLUMN_NAME_STATUS,newStatus);

        /**
         *
         */
        String selection = DBContract.SiteEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {String.valueOf(siteId)};
        int count = database.update(
                DBContract.SiteEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        return count;
    }

    /**
     * Metodo para devolver todos los sitios asociados a un rally
     * @param rallyId Identificador del rally del cual deseo obtener los puntos
     * @return una lista con los sitios asociados al punto
     */
    public List<Site> selectAllSitesFromRally(int rallyId){
        String rawQuery = "Select * FROM " + DBContract.SiteEntry.TABLE_NAME +
                " INNER JOIN " + DBContract.Rally_SiteEntry.TABLE_NAME +
                " ON " + DBContract.SiteEntry.COLUMN_NAME_ID + " = " + DBContract.Rally_SiteEntry.COLUMN_NAME_SITEID +
                " WHERE " + DBContract.Rally_SiteEntry.COLUMN_NAME_RallyID + " = " + rallyId;

        Cursor cursor = database.rawQuery(
                rawQuery,
                null
        );
        if(cursor != null)
            cursor.moveToFirst();

        List<Site> siteList = new ArrayList<Site>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            int index;
            Site site = new Site();

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_ID);
            int siteId = cursor.getInt(index);
            site.setSiteId(siteId);

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_NAME);
            String name = cursor.getString(index);
            site.setSiteName(name);

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_POINTSAWARDED);
            int points = cursor.getInt(index);
            site.setSitePointsAwarded(points);

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_STATUS);
            int status = cursor.getInt(index);
            site.setStatus(status);

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_DESCRIPTION);
            String description = cursor.getString(index);
            site.setSiteDescription(description);

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_LATITUD);
            String latitud = cursor.getString(index);
            site.setLatitud(latitud);

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_LONGITUD);
            String longitud = cursor.getString(index);
            site.setLongitud(longitud);

            index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_TOTALPOINTS);
            int totalPoints = cursor.getInt(index);
            site.setSiteTotalPoints(totalPoints);

            siteList.add(site);
        }
        return siteList;
    }

    /**
     * Clase con la definicion de las tablas de la base de datos
     */
    public final class DBContract {
        // To prevent someone from accidentally instantiating the contract class, make the constructor private.
        private DBContract() {}


        /** Inner class that defines the USER table contents */
        public class UserEntry implements BaseColumns {
            public static final String TABLE_NAME = "USERS";
            public static final String COLUMN_NAME_USERID = "userId";
            public static final String COLUMN_NAME_FACEBOOKID = "facebookId";
            public static final String COLUMN_NAME_GOOGLEID = "googleId";
            public static final String COLUMN_NAME_USERNAME = "username";
            public static final String COLUMN_NAME_FIRSTNAME = "firstName";
            public static final String COLUMN_NAME_LASTNAME = "lastName";
            public static final String COLUMN_NAME_EMAIL = "email";
            public static final String COLUMN_NAME_PHOTOURL = "photoURL";
            public static final String COLUMN_NAME_ISLOGGED = "isLogged";
        }

        /** Inner class that defines the COMPETITION table contents */
        public class CompetitionEntry implements BaseColumns {
            public static final String TABLE_NAME = "COMPETITION";
            public static final String COLUMN_NAME_COMPETITIONID = "competitionId";
            public static final String COLUMN_NAME_ACTIVE = "active";
            public static final String COLUMN_NAME_STARTINGDATE = "startingDate";
            public static final String COLUMN_NAME_FINISHINGDATE = "finishingDate";
            public static final String COLUMN_NAME_ISPUBLIC = "isPublic";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_TOTALPOINTS = "totalPoints";
            public static final String COLUMN_NAME_RALLYID = "rallyId";
        }

        /** Inner class that defines the RALLY contents */
        public class RallyEntry implements BaseColumns {
            public static final String TABLE_NAME = "RALLY";
            public static final String COLUMN_NAME_RALLYID = "rallyId";
            public static final String COLUMN_NAME_NAME = "rallyName";
            public static final String COLUMN_NAME_POINTSAWARDED = "pointsAwarded";
            public static final String COLUMN_NAME_IMAGEURL = "imageUrl";
            public static final String COLUMN_NAME_DESCRIPTION = "rallyDescription";
            public static final String COLUMN_NAME_DOWNLOAD = "download";
            public static final String COLUMN_NAME_MEMORYUSAGE = "memoryUsage";
        }

        /** Inner class that defines the MULTIMEDIA contents */
        public class MultimediaEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA";
            public static final String COLUMN_NAME_ID = "multimediaId";
            public static final String COLUMN_NAME_TYPE = "multimediaType";
            public static final String COLUMN_NAME_URL = "multimediaURL";
        }

        /** Inner class that defines the ACTIVITY contents */
        public class ActivityEntry implements BaseColumns {
            public static final String TABLE_NAME = "ACTIVITY";
            public static final String COLUMN_NAME_ID = "activityId";
            public static final String COLUMN_NAME_TYPE = "activityType";
            public static final String COLUMN_NAME_POINTS = "activityPoints";
            public static final String COLUMN_NAME_STATUS = "activityStatus";
        }

        /** Inner class that defines the SITE contents */
        public class SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "SITE";
            public static final String COLUMN_NAME_ID = "siteId";
            public static final String COLUMN_NAME_NAME = "siteName";
            public static final String COLUMN_NAME_POINTSAWARDED = "sitePointsAwarded";
            public static final String COLUMN_NAME_STATUS = "siteStatus";
            public static final String COLUMN_NAME_DESCRIPTION = "siteDescription";
            public static final String COLUMN_NAME_LATITUD = "latitude";
            public static final String COLUMN_NAME_LONGITUD = "longitude";
            public static final String COLUMN_NAME_TOTALPOINTS = "siteTotalPoints";
        }

        /** Inner class that defines the TERM contents */
        public class TermEntry implements BaseColumns {
            public static final String TABLE_NAME = "TERM";
            public static final String COLUMN_NAME_ID = "termID";
            public static final String COLUMN_NAME_NAME = "termName";
            public static final String COLUMN_NAME_DESCRIPTION = "termDescription";
        }

        /** Inner class that defines the User_Competition table contents */
        public class User_CompetitionEntry implements BaseColumns {
            public static final String TABLE_NAME = "USER_COMPETITION";
            public static final String COLUMN_NAME_USERID = "userId";
            public static final String COLUMN_NAME_ID = "competitionId";
        }

        /** Inner class that defines the Rally_site table contents */
        public class Rally_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "RALLY_SITE";
            public static final String COLUMN_NAME_RallyID = "rallyId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /** Inner class that defines the Rally_site table contents */
        public class Term_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "TERM_SITE";
            public static final String COLUMN_NAME_TermID = "TermId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /** Inner class that defines the Rally_site table contents */
        public class Activity_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "ACTIVITY_SITE";
            public static final String COLUMN_NAME_ACTIVITYID = "activityId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /** Inner class that defines the Rally_site table contents */
        public class Multimedia_ActivityEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA_ACTIVITY";
            public static final String COLUMN_NAME_MULTIMEDIAID = "multimediaId";
            public static final String COLUMN_NAME_ACTIVITYID = "activityId";
        }

        /** Inner class that defines the Rally_site table contents */
        public class Multimedia_TermEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA_ACTIVITY";
            public static final String COLUMN_NAME_TermID = "TermId";
            public static final String COLUMN_NAME_MULTIMEDIAID = "multimediaId";
        }
    }

    /**
     * Clase que permite manejar facilmente la base de datos y lo que se hace con ella
     */
    public static class LocalDBHelper extends SQLiteOpenHelper {
        public static final String dbName = "RallyDB";
        public static final int DATABASE_VERSION = 1;

        private static final String USER_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + DBContract.UserEntry.TABLE_NAME + "(" +
                        DBContract.UserEntry.COLUMN_NAME_USERID + " INTEGER PRIMARY KEY," +
                        DBContract.UserEntry.COLUMN_NAME_FACEBOOKID + " TEXT," +
                        DBContract.UserEntry.COLUMN_NAME_GOOGLEID + " TEXT," +
                        DBContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT UNIQUE NOT NULL," +
                        DBContract.UserEntry.COLUMN_NAME_FIRSTNAME + " TEXT," +
                        DBContract.UserEntry.COLUMN_NAME_LASTNAME + " TEXT," +
                        DBContract.UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                        DBContract.UserEntry.COLUMN_NAME_PHOTOURL + " TEXT," +
                        DBContract.UserEntry.COLUMN_NAME_ISLOGGED + " BIT DEFAULT 0" +
                        ");";

        private static final String COMPETITION_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " +DBContract.CompetitionEntry.TABLE_NAME +"(" +
                        DBContract.CompetitionEntry.COLUMN_NAME_COMPETITIONID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.CompetitionEntry.COLUMN_NAME_ACTIVE +" BIT DEFAULT 1," +
                        DBContract.CompetitionEntry.COLUMN_NAME_STARTINGDATE +" DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        DBContract.CompetitionEntry.COLUMN_NAME_FINISHINGDATE +" DATETIME," +
                        DBContract.CompetitionEntry.COLUMN_NAME_ISPUBLIC +" BIT DEFAULT 1," +
                        DBContract.CompetitionEntry.COLUMN_NAME_NAME +" TEXT NOT NULL," +
                        DBContract.CompetitionEntry.COLUMN_NAME_TOTALPOINTS +" INTEGER," +
                        DBContract.CompetitionEntry.COLUMN_NAME_RALLYID +" INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.CompetitionEntry.COLUMN_NAME_RALLYID+") REFERENCES "+DBContract.RallyEntry.TABLE_NAME+"("+DBContract.RallyEntry.COLUMN_NAME_RALLYID+")" +
                        ");";

        private static final String RALLY_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.RallyEntry.TABLE_NAME+"(" +
                        DBContract.RallyEntry.COLUMN_NAME_RALLYID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.RallyEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        DBContract.RallyEntry.COLUMN_NAME_POINTSAWARDED + " INTEGER NOT NULL," +
                        DBContract.RallyEntry.COLUMN_NAME_IMAGEURL + " TEXT," +
                        DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                        DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD + " BIT DEFAULT 0," +
                        DBContract.RallyEntry.COLUMN_NAME_MEMORYUSAGE + " INTEGER" +
                        ");";

        private static final String MULTIMEDIA_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.MultimediaEntry.TABLE_NAME+"(" +
                        DBContract.MultimediaEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.MultimediaEntry.COLUMN_NAME_TYPE + " INTEGER NOT NULL," +
                        DBContract.MultimediaEntry.COLUMN_NAME_URL + " TEXT NOT NULL" +
                        ");";

        private static final String ACTIVITY_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+ DBContract.ActivityEntry.TABLE_NAME +"(" +
                        DBContract.ActivityEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.ActivityEntry.COLUMN_NAME_POINTS + " INTEGER NOT NULL," +
                        DBContract.ActivityEntry.COLUMN_NAME_TYPE + " INTEGER NOT NULL," +
                        DBContract.ActivityEntry.COLUMN_NAME_STATUS + " INTEGER NOT NULL" +
                        ");";

        private static final String SITE_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.SiteEntry.TABLE_NAME+" (" +
                        DBContract.SiteEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_POINTSAWARDED + " INTEGER NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                        DBContract.SiteEntry.COLUMN_NAME_LATITUD + " TEXT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_LONGITUD + " TEXT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_STATUS + " INTEGER NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_TOTALPOINTS + " INTEGER" +
                        ");";

        private static final String TERM_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+ DBContract.TermEntry.TABLE_NAME +" (" +
                        DBContract.TermEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.TermEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        DBContract.TermEntry.COLUMN_NAME_DESCRIPTION + " TEXT" +
                        ");";

        private static final String USER_COMPETITION_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + DBContract.User_CompetitionEntry.TABLE_NAME + "(" +
                        DBContract.User_CompetitionEntry.COLUMN_NAME_USERID + " INTEGER NOT NULL," +
                        DBContract.User_CompetitionEntry.COLUMN_NAME_ID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.User_CompetitionEntry.COLUMN_NAME_USERID+") REFERENCES "+DBContract.UserEntry.TABLE_NAME+"("+DBContract.UserEntry.COLUMN_NAME_USERID+")," +
                        "FOREIGN KEY ("+DBContract.User_CompetitionEntry.COLUMN_NAME_ID +") REFERENCES "+DBContract.CompetitionEntry.TABLE_NAME+"("+DBContract.CompetitionEntry.COLUMN_NAME_COMPETITIONID+")," +
                        "PRIMARY KEY (" + DBContract.User_CompetitionEntry.COLUMN_NAME_USERID +","+DBContract.User_CompetitionEntry.COLUMN_NAME_ID +")" +
                        ");";

        private static final String RALLY_SITE_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+ DBContract.Rally_SiteEntry.TABLE_NAME +"(" +
                        DBContract.Rally_SiteEntry.COLUMN_NAME_RallyID + " INTEGER NOT NULL," +
                        DBContract.Rally_SiteEntry.COLUMN_NAME_SITEID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.Rally_SiteEntry.COLUMN_NAME_RallyID+") REFERENCES "+DBContract.RallyEntry.TABLE_NAME+"("+DBContract.RallyEntry.COLUMN_NAME_RALLYID+")," +
                        "FOREIGN KEY ("+DBContract.Rally_SiteEntry.COLUMN_NAME_SITEID+") REFERENCES "+DBContract.SiteEntry.TABLE_NAME+"("+DBContract.SiteEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.Rally_SiteEntry.COLUMN_NAME_RallyID+", "+DBContract.Rally_SiteEntry.COLUMN_NAME_SITEID+")" +
                        ");";

        private static final String TERM_SITE_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.Term_SiteEntry.TABLE_NAME+"(" +
                        DBContract.Term_SiteEntry.COLUMN_NAME_TermID + " INTEGER NOT NULL," +
                        DBContract.Term_SiteEntry.COLUMN_NAME_SITEID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.Term_SiteEntry.COLUMN_NAME_TermID+") REFERENCES "+DBContract.TermEntry.TABLE_NAME+"("+DBContract.TermEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.Term_SiteEntry.COLUMN_NAME_SITEID+") REFERENCES "+DBContract.SiteEntry.TABLE_NAME+"("+DBContract.SiteEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.Term_SiteEntry.COLUMN_NAME_TermID+", "+DBContract.Term_SiteEntry.COLUMN_NAME_SITEID+")" +
                        ");";

        private static final String ACTIVITY_SITE_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.Activity_SiteEntry.TABLE_NAME+"(" +
                        DBContract.Activity_SiteEntry.COLUMN_NAME_SITEID + " INTEGER NOT NULL," +
                        DBContract.Activity_SiteEntry.COLUMN_NAME_ACTIVITYID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.Activity_SiteEntry.COLUMN_NAME_SITEID+") REFERENCES "+DBContract.SiteEntry.TABLE_NAME+"("+DBContract.SiteEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.Activity_SiteEntry.COLUMN_NAME_ACTIVITYID+") REFERENCES "+DBContract.ActivityEntry.TABLE_NAME+"("+ DBContract.ActivityEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.Activity_SiteEntry.COLUMN_NAME_SITEID+", "+DBContract.Activity_SiteEntry.COLUMN_NAME_ACTIVITYID+")" +
                        ");";

        private static final String MULTIMEDIA_ACTIVITY_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS  "+DBContract.Multimedia_ActivityEntry.TABLE_NAME+"(" +
                        DBContract.Multimedia_ActivityEntry.COLUMN_NAME_MULTIMEDIAID + " INTEGER NOT NULL," +
                        DBContract.Multimedia_ActivityEntry.COLUMN_NAME_ACTIVITYID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.Multimedia_ActivityEntry.COLUMN_NAME_MULTIMEDIAID+") REFERENCES "+DBContract.MultimediaEntry.TABLE_NAME+"("+DBContract.MultimediaEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.Multimedia_ActivityEntry.COLUMN_NAME_ACTIVITYID+") REFERENCES "+DBContract.ActivityEntry.TABLE_NAME+"("+ DBContract.ActivityEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.Multimedia_ActivityEntry.COLUMN_NAME_MULTIMEDIAID+", "+DBContract.Multimedia_ActivityEntry.COLUMN_NAME_ACTIVITYID+")" +
                        ");";

        private static final String MULTIMEDIA_TERM_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS  "+DBContract.Multimedia_TermEntry.TABLE_NAME+"(" +
                        DBContract.Multimedia_TermEntry.COLUMN_NAME_MULTIMEDIAID + " INTEGER NOT NULL," +
                        DBContract.Multimedia_TermEntry.COLUMN_NAME_TermID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.Multimedia_TermEntry.COLUMN_NAME_MULTIMEDIAID+") REFERENCES "+DBContract.MultimediaEntry.TABLE_NAME+"("+DBContract.MultimediaEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.Multimedia_TermEntry.COLUMN_NAME_TermID+") REFERENCES "+DBContract.TermEntry.TABLE_NAME+"("+DBContract.TermEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.Multimedia_TermEntry.COLUMN_NAME_MULTIMEDIAID+", "+DBContract.Multimedia_TermEntry.COLUMN_NAME_TermID+")" +
                        ");";

        public LocalDBHelper(Context context) {
            super(context, dbName, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(USER_TABLE_CREATE);
            sqLiteDatabase.execSQL(COMPETITION_TABLE_CREATE);
            sqLiteDatabase.execSQL(RALLY_TABLE_CREATE);
            sqLiteDatabase.execSQL(MULTIMEDIA_TABLE_CREATE);
            sqLiteDatabase.execSQL(ACTIVITY_TABLE_CREATE);
            sqLiteDatabase.execSQL(SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(TERM_TABLE_CREATE);

            sqLiteDatabase.execSQL(USER_COMPETITION_TABLE_CREATE);
            sqLiteDatabase.execSQL(RALLY_SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(TERM_SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(ACTIVITY_SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(MULTIMEDIA_ACTIVITY_TABLE_CREATE);
            sqLiteDatabase.execSQL(MULTIMEDIA_TERM_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        public void deleteTable(String tableName) {
            String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + tableName;
        }

    }

}
