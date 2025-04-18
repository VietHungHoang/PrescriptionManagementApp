package com.mad.prescriptionmanagementapp.model;

public class MedicineTableItem {
    private final String name;
    private final String quantity;

    public MedicineTableItem(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }
}
