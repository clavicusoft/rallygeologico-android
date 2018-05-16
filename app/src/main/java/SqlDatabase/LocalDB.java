package SqlDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.nio.channels.ScatteringByteChannel;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class LocalDB{
    public final class DBContract {
        // To prevent someone from accidentally instantiating the contract class, make the constructor private.
        private DBContract() {}

        /* Inner class that defines the USER table contents */
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

        /* Inner class that defines the COMPETITION table contents */
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

        /* Inner class that defines the RALLY contents */
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

        /* Inner class that defines the MULTIMEDIA contents */
        public class MultimediaEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA";
            public static final String COLUMN_NAME_ID = "multimediaId";
            public static final String COLUMN_NAME_TYPE = "multimediaType";
            public static final String COLUMN_NAME_URL = "multimediaURL";
        }

        /* Inner class that defines the ACTIVITY contents */
        public class ActivityEntry implements BaseColumns {
            public static final String TABLE_NAME = "ACTIVITY";
            public static final String COLUMN_NAME_ID = "activityId";
            public static final String COLUMN_NAME_TYPE = "activityType";
            public static final String COLUMN_NAME_POINTS = "activityPoints";
            public static final String COLUMN_NAME_STATUS = "activityStatus";
        }

        /* Inner class that defines the SITE contents */
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

        /* Inner class that defines the TERM contents */
        public class TermEntry implements BaseColumns {
            public static final String TABLE_NAME = "TERM";
            public static final String COLUMN_NAME_ID = "termID";
            public static final String COLUMN_NAME_NAME = "termName";
            public static final String COLUMN_NAME_DESCRIPTION = "termDescription";
        }

        /* Inner class that defines the User_Competition table contents */
        public class User_CompetitionEntry implements BaseColumns {
            public static final String TABLE_NAME = "USER_COMPETITION";
            public static final String COLUMN_NAME_USERID = "userId";
            public static final String COLUMN_NAME_ID = "competitionId";
        }

        /* Inner class that defines the Rally_site table contents */
        public class Rally_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "RALLY_SITE";
            public static final String COLUMN_NAME_RallyID = "rallyId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /* Inner class that defines the Rally_site table contents */
        public class Term_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "TERM_SITE";
            public static final String COLUMN_NAME_TermID = "TermId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /* Inner class that defines the Rally_site table contents */
        public class Activity_SiteEntry implements BaseColumns {
            public static final String TABLE_NAME = "ACTIVITY_SITE";
            public static final String COLUMN_NAME_ACTIVITYID = "activityId";
            public static final String COLUMN_NAME_SITEID = "siteId";
        }

        /* Inner class that defines the Rally_site table contents */
        public class Multimedia_ActivityEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA_ACTIVITY";
            public static final String COLUMN_NAME_MULTIMEDIAID = "multimediaId";
            public static final String COLUMN_NAME_ACTIVITYID = "activityId";
        }

        /* Inner class that defines the Rally_site table contents */
        public class Multimedia_TermEntry implements BaseColumns {
            public static final String TABLE_NAME = "MULTIMEDIA_ACTIVITY";
            public static final String COLUMN_NAME_TermID = "TermId";
            public static final String COLUMN_NAME_MULTIMEDIAID = "multimediaId";
        }
    }

    public class LocalDBHelper extends SQLiteOpenHelper {
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

        /*
         SELECT `rally`.*, `users`.`id`
         FROM `users`
            LEFT JOIN `invitation` ON `invitation`.`user_id_send` = `users`.`id` or `invitation`.`user_id_receive` = `users`.`id`
            LEFT JOIN `competition` ON `invitation`.`competition_id` = `competition`.`id`
            LEFT JOIN `rally` ON `competition`.`rally_id` = `rally`.`id`
        WHERE (`users`.`api_id`=16)
         */
    }

}
