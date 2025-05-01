package com.mad.prescriptionmanagementapp.ui.fragment.addprescription;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddPrescriptionInfoBinding;
import com.mad.prescriptionmanagementapp.ui.activity.AddPrescriptionActivity;

public class AddPrescriptionInfoFragment extends Fragment {
    private FragmentAddPrescriptionInfoBinding binding;

    public AddPrescriptionInfoFragment() {
    }


    public static AddPrescriptionInfoFragment newInstance() {
        AddPrescriptionInfoFragment fragment = new AddPrescriptionInfoFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAddPrescriptionInfoBinding.inflate(inflater, container, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AddPrescriptionActivity) requireActivity()).setCustomTitle("Thêm đơn thuốc");


        this.binding.btnAddDrug.setOnClickListener(v -> {
            // Tạo fragment mới để thay thế
            Fragment newFragment = new SelectDrugFragment(); // Fragment bạn muốn chuyển tới
            // Sử dụng FragmentTransaction để thay thế fragment hiện tại bằng fragment mới
            this.getParentFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_CLOSE)
                    .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
                    .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
                    .commit();
        });
    }
}