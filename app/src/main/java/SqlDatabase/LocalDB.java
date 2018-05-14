package SqlDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Pablo Madrigal on 10/05/2018.
 */

public class LocalDB extends SQLiteOpenHelper{

    private static String dbName = "RallyDB";
    public static final int DATABASE_VERSION = 1;

    public LocalDB(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USERS (" +
                "userId INTEGER PRIMARY KEY," +
                "facebookId TEXT," +
                "googleId TEXT," +
                "username TEXT UNIQUE NOT NULL," +
                "firstName TEXT," +
                "lastName TEXT," +
                "email TEXT," +
                "photoURL TEXT," +
                "isLogged BIT DEFAULT 0" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE USER_COMPETITION (" +
                "userId INTEGER NOT NULL," +
                "competitionId INTEGER NOT NULL," +
                "FOREIGN KEY (userId) REFERENCES USERS(userId)," +
                "FOREIGN KEY (competitionId) REFERENCES COMPETITION(competitionId)," +
                "PRIMARY KEY (userId, competitionId)" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE COMPETITION(" +
                "competitionId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "active BIT DEFAULT 1," +
                "startingDate DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "finishingDate DATETIME," +
                "isPublic BIT DEFAULT 1," +
                "name TEXT NOT NULL," +
                "totalpoints INTEGER," +
                "rallyId INTEGER NOT NULL," +
                "FOREIGN KEY (rallyId) REFERENCES RALLY(rallyId)" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE RALLY (" +
                "rallyId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name TEXT NOT NULL," +
                "pointsAwarded INTEGER NOT NULL," +
                "imageUrl TEXT," +
                "description TEXT," +
                "download BIT DEFAULT 0," +
                "memoryUsage INTEGER" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE SITE (" +
                "siteId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name TEXT NOT NULL," +
                "pointsAwarded INTEGER NOT NULL," +
                "description TEXT," +
                "latitude TEXT NOT NULL," +
                "longitude TEXT NOT NULL," +
                "status INTEGER NOT NULL," +
                "totalpoints INTEGER" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE RALLY_SITE(" +
                "rallyId INTEGER NOT NULL," +
                "siteId INTEGER NOT NULL," +
                "FOREIGN KEY (rallyId) REFERENCES RALLY(rallyId)," +
                "FOREIGN KEY (siteId) REFERENCES SITE(siteId)," +
                "PRIMARY KEY (rallyId, siteId)" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE TERM (" +
                "termID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "termName TEXT NOT NULL," +
                "Description TEXT" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE TERM_SITE(" +
                "termID INTEGER NOT NULL," +
                "siteId INTEGER NOT NULL," +
                "FOREIGN KEY (termID) REFERENCES TERM(termID)," +
                "FOREIGN KEY (siteId) REFERENCES SITE(siteId)," +
                "PRIMARY KEY (termId, siteId)" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE ACTIVITY_SITE(" +
                "siteId INTEGER NOT NULL," +
                "activityId INTEGER NOT NULL," +
                "FOREIGN KEY (siteId) REFERENCES SIDE(siteId)," +
                "FOREIGN KEY (activityId) REFERENCES ACTIVITY(activityId)," +
                "PRIMARY KEY (siteId, activityId)" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE ACTIVITY(" +
                "activityId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "points INTEGER NOT NULL," +
                "activityType INTEGER NOT NULL," +
                "status INTEGER NOT NULL" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE MULTIMEDIA(" +
                "multimediaId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "multimediaType INTEGER NOT NULL," +
                "multimediaURL TEXT NOT NULL" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE MULTIMEDIA_ACTIVITY(" +
                "multimediaId INTEGER NOT NULL," +
                "activityId INTEGER NOT NULL," +
                "FOREIGN KEY (multimediaId) REFERENCES MULTIMEDIA(multimediaId)," +
                "FOREIGN KEY (activityId) REFERENCES ACTIVITY(activityId)," +
                "PRIMARY KEY (multimediaId, activityId)" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE MULTIMEDIA_TERM(" +
                "multimediaId INTEGER NOT NULL," +
                "termID INTEGER NOT NULL," +
                "FOREIGN KEY (multimediaId) REFERENCES MULTIMEDIA(multimediaId)," +
                "FOREIGN KEY (termID) REFERENCES TERM(termID)," +
                "PRIMARY KEY (multimediaId, activityId)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private String getFromDB(){
        String result="";
        return result;
    }
}
