package com.mad.prescriptionmanagementapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mad.prescriptionmanagementapp.R;

public class FrequencyAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] frequencies;

    public FrequencyAdapter(Context context, String[] frequencies) {
        super(context, R.layout.item_frequency, frequencies);
        this.context = context;
        this.frequencies = frequencies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_frequency, parent, false);
        }
        TextView tvFrequency = convertView.findViewById(R.id.tvFrequency);
        tvFrequency.setText(frequencies[position]);
        return convertView;
    }
}
