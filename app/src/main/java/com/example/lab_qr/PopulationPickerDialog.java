package com.example.lab_qr;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class PopulationPickerDialog extends Dialog {

    private static PopulationPickerDialog populationPickerDialog;

    public PopulationPickerDialog(@NonNull Context context) { super(context); }

    public static PopulationPickerDialog getInstance(Context context) {
        populationPickerDialog = new PopulationPickerDialog(context);

        return populationPickerDialog;
    }
}