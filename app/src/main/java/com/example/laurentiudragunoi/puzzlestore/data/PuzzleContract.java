package com.example.laurentiudragunoi.puzzlestore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lavinia Dragunoi on 7/12/2017.
 */
//API contract for the Puzzle Store App
public class PuzzleContract {

    //The name for entire content provider "CONTENT_AUTHORITY"
    public static final String CONTENT_AUTHORITY = "com.example.laurentiudragunoi.puzzlestore";
    //BASE_CONTENT_URI that will be used to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible path, appended to base content URI for possible URI's.
    public static final String PATH_PUZZLES = "puzzlestore";

    //This is a empty constructor that will prevent that the class will be initialized.
    private PuzzleContract() {
    }

    //Inner class that defines constants values for the puzzleStore database table.
    public static final class PuzzleEntry implements BaseColumns {

        //The content URI to access the puzzle data to provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PUZZLES);

        //The MIME type for the {@link #CONTENT_URI} for the list of puzzles of the store
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PUZZLES;

        //The MIME type of the {@link #CONTENT_URI} for a single puzzle item from the list.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PUZZLES;

        //the name of the database table for the store
        public static final String TABLE_NAME = "puzzles";

        //Image of the puzzle *tpe:TEXT
        public static final String COLUMN_PUZZLE_IMAGE = "image";

        //Name of the puzzle * type : TEXT
        public static final String COLUMN_PUZZLE_NAME = "name";

        //Price of the puzzle * type : REAL
        public static final String COLUMN_PUZZLE_PRICE = "price";

        //Quantity of the puzzle * type : INTEGER
        public static final String COLUMN_PUZZLE_QUANTITY = "quantity";

        //Supplier name for the puzzle * type : TEXT
        public static final String COLUMN_PUZZLE_SUPPLIER_NAME = "supplier_name";

        //Supplier email * type : TEXT
        public static final String COLUMN_PUZZLE_SUPPLIER_EMAIL = "supplier_email";

        //Supplier phone number * type :
        public static final String COLUMN_PUZZLE_SUPPLIER_PHONE = "supplier_phone";

    }
}
