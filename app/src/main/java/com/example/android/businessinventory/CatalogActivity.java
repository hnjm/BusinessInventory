package com.example.android.businessinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.businessinventory.data.InventoryContract;
import com.example.android.businessinventory.data.InventoryContract.InventoryEntry;
import com.example.android.businessinventory.data.InventoryDbHelper;
import com.example.android.businessinventory.data.InventoryProvider;

public class CatalogActivity extends AppCompatActivity {
    InventoryDbHelper inventoryDbHelper;

    InventoryProvider inventoryProvider;

    SQLiteDatabase db;
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        insertDummyItem();
        displayInfo();

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyItem(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_ITEM_NAME, "Pulley");
        contentValues.put(InventoryEntry.COLUMN_QUANTITY_IN_STOCK, 4);
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER, "McMasterr-Carr");
        contentValues.put(InventoryEntry.COLUMN_PRICE_PER_UNIT, 7);
        getContentResolver().insert(InventoryContract.CONTENT_URI, contentValues);



    }

    private void displayInfo(){
        TextView textView = findViewById(R.id.sample_query);
        String[] projection = {InventoryEntry._ID, InventoryEntry.COLUMN_ITEM_NAME, InventoryEntry.COLUMN_QUANTITY_IN_STOCK, InventoryEntry.COLUMN_PRICE_PER_UNIT,
                InventoryEntry.COLUMN_SUPPLIER};
        Cursor cursor = getContentResolver().query(InventoryContract.CONTENT_URI, projection, null, null, null);
        cursor.moveToFirst();
        textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_NAME)));
    }
}
