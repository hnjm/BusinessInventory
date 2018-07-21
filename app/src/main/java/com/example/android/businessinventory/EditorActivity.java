package com.example.android.businessinventory;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.businessinventory.data.InventoryContract;
import com.example.android.businessinventory.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity {

    /**
     * declare passed in uri that is extracted from the intent passed from the Catalog Activity
     */
    private Uri mItemUri;

    /**
     * declare the four edit text fields to add/edit for the database
     */
    private EditText mItemName;

    private EditText mQuantity;

    private EditText mPrice;

    private EditText mSupplier;

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

        mItemName = (EditText) findViewById(R.id.edit_text_item_name);

        mPrice = (EditText) findViewById(R.id.edit_text_price_per_unit);

        mQuantity = (EditText) findViewById(R.id.edit_text_quantity);

        mSupplier = (EditText) findViewById(R.id.edit_text_supplier);

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
        String name = mItemName.getText().toString().trim();
        String supplier = mSupplier.getText().toString().trim();
        Float price = Float.parseFloat(mPrice.getText().toString().trim());
        Integer quantity = Integer.parseInt(mQuantity.getText().toString().trim());

        contentValues.put(InventoryEntry.COLUMN_ITEM_NAME, name);
        contentValues.put(InventoryEntry.COLUMN_PRICE_PER_UNIT, price);
        contentValues.put(InventoryEntry.COLUMN_SUPPLIER, supplier);
        contentValues.put(InventoryEntry.COLUMN_QUANTITY_IN_STOCK, quantity);
        if (mItemUri == null){
            getContentResolver().insert(InventoryContract.CONTENT_URI, contentValues);
        }
    }
    private void deletePet(){
        if (mItemUri != null){
            getContentResolver().delete(InventoryContract.CONTENT_URI, null, null);
        }
    }
}
