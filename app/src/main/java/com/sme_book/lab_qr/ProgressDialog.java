package com.sme_book.lab_qr;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sme_book.lab_qr.R;
import com.github.ybq.android.spinkit.sprite.RectSprite;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.PulseRing;
import com.github.ybq.android.spinkit.style.Wave;

public class ProgressDialog extends Dialog {
    public ProgressDialog(@NonNull Context context, Integer selection) {
        super(context);
        if (selection == 1) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_progress);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.spin_kit);
            Sprite fadingCircle = new FadingCircle();
            progressBar.setIndeterminateDrawable(fadingCircle);
        }
        else if (selection == 2) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_progress);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.spin_kit);
            TextView progress_text = (TextView) findViewById(R.id.progress_text);
            progress_text.setVisibility(View.VISIBLE);
            Sprite wave = new Wave();
            progressBar.setIndeterminateDrawable(wave);
        }
    }
}
