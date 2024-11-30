package com.microsoft.projectoxford.emotionsample;
import java.util.ArrayList; // <-- Add this import
import android.content.ContentValues;
import android.content.Context;
import java.io.FileOutputStream;  // <-- Add this import
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "Moodify.db";
    private static final String DATABASE_PATH = "/data/data/com.microsoft.projectoxford.emotionsample/databases/";
    private static final int DATABASE_VERSION = 2; // Increment version for schema changes

    // Table name and columns
    private static final String TABLE_MOOD = "MoodSuggestions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MOOD = "mood";
    private static final String COLUMN_MOVIES = "movies";
    private static final String COLUMN_SONGS = "songs";
    private static final String COLUMN_SERIES = "series";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (!checkDatabase()) {
            copyDatabase(context);
        }
        Log.d(TAG, "DatabaseHelper initialized.");
    }

    // Check if the database exists
    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String path = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e(TAG, "Database not found: " + e.getMessage());
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    // Copy the database from the assets folder to the device's local storage
    private void copyDatabase(Context context) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = context.getAssets().open(DATABASE_NAME);
            File databaseFile = new File(DATABASE_PATH, DATABASE_NAME);
            output = new FileOutputStream(databaseFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            Log.d(TAG, "Database copied to device storage.");
        } catch (IOException e) {
            Log.e(TAG, "Error copying database: " + e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream: " + e.getMessage());
                }
            }
            if (output != null) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing output stream: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOOD_TABLE = "CREATE TABLE " + TABLE_MOOD + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOOD + " TEXT, " +
                COLUMN_MOVIES + " TEXT, " +
                COLUMN_SONGS + " TEXT, " +
                COLUMN_SERIES + " TEXT)";
        db.execSQL(CREATE_MOOD_TABLE);
        Log.d(TAG, "Database table created: " + TABLE_MOOD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD);
            onCreate(db);
        }
    }

    // Add a new mood and its suggestions
    public void addMood(String mood, String movies, String songs, String series) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOOD, mood);
        values.put(COLUMN_MOVIES, movies);
        values.put(COLUMN_SONGS, songs);
        values.put(COLUMN_SERIES, series);

        long result = db.insert(TABLE_MOOD, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert row for mood: " + mood);
        } else {
            Log.d(TAG, "Mood added to database: " + mood);
        }
        db.close();
    }

    // Get suggestions for a given mood
    public ArrayList<String> getSuggestions(String mood) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> suggestions = new ArrayList<>();
        Log.d(TAG, "Fetching suggestions for mood: " + mood);

        Cursor cursor = db.query(TABLE_MOOD, new String[]{COLUMN_MOVIES, COLUMN_SONGS, COLUMN_SERIES},
                COLUMN_MOOD + "=? COLLATE NOCASE", new String[]{mood}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                suggestions.add("Movies: " + cursor.getString(0));
                suggestions.add("Songs: " + cursor.getString(1));
                suggestions.add("Series: " + cursor.getString(2));
                Log.d(TAG, "Suggestions retrieved: " + suggestions);
            } else {
                Log.d(TAG, "No suggestions found for mood: " + mood);
            }
            cursor.close();
        } else {
            Log.e(TAG, "Cursor is null, something went wrong with the query.");
        }

        db.close();
        return suggestions;
    }

    // Update suggestions for a specific mood
    public void updateMood(String mood, String newMovies, String newSongs, String newSeries) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIES, newMovies);
        values.put(COLUMN_SONGS, newSongs);
        values.put(COLUMN_SERIES, newSeries);

        int rowsAffected = db.update(TABLE_MOOD, values, COLUMN_MOOD + "=? COLLATE NOCASE", new String[]{mood});
        if (rowsAffected > 0) {
            Log.d(TAG, "Mood suggestions updated for: " + mood);
        } else {
            Log.e(TAG, "No records found to update for mood: " + mood);
        }

        db.close();
    }

    // Delete suggestions for a specific mood
    public void deleteMood(String mood) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MOOD, COLUMN_MOOD + "=? COLLATE NOCASE", new String[]{mood});
        if (rowsDeleted > 0) {
            Log.d(TAG, "Mood suggestions deleted for: " + mood);
        } else {
            Log.e(TAG, "No records found to delete for mood: " + mood);
        }
        db.close();
    }

    // Debugging: Print all rows in the database
    public void logAllEntries() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MOOD, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG, "Row: ID=" + cursor.getInt(0)
                            + ", Mood=" + cursor.getString(1)
                            + ", Movies=" + cursor.getString(2)
                            + ", Songs=" + cursor.getString(3)
                            + ", Series=" + cursor.getString(4));
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No data found in the database.");
            }
            cursor.close();
        }

        db.close();
    }
}
