package com.mad.prescriptionmanagementapp.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mad.prescriptionmanagementapp.databinding.DialogErrorBinding;

public class ErrorToast {
    public static void showToast(Context context, String message) {
        // Inflate layout custom cho Toast
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogErrorBinding binding = DialogErrorBinding.inflate(inflater);

        // Lấy các view từ layout và thiết lập giá trị
        binding.tvErrorMessage.setText(message);

        // Tạo Toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT); // Thời gian hiển thị
        toast.setGravity(Gravity.BOTTOM, 0, 200); // Đặt Toast xuất hiện từ dưới lên
        toast.setView(binding.getRoot()); // Đặt layout custom cho Toast

        // Lấy chiều rộng màn hình
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Thiết lập kích thước cho Toast
        int minHeight = (int) (56 * displayMetrics.density); // 56dp to pixels
        int paddingHorizontal = (int) (16 * displayMetrics.density); // 16dp to pixels

        // Set layout params với kích thước mới
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, minHeight);
        binding.getRoot().setLayoutParams(params);

        // Thêm padding ngang cho layout
        binding.getRoot().setPadding(paddingHorizontal, 0, paddingHorizontal, 0);

        // Hiển thị Toast
        toast.show();

        // Thêm animation trượt từ dưới lên
        Animation slideUp = new TranslateAnimation(0, 0, 500, 0); // Y-axis di chuyển từ dưới lên
        slideUp.setDuration(500); // Thời gian trượt
        binding.getRoot().startAnimation(slideUp);
    }
}
