package com.example.android.businessinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.businessinventory.data.InventoryContract;
import com.example.android.businessinventory.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * declare passed in uri that is extracted from the intent passed from the Catalog Activity
     */
    private Uri mItemUri;

    /**
     * declare the four edit text fields to add/edit for the database
     */
    private EditText mItemNameEditText;

    private EditText mQuantityEditText;

    private EditText mPriceEditText;

    private EditText mSupplierEditText;

    private String LOG_TAG = this.getClass().getName();

    private boolean mItemChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mPetHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemChanged = true;
            Toast.makeText(EditorActivity.this, ""+mItemChanged, Toast.LENGTH_SHORT).show();
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mItemUri = getIntent().getData();

        if(mItemUri == null){
            setTitle(R.string.activity_name_add_new_item);

        }
        else{
            setTitle("Edit item");
            getLoaderManager().initLoader(0, null, this);

        }

        mItemNameEditText = (EditText) findViewById(R.id.edit_text_item_name);

        mPriceEditText = (EditText) findViewById(R.id.edit_text_price_per_unit);

        mQuantityEditText = (EditText) findViewById(R.id.edit_text_quantity);

        mSupplierEditText = (EditText) findViewById(R.id.edit_text_supplier);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                deletePet();
                return true;
            case R.id.action_save:
                savePet();
                finish();
                return true;
        }

        return false;

    }

    private void savePet(){
        ContentValues contentValues = new ContentValues();
        String name = mItemNameEditText.getText().toString().trim();
        String supplier = mSupplierEditText.getText().toString().trim();
        Float price = Float.parseFloat(mPriceEditText.getText().toString().trim());
        Integer quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());

        contentValues.put(InventoryEntry.COLUMN_ITEM_NAME, name);
        contentValues.put(InventoryEntry.COLUMN_PRICE_PER_UNIT, price);
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER, supplier);
        contentValues.put(InventoryEntry.COLUMN_QUANTITY_IN_STOCK, quantity);
        if (mItemUri == null){
            getContentResolver().insert(InventoryContract.CONTENT_URI, contentValues);
        }
        else {
            getContentResolver().update(mItemUri, contentValues, null, null);
        }
    }
    private void deletePet(){
        if (mItemUri != null){
            getContentResolver().delete(mItemUri, null, null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "On Create Loader");
        String[] projection = {InventoryEntry._ID, InventoryEntry.COLUMN_ITEM_NAME, InventoryEntry.COLUMN_PRICE_PER_UNIT, InventoryEntry.COLUMN_SUPPLIER, InventoryEntry.COLUMN_QUANTITY_IN_STOCK};

        return new CursorLoader(this, mItemUri, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "On Load Finished");
        if(data.moveToFirst()) {
            String name = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_NAME));
            String price = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE_PER_UNIT));
            String quantity = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_QUANTITY_IN_STOCK));
            String supplier = data.getString(data.getColumnIndexOrThrow(InventoryEntry.COLUMN_SUPPLIER));
            mItemNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity);
            mSupplierEditText.setText(supplier);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "On Loader Reset");
        mSupplierEditText.getText().clear();
        mQuantityEditText.getText().clear();
        mPriceEditText.getText().clear();
        mItemNameEditText.getText().clear();
    }
}
