package com.mksol.assessment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;

        try {
            if (needsPermission()) {
                String[] perms = retrievePermissions(mContext);
                requestPermissions(perms, 10);
            } else {

                continueStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean needsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        else
            return false;
    }

    public static String[] retrievePermissions(Context context) {
        try {
            return context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("This should have never happened.", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (hasAllPermissionsGranted(grantResults)) {
                continueStart();
            } else {
                showAlertDialogFinish("Required permission has not been granted", "Error");
            }
        }
    }

    public boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void continueStart() {
        startActivity(new Intent(mContext, LocationActivity.class));
        finish();
    }
}