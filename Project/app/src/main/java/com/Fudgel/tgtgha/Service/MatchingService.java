package com.Fudgel.tgtgha.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.Fudgel.tgtgha.Model.RouteModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import okhttp3.Route;

public class MatchingService extends Service {
    public MatchingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MATCH","SUCCESS!");
                matchOrCreate(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MATCH","ERROR: " + databaseError.getMessage());
            }
        });


    }

    public void matchOrCreate(DataSnapshot dataSnapshot){

        Boolean foundMatch = false;

        DataSnapshot Routes = dataSnapshot.child("route");

        for(DataSnapshot item : Routes.getChildren()) {

            if(item.child("Time").getValue().toString().equals(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Time").getValue().toString()) &&
                    item.child("Goal").getValue().toString().equals(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Goal").getValue().toString())&&
                    getDistance(dataSnapshot, item.child("Users").child("1").getValue().toString()) < 2000){

                FirebaseDatabase.getInstance().getReference("route/" + item.child("id").getValue().toString()).child("Users").child("2").setValue(FirebaseAuth.getInstance().getUid());

                foundMatch = true;

            }
        }

        if (!foundMatch){
            waitingForMatch(dataSnapshot);
        }

    }

    public void waitingForMatch(DataSnapshot dataSnapshot){

        String id = String.valueOf(new Date().getTime());

        RouteModel route = new RouteModel();
        route.setID(id);
        route.setGoal(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Time").getValue().toString());
        route.setTime(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getUid()).child("Route").child("Goal").getValue().toString());

        FirebaseDatabase.getInstance().getReference().child("routes").child(id).setValue(route);
        FirebaseDatabase.getInstance().getReference().child("routes").child(id).child("Users").child("1").setValue(route);
        FirebaseDatabase.getInstance().getReference().child("routes").child(id).child("Users").child("2").setValue("None... yet!");

        FirebaseDatabase.getInstance().getReference().child("routes").child(id).child("Users").child("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MATCH","MATCHED!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chat","ERROR: " + databaseError.getMessage());
            }
        });

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
        return R * c * 1000; // convert to meters
    }
}
