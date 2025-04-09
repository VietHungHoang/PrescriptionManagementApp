package com.mad.prescriptionmanagementapp.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.FragmentSelectUserTypeBinding;
import com.mad.prescriptionmanagementapp.ui.activity.FragmentInteractionListener;
import com.mad.prescriptionmanagementapp.ui.viewmodel.UserDataCollectionViewModel;

public class SelectUserTypeFragment extends Fragment {

    private FragmentSelectUserTypeBinding binding;
    private FragmentInteractionListener listener;
    private String selectedUserType = null;
    private UserDataCollectionViewModel dataViewModel;

    public static final String USER_TYPE_DOCTOR = "doctor";
    public static final String USER_TYPE_CUSTOMER = "customer";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            this.listener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentInteractionListener");
        }
    }

    public SelectUserTypeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataViewModel = new ViewModelProvider(requireActivity()).get(UserDataCollectionViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_user_type, container, false);
        return binding.getRoot();
//        View view = inflater.inflate(R.layout.fragment_select_user_type, container, false);
//
//        optionUser = view.findViewById(R.id.optionUser);
//        optionDoctor = view.findViewById(R.id.optionDoctor);
//        tickUser = view.findViewById(R.id.tickUser);
//        tickDoctor = view.findViewById(R.id.tickDoctor);
//
//        optionUser.setOnClickListener(v -> selectUserType(true));
//        optionDoctor.setOnClickListener(v -> selectUserType(false));

//        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (listener != null) {
            listener.setToolbarTitle("Chọn loại người dùng");
            listener.enableContinueButton(false , "Tiếp tục");
        }

        this.binding.optionUser.setOnClickListener(v -> selectUserType(true));
        this.binding.optionDoctor.setOnClickListener(v -> selectUserType(false));
    }

    private void selectUserType(boolean isUser) {
        if (isUser) {
            this.binding.tickUser.setVisibility(View.VISIBLE);
            this.binding.tickDoctor.setVisibility(View.GONE);
            this.dataViewModel.setRole(1L);
        } else {
            this.binding.tickUser.setVisibility(View.GONE);
            this.binding.tickDoctor.setVisibility(View.VISIBLE);
            this.dataViewModel.setRole(2L);
        }
        if (listener != null) {
            listener.enableContinueButton(this.dataViewModel.getRoleId() != null, "Tiếp tục");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null; // Avoid memory leak
    }

    // Phương thức để Activity lấy lựa chọn của người dùng
    public String getSelectedUserType() {
        return selectedUserType;
    }
}