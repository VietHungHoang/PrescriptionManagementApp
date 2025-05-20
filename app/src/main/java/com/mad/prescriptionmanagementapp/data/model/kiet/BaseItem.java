package com.mad.prescriptionmanagementapp.data.model.kiet;

public abstract class BaseItem {
    public static final int TYPE_PRESCRIPTION = 0;
    public static final int TYPE_GROUP = 1;

    public abstract int getType();
}
