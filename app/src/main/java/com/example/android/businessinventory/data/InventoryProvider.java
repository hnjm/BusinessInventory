package com.example.android.businessinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class InventoryProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the inventory table
     */
    private static final int INVENTORY = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int INVENTORY_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        //this uri maps to selecting the entire table of pets from the pets SQL database
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);


        //this uri maps to selecting a single pet from the pets SQL database
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */

    public InventoryDbHelper inventoryDbHelper;

    @Override
    public boolean onCreate() {
        inventoryDbHelper = new InventoryDbHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = inventoryDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // For the INVENTORY code, query the inventory table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table

                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                // For the INVENTORY_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertInventory(Uri uri, ContentValues values) {

        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);

        String supplier = values.getAsString(InventoryContract.InventoryEntry.COLUMN_SUPPLIER);

        Float pricePerUnit = values.getAsFloat(InventoryContract.InventoryEntry.COLUMN_PRICE_PER_UNIT);

        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_QUANTITY_IN_STOCK);

        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        if (pricePerUnit != null && pricePerUnit < 0) {
            Log.v(this.getClass().getName(), "PRICE PER UNIT: " + pricePerUnit);
            throw new IllegalArgumentException("pricePerUnit Problem");
        }
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity problem");

        }


        SQLiteDatabase petsDatabase = inventoryDbHelper.getWritableDatabase();
        long id = petsDatabase.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Toast.makeText(this.getContext(), "Failed to insert row for " + uri.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Sanity checks for proper updating values
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

//        String breed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_PRICE_PER_UNIT)) {
            Integer pricePerUnit = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRICE_PER_UNIT);
            if (pricePerUnit != null && pricePerUnit < 0) {
                throw new IllegalArgumentException("Problem Price Per Unit");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_QUANTITY_IN_STOCK)) {
            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_QUANTITY_IN_STOCK);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Problem Quantity");

            }
        }


        if (values.size() == 0) {
            return 0;
        }


        // TODO: Update the selected pets in the pets database table with the given ContentValues
        SQLiteDatabase sqLiteDatabase = inventoryDbHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri, null);
        // TODO: Return the number of rows that were affected
        return sqLiteDatabase.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = inventoryDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                getContext().getContentResolver().notifyChange(uri, null);
                // Delete all rows that match the selection and selection args
                return database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
            case INVENTORY_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
