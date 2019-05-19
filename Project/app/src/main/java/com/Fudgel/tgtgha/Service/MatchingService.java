package com.Fudgel.tgtgha.Service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.Fudgel.tgtgha.Model.RouteModel;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import okhttp3.Route;

public class MatchingService extends Service {


    private final IBinder binder = new MatchBinder();

    //create binder
    public class MatchBinder extends Binder {
        public MatchingService getService() {
            Log.e("MATCH", "Service constructor");
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("MATCH","SUCCESS!");
                    matchOrCreate(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("MATCH","ERROR: " + databaseError.getMessage());
                }
            });
            return MatchingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.e("MATCH", "Service bound, returning bool");return binder;
    }

    @Override
    public boolean onUnbind (Intent intent){Log.i("MATCH", "Service unbound");return super.onUnbind(intent);}

    @Override
    public void onCreate(){
        super.onCreate();
        Log.e("MATCH","onCreate");


    }



    public void matchOrCreate(DataSnapshot dataSnapshot){

        Boolean foundMatch = false;

        DataSnapshot Routes = dataSnapshot.child("routes");

        for(DataSnapshot item : Routes.getChildren()) {
            if (!foundMatch) {
                if (!item.child("Users").child("1").getValue().toString().equals(FirebaseAuth.getInstance().getUid()) &&
                        item.child("time").getValue().toString().equals(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Time").getValue().toString()) &&
                        item.child("goal").getValue().toString().equals(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Goal").getValue().toString()) &&
                        getDistance(dataSnapshot, item.child("Users").child("1").getValue().toString()) < 2000) {
                    Log.e("MATCH", item.toString());
                    FirebaseDatabase.getInstance().getReference("routes/" + item.getKey()).child("Users").child("2").setValue(FirebaseAuth.getInstance().getUid());
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Chat").setValue(item.getKey());
                    foundMatch = true;

                }
            }
        }

        if (!foundMatch){
            waitingForMatch(dataSnapshot);
        }

    }

    public void waitingForMatch(DataSnapshot dataSnapshot){
        boolean updated = false;
        String id = String.valueOf(new Date().getTime());

        RouteModel route = new RouteModel();
        route.setID(id);
        route.setGoal(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Goal").getValue().toString());
        route.setTime(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Time").getValue().toString());

        FirebaseDatabase.getInstance().getReference().child("routes").child(id).setValue(route);
        FirebaseDatabase.getInstance().getReference().child("routes").child(id).child("Users").child("1").setValue(FirebaseAuth.getInstance().getUid());
        FirebaseDatabase.getInstance().getReference().child("routes").child(id).child("Users").child("2").setValue("None... yet!");

        FirebaseDatabase.getInstance().getReference().child("routes").child(id).child("Users").child("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MATCH","YOU GOT A MATCH!");
                makeChat(dataSnapshot, id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chat","ERROR: " + databaseError.getMessage());
            }
        });

    }

    public void makeChat(DataSnapshot dataSnapshot, String id){
        FirebaseDatabase.getInstance().getReference().child("chats").child(id).child("users").child("1").setValue(FirebaseAuth.getInstance().getUid());
        FirebaseDatabase.getInstance().getReference().child("chats").child(id).child("users").child("2").setValue(dataSnapshot.getValue().toString());
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chat").setValue(id);
    }

    public double getDistance(DataSnapshot dataSnapshot, String uid){

        String chat = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chat").getValue().toString();
        final int R = 6371; // Radius of the earth
        double lat = (Double) dataSnapshot.child("Users").child(uid).child("location").child("latitude").getValue();
        double lon = (Double) dataSnapshot.child("Users").child(uid).child("location").child("longitude").getValue();
        double myLat = (Double) dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("location").child("latitude").getValue();
        double myLon = (Double) dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("location").child("longitude").getValue();

        double latDistance = Math.toRadians(lat - myLat);
        double lonDistance = Math.toRadians(lon - myLon);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(myLat)) * Math.cos(Math.toRadians(lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Log.e("DISTANCE: ", String.valueOf(R * c * 1000) );
        return R * c * 1000; // convert to meters
    }
}
