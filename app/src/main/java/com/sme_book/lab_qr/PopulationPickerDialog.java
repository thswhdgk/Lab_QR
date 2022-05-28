package com.sme_book.lab_qr;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;

public class PopulationPickerDialog extends Dialog {

    private static PopulationPickerDialog populationPickerDialog;

    public PopulationPickerDialog(@NonNull Context context) { super(context); }

    public static PopulationPickerDialog getInstance(Context context) {
        populationPickerDialog = new PopulationPickerDialog(context);

        return populationPickerDialog;
    }
}