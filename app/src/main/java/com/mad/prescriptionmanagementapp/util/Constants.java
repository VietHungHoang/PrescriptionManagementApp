package com.mad.prescriptionmanagementapp.util;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String BASE_URL = "http://192.168.0.100:8080/api/v1/";

    public static final String GOOGLE_WEB_CLIENT_ID = "689157132294-p268fum6akhfo29qmui996v1ss07oqgi.apps.googleusercontent.com";

    public static final String TAG = "MedPTIT";

    public static final String NOTIFICATION_CHANNEL_ID_REMINDERS = "drug_reminder_channel";

    public static final String ACTION_CONFIRM = "com.yourapp.ACTION_CONFIRM";
    public static final String ACTION_SNOOZE = "com.yourapp.ACTION_SNOOZE";
    public static final String ACTION_SKIP = "com.yourapp.ACTION_SKIP";

    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    public static final String EXTRA_REMINDER_IDS_LIST = "extra_reminder_ids_list"; // ArrayList<Long>

    public static final int SNOOZE_DURATION_MINUTES = 5;

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

}
