package com.mksol.assessment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public Context mContext;
    public Activity mActivity;

    private AlertDialog progressDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            getSupportActionBar().hide(); // hide the title bar

        } catch (Exception ee) {

        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setResult(RESULT_CANCELED);

    }

    public void showAlertDialogError(String message, String title) {
        try {

            new AlertDialog.Builder(this, R.style.AppThemeRainProgress_Dark_Dialog)
                    .setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("Ok", null)
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialogFinish(String message) {
        new AlertDialog.Builder(this, R.style.AppThemeRainProgress_Dark_Dialog)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void showAlertDialogFinish(String message, String title) {
        new AlertDialog.Builder(this, R.style.AppThemeRainProgress_Dark_Dialog)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    protected void showProgressDialog(Context context, String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }

        LayoutInflater li = LayoutInflater.from(this);
        View myView = li.inflate(R.layout.layout_loading_dialog, null);

        builder = new AlertDialog.Builder(context, R.style.AppThemeRainProgress_Dark_Dialog);
        builder.setCancelable(false);

        builder.setView(myView);

        TextView txt = (TextView) myView.findViewById((R.id.textMessage));
        txt.setText(message);

        ProgressBar prog = (ProgressBar) myView.findViewById(R.id.progress);

        progressDialog = builder.create();

        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog == null) {
            return;
        }

        progressDialog.dismiss();
    }

}

