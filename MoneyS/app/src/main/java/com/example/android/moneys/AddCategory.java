package com.example.android.moneys;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.moneys.helpers.AppBaseActivity;
import com.example.android.moneys.helpers.DBHelper;

import java.util.ArrayList;

import static com.example.android.moneys.helpers.DBHelper.COL_EX;
import static com.example.android.moneys.helpers.DBHelper.COL_IN;
import static com.example.android.moneys.helpers.DBHelper.TABLE_NAME_EXPENSE;
import static com.example.android.moneys.helpers.DBHelper.TABLE_NAME_INCOME;

public class AddCategory extends AppBaseActivity implements View.OnClickListener {
    final String LOG_TAG = "myLogs";
    DBHelper dbHelper;
    EditText etCategoryName;
    Spinner spinner;
    Spinner spCategoryChoose;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Button addCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        dbHelper = new DBHelper(this);

        spinnerOneCreation();

        addCategory = (Button) findViewById(R.id.btnAddCategory);
        addCategory.setOnClickListener(this);
    }

    public void spinnerOneCreation(){
        spinner = (Spinner) findViewById(R.id.spCategoryChoose);
        list = new ArrayList<String>();
        list.add(0,"Expense");
        list.add(1,"Income");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title");
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.btnAddCategory:
                writeCategory(v);
                break;
            default:
                break;
        }
    }

    public void writeCategory(View view){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        etCategoryName = (EditText) findViewById(R.id.etCategoryName);
        spCategoryChoose = (Spinner) findViewById(R.id.spCategoryChoose);

        String catName = etCategoryName.getText().toString();
        String catChoose = spCategoryChoose.getSelectedItem().toString();

        if (catChoose.equals("Expense")){
            db.execSQL("insert into " + TABLE_NAME_EXPENSE + "(" + COL_EX + " ) values ('"+ catName + "')");
        } else {
            db.execSQL("insert into " + TABLE_NAME_INCOME + "(" + COL_IN + " ) values ('"+ catName + "')");
        }

        Toast.makeText(AddCategory.this, "Category added", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
