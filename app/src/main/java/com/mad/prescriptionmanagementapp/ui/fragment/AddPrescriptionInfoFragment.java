package com.mad.prescriptionmanagementapp.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.FragmentAddPrescriptionInfoBinding;
import com.mad.prescriptionmanagementapp.ui.viewmodel.AddPrescriptionViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPrescriptionInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPrescriptionInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPrescriptionInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPrescriptionInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPrescriptionInfoFragment newInstance(String param1, String param2) {
        AddPrescriptionInfoFragment fragment = new AddPrescriptionInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentAddPrescriptionInfoBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        this.binding.btnThemThuoc.setOnClickListener(v -> {
            Log.d("Fragment", "Button 'Thêm Thuốc' clicked");
            // Tạo fragment mới để thay thế
            Fragment newFragment = new SelectDrugFragment(); // Fragment bạn muốn chuyển tới
            // Sử dụng FragmentTransaction để thay thế fragment hiện tại bằng fragment mới
            this.getParentFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                    .replace(R.id.fragment_container, newFragment)  // id container chứa fragment
                    .addToBackStack(null)  // Thêm vào back stack (để khi bấm back sẽ quay lại fragment trước đó)
                    .commit();
        });
    }
}