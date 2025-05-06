package com.mad.prescriptionmanagementapp.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Tools {

    public static String formatNumber(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("0.##", symbols); // tối đa 2 số sau dấu phẩy
        return df.format(value);
    }
}
