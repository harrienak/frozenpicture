package com.geckoapps.raaddeplaat.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ImageView;

import com.geckoapps.raaddeplaat.model.Level;
import com.geckoapps.raaddeplaat.model.Toolbar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    // The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.geckoapps.raaddeplaat/databases/";

    private static String DB_NAME = "levels";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    // TABLE CATEGORIEEN
    private final String TABLE_LEVELS = "levels";

    private final String LEVELS_NR = "nr";
    private final String LEVELS_ANSWER = "answer";
    private final String LEVELS_ANSWER_NL = "answer_nl";
    private final String LEVELS_IMAGE = "image";
    private final String LEVELS_TILES = "tiles";
    private final String LEVELS_TILESPERTURN = "tilesPerTurn";
    private final String LEVELS_COINS = "coins";

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException e) {

            // database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //get LEVEL
    public Level getLevel(int getCurrentLevel, Context context, ImageView image, Toolbar toolbar) {

        openDataBase();
        Cursor cursor;
        try {
            // ask the database object to create the cursor.
            cursor = myDataBase.query(TABLE_LEVELS, new String[]{LEVELS_NR,
                            LEVELS_ANSWER, LEVELS_IMAGE, LEVELS_TILES, LEVELS_TILESPERTURN, LEVELS_COINS, LEVELS_ANSWER_NL},
                    LEVELS_NR + " = " + getCurrentLevel, null, null, null, null);

            // move the cursor's pointer to position zero.
            cursor.moveToFirst();

            // if there is data after the current cursor position, add it
            // to the ArrayList.
            if (!cursor.isAfterLast()) {
                do {
                    return new Level(context, cursor.getInt(0), cursor.getString(2), cursor.getString(1), cursor.getString(6), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), image, toolbar);
                }
                // move the cursor's pointer up one position.
                while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        // return the ArrayList that holds the data collected from
        // the database.
        return null;
    }
/*
    public void addLevelToDB(int i, HulpLevel l, String lang) {

        ContentValues localContentValues = new ContentValues();
        localContentValues.put(LEVELS_NR, i);
        localContentValues.put(LEVELS_ANSWER, l.getAnswer());
        localContentValues.put(LEVELS_SlOGAN, l.getSlogan());
        localContentValues.put(LEVELS_HINT, l.getHint());
        localContentValues.put(LEVELS_IMAGE, l.getImage());

        try {
            this.myDataBase.insert(TABLE_LEVELS, null, localContentValues);
            return;
        } catch (Exception localException) {
            Log.e("DB ERROR addlevelpacks:", localException.toString());
            localException.printStackTrace();
        }
    }*/
}




