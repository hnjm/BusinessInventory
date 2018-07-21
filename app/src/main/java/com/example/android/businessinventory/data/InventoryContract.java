package com.example.android.businessinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {
    //declare authority for content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.businessinventory";

    //declare base content URI for content provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content:// " + CONTENT_AUTHORITY);

    //declare path for table name in content provider
    public static final String PATH_INVENTORY = "inventory";

    //declare content uri for the inventory table for searching the database
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

    public static abstract class InventoryEntry implements BaseColumns{

        //declare table name
        public static final String TABLE_NAME = "inventory";

        //establish column names for the table
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "itemName";
        public static final String COLUMN_QUANTITY_IN_STOCK = "quantityInStock";
        public static final String COLUMN_PRICE_PER_UNIT = "pricePerUnit";
        public static final String COLUMN_SUPPLIER = "supplier";


        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
    }
}
