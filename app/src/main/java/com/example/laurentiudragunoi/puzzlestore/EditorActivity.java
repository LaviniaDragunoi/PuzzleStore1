package com.example.laurentiudragunoi.puzzlestore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.laurentiudragunoi.puzzlestore.data.PuzzleContract.PuzzleEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.valueOf;

//Create new entry of puzzle items or updates existing ones.
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // The request code to store image from the Galary
    static final int RESULT_LOAD_IMAGE = 1;
    private static final int EXISTING_PET_LOADER = 0;
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    //EditText field to enter the puzzle's quatity.
    public EditText mQuantityEditText;
    private boolean mItemHasChanged = false;
    //Content URI for the existing puzzle (null if it's a new puzzle)
    private Uri mCurrentPuzzleUri;
    //Current image constant no image

    //Image Uri variable
    private Uri imageUri;
    //ImageView field to enter the puzzle's image
    private ImageView nImageView;
    //EditText field to enter the puzzle's name
    private EditText mNameEditText;
    //EditText field to enter the puzzle's price
    private EditText mPriceEditText;
    // Integer variable for quantity changer that will be initiated with mQuantityEditText value.
    private int quantity;
    //EditText field to enter the supplier name.
    private EditText mSupplierNameEditText;
    //EditText field to enter the supplier phone number.
    private EditText mSupplierPhoneEditText;
    //EditText field to enter the supplier email.
    private EditText mSupplierEmailEditText;
    //TextView for image Uri to save in sql
    private String mSaveImageText;
    //MinusButton  for quantity;
    private Button minusButton;
    //PlusButton for quantity;
    private Button plusButton;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch tis activity: to create a new entry item or
        // to update an existing one.
        Intent intent = getIntent();
        mCurrentPuzzleUri = intent.getData();
        //If the intent DOES NOT contain a pet content URI, then we know that we are creating a new entry
        if (mCurrentPuzzleUri == null) {
            //This is a new entry, so change the app bar to say "Add new item"
            setTitle(getString(R.string.editor_activity_title_add_item));
        } else {
            //Otherwise this is an existing pet so change the app bar to say "Edit Item"
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        nImageView = (ImageView) findViewById(R.id.new_image);
        nImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_OPEN_DOCUMENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });

        mNameEditText = (EditText) findViewById(R.id.puzzle_name);
        mPriceEditText = (EditText) findViewById(R.id.puzzle_price);
        mQuantityEditText = (EditText) findViewById(R.id.puzzle_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone);
        mSupplierEmailEditText = (EditText) findViewById(R.id.supplier_email);

        //Make the - button to modify quantity variable by - 1 by each click
        minusButton = (Button) findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Integer variable for quantity changer that will be initiated with
                // mQuantityEditText value.
                if (TextUtils.isEmpty(mQuantityEditText.getText().toString())){
                    Toast.makeText(EditorActivity.this, R.string.positive_quantity,
                            Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    quantity = Integer.parseInt(valueOf(mQuantityEditText.getText().toString()));
                if (quantity == 0) {
                    Toast.makeText(EditorActivity.this, R.string.positive_quantity,
                            Toast.LENGTH_SHORT).show();
                } else {
                    quantity = quantity - 1;
                    mQuantityEditText.setText(valueOf(quantity));
                }
                }
            }
        });

        //Make the + button to modify quantity variable by + 1 by each click
        plusButton = (Button) findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Integer variable for quantity changer that will be initiated with
                // mQuantityEditText value.
                if (TextUtils.isEmpty(mQuantityEditText.getText().toString())){
                    Log.e(LOG_TAG,getString(R.string.empty_stock));
                    return;
                }else{
                    quantity = Integer.parseInt(mQuantityEditText.getText().toString());
                    quantity = quantity + 1;
                    mQuantityEditText.setText(valueOf(quantity));
                }
            }
        });

        //Order buttons, that will help the user to make an order by email or phone
        FloatingActionButton phoneFab = (FloatingActionButton) findViewById(R.id.phone_order);
        phoneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText().toString()));
                startActivity(intent);
            }
        });

        FloatingActionButton emailFab = (FloatingActionButton) findViewById(R.id.email_order);
        emailFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + mSupplierEmailEditText.getText().toString()));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_email_subject) +
                        mNameEditText.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.order_email));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        nImageView.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);
        minusButton.setOnTouchListener(mTouchListener);
        plusButton.setOnTouchListener(mTouchListener);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
           try{
            imageUri = data.getData();
            Log.i(LOG_TAG, "Uri: " + imageUri.toString());

            mSaveImageText = imageUri.toString();
            int takeFlags = data.getFlags();
            takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {
                if (Build.VERSION.SDK_INT > 19) {
                    getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                }

            }
            catch (SecurityException e){
                e.printStackTrace();
            }
            nImageView.setImageBitmap(getBitmapFromUri(imageUri));
        }catch (Exception e) {
            e.printStackTrace();
        }
        }
    }


    public Bitmap getBitmapFromUri(Uri uri) {
        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = nImageView.getWidth();
        int targetH = nImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;


            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item(puzzle).
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void savePuzzle() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();

        //checking if all inputs are valid
        if (mCurrentPuzzleUri == null && TextUtils.isEmpty(mSaveImageText) || TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(priceString)  || TextUtils.isEmpty(quantityString)) {
            return;
        }

        if(imageUri == null){
            //empty image
            return;
        }
        // Create a ContentValues object
        ContentValues values = new ContentValues();
        values.put(PuzzleEntry.COLUMN_PUZZLE_IMAGE, imageUri.toString());
        values.put(PuzzleEntry.COLUMN_PUZZLE_NAME, nameString);

        // If the price and quantity are not provided by the user, don't try to parse the string
        // into an integer value. Use 0 by default for both of them.
        Double price = 0.0;
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }else {
            Toast.makeText(this,R.string.no_null_quantity, Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        else {
            Toast.makeText(this,R.string.no_null_quantity, Toast.LENGTH_SHORT).show();
        }
        values.put(PuzzleEntry.COLUMN_PUZZLE_PRICE, price);
        values.put(PuzzleEntry.COLUMN_PUZZLE_QUANTITY, quantity);
        values.put(PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_NAME, supplierNameString);
        values.put(PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_PHONE, supplierPhoneString);
        values.put(PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_EMAIL, supplierEmailString);
        // Determine if this is a new or existing puzzle by checking if mCurrentPuzzleUri is null or not
        if (mCurrentPuzzleUri == null) {
            // This is a NEW item, so insert a new puzzle into the provider,
            Uri newUri = getContentResolver().insert(PuzzleEntry.CONTENT_URI, values);

            //show a toast message depending on whether or not the insertion  was successful
            if (newUri == null) {
                //If the new content URI is null , then there was an error with insertion
                Toast.makeText(this, getString(R.string.editor_insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                //otherwise, the insertion was successful and we can display a toast
                Toast.makeText(this, getString(R.string.editor_insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING puzzle, so update the puzzle with content URI: mCurrentPuzzleUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPuzzleUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentPuzzleUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/edit_menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    //This method is called after invalidateOptionsMenu(), so that the menu can be updated
    // (some menu items can be hidden or made visible)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new entry, hide the "Delete" menu item.
        if (mCurrentPuzzleUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savePuzzle();
                //exist activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies the columns from the table that we are interested
        String[] projection = {
                PuzzleEntry._ID,
                PuzzleEntry.COLUMN_PUZZLE_IMAGE,
                PuzzleEntry.COLUMN_PUZZLE_NAME,
                PuzzleEntry.COLUMN_PUZZLE_PRICE,
                PuzzleEntry.COLUMN_PUZZLE_QUANTITY,
                PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_NAME,
                PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_EMAIL,
                PuzzleEntry.COLUMN_PUZZLE_SUPPLIER_PHONE};
        //This loader will execute the ContentProvider 's query method on a background thread
        return new CursorLoader(this,
                mCurrentPuzzleUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            //Find the columns of puzzle attributes that we're interested in
            int imageColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_IMAGE);
            int nameColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_NAME);
            int priceColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(PuzzleEntry.COLUMN_PUZZLE_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(PuzzleEntry.
                    COLUMN_PUZZLE_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(PuzzleEntry.
                    COLUMN_PUZZLE_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(PuzzleEntry.
                    COLUMN_PUZZLE_SUPPLIER_EMAIL);

            // Extract out the value from the Cursor for the given column index
            String image = cursor.getString(imageColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);

            // Update the views on the screen with the values from the database
            if (image != null) {
                imageUri = Uri.parse(image);
                nImageView.setImageURI(imageUri);
            }
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
            mSupplierEmailEditText.setText(supplierEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //if the loader is invalidated , clear out all the data from the input fields.

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mSupplierEmailEditText.setText("");

    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deletePuzzle();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePuzzle() {
        // Only perform the delete if this is an existing puzzle.
        if (mCurrentPuzzleUri != null) {
            // Call the ContentResolver to delete the puzzle at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPuzzleUri
            // content URI already identifies the puzzle that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPuzzleUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
        // Close the activity
        finish();
    }
}