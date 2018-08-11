package com.example.laurentiudragunoi.puzzlestore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.laurentiudragunoi.puzzlestore.data.PuzzleContract.PuzzleEntry;

/**
 * Created by Lavinia Dragunoi on 7/12/2017.
 */

public class PuzzleProvider extends ContentProvider {

    //Tag for the log messages
    public static final String LOG_TAG = PuzzleProvider.class.getSimpleName();
    //Uri match code for the content URI the puzzles table
    private static final int PUZZLES = 100;
    //Uri match code for the content URI for a single puzzle in the puzzles table;
    private static final int PUZZLE_ID = 101;
    //UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer .This is run the first  time anything is called from this class.
    static {
        sUriMatcher.addURI(PuzzleContract.CONTENT_AUTHORITY, PuzzleContract.PATH_PUZZLES, PUZZLES);
        sUriMatcher.addURI(PuzzleContract.CONTENT_AUTHORITY, PuzzleContract.PATH_PUZZLES + "/#", PUZZLE_ID);
    }

    private PuzzleDbHelper mDbHelper;
    //cursor will hold the result of the query
    private Cursor cursor;

    //Initialize the provider and the database helper object.
    @Override
    public boolean onCreate() {
        mDbHelper = new PuzzleDbHelper(getContext());
        return true;
    }

    //Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PUZZLES:
                // For the PUZZLES code, query the puzzles table directly with the given
                // projection, selection, selection arguments, and sort order.
                cursor = db.query(PuzzleEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PUZZLE_ID:
                // For the PUZZLE_ID code, extract out the ID from the URI.
                selection = PuzzleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the puzzles table
                cursor = db.query(PuzzleEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Set notification URI on the cursor, so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    //Insert new data into the provider with the given ContentValues.
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PUZZLES:
                return insertPuzzle(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPuzzle(Uri uri, ContentValues values) {
        //Check that the image is not null
        String image = values.getAsString(PuzzleEntry.COLUMN_PUZZLE_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Item requires an image");
        }
        //Check that the name is not null
        String name = values.getAsString(PuzzleEntry.COLUMN_PUZZLE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer quantity = values.getAsInteger(PuzzleEntry.COLUMN_PUZZLE_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity has to be a positive number");
        }

        // If the price is provided, check that it's greater than or equal to 0
        Double price = values.getAsDouble(PuzzleEntry.COLUMN_PUZZLE_QUANTITY);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price has to be a positive number");
        }

        //Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //Insert the new  with the given value
        long id = db.insert(PuzzleEntry.TABLE_NAME, null, values);
        //if the ID is -1 , then the insertion failed. Log an error and the return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
            //Notify all listeners that the data has changed for the pet content URI
            getContext().getContentResolver().notifyChange(uri, null);
            // Once we know the ID of the new row in the table,
            // return the new URI with the ID appended to the end of it
            return ContentUris.withAppendedId(uri, id);

    }

        @Override
        public int update (Uri uri, ContentValues contentValues, String selection,
                String[]selectionArgs){
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case PUZZLES:
                    return updatePuzzle(uri, contentValues, selection, selectionArgs);
                case PUZZLE_ID:
                    selection = PuzzleEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    return updatePuzzle(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }
    //Update items(puzzles) in the database with the given content values.
    private int updatePuzzle(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //If the {@link PuzzleEntry#COLUMN_PUZZLE_NAME} key is present , check that the name is not null
        if (values.containsKey(PuzzleEntry.COLUMN_PUZZLE_NAME)) {
            String name = values.getAsString(PuzzleEntry.COLUMN_PUZZLE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        //If the {@link PuzzleEntry#COLUMN_PUZZLE_QUANTITY} key is present , check that is positive
        if (values.containsKey(PuzzleEntry.COLUMN_PUZZLE_QUANTITY)) {
            Integer quantity = values.getAsInteger(PuzzleEntry.COLUMN_PUZZLE_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Quantity has to be a positive number");
            }
        }

        //If the {@link PuzzleEntry#COLUMN_PUZZLE_PRICE} key is present , check that is positive
        if (values.containsKey(PuzzleEntry.COLUMN_PUZZLE_PRICE)) {
            Double price = values.getAsDouble(PuzzleEntry.COLUMN_PUZZLE_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Price has to be a positive number");
            }
        }
        //If there are no value to update , then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        //Otherwise , get writable database to update the data
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(PuzzleEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0){
            //Notify all listeners that the data has changed for the pet content URI
            getContext().getContentResolver().notifyChange(uri, null);}

        //Return the number of database rows affected by the update statement
        return rowsUpdated;
    }

    //Delete the data at the given selection and selection arguments.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;
        // Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PUZZLES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(PuzzleEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);}
                return rowsDeleted;

            case PUZZLE_ID:
                // Delete a single row given by the ID in the URI
                selection = PuzzleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Delete a single row given by the ID in the URI
                rowsDeleted = db.delete(PuzzleEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);}
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
         * Returns the MIME type of data for the content URI.
         */
        @Override
        public String getType (Uri uri){
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case PUZZLES:
                    return PuzzleEntry.CONTENT_LIST_TYPE;
                case PUZZLE_ID:
                    return PuzzleEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
        }
    }
