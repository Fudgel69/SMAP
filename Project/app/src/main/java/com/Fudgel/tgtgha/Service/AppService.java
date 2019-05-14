package com.Fudgel.tgtgha.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Binder;
import android.support.annotation.NonNull;
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

    private IBinder binder = new AppBinder();

    //create binder
    public class AppBinder extends Binder {
        public AppService getService() { return AppService.this; }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        new BackgroundTask().execute();

    }



    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }


    @Override
    public boolean onUnbind (Intent intent){return super.onUnbind(intent);}


    //stops the Service task, when the application is removed from the "last used"-listen
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    private class BackgroundTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
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

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").setValue(location);
            }
        };
    }
}
