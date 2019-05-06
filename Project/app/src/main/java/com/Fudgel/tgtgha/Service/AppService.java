package com.Fudgel.tgtgha.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;

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
