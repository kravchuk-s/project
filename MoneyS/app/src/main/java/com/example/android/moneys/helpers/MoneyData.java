package com.example.android.moneys.helpers;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by stas on 16.10.2017.
 */

public class MoneyData implements Comparable<MoneyData> {

    private String sum;
    private String category;
    private String note;
    private String date;
    private Date dateD;
    private String id;

    public MoneyData(String sum, String category, String note, String date, String id) {
        this.sum = sum;
        this.category = category;
        this.note = note;
        this.date = date;
        this.id = id;
    }


    @Override
    public String toString() {
        return "MoneyData{" +
                "sum='" + sum + '\'' +
                ", category='" + category + '\'' +
                ", note='" + note + '\'' +
                ", date='" + date + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getSum() {
        return sum;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public Date getDateD() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateInString = getDate();
        try {
            dateD = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateD;
    }

    @Override
    public int compareTo(@NonNull MoneyData o) {
        return getDateD().compareTo(o.getDateD());
    }
}
