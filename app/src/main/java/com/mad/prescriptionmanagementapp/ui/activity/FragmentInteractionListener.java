package com.mad.prescriptionmanagementapp.ui.activity;

public interface FragmentInteractionListener {
    void setToolbarTitle(String title);

    void enableContinueButton(boolean enabled, String text);
}
