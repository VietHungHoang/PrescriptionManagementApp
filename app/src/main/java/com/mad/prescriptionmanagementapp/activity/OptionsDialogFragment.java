package com.mad.prescriptionmanagementapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mad.prescriptionmanagementapp.R;

public class OptionsDialogFragment extends BottomSheetDialogFragment {

    public interface OptionsDialogListener {
        void onPrescriptionSelected();
        void onAppointmentSelected();
    }

    private OptionsDialogListener listener;

    public static OptionsDialogFragment newInstance() {
        return new OptionsDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OptionsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OptionsDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        CardView cardPrescription = view.findViewById(R.id.cardPrescription);
        CardView cardAppointment = view.findViewById(R.id.cardAppointment);

        // Set up close button
        btnClose.setOnClickListener(v -> dismiss());

        // Set up prescription option
        cardPrescription.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPrescriptionSelected();
            }
            dismiss();
        });

        // Set up appointment option
        cardAppointment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentSelected();
            }
            dismiss();
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

