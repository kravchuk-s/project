package com.example.android.moneys.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.example.android.moneys.helpers.DBHelper.DATABASE_NAME;

/**
 * Created by stas on 29.10.2017.
 */

public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {

    private Context context;

    private final String EXPORT_LOG = "EXPORT LOG";


    @RequiresApi(api = Build.VERSION_CODES.M)
    public ExportDatabaseCSVTask(Context context, Activity activity) {
        this.context = context;
        onPostExecute(doInBackground());
    }

    @Override

    protected void onPreExecute() {

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected Boolean doInBackground(final String... args) {

        File dbFile = context.getDatabasePath(DATABASE_NAME);
        DBHelper dbhelper = new DBHelper(context.getApplicationContext());
        Log.d(EXPORT_LOG, dbFile.toString());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "csvdata.csv");

            try {

                if (file.createNewFile()) {
                    Log.d(EXPORT_LOG, "Creating new file");
                } else {
                    Log.d(EXPORT_LOG, "File already exists");
                }

                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM finances", null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {

                    String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                            curCSV.getString(3), curCSV.getString(4)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();

                Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
                sendIntent.setType("application/csv");
                Uri U = Uri.fromFile(file);
                sendIntent.putExtra(Intent.EXTRA_STREAM, U);
                context.startActivity(Intent.createChooser(sendIntent, "Send Mail"));

                return true;
            } catch (SQLException sqlEx) {

                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                return false;
            } catch (IOException e) {

                Log.e("MainActivity", e.getMessage(), e);
                return false;
            }

    }

    protected void onPostExecute(final Boolean success) {

        if (success) {
            Toast.makeText(context, "Export succeed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }


}
