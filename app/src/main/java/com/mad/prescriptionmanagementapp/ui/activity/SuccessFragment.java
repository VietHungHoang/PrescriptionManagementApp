package com.mad.prescriptionmanagementapp.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.mad.prescriptionmanagementapp.R;

public class SuccessFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);

        // Tìm CardView để xử lý sự kiện nhấn
        CardView cardView = view.findViewById(R.id.card_view_success);

        // Đóng Fragment khi nhấn vào CardView
        cardView.setOnClickListener(v -> closeFragment());

        // Tự động đóng Fragment sau 2 giây
        new Handler(Looper.getMainLooper()).postDelayed(this::closeFragment, 2000);

        return view;
    }

    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        }
    }
}