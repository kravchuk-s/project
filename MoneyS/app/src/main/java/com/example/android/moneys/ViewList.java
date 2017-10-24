package com.example.android.moneys;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moneys.helpers.AppBaseActivity;
import com.example.android.moneys.helpers.DBHelper;
import com.example.android.moneys.helpers.FourColumnListAdapter;
import com.example.android.moneys.helpers.MoneyData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.example.android.moneys.helpers.DBHelper.TABLE_NAME_EXPENSE;
import static com.example.android.moneys.helpers.DBHelper.TABLE_NAME_INCOME;

public class ViewList extends AppBaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    final Context context = this;
    DBHelper dbHelper;
    ListView listView;
    ArrayList<MoneyData> moneyDataList;
    MoneyData moneyData;
    final String LOG_TAG = "myLogs";
    FourColumnListAdapter adapter;
    int positionL;

    Spinner spChooseFrom;
    Spinner spChooseCategory;
    ArrayList<String> listOne;
    ArrayList<String> listTwo;
    ArrayAdapter<String> adapterOne;
    ArrayAdapter<String> adapterTwo;
    Cursor mData;

    TextView date_1;
    TextView date_2;
    private Calendar calendar;
    private int year_1, month_1, day_1;
    private int year_2, month_2, day_2;

    Button btShowByFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcontents_layout);

        dbHelper = new DBHelper(this);

        moneyDataList = new ArrayList<>();

        spinnerCreation();

        date_1 = (TextView) findViewById(R.id.txDate_1);
        date_2 = (TextView) findViewById(R.id.txDate_2);
        date_1.setOnClickListener(ViewList.this);
        date_2.setOnClickListener(ViewList.this);

        date();

        btShowByFilters = (Button) findViewById(R.id.btShowByFilters);
        btShowByFilters.setOnClickListener(this);

        setMoneyDataList();
        listViewCreation();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.txDate_1:
//                Toast.makeText(ViewList.this, "Date 1", Toast.LENGTH_LONG).show();
                setDate(v, 1);
                break;
            case R.id.txDate_2:
//                Toast.makeText(ViewList.this, "Date 2", Toast.LENGTH_LONG).show();
                setDate(v, 2);
                break;
            case R.id.btShowByFilters:
