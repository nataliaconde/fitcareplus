package br.com.fitcareplus.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import br.com.fitcareplus.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;
    private boolean isActive;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
        this.isActive = false;
    }

    public void show() {
        if (!this.isActive) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

            LayoutInflater inflater = this.activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.loading_dialog, null));
            builder.setCancelable(false);

            this.alertDialog = builder.create();
            this.alertDialog.show();
            this.isActive = true;
        }
    }

    public void dismiss() {
        if(this.isActive) {
            this.alertDialog.dismiss();
            this.isActive = false;
        }
    }
}
