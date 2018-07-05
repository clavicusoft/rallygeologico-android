package SqlDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import SqlEntities.Activity;
import SqlEntities.Competition;
import SqlEntities.CompetitionStatistics;
import SqlEntities.Multimedia;
import SqlEntities.OpcionesDB;
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
        values.put(DBContract.UserEntry.COLUMN_NAME_PASSWORD, user.getPassword());
        values.put(DBContract.UserEntry.COLUMN_NAME_FIRSTNAME, user.getFirstName());
        if(user.isLogged()){
            values.put(DBContract.UserEntry.COLUMN_NAME_ISLOGGED, 1);
        } else {
            values.put(DBContract.UserEntry.COLUMN_NAME_ISLOGGED, 0);
        }
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

        if(user.getCompetitions().size()>0){
            for(int i = 0; i < user.getCompetitions().size(); i++) {
                try {
                    newRowId+=this.insertCompetition(user.getCompetitions().get(i));
                } catch (android.database.sqlite.SQLiteException e) {}
                try {
                    newRowId+=this.insertUser_Competition(user.getUserId(),user.getCompetitions().get(i).getCompetitionId());
                } catch (android.database.sqlite.SQLiteException e) {}
            }
        }

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
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_DESCRIPTION, competition.getDescription());
        values.put(DBContract.CompetitionEntry.COLUMN_NAME_NAME, competition.getName());
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
        values.put(DBContract.RallyEntry.COLUMN_NAME_IMAGEURL, rally.getImageURL());
        values.put(DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION, rally.getDescription());
        values.put(DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD, rallyDownloaded);

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
        values.put(DBContract.ActivityEntry.COLUMN_NAME_NAME, activity.getActivityName());
        values.put(DBContract.ActivityEntry.COLUMN_NAME_DESCRIPTION, activity.getActivityDescription());
        values.put(DBContract.ActivityEntry.COLUMN_NAME_POINTS, activity.getActivityPoints());
        values.put(DBContract.ActivityEntry.COLUMN_NAME_TYPE, activity.getGetActivityType());
        values.put(DBContract.ActivityEntry.COLUMN_NAME_VISITED, activity.is_visited());

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
        values.put(DBContract.SiteEntry.COLUMN_NAME_DESCRIPTION, site.getSiteDescription());
        values.put(DBContract.SiteEntry.COLUMN_NAME_LATITUD, site.getLatitud());
        values.put(DBContract.SiteEntry.COLUMN_NAME_LONGITUD, site.getLongitud());
        values.put(DBContract.SiteEntry.COLUMN_NAME_POINTSFORVISIT, site.getPointsForVisit());
        values.put(DBContract.SiteEntry.COLUMN_NAME_ISVISITED, site.is_visited());
        values.put(DBContract.SiteEntry.COLUMN_NAME_ISEASTEREGG, site.is_easter_egg());
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
     * Metodo para ingresar una opcion a la base de datos local
     * @param opciones que desea ser ingresado
     */
    public long insertOption(OpcionesDB opciones){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.OptionEntry.COLUMN_NAME_ID, opciones.getOptionsId() );
        values.put(DBContract.OptionEntry.COLUMN_NAME_ISCORRECT, opciones.is_correct());
        values.put(DBContract.OptionEntry.COLUMN_NAME_TEXT, opciones.getOptionsText());
        values.put(DBContract.OptionEntry.COLUMN_NAME_ACTIVITYID, opciones.getActivitiId());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.TermEntry.TABLE_NAME,
                null,
                values
        );

        return newRowId;
    }

    /**
     * Metodo para ingresar una CompetitionStatitics a la base de datos local
     * @param competitionStatistics que desea ser ingresado
     */
    public long insertCompetitionStatistics(CompetitionStatistics competitionStatistics){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.CompetitionStatisticsEntry.COLUMN_NAME_ID, competitionStatistics.getCompetitionStatisticsId());
        values.put(DBContract.CompetitionStatisticsEntry.COLUMN_NAME_POINTS, competitionStatistics.getPoints());

        /**
         * Inserta la nueva linea en la base de datos y devuelve la llave primaria de la nueva linea
         */
        long newRowId = database.insert(
                DBContract.TermEntry.TABLE_NAME,
                null,
                values
        );

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
     * Crea la relacion entre una actividad y la estadisticas de competencias relacionado
     * @param competitionStatisticId identificador de la estadistica de la competencia
     * @param activityId identificador de la actividad
     * @return filas modificadas
     */
    public long insertCompetitionStatistics_Activity(int competitionStatisticId, int activityId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_ACTIVITYID, activityId);
        values.put(DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_CompetitionStatisticsID, competitionStatisticId);

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
     * Crea la relacion entre una actividad y la estadisticas de competencias relacionado
     * @param competitionStatisticId identificador de la estadistica de la competencia
     * @param siteId identificador del sitio
     * @return filas modificadas
     */
    public long insertCompetitionStatistics_Site(int competitionStatisticId, int siteId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_SITEID, siteId);
        values.put(DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_CompetitionStatisticsID, competitionStatisticId);

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
     * Crea la relacion entre una actividad y la estadisticas de competencias relacionado
     * @param competitionStatisticId identificador de la estadistica de la competencia
     * @param userId identificador del usuario
     * @return filas modificadas
     */
    public long insertCompetitionStatistics_User_Competition(int competitionStatisticId, int userId, int competitionId){
        /**
         * Crea un mapa de valores donde los nombres de las columnas es el Key
         */
        ContentValues values = new ContentValues();
        values.put(DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_USERID, userId);
        values.put(DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONSTATISTICSID, competitionStatisticId);
        values.put(DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONID, competitionId);

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
                DBContract.UserEntry.COLUMN_NAME_PASSWORD,
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
                DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD,
                DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION,
                DBContract.RallyEntry.COLUMN_NAME_IMAGEURL,
                DBContract.RallyEntry.COLUMN_NAME_NAME
        };

        /**
         *  Como queremos que esten ordenados los resultados
         */
        String sortOrder =
                DBContract.RallyEntry.COLUMN_NAME_NAME + " ASC";

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

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_NAME);
            String name = cursor.getString(index);
            rally.setName(name);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_IMAGEURL);
            String imageURL = cursor.getString(index);
            rally.setImageURL(imageURL);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION);
            String description = cursor.getString(index);
            rally.setDescription(description);

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD);
            int temporatl = cursor.getInt(index);
            boolean download = temporatl>0;
            rally.setDownloaded(download);

            mArrayList.add(rally);
        }
        return mArrayList;
    }

    public Rally selectRallyFromId(String rallyId){
        /**
         * Se describen las columnas que va a devolver la consulta
         */
        String[] columnas = {
                DBContract.RallyEntry.COLUMN_NAME_RALLYID,
                DBContract.RallyEntry.COLUMN_NAME_NAME,
                DBContract.RallyEntry.COLUMN_NAME_IMAGEURL,
                DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION,
                DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD
        };

        String selection = DBContract.RallyEntry.COLUMN_NAME_RALLYID + " = ?";
        String[] selectionArgs = { rallyId };

        // Consulta
        Cursor cursor = database.query(
                DBContract.RallyEntry.TABLE_NAME,    // La tabla en la que se hace la consulta
                columnas,                           // El arreglo de las columnas que queremos que devuelva
                selection,                          // Las columnas para el WHERE
                selectionArgs,                      // Los valores para cada una de las columnas
                null,                       // La agrupación de las filas
                null,                        // El parametro HAVING para agrupar las filas
                null                          // The sort order
        );
        if(cursor != null)
            cursor.moveToFirst();

        Rally rally = null;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            int index;
            rally = new Rally();

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_RALLYID);
            int id = cursor.getInt(index);
            rally.setRallyId(id);

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

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_NAME);
            String name = cursor.getString(index);
            rally.setName(name);
        }
        return rally;
    }

    public ArrayList<Rally> selectAllDownloadedRallies(){
        /**
         * Se describen las columnas que va a devolver la consulta
         */
        String[] columnas = {
                DBContract.RallyEntry.COLUMN_NAME_RALLYID,
                DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD,
                DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION,
                DBContract.RallyEntry.COLUMN_NAME_IMAGEURL,
                DBContract.RallyEntry.COLUMN_NAME_NAME
        };

        // Como queremos que esten ordenados los resultados
        String sortOrder = DBContract.RallyEntry.COLUMN_NAME_NAME + " ASC";

        String selection = DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD + " = ?";
        String[] selectionArgs = { "1" };

        // La consulta en si
        Cursor cursor = database.query(
                DBContract.RallyEntry.TABLE_NAME,    // La tabla en la que se hace la consulta
                columnas,                           // El arreglo de las columnas que queremos que devuelva
                selection,                          // Las columnas para el WHERE
                selectionArgs,                      // Los valores para cada una de las columnas
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

            index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_NAME);
            String name = cursor.getString(index);
            rally.setName(name);

            mArrayList.add(rally);
        }
        return mArrayList;
    }

    /**
     * Metodo para devolver todos los rallies asociados a un usuario
     * @param id Identificador del usuario del cual deseo obtener los rallies
     * @return una lista con los rallies asociados al usuario
     */
    public ArrayList<Rally> selectAllRalliesFromUser(int id){
        String rawQuery = "Select * FROM " + DBContract.RallyEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.CompetitionEntry.TABLE_NAME + " b " +
                " ON a." + DBContract.RallyEntry.COLUMN_NAME_RALLYID + " = b." + DBContract.CompetitionEntry.COLUMN_NAME_RALLYID +
                " INNER JOIN " + DBContract.User_CompetitionEntry.TABLE_NAME + " c " +
                " ON c." + DBContract.User_CompetitionEntry.COLUMN_NAME_ID + " = b." + DBContract.CompetitionEntry.COLUMN_NAME_COMPETITIONID +
                " WHERE c." + DBContract.User_CompetitionEntry.COLUMN_NAME_USERID + " = ?";

        String userId = "" + id;
        Cursor cursor = database.rawQuery(rawQuery,new String[]{userId});
        ArrayList<Rally> rallyList = new ArrayList<Rally>();

        if(cursor.moveToFirst()){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                int index;
                Rally rally = new Rally();

                index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_RALLYID);
                int rallyId = cursor.getInt(index);
                rally.setRallyId(rallyId);

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

                index = cursor.getColumnIndexOrThrow(DBContract.RallyEntry.COLUMN_NAME_NAME);
                String name = cursor.getString(index);
                rally.setName(name);

                rallyList.add(rally);
            }
        }

        return rallyList;
    }

    public User selectUser(String userId){
        String rawQuery = "Select * FROM " + DBContract.UserEntry.TABLE_NAME + " a " +
                " WHERE a." + DBContract.UserEntry.COLUMN_NAME_USERID + " = ?";

        Cursor cursor = database.rawQuery(rawQuery,new String[]{userId});
        User user = null;

        if(cursor.moveToFirst()) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                user = new User();
                int index;

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_EMAIL);
                String email = cursor.getString(index);
                user.setEmail(email);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_FIRSTNAME);
                String fisrtName = cursor.getString(index);
                user.setFirstName(fisrtName);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_LASTNAME);
                String lastName = cursor.getString(index);
                user.setLastName(lastName);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_PHOTOURL);
                String photo = cursor.getString(index);
                user.setPhotoUrl(photo);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_USERID);
                int id = cursor.getInt(index);
                user.setUserId(id);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_USERNAME);
                String username = cursor.getString(index);
                user.setUsername(username);
            }
        }

        return user;
    }

    public User selectUserByUsername(String username, String contrasena){
        String rawQuery = "Select * FROM " + DBContract.UserEntry.TABLE_NAME +
                " WHERE " + DBContract.UserEntry.COLUMN_NAME_USERNAME + " = ?" +
                " AND " + DBContract.UserEntry.COLUMN_NAME_PASSWORD + " = ?";

        Cursor cursor = database.rawQuery(rawQuery,new String[]{username, contrasena});

        User user = null;

        if(cursor.moveToFirst()) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                user = new User();
                int index;

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_EMAIL);
                String email = cursor.getString(index);
                user.setEmail(email);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_FIRSTNAME);
                String fisrtName = cursor.getString(index);
                user.setFirstName(fisrtName);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_LASTNAME);
                String lastName = cursor.getString(index);
                user.setLastName(lastName);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_PHOTOURL);
                String photo = cursor.getString(index);
                user.setPhotoUrl(photo);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_USERID);
                int id = cursor.getInt(index);
                user.setUserId(id);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_USERNAME);
                String uname = cursor.getString(index);
                user.setUsername(uname);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_ISLOGGED);
                String logged = cursor.getString(index);
                if(logged.equalsIgnoreCase("0")){
                    user.setLogged(false);
                }else{
                    user.setLogged(true);
                }
            }
        }

        return user;
    }

    public User selectLoggedUser(){
        String rawQuery = "Select * FROM " + DBContract.UserEntry.TABLE_NAME + " a " +
                " WHERE a." + DBContract.UserEntry.COLUMN_NAME_ISLOGGED + " = ?";

        Cursor cursor = database.rawQuery(rawQuery,new String[] {"1"});
        User user = null;

        if(cursor.moveToFirst()) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                user = new User();
                int index;

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_EMAIL);
                String email = cursor.getString(index);
                user.setEmail(email);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_FIRSTNAME);
                String fisrtName = cursor.getString(index);
                user.setFirstName(fisrtName);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_LASTNAME);
                String lastName = cursor.getString(index);
                user.setLastName(lastName);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_PHOTOURL);
                String photo = cursor.getString(index);
                user.setPhotoUrl(photo);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_USERID);
                int id = cursor.getInt(index);
                user.setUserId(id);

                index = cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME_USERNAME);
                String uname = cursor.getString(index);
                user.setUsername(uname);
            }
        }

        return user;
    }

    public void updateUser(User user){
        ContentValues values = new ContentValues();
        values.put(DBContract.UserEntry.COLUMN_NAME_USERNAME,user.getUsername());
        values.put(DBContract.UserEntry.COLUMN_NAME_EMAIL, user.getEmail());
        values.put(DBContract.UserEntry.COLUMN_NAME_PASSWORD, user.getPassword());
        values.put(DBContract.UserEntry.COLUMN_NAME_FIRSTNAME, user.getFirstName());
        if (user.isLogged()){
            values.put(DBContract.UserEntry.COLUMN_NAME_ISLOGGED, 1);
        } else {
            values.put(DBContract.UserEntry.COLUMN_NAME_ISLOGGED, 0);
        }
        values.put(DBContract.UserEntry.COLUMN_NAME_LASTNAME, user.getLastName());
        values.put(DBContract.UserEntry.COLUMN_NAME_PHOTOURL, user.getPhotoUrl());
        values.put(DBContract.UserEntry.COLUMN_NAME_USERID, user.getUserId());

        database.update(
                DBContract.UserEntry.TABLE_NAME,
                values,
                DBContract.UserEntry.COLUMN_NAME_USERID + " = " + user.getUserId(),
                null
        );
    }

    /**
     * Actualiza un rally en la base de datos
     * @param rally que queremos actualizar en la base de datos
     */
    public void updateRally(Rally rally){
        ContentValues values = new ContentValues();
        values.put(DBContract.RallyEntry.COLUMN_NAME_RALLYID,rally.getRallyId());
        values.put(DBContract.RallyEntry.COLUMN_NAME_IMAGEURL, rally.getImageURL());
        values.put(DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD, rally.getIsDownloaded());
        values.put(DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION, rally.getDescription());
        values.put(DBContract.RallyEntry.COLUMN_NAME_NAME, rally.getName());

        database.update(
                DBContract.RallyEntry.TABLE_NAME,
                values,
                DBContract.RallyEntry.COLUMN_NAME_RALLYID + " = " + rally.getRallyId(),
                null
        );

        long newRowId = 0;
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
    }

    /*Contador de puntos*/
    public void updatePointsAwarded(int siteID)
    {

    }

    /**
     * Metodo para actualizar el estatus de un sitio en la base de datos local
     * @param siteId identificador del sitio a modificar
     * @return cantidad de filas modificadas, devuelve un -1 si ocurrio un error
     */
    public int updateSiteVisit(int siteId){
        ContentValues values = new ContentValues();
        values.put(DBContract.SiteEntry.COLUMN_NAME_ISVISITED,true);
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
     * Metodo para actualizar el estatus de un sitio en la base de datos local
     * @param siteId identificador del sitio a modificar
     * @return cantidad de filas modificadas, devuelve un -1 si ocurrio un error
     */
    public int updateSiteVisit(int siteId, int status){
        ContentValues values = new ContentValues();
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
     * @param siteId Identificador del sitio
     * @return un sitio
     */
    public Site selectSiteFromId(int siteId){
        String rawQuery = "Select * FROM " + DBContract.SiteEntry.TABLE_NAME + " a " +
                " WHERE a." + DBContract.SiteEntry.COLUMN_NAME_ID + " = " + siteId;

        Cursor cursor = database.rawQuery(
                rawQuery,
                null
        );

        Site site = new Site();

        if(cursor != null){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                int index;

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_NAME);
                String name = cursor.getString(index);
                site.setSiteName(name);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_DESCRIPTION);
                String description = cursor.getString(index);
                site.setSiteDescription(description);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_LATITUD);
                String latitud = cursor.getString(index);
                site.setLatitud(latitud);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_LONGITUD);
                String longitud = cursor.getString(index);
                site.setLongitud(longitud);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_POINTSFORVISIT);
                int siteVIsitedPoints = cursor.getInt(index);
                site.setPointsForVisit(siteVIsitedPoints);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_ID);
                int siteId2 = cursor.getInt(index);
                site.setSiteId(siteId2);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_ISVISITED);
                int temporal2 = cursor.getInt(index);
                boolean visited = temporal2>0;
                site.set_visited(visited);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_ISEASTEREGG);
                int temporal1 = cursor.getInt(index);
                boolean easterEgg = temporal1>0;
                site.setIs_easter_egg(easterEgg);

            }
        }

        return site;
    }

    /**
     * Metodo para devolver todos los sitios asociados a un rally
     * @param rallyId Identificador del rally del cual deseo obtener los puntos
     * @return una lista con los sitios asociados al punto
     */
    public List<Site> selectAllSitesFromRally(int rallyId){
        String rawQuery = "Select * FROM " + DBContract.SiteEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.Rally_SiteEntry.TABLE_NAME + " b " +
                " ON " + " a." + DBContract.SiteEntry.COLUMN_NAME_ID + " = b." + DBContract.Rally_SiteEntry.COLUMN_NAME_SITEID +
                " WHERE b." + DBContract.Rally_SiteEntry.COLUMN_NAME_RallyID + " = " + rallyId;

        Cursor cursor = database.rawQuery(
                rawQuery,
                null
        );

        List<Site> siteList = new ArrayList<Site>();

        if(cursor != null){
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

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_DESCRIPTION);
                String description = cursor.getString(index);
                site.setSiteDescription(description);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_LATITUD);
                String latitud = cursor.getString(index);
                site.setLatitud(latitud);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_LONGITUD);
                String longitud = cursor.getString(index);
                site.setLongitud(longitud);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_POINTSFORVISIT);
                int siteVIsitedPoints = cursor.getInt(index);
                site.setPointsForVisit(siteVIsitedPoints);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_ISVISITED);
                int temporal2 = cursor.getInt(index);
                boolean visited = temporal2>0;
                site.set_visited(visited);

                index = cursor.getColumnIndexOrThrow(DBContract.SiteEntry.COLUMN_NAME_ISEASTEREGG);
                int temporal1 = cursor.getInt(index);
                boolean easterEgg = temporal1>0;
                site.setIs_easter_egg(easterEgg);

                siteList.add(site);
            }
        }

        return siteList;
    }

    /**
     * Metodo para devolver todas las activities asociados a un sitio
     * @param siteId Identificador del sitio del cual deseo obtener las actividades
     * @return una lista con las actividades asociados al punto
     */
    public List<Activity> selectAllActivitiesFromSite(int siteId){
        String rawQuery = "Select * FROM " + DBContract.ActivityEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.Activity_SiteEntry.TABLE_NAME + " b " +
                " ON " + " a." + DBContract.ActivityEntry.COLUMN_NAME_ID + " = b." + DBContract.Activity_SiteEntry.COLUMN_NAME_ACTIVITYID +
                " WHERE b." + DBContract.Activity_SiteEntry.COLUMN_NAME_SITEID + " = " + siteId;

        Cursor cursor = database.rawQuery(
                rawQuery,
                null
        );

        List<Activity> activityList = new ArrayList<Activity>();

        if(cursor != null){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                int index;
                Activity activity = new Activity();

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_ID);
                int activityId = cursor.getInt(index);
                activity.setActivityId(activityId);

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_NAME);
                String name = cursor.getString(index);
                activity.setActivityName(name);

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_DESCRIPTION);
                String description = cursor.getString(index);
                activity.setActivityDescription(description);

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_POINTS);
                int points = cursor.getInt(index);
                activity.setActivityPoints(points);

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_TYPE);
                int type = cursor.getInt(index);
                activity.setGetActivityType(type);

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_VISITED);
                int temporal = cursor.getInt(index);
                boolean visited = temporal>0;
                activity.setIs_visited(visited);

                activityList.add(activity);
            }
        }

        return activityList;
    }

    /**
     * Metodo para devolver todos los terminos asociados a un sitio
     * @param siteId Identificador del sitio del cual deseo obtener los terminos
     * @return una lista con los terminos asociados al punto
     */
    public List<Term> selectAllTermsFromSite(int siteId){
        String rawQuery = "Select * FROM " + DBContract.TermEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.Term_SiteEntry.TABLE_NAME + " b " +
                " ON " + " a." + DBContract.TermEntry.COLUMN_NAME_ID + " = b." + DBContract.Term_SiteEntry.COLUMN_NAME_TermID +
                " WHERE b." + DBContract.Term_SiteEntry.COLUMN_NAME_SITEID + " = " + siteId;

        Cursor cursor = database.rawQuery(
                rawQuery,
                null
        );

        List<Term> termList = new ArrayList<Term>();

        if(cursor != null){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                int index;
                Term term = new Term();

                index = cursor.getColumnIndexOrThrow(DBContract.TermEntry.COLUMN_NAME_ID);
                int termId = cursor.getInt(index);
                term.setTermId(termId);

                index = cursor.getColumnIndexOrThrow(DBContract.TermEntry.COLUMN_NAME_DESCRIPTION);
                String descripcion = cursor.getString(index);
                term.setTermDescription(descripcion);

                index = cursor.getColumnIndexOrThrow(DBContract.TermEntry.COLUMN_NAME_NAME);
                String name = cursor.getString(index);
                term.setTermName(name);

                termList.add(term);
            }
        }

        return termList;
    }

    /**
     * Metodo para devolver toda la multimedia asociados a una activity
     * @param activityId Identificador del activity del cual deseo obtener la multimedia
     * @return una lista con la multimedia asociados al activity
     */
    public List<Multimedia> selectAllMultimediaFromActivities(int activityId){
        String rawQuery = "Select * FROM " + DBContract.MultimediaEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.Multimedia_ActivityEntry.TABLE_NAME + " b " +
                " ON " + " a." + DBContract.MultimediaEntry.COLUMN_NAME_ID + " = b." + DBContract.Multimedia_ActivityEntry.COLUMN_NAME_MULTIMEDIAID +
                " WHERE b." + DBContract.Multimedia_ActivityEntry.COLUMN_NAME_ACTIVITYID + " = " + activityId;

        Cursor cursor = database.rawQuery(
                rawQuery,
                null
        );

        List<Multimedia> multimediaList = new ArrayList<Multimedia>();

        if(cursor != null){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                int index;
                Multimedia multimedia = new Multimedia();

                index = cursor.getColumnIndexOrThrow(DBContract.MultimediaEntry.COLUMN_NAME_ID);
                int multimediaId = cursor.getInt(index);
                multimedia.setMultimediaId(multimediaId);

                index = cursor.getColumnIndexOrThrow(DBContract.MultimediaEntry.COLUMN_NAME_TYPE);
                int type = cursor.getInt(index);
                multimedia.setMultimediaType(type);

                index = cursor.getColumnIndexOrThrow(DBContract.MultimediaEntry.COLUMN_NAME_URL);
                String url = cursor.getString(index);
                multimedia.setMultimediaURL(url);

                index = cursor.getColumnIndexOrThrow(DBContract.MultimediaEntry.COLUMN_NAME_NAME);
                String name = cursor.getString(index);
                multimedia.setMultimediaName(name);

                multimediaList.add(multimedia);
            }
        }

        return multimediaList;
    }

    /**
     * Metodo para devolver toda la multimedia asociados a un Termino
     * @param termId Identificador del termino del cual deseo obtener la multimedia
     * @return una lista con la multimedia asociados al termino
     */
    public List<Multimedia> selectAllMultimediaFromTerm(int termId){
        String rawQuery = "Select * FROM " + DBContract.MultimediaEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.Multimedia_TermEntry.TABLE_NAME + " b " +
                " ON " + " a." + DBContract.MultimediaEntry.COLUMN_NAME_ID + " = b." + DBContract.Multimedia_TermEntry.COLUMN_NAME_MULTIMEDIAID +
                " WHERE b." + DBContract.Multimedia_TermEntry.COLUMN_NAME_TermID + " = " + termId;

        Cursor cursor = database.rawQuery(
                rawQuery,
                null
        );

        List<Multimedia> multimediaList = new ArrayList<Multimedia>();

        if(cursor != null){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                int index;
                Multimedia multimedia = new Multimedia();

                index = cursor.getColumnIndexOrThrow(DBContract.MultimediaEntry.COLUMN_NAME_ID);
                int multimediaId = cursor.getInt(index);
                multimedia.setMultimediaId(multimediaId);

                index = cursor.getColumnIndexOrThrow(DBContract.MultimediaEntry.COLUMN_NAME_TYPE);
                int type = cursor.getInt(index);
                multimedia.setMultimediaType(type);

                index = cursor.getColumnIndexOrThrow(DBContract.MultimediaEntry.COLUMN_NAME_URL);
                String url = cursor.getString(index);
                multimedia.setMultimediaURL(url);

                multimediaList.add(multimedia);
            }
        }

        return multimediaList;
    }

    /**
     * Clase con la definicion de las tablas de la base de datos
     */
    public final class DBContract {
        // To prevent someone from accidentally instantiating the contract class, make the constructor private.
        private DBContract() {}


        /** Inner class that defines the USER table contents */
        public class UserEntry implements BaseColumns {
            private static final String TABLE_NAME = "USERS";
            private static final String COLUMN_NAME_USERID = "userId";
            private static final String COLUMN_NAME_PASSWORD = "password";
            private static final String COLUMN_NAME_USERNAME = "username";
            private static final String COLUMN_NAME_FIRSTNAME = "firstName";
            private static final String COLUMN_NAME_LASTNAME = "lastName";
            private static final String COLUMN_NAME_EMAIL = "email";
            private static final String COLUMN_NAME_PHOTOURL = "photoURL";
            private static final String COLUMN_NAME_ISLOGGED = "isLogged";
        }

        /** Inner class that defines the COMPETITION table contents */
        public class CompetitionEntry implements BaseColumns {
            public static final String TABLE_NAME = "COMPETITION";
            public static final String COLUMN_NAME_COMPETITIONID = "competitionId";
            public static final String COLUMN_NAME_ACTIVE = "is_active";
            public static final String COLUMN_NAME_STARTINGDATE = "startingDate";
            public static final String COLUMN_NAME_FINISHINGDATE = "finishingDate";
            public static final String COLUMN_NAME_ISPUBLIC = "is_Public";
            public static final String COLUMN_NAME_NAME = "name";
            public static final String COLUMN_NAME_DESCRIPTION = "description";
            public static final String COLUMN_NAME_RALLYID = "rallyId";
        }

        /** Inner class that defines the RALLY contents */
        public class RallyEntry implements BaseColumns {
            public static final String TABLE_NAME = "RALLY";
            public static final String COLUMN_NAME_RALLYID = "rallyId";
            public static final String COLUMN_NAME_NAME = "rallyName";
            public static final String COLUMN_NAME_IMAGEURL = "imageUrl";
            public static final String COLUMN_NAME_DESCRIPTION = "rallyDescription";
            public static final String COLUMN_NAME_DOWNLOAD = "is_download";
        }

        /** Inner class that defines the MULTIMEDIA contents */
        public class MultimediaEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA";
            public static final String COLUMN_NAME_ID = "multimediaId";
            public static final String COLUMN_NAME_TYPE = "multimediaType";
            public static final String COLUMN_NAME_URL = "multimediaURL";
            public static final String COLUMN_NAME_NAME = "multimediaName";
        }

        /** Inner class that defines the ACTIVITY contents */
        public class ActivityEntry implements BaseColumns {
            public static final String TABLE_NAME = "ACTIVITY";
            public static final String COLUMN_NAME_ID = "activityId";
            public static final String COLUMN_NAME_NAME = "activityName";
            public static final String COLUMN_NAME_DESCRIPTION = "activityDescription";
            public static final String COLUMN_NAME_POINTS = "activityPoints";
            public static final String COLUMN_NAME_TYPE = "activityType";
            public static final String COLUMN_NAME_VISITED = "is_visited";
        }

        /** Inner class that defines the SITE contents */
        public class SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "SITE";
            public static final String COLUMN_NAME_ID = "siteId";
            public static final String COLUMN_NAME_NAME = "siteName";
            public static final String COLUMN_NAME_DESCRIPTION = "siteDescription";
            public static final String COLUMN_NAME_LATITUD = "latitude";
            public static final String COLUMN_NAME_LONGITUD = "longitude";
            public static final String COLUMN_NAME_POINTSFORVISIT = "pointsForVisit";
            public static final String COLUMN_NAME_ISVISITED = "is_visited";
            public static final String COLUMN_NAME_ISEASTEREGG = "is_easter_egg";
        }

        /** Inner class that defines the TERM contents */
        public class TermEntry implements BaseColumns {
            public static final String TABLE_NAME = "TERM";
            public static final String COLUMN_NAME_ID = "termID";
            public static final String COLUMN_NAME_NAME = "termName";
            public static final String COLUMN_NAME_DESCRIPTION = "termDescription";
        }

        /** Inner class that defines the OPTIONS contents */
        public class OptionEntry implements BaseColumns {
            public static final String TABLE_NAME = "OPTION";
            public static final String COLUMN_NAME_ID = "optionID";
            public static final String COLUMN_NAME_TEXT = "OptionText";
            public static final String COLUMN_NAME_ISCORRECT = "is_correct";
            public static final String COLUMN_NAME_ACTIVITYID = "ActivityId";
        }

        /** Inner class that defines the COMPETITIONSTATISTICS contents */
        public class CompetitionStatisticsEntry implements BaseColumns {
            public static final String TABLE_NAME = "COMPETITIONSTATISTICS";
            public static final String COLUMN_NAME_ID = "CompetitionStatisticsID";
            public static final String COLUMN_NAME_POINTS = "points";
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

        /** Inner class that defines the Term_site table contents */
        public class Term_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "TERM_SITE";
            public static final String COLUMN_NAME_TermID = "TermId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /** Inner class that defines the CompetitionStatistics_site table contents */
        public class CompetitionStatistics_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "COMPETITIONSTATISTICS_SITE";
            public static final String COLUMN_NAME_CompetitionStatisticsID = "CompetitionStatisticsId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /** Inner class that defines the Activity_Site table contents */
        public class Activity_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "ACTIVITY_SITE";
            public static final String COLUMN_NAME_ACTIVITYID = "activityId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /** Inner class that defines the CompetitionStatistics_Activity table contents */
        public class CompetitionStatistics_ActivityEntry implements BaseColumns {
            public static final String TABLE_NAME = "COMPETITIONSTATISTICS_ACTIVITY";
            public static final String COLUMN_NAME_ACTIVITYID = "activityId";
            public static final String COLUMN_NAME_CompetitionStatisticsID = "CompetitionStatisticsId";
        }

        /** Inner class that defines the CompetitionStatistics_Activity table contents */
        public class CompetitionStatistics_User_CompetitionEntry implements BaseColumns {
            public static final String TABLE_NAME = "COMPETITIONSTATISTICS_USER_COMPETITION";
            public static final String COLUMN_NAME_USERID = "userId";
            public static final String COLUMN_NAME_COMPETITIONID = "competitionId";
            public static final String COLUMN_NAME_COMPETITIONSTATISTICSID = "CompetitionStatisticsId";
        }

        /** Inner class that defines the Multimedia_Activity table contents */
        public class Multimedia_ActivityEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA_ACTIVITY";
            public static final String COLUMN_NAME_MULTIMEDIAID = "multimediaId";
            public static final String COLUMN_NAME_ACTIVITYID = "activityId";
        }

        /** Inner class that defines the Multimedia_TermEntry table contents */
        public class Multimedia_TermEntry implements BaseColumns {
            private static final String TABLE_NAME = "MULTIMEDIA_ACTIVITY";
            private static final String COLUMN_NAME_TermID = "TermId";
            private static final String COLUMN_NAME_MULTIMEDIAID = "multimediaId";
        }
    }

    /**
     * Clase que permite manejar facilmente la base de datos y lo que se hace con ella
     */
    public static class LocalDBHelper extends SQLiteOpenHelper {
        private static final String dbName = "RallyDB";
        private static final int DATABASE_VERSION = 1;

        private static final String USER_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + DBContract.UserEntry.TABLE_NAME + "(" +
                        DBContract.UserEntry.COLUMN_NAME_USERID + " INTEGER PRIMARY KEY," +
                        DBContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT," +
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
                        DBContract.CompetitionEntry.COLUMN_NAME_DESCRIPTION +" TEXT NOT NULL," +
                        DBContract.CompetitionEntry.COLUMN_NAME_NAME +" TEXT NOT NULL," +
                        DBContract.CompetitionEntry.COLUMN_NAME_RALLYID +" INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.CompetitionEntry.COLUMN_NAME_RALLYID+") REFERENCES "+DBContract.RallyEntry.TABLE_NAME+"("+DBContract.RallyEntry.COLUMN_NAME_RALLYID+")" +
                        ");";

        private static final String RALLY_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.RallyEntry.TABLE_NAME+"(" +
                        DBContract.RallyEntry.COLUMN_NAME_RALLYID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.RallyEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        DBContract.RallyEntry.COLUMN_NAME_IMAGEURL + " TEXT," +
                        DBContract.RallyEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                        DBContract.RallyEntry.COLUMN_NAME_DOWNLOAD + " BIT DEFAULT 0" +
                        ");";

        private static final String MULTIMEDIA_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.MultimediaEntry.TABLE_NAME+"(" +
                        DBContract.MultimediaEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.MultimediaEntry.COLUMN_NAME_TYPE + " INTEGER NOT NULL," +
                        DBContract.MultimediaEntry.COLUMN_NAME_URL + " TEXT NOT NULL, " +
                        DBContract.MultimediaEntry.COLUMN_NAME_NAME + " TEXT NOT NULL" +
                        ");";

        private static final String ACTIVITY_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+ DBContract.ActivityEntry.TABLE_NAME +"(" +
                        DBContract.ActivityEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.ActivityEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        DBContract.ActivityEntry.COLUMN_NAME_DESCRIPTION +" TEXT NOT NULL," +
                        DBContract.ActivityEntry.COLUMN_NAME_POINTS + " INTEGER NOT NULL," +
                        DBContract.ActivityEntry.COLUMN_NAME_TYPE + " INTEGER NOT NULL" +
                        ");";

        private static final String SITE_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+DBContract.SiteEntry.TABLE_NAME+" (" +
                        DBContract.SiteEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                        DBContract.SiteEntry.COLUMN_NAME_LATITUD + " TEXT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_LONGITUD + " TEXT NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_POINTSFORVISIT + " INTEGER NOT NULL," +
                        DBContract.SiteEntry.COLUMN_NAME_ISVISITED + " BIT DEFAULT 0," +
                        DBContract.SiteEntry.COLUMN_NAME_ISEASTEREGG + " BIT DEFAULT 0" +
                        ");";

        private static final String TERM_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+ DBContract.TermEntry.TABLE_NAME +" (" +
                        DBContract.TermEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.TermEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        DBContract.TermEntry.COLUMN_NAME_DESCRIPTION + " TEXT" +
                        ");";

        private static final String COMPETITIONSTATISTICS_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+ DBContract.CompetitionStatisticsEntry.TABLE_NAME +" (" +
                        DBContract.CompetitionStatisticsEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.CompetitionStatisticsEntry.COLUMN_NAME_POINTS + " INTEGER NOT NULL" +
                        ");";

        private static final String OPTION_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS "+ DBContract.OptionEntry.TABLE_NAME +" (" +
                        DBContract.OptionEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.OptionEntry.COLUMN_NAME_TEXT + " TEXT NOT NULL," +
                        DBContract.OptionEntry.COLUMN_NAME_ISCORRECT + " BIT DEFAULT 0," +
                        DBContract.OptionEntry.COLUMN_NAME_ACTIVITYID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.OptionEntry.COLUMN_NAME_ACTIVITYID+") REFERENCES "+DBContract.ActivityEntry.TABLE_NAME+"("+DBContract.ActivityEntry.COLUMN_NAME_ID+")" +
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

        private static final String COMPETITIONSTATISTICS_SITE_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS  "+DBContract.CompetitionStatistics_SiteEntry.TABLE_NAME+"(" +
                        DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_CompetitionStatisticsID + " INTEGER NOT NULL," +
                        DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_SITEID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_CompetitionStatisticsID+") REFERENCES "+DBContract.CompetitionStatisticsEntry.TABLE_NAME+"("+DBContract.CompetitionStatisticsEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_SITEID+") REFERENCES "+DBContract.SiteEntry.TABLE_NAME+"("+DBContract.SiteEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_CompetitionStatisticsID+", "+DBContract.CompetitionStatistics_SiteEntry.COLUMN_NAME_SITEID+")" +
                        ");";

        private static final String COMPETITIONSTATISTICS_ACTIVITY_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS  "+DBContract.CompetitionStatistics_ActivityEntry.TABLE_NAME+"(" +
                        DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_CompetitionStatisticsID + " INTEGER NOT NULL," +
                        DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_ACTIVITYID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_CompetitionStatisticsID+") REFERENCES "+DBContract.CompetitionStatisticsEntry.TABLE_NAME+"("+DBContract.CompetitionStatisticsEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_ACTIVITYID+") REFERENCES "+DBContract.ActivityEntry.TABLE_NAME+"("+DBContract.SiteEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_CompetitionStatisticsID+", "+DBContract.CompetitionStatistics_ActivityEntry.COLUMN_NAME_ACTIVITYID+")" +
                        ");";

        private static final String COMPETITIONSTATISTICS_USER_COMPETITION_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS  "+DBContract.CompetitionStatistics_User_CompetitionEntry.TABLE_NAME+"(" +
                        DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONSTATISTICSID + " INTEGER NOT NULL," +
                        DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_USERID + " INTEGER NOT NULL," +
                        DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONID + " INTEGER NOT NULL," +
                        "FOREIGN KEY ("+DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONSTATISTICSID+") REFERENCES "+DBContract.CompetitionStatisticsEntry.TABLE_NAME+"("+DBContract.CompetitionStatisticsEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_USERID+") REFERENCES "+DBContract.UserEntry.TABLE_NAME+"("+DBContract.SiteEntry.COLUMN_NAME_ID+")," +
                        "FOREIGN KEY ("+DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONID+") REFERENCES "+DBContract.CompetitionEntry.TABLE_NAME+"("+DBContract.SiteEntry.COLUMN_NAME_ID+")," +
                        "PRIMARY KEY ("+DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONSTATISTICSID+", "+DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_USERID+", "+DBContract.CompetitionStatistics_User_CompetitionEntry.COLUMN_NAME_COMPETITIONID+")" +
                        ");";

        private LocalDBHelper(Context context) {
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
            sqLiteDatabase.execSQL(OPTION_TABLE_CREATE);
            sqLiteDatabase.execSQL(COMPETITIONSTATISTICS_TABLE_CREATE);

            sqLiteDatabase.execSQL(USER_COMPETITION_TABLE_CREATE);
            sqLiteDatabase.execSQL(RALLY_SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(TERM_SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(ACTIVITY_SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(MULTIMEDIA_ACTIVITY_TABLE_CREATE);
            sqLiteDatabase.execSQL(MULTIMEDIA_TERM_TABLE_CREATE);
            sqLiteDatabase.execSQL(COMPETITIONSTATISTICS_ACTIVITY_TABLE_CREATE);
            sqLiteDatabase.execSQL(COMPETITIONSTATISTICS_SITE_TABLE_CREATE);
            sqLiteDatabase.execSQL(COMPETITIONSTATISTICS_USER_COMPETITION_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        public void deleteTable(String tableName) {
            String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + tableName;
        }

    }

}
