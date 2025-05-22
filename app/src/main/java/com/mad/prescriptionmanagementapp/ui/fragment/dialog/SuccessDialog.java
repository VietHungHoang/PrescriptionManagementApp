package com.mad.prescriptionmanagementapp.ui.fragment.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mad.prescriptionmanagementapp.R;

public class SuccessDialog {
    private final Fragment fragment;
    private final String successMessage;
    private View dialogView;
    private AlertDialog alertDialog;
    private boolean isResetDimmed;
    public SuccessDialog(Fragment fragment, String successMessage, boolean isRestDimmed) {
        this.fragment = fragment;
        this.successMessage = successMessage;
        this.isResetDimmed = isRestDimmed;
        this.initDialog();


    }

    private void initDialog() {
        // Tạo View từ layout đã định nghĩa
        LayoutInflater inflater = fragment.getLayoutInflater();
        this.dialogView = inflater.inflate(R.layout.dialog_success, null);

        // Lấy các view cần thiết từ dialogView
        TextView errorMessageTextView = dialogView.findViewById(R.id.tv_success_message);
        errorMessageTextView.setText(successMessage);

        // Tạo AlertDialog
        this.alertDialog = new AlertDialog.Builder(this.fragment.requireActivity())
                .setView(dialogView) // Đặt custom layout vào AlertDialog
                .setCancelable(false) // Không cho phép đóng ngoài các nút
                .create();
    }

    public void showDialog() {
        // Hiển thị AlertDialog
        alertDialog.show();
        this.setUpTransition();
    }

    private void setUpTransition() {
        // Thiết lập vị trí gần sát phía trên màn hình
        Window window = this.alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.TOP); // Đặt vị trí của dialog gần phía trên
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

            if(this.isResetDimmed) {
                window.setDimAmount(0f);
            }

            // Set vị trí cách top 80px
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.y = 80;
            window.setAttributes(layoutParams);
            // Lấy chiều cao của màn hình
            DisplayMetrics displayMetrics = new DisplayMetrics();
            this.fragment.requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;

            // Thêm animation cho việc trượt xuống
            TranslateAnimation slideDown = new TranslateAnimation(0, 0, -screenHeight , 0);
            slideDown.setDuration(200); // Thời gian trượt xuống
            dialogView.startAnimation(slideDown);
        }

        // Dùng Handler để tự động đóng AlertDialog sau 3 giây và thêm animation trượt lên
        new Handler().postDelayed(() -> {
            // Animation trượt lên
            TranslateAnimation slideUp = new TranslateAnimation(0, 0, 0, -alertDialog.getWindow().getDecorView().getHeight());
            slideUp.setDuration(500); // Thời gian trượt lên
            dialogView.startAnimation(slideUp);

            // Đóng dialog sau khi animation hoàn tất
            new Handler().postDelayed(alertDialog::dismiss, 500); // Đợi cho animation hoàn tất
        }, 1000); // 3 giây hiển thị trước khi đóng
    }
}
