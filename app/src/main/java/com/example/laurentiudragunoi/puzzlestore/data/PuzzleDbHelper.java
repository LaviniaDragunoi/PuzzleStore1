package com.example.laurentiudragunoi.puzzlestore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.laurentiudragunoi.puzzlestore.data.PuzzleContract.PuzzleEntry;

/**
 * Created by Lavinia Dragunoi on 7/12/2017.
 */

public class PuzzleDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "store.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link PuzzleDbHelper}.
     *
     * @param context of the app
     */
    public PuzzleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create SQL table
        String SQL_CREATE_PUZZLES_TABLE =
                "CREATE TABLE " + PuzzleEntry.TABLE_NAME + " (" +
                        PuzzleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PuzzleEntry.COLUMN_PUZZLE_IMAGE + " TEXT, " +
                        PuzzleEntry.COLUMN_PUZZLE_NAME + " TEXT NOT NULL, " +
                        PuzzleEntry.COLUMN_PUZZLE_PRICE + " REAL NOT NULL, " +
                        PuzzleEntry.COLUMN_PUZZLE_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                        PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_NAME + " TEXT NOT NULL, " +
                        PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_EMAIL + " TEXT, " +
                        PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_PHONE + " TEXT);";

        db.execSQL(SQL_CREATE_PUZZLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
