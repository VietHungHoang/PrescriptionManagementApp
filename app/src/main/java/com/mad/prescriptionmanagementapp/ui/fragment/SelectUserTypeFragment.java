package com.mad.prescriptionmanagementapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.prescriptionmanagementapp.R;

public class SelectUserTypeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private LinearLayout optionUser, optionDoctor;
    private ImageView tickUser, tickDoctor;

    public interface OnUserTypeSelectedListener {
        void onUserTypeSelected(String userType);
    }

    private OnUserTypeSelectedListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnUserTypeSelectedListener) {
            mListener = (OnUserTypeSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnUserTypeSelectedListener");
        }
    }

    public SelectUserTypeFragment() {
    }

    public static SelectUserTypeFragment newInstance(String param1, String param2) {
        SelectUserTypeFragment fragment = new SelectUserTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_select_user_type, container, false);

        optionUser = view.findViewById(R.id.optionUser);
        optionDoctor = view.findViewById(R.id.optionDoctor);
        tickUser = view.findViewById(R.id.tickUser);
        tickDoctor = view.findViewById(R.id.tickDoctor);

        optionUser.setOnClickListener(v -> selectUserType(true));
        optionDoctor.setOnClickListener(v -> selectUserType(false));

        return view;
    }

    private void selectUserType(boolean isUser) {
        if (isUser) {
            tickUser.setVisibility(View.VISIBLE);
            tickDoctor.setVisibility(View.GONE);
        } else {
            tickUser.setVisibility(View.GONE);
            tickDoctor.setVisibility(View.VISIBLE);
        }
        if (mListener != null) {
            mListener.onUserTypeSelected("CUSTOMER");
        }
    }
}