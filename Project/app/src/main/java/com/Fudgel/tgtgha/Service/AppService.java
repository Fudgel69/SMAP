package com.Fudgel.tgtgha.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class AppService extends Service {

    private IBinder binder = new AppBinder();

    //create binder
    public class AppBinder extends Binder {
        public AppService getService() { return AppService.this; }
    }



    @Override
    public void onCreate() {
        super.onCreate();

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
}
