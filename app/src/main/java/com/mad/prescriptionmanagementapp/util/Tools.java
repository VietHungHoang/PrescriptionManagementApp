package com.mad.prescriptionmanagementapp.util;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.mad.prescriptionmanagementapp.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class Tools {

    public static String formatNumber(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("0.##", symbols); // tối đa 2 số sau dấu phẩy
        return df.format(value);
    }

    public static void replaceFragment(FragmentActivity activity, Fragment newFragment, String transactionName) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.zoom_in,
                        R.anim.fade_out,
                        R.anim.zoom_out,
                        R.anim.fade_out)
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(transactionName)
                .commit();
    }

    public static void addFragment(Fragment fragment, Fragment newFragment, String transactionName) {
        fragment.requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .setCustomAnimations(
                        R.anim.zoom_in,
                        R.anim.fade_out,
                        R.anim.zoom_out,
                        R.anim.fade_out)
                .add(R.id.fragment_container, newFragment)
                .addToBackStack(transactionName)
                .commit();
    }

    @SuppressLint("DefaultLocale")
    public static void setupDatePickerDialog(Context context, EditText editText, boolean defaultToday, String currentDay) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (currentDay == null) {
            if(defaultToday) {
                editText.setText(String.format("%02d/%02d/%d", day, month + 1, year));
            }
        } else {
            editText.setText(currentDay);
        }


        editText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        editText.setText(dateStr);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

}
