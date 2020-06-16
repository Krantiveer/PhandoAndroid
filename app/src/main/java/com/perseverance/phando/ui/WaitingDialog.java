package com.perseverance.phando.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.perseverance.phando.R;


public class WaitingDialog extends Dialog {

    public WaitingDialog(Context context) {

        super(context, R.style.WaitingDialog);
        setContentView(R.layout.dialog_waiting);
    }


    public void setMessage(CharSequence message) {
        ((TextView) findViewById(R.id.message)).setText(message);
        findViewById(R.id.message).setVisibility(View.GONE);
    }

    public void setMessage(int messageResId) {
        ((TextView) findViewById(R.id.message)).setText(messageResId);
        findViewById(R.id.message).setVisibility(View.GONE);
    }

}
