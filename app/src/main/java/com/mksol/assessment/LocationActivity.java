package com.mksol.assessment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationActivity extends BaseActivity {

    LocationManager locationManager;
    Location location;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;

        progress = (ProgressBar) findViewById(R.id.progress);

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showAlertDialogError("Location services not enabled", "Error");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 10, locationListenerGPS);
        if (isLocationEnabled()) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            getWeather(location);
        } else {
            showAlertDialogError("Location services not enabled", "Error");
        }

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLocationEnabled()) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        showAlertDialogError("Location services not enabled", "Error");
                        return;
                    }
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    getWeather(location);
                } else {
                    showAlertDialogError("Location services not enabled", "Error");

                }

                pullToRefresh.setRefreshing(false);
            }
        });

    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            getWeather(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    protected void onResume() {
        super.onResume();
        isLocationEnabled();
    }

    private boolean isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private final class LongOperation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=53f9d8e4213222cf517d86dc406d67fc&units=metric");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {

                            JSONObject mainObject = null;
                            try {

                                mainObject = new JSONObject(total.toString());
                                JSONObject uniObject = mainObject.getJSONObject("main");
                                String temp = uniObject.getString("temp");
                                String hum = uniObject.getString("humidity");
                                String press = uniObject.getString("pressure");
                                String temp_min = uniObject.getString("temp_min");
                                String temp_max = uniObject.getString("temp_max");

                                JSONObject uniObjectW = mainObject.getJSONObject("wind");
                                String speed = uniObjectW.getString("speed");
                                String deg = uniObjectW.getString("deg");


                                TextView textViewTempVal = (TextView) findViewById(R.id.textViewTempVal);
                                textViewTempVal.setText(temp + "° C");

                                TextView textViewHumVal = (TextView) findViewById(R.id.textViewHumVal);
                                textViewHumVal.setText(hum);

                                TextView textViewPresVal = (TextView) findViewById(R.id.textViewPresVal);
                                textViewPresVal.setText(press);

                                TextView textViewTempMinVal = (TextView) findViewById(R.id.textViewTempMinVal);
                                textViewTempMinVal.setText(temp_min + "° C");

                                TextView textViewTempMaxVal = (TextView) findViewById(R.id.textViewTempMaxVal);
                                textViewTempMaxVal.setText(temp_max + "° C");

                                TextView textViewWindSpeedVal = (TextView) findViewById(R.id.textViewWindSpeedVal);
                                textViewWindSpeedVal.setText(speed);

                                TextView textViewDegSpeedVal = (TextView) findViewById(R.id.textViewWindDegVal);
                                textViewDegSpeedVal.setText(deg);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {
            progress.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
           }
        }
    }

    private void getWeather(Location location) {
        try {
            this.location = location;

            if (location == null)
                showAlertDialogError("Unable to get GPS co-ordinates", "Error");
            else {
                new LongOperation().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (location == null)
                showAlertDialogError("Unable to get GPS co-ordinates", "Error");
        }
    }

}
