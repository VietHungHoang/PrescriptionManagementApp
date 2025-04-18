package com.mad.prescriptionmanagementapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mad.prescriptionmanagementapp.R;

public class DrugBillFragment extends Fragment {

    public DrugBillFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_drug_bill, container, false);

        TextView tvEdit = rootView.findViewById(R.id.tvEdit);
        FrameLayout containerFragments = rootView.findViewById(R.id.containerFragments);

        // Sự kiện khi nhấn "Chỉnh sửa"
        tvEdit.setOnClickListener(v -> {
            // Example: Thay thế container bằng một fragment mới (Ví dụ: Fragment Chi tiết thuốc)
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.containerFragments, new DrugDetailsFragment());
            transaction.addToBackStack(null);  // Thêm fragment vào back stack
            transaction.commit();
        });

        return rootView;
    }
}
