package com.example.android.businessinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.businessinventory.data.InventoryContract;
import com.example.android.businessinventory.data.InventoryContract.InventoryEntry;
import com.example.android.businessinventory.data.InventoryDbHelper;
import com.example.android.businessinventory.data.InventoryProvider;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    InventoryDbHelper inventoryDbHelper;

    InventoryProvider inventoryProvider;

    SQLiteDatabase db;

    InventoryCursorAdapter inventoryCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewItemClicked();
            }
        });

//        insertDummyItem();
//        displayInfo();
        //get database table for the cursor adapter
//        Cursor cursor = getContentResolver().query(InventoryContract.CONTENT_URI, null, null, null, null);

        //initialize the cursor adapter
        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);


        //set the cursor adapter onto the listview in the catalog activity
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(inventoryCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editItemClicked(id);

            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_dummy_item) {
            insertDummyItem();
            return true;
        }
        else if (id == R.id.action_clear_inventory){
            clearInventory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyItem(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_ITEM_NAME, "Pulley");
        contentValues.put(InventoryEntry.COLUMN_QUANTITY_IN_STOCK, 4);
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER, "McMasterr-Carr");
        contentValues.put(InventoryEntry.COLUMN_PRICE_PER_UNIT, 7.23);
        getContentResolver().insert(InventoryContract.CONTENT_URI, contentValues);

    }

    private void clearInventory(){
        getContentResolver().delete(InventoryContract.CONTENT_URI, null, null);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String[] projection = {InventoryEntry._ID, InventoryEntry.COLUMN_ITEM_NAME, InventoryEntry.COLUMN_QUANTITY_IN_STOCK, InventoryEntry.COLUMN_PRICE_PER_UNIT, InventoryEntry.COLUMN_SUPPLIER};
        return new CursorLoader(this, InventoryContract.CONTENT_URI, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        inventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        inventoryCursorAdapter.swapCursor(null);
    }

    private void addNewItemClicked(){
        Intent intent = new Intent(this, EditorActivity.class);
        intent.setData(null);
        startActivity(intent);
    }

    private void editItemClicked(long id){
        Intent intent = new Intent(this, EditorActivity.class);
        Uri itemUri = ContentUris.withAppendedId(InventoryContract.CONTENT_URI, id);
        intent.setData(itemUri);
        startActivity(intent);
    }

//    private void displayInfo(){
//        TextView textView = findViewById(R.id.sample_query);
//        String[] projection = {InventoryEntry._ID, InventoryEntry.COLUMN_ITEM_NAME, InventoryEntry.COLUMN_QUANTITY_IN_STOCK, InventoryEntry.COLUMN_PRICE_PER_UNIT,
//                InventoryEntry.COLUMN_SUPPLIER};
//        Cursor cursor = getContentResolver().query(InventoryContract.CONTENT_URI, projection, null, null, null);
//        cursor.moveToLast();
//        textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE_PER_UNIT)));
//    }
}
