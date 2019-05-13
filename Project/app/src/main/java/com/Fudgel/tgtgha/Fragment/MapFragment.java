package com.Fudgel.tgtgha.Fragment;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.Fudgel.tgtgha.R;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



public class MapFragment extends Fragment {


    private Location myLocation = new Location("");
    private FusedLocationProviderClient fusedLocationProviderClient;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                LatLng latLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                if ( ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},1 );
                }
                mMap.setMyLocationEnabled(true);

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng( 56.156635, 10.210365))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.addMarker(new MarkerOptions()
                .position(new LatLng(56.156635, 10.210365))
                .title("Location of da frand"));

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

            }
        });


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
}
