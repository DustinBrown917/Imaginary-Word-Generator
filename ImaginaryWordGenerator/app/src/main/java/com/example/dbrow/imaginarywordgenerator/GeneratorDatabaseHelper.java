package com.example.dbrow.imaginarywordgenerator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GeneratorDatabaseHelper extends SQLiteOpenHelper {

    private static String LOG_TAG = "DBH";

    private String dbPath = null;
    public static final String DATABASE_NAME = "generator.db";
    private static final int DATABASE_VERSION = 5;
    private SQLiteDatabase db;
    private final Context context;

    public static final String LETTER_TABLE_NAME = "letters";
    public static final String LETTER_COL_VOWELS = "vowels";
    public static final String LETTER_COL_CONSONANTS = "consonants";

    public static final String START_TABLE_NAME = "word_start_followers";
    public static final String END_TABLE_NAME = "word_end_followers";
    public static final String MID_TABLE_NAME = "word_mid_followers";

    public GeneratorDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dbPath = "/data/data/" + context.getPackageName() + "/" + "databases/";
        this.context = context;

        Log.d(LOG_TAG, getFinalPath());
    }

    private String getFinalPath(){
        return dbPath + DATABASE_NAME;
    }

    public void createDatabase() throws GeneratorDatabaseException{


        if(!databaseExists()){
            this.getReadableDatabase();
            Log.d(LOG_TAG, "Database does not exist, creating new file.");
            try{

                copyDatabase();

            } catch (IOException e){
                Log.e(LOG_TAG, e.getMessage());
                throw new GeneratorDatabaseException(
                        "The generator database could not be copied to the desired directory.",
                        e
                );
            }
        } else {
            manualUpgradeIfNeeded();
        }

    }

    private boolean databaseExists(){
        SQLiteDatabase db = null;
        try{
            db = SQLiteDatabase.openDatabase(getFinalPath(), null, SQLiteDatabase.OPEN_READONLY);
            db.close();
        }catch (SQLiteException e){
            //No need to handle this.
        }


        Log.e(LOG_TAG, "Database exists: " + (db != null));
        return (db != null);
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = context.getAssets().open(DATABASE_NAME);

        FileOutputStream outputStream = new FileOutputStream(getFinalPath());

        byte[] buffer = new byte[10];
        int length;

        while((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
        Log.d(LOG_TAG, "New file created successfully.");
    }

    public void openDataBase(int permissions) throws SQLiteException{
        db = SQLiteDatabase.openDatabase(getFinalPath(),null, permissions);

    }

    @Override
    public synchronized void close(){
        if(db != null){
            db.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void manualUpgradeIfNeeded(){
        boolean upgraded = false;
        openDataBase(SQLiteDatabase.OPEN_READONLY);
        if(db.getVersion() < DATABASE_VERSION){
            close();
            try{
                copyDatabase();
                upgraded = true;
                Log.d(LOG_TAG, "Database updated.");
            } catch (IOException e){
                e.printStackTrace();
            }
            openDataBase(SQLiteDatabase.OPEN_READWRITE);
            db.setVersion(DATABASE_VERSION);
        }
        db.close();
        //If data base was upgraded, ensure it is readable.
        if(upgraded){
            try{
                createDatabase();
            } catch(GeneratorDatabaseException e){
                e.printStackTrace();
            }
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs){
        return db.rawQuery(sql, selectionArgs);
    }
}