//                Toast.makeText(ViewList.this, "Show by filters", Toast.LENGTH_LONG).show();
                showByFilters();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case R.id.update:
                Log.d(LOG_TAG, "UPDATE");
                updateRow(position);
                return true;
            case R.id.delete:
                Log.d(LOG_TAG, "DELETE");
                deleteRow(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void listViewCreation() {
        Log.d(LOG_TAG, "inside list view creation");
        if (moneyDataList.size() > 0) {
            Collections.sort(moneyDataList);
            Collections.reverse(moneyDataList);
            adapter = new FourColumnListAdapter(this, R.layout.view_list, moneyDataList);
            listView = (ListView) findViewById(R.id.listView);
            registerForContextMenu(listView);
            listView.setAdapter(adapter);
        }
    }

    public void deleteRow(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Delete Row");
        alertDialog.setMessage("Are you sure you want to delete this row?");
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Log.d(LOG_TAG, moneyDataList.toString());
                String id = moneyDataList.get(position).getId();
                db.execSQL("delete from finances where id = '" + id + "'");
                moneyDataList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(ViewList.this, "Row deleted", Toast.LENGTH_LONG).show();

            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }


    //for spinners
    public void spinnerCreation() {
        listTwo = new ArrayList<>();
        spChooseFrom = (Spinner) findViewById(R.id.spChooseFrom);
        listOne = new ArrayList<String>();
        listOne.add(0, "All");
        listOne.add(1, "Expense");
        listOne.add(2, "Income");
        adapterOne = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, listOne);
        adapterOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChooseFrom.setAdapter(adapterOne);
        spChooseFrom.setPrompt("Title");
        spChooseFrom.setSelection(0);
        spChooseFrom.setOnItemSelectedListener(this);
    }

    public void readCategories(String selectedItem) {
        String table;
        listTwo.clear();
        Log.d(LOG_TAG, selectedItem);
        int j = 1;
        if (selectedItem.equals("Expense")) {
            table = TABLE_NAME_EXPENSE;
        } else if (selectedItem.equals("Income")) {
            table = TABLE_NAME_INCOME;
        } else {
            //if All choosed
            table = TABLE_NAME_EXPENSE;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(table, null, null, null, null, null, null);
            j = 0;
            while (cursor.moveToNext()) {
                Log.d(LOG_TAG, "In cycle");
                listTwo.add(j, cursor.getString(1));
                j++;
            }
            Log.d(LOG_TAG, listTwo.toString());
            table = TABLE_NAME_INCOME;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        int i = 0;
        if (j > 1)
            i = j;
        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, "In cycle");
            listTwo.add(i, cursor.getString(1));
            i++;
        }
        listTwo.add(i, "All");
        Collections.sort(listTwo);
        Log.d(LOG_TAG, listTwo.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.spChooseFrom) {
            readCategories(spChooseFrom.getSelectedItem().toString());
            spinnerTwoCreation();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void spinnerTwoCreation() {
        spChooseCategory = (Spinner) findViewById(R.id.spChooseCategory);
        adapterTwo = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, listTwo);
        adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChooseCategory.setAdapter(adapterTwo);
        spChooseCategory.setPrompt("Title");
        spChooseCategory.setSelection(0);
        spChooseCategory.setOnItemSelectedListener(this);
    }

    //for date
    public void date() {
        calendar = Calendar.getInstance();

        year_1 = calendar.get(Calendar.YEAR);
        month_1 = calendar.get(Calendar.MONTH);
        day_1 = calendar.get(Calendar.DAY_OF_MONTH);

        year_2 = calendar.get(Calendar.YEAR);
        month_2 = calendar.get(Calendar.MONTH);
        day_2 = calendar.get(Calendar.DAY_OF_MONTH);

        showDate_1(year_1 - 1, month_1 + 1, day_1);
        showDate_2(year_2, month_2 + 1, day_2);
    }

    private void showDate_1(int year, int month, int day) {
        date_1.setText(new StringBuilder().append(day).append(".")
                .append(month).append(".").append(year));
    }

    private void showDate_2(int year, int month, int day) {
        date_2.setText(new StringBuilder().append(day).append(".")
                .append(month).append(".").append(year));
    }

    private DatePickerDialog.OnDateSetListener myDateListener_1 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate_1(arg1, arg2 + 1, arg3);
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener_2 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate_2(arg1, arg2 + 1, arg3);
                }
            };

    @SuppressWarnings("deprecation")
    public void setDate(View view, int i) {
        showDialog(i);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 1) {
            return new DatePickerDialog(this,
                    myDateListener_1, year_1, month_1, day_1);
        }
        if (id == 2) {
            return new DatePickerDialog(this,
                    myDateListener_2, year_2, month_2, day_2);
        }
        return null;
    }

    public void showByFilters() {
        spChooseFrom = (Spinner) findViewById(R.id.spChooseFrom);
        spChooseCategory = (Spinner) findViewById(R.id.spChooseCategory);

        String spChooseFrom_string = spChooseFrom.getSelectedItem().toString();
        String spChooseCategory_string = spChooseCategory.getSelectedItem().toString();

        String date_1_string = date_1.getText().toString();
        String date_2_string = date_2.getText().toString();

        Log.d(LOG_TAG, spChooseFrom_string + " " + spChooseCategory_string + " " + date_1_string + " " + date_2_string);

        setMoneyDataListWithDate(spChooseCategory_string, date_1_string, date_2_string);
        listViewCreation();
    }

    public void setMoneyDataList() {
        moneyDataList.clear();
        mData = dbHelper.getListContents();
        int numOfRows = mData.getCount();
        Log.d(LOG_TAG, "list view + num of rows" + numOfRows);
        if (numOfRows == 0) {
            Toast.makeText(ViewList.this, "There is no Data", Toast.LENGTH_LONG).show();
        } else {
            int i = 0;
            while (mData.moveToNext()) {
                moneyData = new MoneyData(mData.getString(1), mData.getString(2),
                        mData.getString(3), mData.getString(4), mData.getString(0));
                moneyDataList.add(i, moneyData);
                i++;
            }
        }
    }

    public void setMoneyDataListWithDate(String cat, String date1, String date2) {
        boolean isContains = false;
        moneyDataList.clear();
        Date dateD1;
        Date dateD2;
        Date dataDB;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

        mData = dbHelper.getListContents();
        int numOfRows = mData.getCount();
        Log.d(LOG_TAG, "list view + num of rows" + numOfRows);
        if (numOfRows == 0) {
            Toast.makeText(ViewList.this, "There is no Data", Toast.LENGTH_LONG).show();
        } else {
            Log.d(LOG_TAG, "iside else-if");
            int i = 0;
            while (mData.moveToNext()) {
                try {
                    dateD1 = formatter.parse(date1);
                    dateD2 = formatter.parse(date2);
                    dataDB = formatter.parse(mData.getString(4));
                    if (cat.equals("All")) {
                        for (int j = 0; j < listTwo.size(); j++) {
                            if (mData.getString(2).contains(listTwo.get(j))) {
                                Log.d(LOG_TAG, mData.getString(2) + " | " + listTwo.get(j));
                                isContains = true;
                            }
                        }
                    } else {
                        if (mData.getString(2).contains(cat)) {
                            isContains = true;
                        }
                    }
                    Log.d(LOG_TAG, Boolean.toString(isContains));
                    if (dataDB.after(dateD1) && dataDB.before(dateD2) && isContains) {
                        Log.d(LOG_TAG, "Inside if statement");
                        moneyData = new MoneyData(mData.getString(1), mData.getString(2),
                                mData.getString(3), mData.getString(4), mData.getString(0));
                        moneyDataList.add(i, moneyData);
                        i++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                isContains = false;
            }
        }
    }

    public void updateRow(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Update Row");
        alertDialog.setMessage("Are you sure you want to update this row?");
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Log.d(LOG_TAG, " ibside updating()" + moneyDataList.toString());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String id = moneyDataList.get(position).getId();
                String sum = moneyDataList.get(position).getSum();
                String category = moneyDataList.get(position).getCategory();
                String date = moneyDataList.get(position).getDate();
                String note = moneyDataList.get(position).getNote();
                Log.d(LOG_TAG, "before intent in update row");
                Intent intent = new Intent(getApplicationContext(), FinanceOperation.class);
                intent.putExtra("id", id );
                intent.putExtra("sum", sum);
                intent.putExtra("category", category);
                intent.putExtra("date", date);
                intent.putExtra("note", note);
                startActivity(intent);

            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

//    public void updating( int position){
//        Log.d(LOG_TAG, " ibside updating()" + moneyDataList.toString());
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        String id = moneyDataList.get(position).getId();
//
//        String sum;
//        String category;
//        String date;
//        String note;
//
//        TextView txSumUp = (TextView) findViewById(R.id.txSumUp);
//        txDateUp = (TextView) findViewById(R.id.txDateUp);
//        EditText etNoteUp = (EditText) findViewById(R.id.etNoteUp);
//        Spinner spCategoryUp = (Spinner) findViewById(R.id.spCategoryUp);
//        Log.d(LOG_TAG, "Dialog 1");
//        Dialog dialog = new Dialog(ViewList.this);
//        Log.d(LOG_TAG, "Dialog 2");
//        dialog.setTitle("Update");
//        Log.d(LOG_TAG, "Dialog 3");
//        dialog.setContentView(R.layout.dialog_updating);
//        Log.d(LOG_TAG, "Dialog 4");
//        Button btUpdate =(Button)dialog.findViewById(R.id.btUpdate);
//        btUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ViewList.this, "Clicked UPDATE", Toast.LENGTH_LONG).show();
//            }
//        });
//        Log.d(LOG_TAG, "Dialog 5");
//        txDateUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(LOG_TAG, "Dialog 5");
//                setDate(v,3);
//                Log.d(LOG_TAG, "Dialog 6");
//                Toast.makeText(ViewList.this, "Clicked DATE", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        dialog.show();
//        dateUp();
//
////        db.execSQL("delete from finances where id = '" + id + "'");
////        moneyDataList.remove(position);
////        adapter.notifyDataSetChanged();
//        Toast.makeText(ViewList.this, "Row updated", Toast.LENGTH_LONG).show();
//    }
//
//    public void dateUp() {
//        Log.d(LOG_TAG, "dateUp");
//        txDateUp = (TextView) findViewById(R.id.txDateUp);
//
//        calendarUp = Calendar.getInstance();
//        Log.d(LOG_TAG, "dateUp 2");
//        yearUp = calendarUp.get(Calendar.YEAR);
//        Log.d(LOG_TAG, "dateUp 3");
//        monthUp = calendarUp.get(Calendar.MONTH);
//        dayUp = calendarUp.get(Calendar.DAY_OF_MONTH);
//        showDateUp(yearUp, monthUp + 1, dayUp);
//        Log.d(LOG_TAG, "dateUp 4");
//    }
//
//    private void showDateUp(int year, int month, int day) {
//        Log.d(LOG_TAG, "dateUp 5");
//        txDateUp.setText(new StringBuilder().append(day).append(".")
//                .append(month).append(".").append(year));
//        Log.d(LOG_TAG, "dateUp 5");
//    }
//
//    private DatePickerDialog.OnDateSetListener myDateListenerUp = new
//            DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker arg0,
//                                      int arg1, int arg2, int arg3) {
//                    // TODO Auto-generated method stub
//                    // arg1 = year
//                    // arg2 = month
//                    // arg3 = day
//                    Log.d(LOG_TAG, "dateUp 6");
//                    showDateUp(arg1, arg2 + 1, arg3);
//                }
//            };
}

