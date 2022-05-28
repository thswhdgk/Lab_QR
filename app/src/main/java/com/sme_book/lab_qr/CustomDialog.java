package com.sme_book.lab_qr;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.sme_book.lab_qr.R;

public class CustomDialog extends Dialog {

    private static CustomDialog customDialog;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public static CustomDialog getInstance(Context context) {
        customDialog = new CustomDialog(context);

        return customDialog;
    }

    public void showDialog(Uri uri) {
        customDialog.setContentView(R.layout.activity_popup);

        Button btn_ok = customDialog.findViewById(R.id.btn_ok);
        ImageView iv_lab = customDialog.findViewById(R.id.iv_lab);

        Glide.with(getContext()).load(uri).into(iv_lab);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }
}
