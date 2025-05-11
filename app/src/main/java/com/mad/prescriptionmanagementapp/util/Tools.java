package com.mad.prescriptionmanagementapp.util;

import androidx.fragment.app.Fragment;

import com.mad.prescriptionmanagementapp.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Tools {

    public static String formatNumber(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("0.##", symbols); // tối đa 2 số sau dấu phẩy
        return df.format(value);
    }

    public static void changeFragment(Fragment oldFragment, Fragment newFragment ) {
        oldFragment.getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.zoom_in,
                        R.anim.fade_out,
                        R.anim.zoom_out,
                        R.anim.fade_out )
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit();
    }

}
