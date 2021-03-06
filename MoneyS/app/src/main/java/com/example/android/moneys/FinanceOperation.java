package com.example.android.moneys;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moneys.helpers.AppBaseActivity;
import com.example.android.moneys.helpers.DBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.example.android.moneys.helpers.DBHelper.TABLE_NAME_EXPENSE;
import static com.example.android.moneys.helpers.DBHelper.TABLE_NAME_INCOME;

public class FinanceOperation extends AppBaseActivity implements View.OnClickListener {
    final String LOG_TAG = "myLogs";

    ArrayAdapter<String> adapter;
    List<String> list;
    Spinner spinner;

    ImageButton btDate;

    EditText etNote;
    Spinner spCategory;
    EditText txSum;
    DBHelper dbHelper;

    private Calendar calendar;
    private TextView txDate;
    private int year, month, day;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_activity);
        dbHelper = new DBHelper(this);
        extras = getIntent().getExtras();

        spinnerCreation();
        date();
        btDate = (ImageButton) findViewById(R.id.btDate);
        btDate.setOnClickListener(this);
        if (extras != null) {
            setUpdateData();
        }

    }

    public void date() {

        txDate = (TextView) findViewById(R.id.txDate);

        if (extras != null) {
            String dateD = (String) extras.get("date");
            String[] arr = dateD.split("\\.");
            Log.d(LOG_TAG, Arrays.toString(arr) + " | " + dateD);
            day = Integer.valueOf(arr[0]);
            month = Integer.valueOf(arr[1]);
            year = Integer.valueOf(arr[2]);
            showDate(year, month, day);
        } else {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
        }


    }

    private void showDate(int year, int month, int day) {
        txDate.setText(new StringBuilder().append(day).append(".")
                .append(month).append(".").append(year));
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }


    public void spinnerCreation() {
        String symbol;
        if (extras != null) {
            String sum = (String) extras.get("sum");
            if(Integer.valueOf(sum) < 0) {
                symbol = sum.substring(0, 1);
            } else {
                symbol = "+";
            }
        } else {
            symbol = MainActivity.getSum();
        }
        spinner = (Spinner) findViewById(R.id.spCategory);
        list = new ArrayList<String>();
        readCategories(symbol);
        Collections.sort(list);
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Title");
        if (extras != null) {
            String categoryName = (String) extras.get("category");
            int point = list.indexOf(categoryName);
            spinner.setSelection(point);
        } else {
            spinner.setSelection(0);
        }

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

    public void setUpdateData() {
        etNote = (EditText) findViewById(R.id.etNote);
        txSum = (EditText) findViewById(R.id.txSum);
        Button btAdd = (Button) findViewById(R.id.btnAdd);

        String noteUp = (String) extras.get("note");
        String sumUp = (String) extras.get("sum");
        if (Integer.valueOf(sumUp) < 0) {
            sumUp = sumUp.substring(1);
        }

        btAdd.setText(R.string.update_finance_operation);
        etNote.setText(noteUp);
        txSum.setText(sumUp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.btDate:
                setDate(v);
                break;
            default:
                break;
        }
    }


    public void readCategories(String symbol) {
        String table;
        if (symbol.equals("-")) {
            table = TABLE_NAME_EXPENSE;
        } else {
            table = TABLE_NAME_INCOME;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            list.add(i, cursor.getString(1));
            i++;
        }
    }

    public void goToMainActivity(View view) {
        Log.d(LOG_TAG, "---- inside changeActivity ---");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void write(View view) {
        etNote = (EditText) findViewById(R.id.etNote);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        txSum = (EditText) findViewById(R.id.txSum);
        txDate = (TextView) findViewById(R.id.txDate);
        String symbol;
        if (extras != null) {
            String sum = (String) extras.get("sum");
            if(Integer.valueOf(sum) < 0) {
                symbol = sum.substring(0, 1);
            } else {
                symbol = "";
            }
        } else {
            symbol = MainActivity.getSum();
        }
        symbol += txSum.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String note = etNote.getText().toString();
        String date = txDate.getText().toString();

        boolean insertData = dbHelper.addData(symbol, category, note, date);
        if (extras != null) {
            if (insertData == true) {
                Toast.makeText(FinanceOperation.this, "Data Updated", Toast.LENGTH_LONG).show();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String id = (String) extras.get("id");
                db.execSQL("delete from finances where id = '" + id + "'");
            } else {
                Toast.makeText(FinanceOperation.this, R.string.went_wrong_delete_category, Toast.LENGTH_LONG).show();
            }
        } else {
            if (insertData == true) {
                Toast.makeText(FinanceOperation.this, R.string.data_inserted_finance_operation, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(FinanceOperation.this, R.string.went_wrong_delete_category, Toast.LENGTH_LONG).show();
            }
        }

        goToMainActivity(view);
    }
}
