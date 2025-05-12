package com.mad.prescriptionmanagementapp.ui.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;

import com.mad.prescriptionmanagementapp.R;
import com.mad.prescriptionmanagementapp.databinding.DialogConfirmBinding;

public class ConfirmDialog {

    public interface ConfirmationDialogListener {
        void onConfirm();
    }

    public static Dialog showConfirmationDialog(
            Context context,
            String title,
            String message,
            String confirmText,
            String cancelText,
            ConfirmationDialogListener listener) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogConfirmBinding binding = DialogConfirmBinding.inflate(inflater);
        dialog.setContentView(binding.getRoot());

        // Make dialog background transparent to show the rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Set title and message
        if (title != null) {
            binding.tvMessage.setText(title);
        }

        if (message != null) {
            binding.tvMessage.setText(message);
        }

        // Set button texts
        if (confirmText != null) {
            binding.btnConfirm.setText(confirmText);
        }

        if (cancelText != null) {
            binding.btnCancel.setText(cancelText);
        }

        // Set button click listeners
        binding.btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onConfirm();
            }
        });

        binding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onCancel();
            }
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showCancelConfirmationDialog(
            Context context,
            ConfirmationDialogListener listener,
            String title,
            String message,
            String confirm) {


        return showConfirmationDialog(
                context,
                title,
                message,
                confirm,
                "Đóng",
                listener
        );
    }
}