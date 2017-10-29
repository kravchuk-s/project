package com.example.android.moneys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.moneys.helpers.AppBaseActivity;
import com.example.android.moneys.helpers.DBHelper;

import java.text.NumberFormat;

import static android.content.ContentValues.TAG;
import static com.example.android.moneys.helpers.DBHelper.COL2;
import static com.example.android.moneys.helpers.DBHelper.TABLE_NAME;


public class MainActivity extends AppBaseActivity {

    public static String sum;
    final String LOG_TAG = "myLogs";

    DBHelper dbHelper;
    double totalSum;
    double incomeSum;
    double expenseSum;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        dbHelper = new DBHelper(this);

        showCurrentMoney();
        if(Integer.valueOf(Build.VERSION.SDK) >= 23)
        requestPerm();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean requestPerm(){
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)  {
            Log.v(TAG,"Permission is granted");
            return true;
        } else {
            Log.v(TAG,"Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    public void deleteCategoryActivity(){
        Intent intent = new Intent(this, DeleteCategory.class);
        startActivity(intent);
    }
    public void addCategoryActivity(){
        Intent intent = new Intent(this, AddCategory.class);
        startActivity(intent);
    }

    public void showData() {
        Intent intent = new Intent(this, ViewList.class);
        startActivity(intent);
    }

    public void writeExpense(View view) {
        sum = "-";
        Log.d(LOG_TAG, "---- inside changeActivity ---");
        Intent intent = new Intent(this, FinanceOperation.class);
        startActivity(intent);
    }

    public void writeIncome(View view) {
        sum = "";
        Log.d(LOG_TAG, "---- inside changeActivity ---");
        Intent intent = new Intent(this, FinanceOperation.class);
        startActivity(intent);
    }

    public void showCurrentMoney() {
        Log.d(LOG_TAG, "--- Inside showCurrentMoney ---");
        TextView totalSum = (TextView) findViewById(R.id.totalSum);
        TextView expenseSum = (TextView) findViewById(R.id.totalExpense);
        TextView inputSum = (TextView) findViewById(R.id.totalIncome);
        read();
        String tot = NumberFormat.getCurrencyInstance().format(this.totalSum);
        String inTot = NumberFormat.getCurrencyInstance().format(this.incomeSum);
        String exTot = NumberFormat.getCurrencyInstance().format(this.expenseSum);
        totalSum.setText(tot);
        expenseSum.setText(exTot);
        inputSum.setText(inTot);
    }

    public void read() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "--- Rows in expense: ---");
//        make a query of all the data from the table expense, get Cursor
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
//        position the cursor on the first row of the sample
//        if there are no rows in the selection, false will be returned
        if (c.moveToFirst()) {
            //getting number of row by name
            int sumColIndex = c.getColumnIndex(COL2);
            totalSum = 0;
            double tempDouble = 0.0;
            do {
// write everything to log
                tempDouble = Double.parseDouble(c.getString(sumColIndex));
                Log.d(LOG_TAG, "sum = " + c.getString(sumColIndex));
                totalSum += tempDouble;
                if (tempDouble < 0)
                    expenseSum += tempDouble;
                if (tempDouble > 0) {
                    incomeSum += tempDouble;
                }
// go to next line
// if there is no next line - return false - out of cycle
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
    }

    public static String getSum() {
        return sum;
    }
}
