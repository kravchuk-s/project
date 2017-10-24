package com.example.android.moneys.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.moneys.R;

import java.util.ArrayList;

/**
 * Created by stas on 16.10.2017.
 */

public class FourColumnListAdapter extends ArrayAdapter<MoneyData> {

    private LayoutInflater layoutInflater;
    private ArrayList<MoneyData> mData;
    private int mViewResourceId;

    public FourColumnListAdapter (Context context, int textViewResourceId, ArrayList<MoneyData> mData){
        super(context,textViewResourceId, mData);
        this.mData = mData;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public View getView( int position, View convertView, ViewGroup parent){
        convertView = layoutInflater.inflate(mViewResourceId, null);

        MoneyData moneyData = mData.get(position);

        if (moneyData != null){
            TextView txSum = (TextView) convertView.findViewById(R.id.textSum);
            TextView txCategory = (TextView) convertView.findViewById(R.id.textCategory);
            TextView txNote = (TextView) convertView.findViewById(R.id.textNote);
            TextView txDate = (TextView) convertView.findViewById(R.id.textDate);

            if (txSum != null){
                txSum.setText(moneyData.getSum());
            }
            if (txCategory != null){
                txCategory.setText(moneyData.getCategory());
            }
            if (txNote != null){
                txNote.setText(moneyData.getNote());
            }
            if (txDate != null){
                txDate.setText(moneyData.getDate());
            }

        }
        return convertView;
    }
}
