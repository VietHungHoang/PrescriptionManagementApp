package com.mad.prescriptionmanagementapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MedicineItemFragment extends Fragment {

    public MedicineItemFragment() {
        // Required empty public constructor
    }

    public static MedicineItemFragment newInstance(String time, String prescription, String medicines) {
        MedicineItemFragment fragment = new MedicineItemFragment();
        Bundle args = new Bundle();
        args.putString("time", time);
        args.putString("prescription", prescription);
        args.putString("medicines", medicines);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_item, container, false);

        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvPrescription = view.findViewById(R.id.tvPrescription);
        TextView tvMedicineList = view.findViewById(R.id.tvMedicineList);

        if (getArguments() != null) {
            tvTime.setText(getArguments().getString("time", "00:00"));
            tvPrescription.setText(getArguments().getString("prescription", ""));
            tvMedicineList.setText(getArguments().getString("medicines", ""));
        }

        return view;
    }
}
