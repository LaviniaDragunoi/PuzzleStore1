package com.example.laurentiudragunoi.puzzlestore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laurentiudragunoi.puzzlestore.data.PuzzleContract.PuzzleEntry;

/**
 * Created by Lavinia Dragunoi on 7/13/2017.
 */

public class PuzzleCursorAdapter extends CursorAdapter {
    //Tag for the log messages
    public static final String LOG_TAG = PuzzleCursorAdapter.class.getSimpleName();
    private ImageView saleImage;
    private Uri puzzleImage;
    private int quantity;
    private static Context mContext;

    /**
     * Constructs a new {@link PuzzleCursorAdapter}.
     *
     * @param context The context
     * @param cursor       The cursor from which to get the data.
     */
    public PuzzleCursorAdapter(Context context, Cursor cursor) {

        super(context, cursor, 0);
        mContext = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in list_item.xml
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent ,false);
        return view;
    }

    /**
     * This method binds the puzzle data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The c1ursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        ImageView imageView = view.findViewById(R.id.puzzle_image);
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);

        //Find the columns of puzzle attributes that we're interested in
        int imageColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_IMAGE);
        int nameColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_NAME);
        int priceColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_QUANTITY);

        //Read the puzzle attributes from the Cursor for the current puzzle
        String puzzleImageString = cursor.getString(imageColumnIndex);
        if (puzzleImageString != null) {
            puzzleImage = Uri.parse(puzzleImageString);
        }
        String puzzleName = cursor.getString(nameColumnIndex);
        String puzzlePrice = cursor.getString(priceColumnIndex);
        String puzzleQuantity = cursor.getString(quantityColumnIndex);

        // If the quantity and price are empty strings or null, then use some default text
        // that says "Unknown info", so the TextView isn't blank. And if the quantity is 0 than set
        //the no sale image
        saleImage = view.findViewById(R.id.sale_image);
        final int puzzleId = cursor.getInt(cursor.getColumnIndex(PuzzleEntry._ID));
        final String finalPuzzleQuantity = puzzleQuantity;
        saleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the int value of the String puzzleQuantity
                quantity = Integer.parseInt(finalPuzzleQuantity);
                Uri puzzleUri = ContentUris.withAppendedId(PuzzleEntry.CONTENT_URI, puzzleId);
                saleButton(context, puzzleUri, quantity);
            }
        });

        if (TextUtils.isEmpty(puzzleQuantity) && TextUtils.isEmpty(puzzlePrice)) {
            puzzlePrice = context.getString(R.string.unknown_details);
            puzzleQuantity = context.getString(R.string.unknown_details);
        } else if (Integer.parseInt(puzzleQuantity) == 0) {
            saleImage.setImageResource(R.drawable.ic_remove_shopping);
        } else {
            saleImage.setImageResource(R.drawable.ic_shopping_cart);
        }

        // Update the TextViews with the attributes for the current puzzle
        imageView.setImageURI(puzzleImage);
        nameTextView.setText(puzzleName);
        priceTextView.setText(puzzlePrice);
        quantityTextView.setText(puzzleQuantity);
    }

    private void saleButton(Context context, Uri uri, int quantity){

        if (quantity == 0) {
            Log.e(LOG_TAG,context.getString(R.string.empty_stock));
        } else{
            quantity --;

            ContentValues contentValues = new ContentValues();
            contentValues.put(PuzzleEntry.COLUMN_PUZZLE_QUANTITY, quantity);
            int rowsAffected = context.getContentResolver().update(uri, contentValues, null, null);

            // Display error message in Log if sale button fails to update quantity
            if (!(rowsAffected > 0)) {
                Log.e(LOG_TAG, context.getString(R.string.error_sale_update));
            }
        }
        }
    }



