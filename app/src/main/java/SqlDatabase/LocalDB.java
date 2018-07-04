package SqlDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
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
    public long insertUser_Competition(String userId, String competitionId){
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

    /*
     * Metodo para haer pruebas en la base de datos
     */
    /*public void prueba(){
        database.execSQL("delete from "+ DBContract.Rally_SiteEntry.TABLE_NAME);
        //database.execSQL("delete from "+ DBContract.UserEntry.TABLE_NAME);
        database.execSQL("delete from "+ DBContract.SiteEntry.TABLE_NAME);
        database.execSQL("delete from "+ DBContract.RallyEntry.TABLE_NAME);
        User user1 = new User("1","password1","Usuario 1","Pablo ","Madrigal"," Correo 1","Foto 1",false);
        User user2 = new User("2","password2","Usuario 2","Marco ","Madrigal"," Correo 2","Foto 2",false);
        long prueba1 = this.insertUser(user1);
        long prueba2 = this.insertUser(user2);

        String descripcionRally1 = "El Rally #1 incluye localidades en sitios del cantón de La Cruz como: El parque nacional Santa Rosa, con increíbles paisajes naturales declarados patrimonio de la humanidad; y Cuajiniquil, pueblo costero cuya principal actividad económica es la pesca y el creciente desarrollo turístico.";
        String descripcionRally2 = "Esta geoaventura abarca pueblos costeros del cantón de La Cruz, entre los que destacan Cuajiniquil, pueblo cercano a sitios turísticos; El Jobo, pueblo con diversidad de playas y el centro poblacional del cantón, identificado por sus maravillosas vistas a la cordillera volcánica de Guanacaste y a bahía Salinas.";
        Rally rally1 = new Rally(1,"rally 1",descripcionRally1, 3,"https://www.google.com/logos/doodles/2013/qixi_festival__chilseok_-2009005-hp.jpg","Utiliza 34Mb",false);
        Rally rally2 = new Rally(2,"rally 2",descripcionRally2, 6,"https://www.google.com/logos/2012/montessori-hp.jpg","Utiliza 55Mb ",false);
        String descripcionSitio1 = "Desde este punto se pueden observar volcanes de la Cordillera Volcánica de Guanacaste. El volcán Orosí (N48°), el volcán Cacao (N60°) y el volcán Rincón de la Vieja (N90°).Hacia el azimut 110° (Sureste) se observa el cerro Góngora que es un domo volcánico con una edad de unos 8 millones de años. Un domo de lava se forma cuando sale lava muy densa o viscosa, que no puede fluir y se enfría. Queda como una protuberancia del terreno. Ligeramente a la derecha del cerro Góngora se observan protuberancias del terreno más pequeñas que corresponden con los domos de Cañas Dulces, que fueron domos que se formaron por erupciones de lava que ocurrieron hace unos 1.5 millones de años. El participante en este juego está parado sobre la Meseta de Ignimbrita, que es una planicie formada por una serie de erupciones volcánicas violentas que cubrieron la topografía existente hace unos 2 millones explosión  de  un  volcán.  En  este  caso  rellenaron  la  topografía  existente  y  dejaron  una  planicie  (Meseta  de  ignimbrita). Desde  este  punto  se  puede  observar  la  península  de  Santa  Elena  al  Noroeste,    El  cerro  El  Inglés  que  es  uno  de  los  puntos  más  altos  de  la  península  de  Santa  Elena  con  más  de  500  m  de  altura.    Desde  este  sitio  lo  puede  observar  hacia  el  Noroeste  (305°),  que  está  compuesta  por  rocas  provenientes  del  manto    terrestre.    Es  decir,  rocas  que  viajaron  desde  más  de  40  kilómetros  para  llegar  a  la  superficie  terrestre.  Estas  rocas  son  más  antiguas  que  80  millones  de  años.";
        String descripcionSitio2 = "La Casona está edificada sobre rocas de la meseta ignimbrítica de unos 2 millones de años de antiguedad.  Específicamente en este sitio, estas rocas contienen fragmentos de lava negruscos, que se llaman escorias por contener muchos poros, que fueron cavidades que contenían gases volcánicos cuando se formaron.";

        String descripcionSitio3="Una  discordancia  angular  es  una  superficie  que  representa  una  roca  más  joven,  depositada  sobre  una  más  antigua  que  ha  sido  deformada  y  erosionad";
        String descripcionSitio4="Las  peridotitas  (lo  lleva  a  4a)  tuvieron  un  viaje  de  por  lo  menos  40  kilómetros  desde  el  interior  del  planeta  Tierra  hasta  el  sitio  donde  están  hoy,  la  península  de  Santa  Elena.  Durante  este  viaje,  las  peridotitas  fueron  “cruzadas”  por  otra  roca  fundida,  que  entró  y  rellenó  las  zonas  más  débiles  de  la  peridotita";
        String descripcionSitio5="Este  lugar  muestra  la  fuerza  y  dramatismo  de  las  fuerzas  de  la  Tierra,  pues  antes  del  12  de  Octubre  del  2017  había  una  poza,  que  se  llamaba  la  Poza  de  El  General";
        String descripcionSitio6="En  esta  localidad  se  observan  varios  estratos,  que  son  las  capas  en  que  se  encuentran  divididos  los  sedimentos,  como  un  resultado  de  sus  características  físicas.  Estas  rocas  se  formaron  por  acumulación  de  arenas,  en  el  fondo  marino  hace  unos  35  millones  de  años. ";


        Site site1 = new Site(1,"El Monumento",descripcionSitio1,"10.5005","-85.3669",1,20,5);
        Site site2 = new Site(2,"La Casona",descripcionSitio2,"10.5002","-85.3675",1,20,5);
        Site site3 = new Site(3,"La Discortancia de la Cortina",descripcionSitio3,"10.56874","-85.39370",4,20,5);
        Site site4 = new Site(4,"Peridotitas de Murciélago",descripcionSitio4,"10.53975","-85.43823",4,20,5);
        Site site5 = new Site(5,"La expoza de El General",descripcionSitio5,"10.53820","-85.43826",1,20,5);
        Site site6 = new Site(6,"La 4x4",descripcionSitio6,"10.56077","-85.42248",1,20,5);

        rally1.addSite(site1);
        rally1.addSite(site2);
        rally1.addSite(site3);
        rally1.addSite(site4);
        rally1.addSite(site5);
        rally1.addSite(site6);

        String descripcionSitio7 = "Desde este mirador. Se observa La isla Los Muñecos. Localice visualmente el muñeco de la isla, que es un monolito de piedra caliza (relicto de erosión) en el extremo izquierdo de la isla. Active la brújula. Dirija la brújula hacia el “muñeco” y acepte el azimuth. Esta isla está compuesta por calizas, que son rocas ricas en carbonato de calcio (CaCO3).  Estas rocas se disuelven con el agua y forman hermosas “esculturas” como El Muñeco.  Anteriormente eran 2 muñecos, pero hace unos años el muñeco más grande, que llamaban Nefertiti desapareció.  Las rocas calizas que conforman esta isla fueron originados por construcciones de arrecifes de coral que se formaron hace unos 30 millones de años. ";
        String descripcionSitio8 = "Estas rocas se formaron hace unos 35 millones de años, son muy parecidas a las de la playa 4x4.  Se pueden observar algunos troncos. y espectaculares bioturbaciones destacadas con líneas punteadas y flechas. ";
        Site site7 = new Site(7,"El mirador",descripcionSitio7,"10.5775","-85.4186",1,15,0);
        Site site8 = new Site(8,"La Islita",descripcionSitio8,"10.5783","-85.4176",1,25,0);
        rally2.addSite(site7);
        rally2.addSite(site8);
        this.insertRally(rally1);
        this.insertRally(rally2);
    }*/

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

    public Rally selectRallyFromId(String rallyId){
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
        }
        return rally;
    }

    public ArrayList<Rally> selectAllDownloadedRallies(){
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
     * @param id Identificador del usuario del cual deseo obtener los rallies
     * @return una lista con los rallies asociados al usuario
     */
    public ArrayList<Rally> selectAllRalliesFromUser(String id){
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
        }

        return rallyList;
    }

    public int selectAllRalliesCountFromUser(String id){
        String rawQuery = "SELECT COUNT(" + DBContract.CompetitionEntry.COLUMN_NAME_TOTALPOINTS + ") FROM " + DBContract.RallyEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.CompetitionEntry.TABLE_NAME + " b " +
                " ON a." + DBContract.RallyEntry.COLUMN_NAME_RALLYID + " = b." + DBContract.CompetitionEntry.COLUMN_NAME_RALLYID +
                " INNER JOIN " + DBContract.User_CompetitionEntry.TABLE_NAME + " c " +
                " ON c." + DBContract.User_CompetitionEntry.COLUMN_NAME_ID + " = b." + DBContract.CompetitionEntry.COLUMN_NAME_COMPETITIONID +
                " WHERE c." + DBContract.User_CompetitionEntry.COLUMN_NAME_USERID + " = ?";

        String userId = "" + id;
        Cursor cursor = database.rawQuery(rawQuery,new String[]{userId});
        int rallies = 0;
        if(cursor.moveToFirst()){
            // The Cursor is now set to the right position
            if(cursor.moveToNext()){
                rallies = cursor.getInt(0);
            } else {
                rallies = 0;
            }
        }
        return rallies;
    }

    public int selectAllRalliesPointsFromUser(String id){
        String rawQuery = "SELECT SUM(" + DBContract.CompetitionEntry.COLUMN_NAME_TOTALPOINTS + ") FROM " + DBContract.RallyEntry.TABLE_NAME + " a " +
                " INNER JOIN " + DBContract.CompetitionEntry.TABLE_NAME + " b " +
                " ON a." + DBContract.RallyEntry.COLUMN_NAME_RALLYID + " = b." + DBContract.CompetitionEntry.COLUMN_NAME_RALLYID +
                " INNER JOIN " + DBContract.User_CompetitionEntry.TABLE_NAME + " c " +
                " ON c." + DBContract.User_CompetitionEntry.COLUMN_NAME_ID + " = b." + DBContract.CompetitionEntry.COLUMN_NAME_COMPETITIONID +
                " WHERE c." + DBContract.User_CompetitionEntry.COLUMN_NAME_USERID + " = ?";

        String userId = "" + id;
        Cursor cursor = database.rawQuery(rawQuery,new String[]{userId});
        int points = 0;
        if(cursor.moveToFirst()){
                // The Cursor is now set to the right position
            if(cursor.moveToNext()){
                points = cursor.getInt(0);
            } else {
                points = 0;
            }
        }
        return points;
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
                String id = cursor.getString(index);
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
                String id = cursor.getString(index);
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
                String id = cursor.getString(index);
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
        values.put(DBContract.RallyEntry.COLUMN_NAME_POINTSAWARDED,rally.getPointsAwarded());
        values.put(DBContract.RallyEntry.COLUMN_NAME_MEMORYUSAGE, rally.getMemoryUsage());
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
     * @param newStatus nuevo estatus para el sitio seleccionado
     * @return cantidad de filas modificadas, devuelve un -1 si ocurrio un error
     */
    public int updateSiteVisit(int siteId, int newStatus){
        ContentValues values = new ContentValues();
        values.put(DBContract.SiteEntry.COLUMN_NAME_STATUS,newStatus);

        /*
            Asocia los puntos
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

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_POINTS);
                int points = cursor.getInt(index);
                activity.setActivityPoints(points);

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_STATUS);
                int status = cursor.getInt(index);
                activity.setActivityStatus(status);

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_TYPE);
                int type = cursor.getInt(index);
                activity.setGetActivityType(type);

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

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_STATUS);
                String url = cursor.getString(index);
                multimedia.setMultimediaURL(url);

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

                index = cursor.getColumnIndexOrThrow(DBContract.ActivityEntry.COLUMN_NAME_STATUS);
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
            public static final String TABLE_NAME = "USERS";
            public static final String COLUMN_NAME_USERID = "userId";
            public static final String COLUMN_NAME_PASSWORD = "password";
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
