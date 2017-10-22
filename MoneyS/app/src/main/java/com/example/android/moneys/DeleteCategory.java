package com.example.android.moneys;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.moneys.DBHelper.TABLE_NAME_EXPENSE;
import static com.example.android.moneys.DBHelper.TABLE_NAME_INCOME;

public class DeleteCategory extends AppBaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    final String LOG_TAG = "myLogs";
    DBHelper dbHelper;
    Spinner spFinanceChoose;
    Spinner spCategoryChooseToDelete;
    ArrayList<String> listOne;
    ArrayList<String> listTwo;
    ArrayAdapter<String> adapterOne;
    ArrayAdapter<String> adapterTwo;
    Button delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_category);

        dbHelper = new DBHelper(this);

        spinnerCreation();

        delete = (Button) findViewById(R.id.btDeleteCategory);
        delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.btDeleteCategory:
                deleteCategory(v);
                break;
            default:
                break;
        }
    }

    public void spinnerCreation() {
        listTwo = new ArrayList<>();
        spFinanceChoose = (Spinner) findViewById(R.id.spFinanceChoose);
        listOne = new ArrayList<String>();
        listOne.add(0, "Expense");
        listOne.add(1, "Income");
        adapterOne = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, listOne);
        adapterOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFinanceChoose.setAdapter(adapterOne);
        spFinanceChoose.setPrompt("Title");
        spFinanceChoose.setSelection(0);
        spFinanceChoose.setOnItemSelectedListener(this);
    }

    public void readCategories(String selectedItem) {
        String table;
        listTwo.clear();
        Log.d(LOG_TAG, selectedItem);
        if (selectedItem.equals("Expense")) {
            table = TABLE_NAME_EXPENSE;
        } else {
            table = TABLE_NAME_INCOME;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, "In cycle");
            listTwo.add(i, cursor.getString(1));
            i++;
        }
        Log.d(LOG_TAG, listTwo.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.spFinanceChoose) {
            readCategories(spFinanceChoose.getSelectedItem().toString());
            spinnerTwoCreation();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void spinnerTwoCreation() {
        spCategoryChooseToDelete = (Spinner) findViewById(R.id.spCategoryChooseToDelete);
        adapterTwo = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, listTwo);
        adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoryChooseToDelete.setAdapter(adapterTwo);
        spCategoryChooseToDelete.setPrompt("Title");
        spCategoryChooseToDelete.setSelection(0);
        spCategoryChooseToDelete.setOnItemSelectedListener(this);
    }

    public void deleteCategory(View view) {
        String tableName = spFinanceChoose.getSelectedItem().toString();
        String catToDel = spCategoryChooseToDelete.getSelectedItem().toString();
        tableName = tableName.toLowerCase();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean isDeleted = db.delete(tableName, "category=" + "'" + catToDel + "'", null) > 0;
        if (isDeleted == true){
            Toast.makeText(DeleteCategory.this, "Category deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(DeleteCategory.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
