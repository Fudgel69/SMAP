package com.Fudgel.tgtgha.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AppService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private final IBinder binder = new AppBinder();

    //create binder
    public class AppBinder extends Binder {
        public AppService getService() {  Log.i("SERVICE", "Service constructor");return AppService.this; }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SERVICE", "Service created");
        new BackgroundTask().execute();

    }



    @Override
    public IBinder onBind(Intent intent){
        Log.i("SERVICE", "Service bound, returning bool");return binder;
    }


    @Override
    public boolean onUnbind (Intent intent){Log.i("SERVICE", "Service bound");return super.onUnbind(intent);}


    //stops the Service task, when the application is removed from the "last used"-listen
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    private class BackgroundTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.i("SERVICE", "Background task firing!");
            while (true)
            {
                try {
                    Thread.sleep(20000); //Sleep for 2 minutes
                    UpdateLocation();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void UpdateLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("SERVICE", "Logging location...");
                FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").setValue(location);
            }
        };
        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").setValue(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }
}
