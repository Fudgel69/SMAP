package com.Fudgel.tgtgha.Fragment;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.Fudgel.tgtgha.R;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapFragment extends Fragment {


    private Location myLocation = new Location("");
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LatLng areaGoal;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        areaGoal = new LatLng(56.2,10.180556);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                map = mMap;

                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                map.clear(); //clear old markers

                if ( ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},1 );
                }
                map.setMyLocationEnabled(true);

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng( 56.156635, 10.210365))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                map.addMarker(new MarkerOptions()
                        .position(new LatLng(56.156635, 10.210365))
                        .title("Location of da frand"));



                map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
            }
        });

        getFriends();

        return rootView;
    }

    public void getLocation(){

        if ( ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},1 );
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                }else{
                }
            }
        });
    }

    public void getFriends(){

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Chat","SUCCESS!");
                try{
                    handleFirends(dataSnapshot);
                } catch (Exception e) {
                    Log.e("MapFragment:", "Exception men handling friends - " + e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chat","ERROR: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Connection refused!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Pins matched user to the map
    public void handleFirends(DataSnapshot dataSnapshot){

        map.clear();
        LatLng loc;

        String chat = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chat").getValue().toString();

        if (!dataSnapshot.child("chats").child(chat).child("users").child("1").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

            loc = new LatLng(
                    (Double) dataSnapshot.child("Users").child(dataSnapshot.child("chats").child(chat).child("users").child("1").getValue().toString()).child("location").child("latitude").getValue(),
                    (Double) dataSnapshot.child("Users").child(dataSnapshot.child("chats").child(chat).child("users").child("1").getValue().toString()).child("location").child("longitude").getValue());
            map.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(dataSnapshot.child("Users").child(dataSnapshot.child("chats").child(chat).child("users").child("1").getValue().toString()).child("userName").getValue().toString())
            );
        } else {
            loc = new LatLng(
                    (Double) dataSnapshot.child("Users").child(dataSnapshot.child("chats").child(chat).child("users").child("2").getValue().toString()).child("location").child("latitude").getValue(),
                    (Double) dataSnapshot.child("Users").child(dataSnapshot.child("chats").child(chat).child("users").child("2").getValue().toString()).child("location").child("longitude").getValue());
            map.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(dataSnapshot.child("Users").child(dataSnapshot.child("chats").child(chat).child("users").child("2").getValue().toString()).child("userName").getValue().toString())
            );
        }

        switch (dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Route").child("Goal").getValue().toString()){
            case "Skejby":
                areaGoal = new LatLng(56.2,10.180556);
                break;
            case "Viby J":
                areaGoal = new LatLng(56.125, 10.161111);
                break;
            case "Aarhus C":
                areaGoal = new LatLng(56.162937, 10.203921);
                break;
            case "Aarhus V":
                areaGoal = new LatLng(56.171597, 10.163039);
                break;
            case "Aarhus N":
                areaGoal = new LatLng(56.188264, 10.196372);
                break;
            case "Aarhus S":
                areaGoal = new LatLng(56.125, 10.161111);
                break;
        }

        map.addCircle(new CircleOptions()
                .center(areaGoal)
                .radius(500)
                .strokeColor(Color.RED));
    }
}
