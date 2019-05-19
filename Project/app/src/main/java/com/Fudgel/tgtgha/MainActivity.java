package com.Fudgel.tgtgha;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.Fudgel.tgtgha.Service.AppService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_REQUEST_CODE = 4004;

    //Authentication
    private List<AuthUI.IdpConfig> providers;

    //Service
    private ServiceConnection serviceConnection;
    private AppService appService;
    private Boolean bound = false;

    private ImageButton locButton;
    private ImageButton chatButton;
    private ImageButton signOutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( Build.VERSION.SDK_INT >= 23){
            if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},1 );
            }
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                requestPermissions(new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION},1 );
            }
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {

                requestPermissions(new String[]{ Manifest.permission.CAMERA},1 );
            }
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.LOCATION_HARDWARE ) != PackageManager.PERMISSION_GRANTED ) {

                requestPermissions(new String[]{ android.Manifest.permission.LOCATION_HARDWARE},1 );
            }
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED ) {

                requestPermissions(new String[]{ android.Manifest.permission.INTERNET},1 );
            }
        }

        //Setup
        initProviders();
        setID();
        clickSetup();

        //Service
        appServiceConnection();
        startService();
    }


    private void setID() {
        chatButton = findViewById(R.id.btn_chat);
        locButton = findViewById(R.id.btn_Location);
        signOutButton = findViewById(R.id.btn_logout);
    }

    private void clickSetup() {
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateLocationActivity.class);
                Log.e("ClickLsistener","called");
                startActivity(intent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                signOutButton.setEnabled(false);
                                showSignInOptions();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void appServiceConnection() {

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "AppServiceConnection: connected to service");
                appService = ((AppService.AppBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                appService = null;
            }
        };
    }


    private void initProviders() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        showSignInOptions();
    }


    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.MyTheme)
                        .build(),MY_REQUEST_CODE
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE){

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){

                signOutButton.setEnabled(true);
            }
            else{
                Toast.makeText(this,""+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startService() {
        Intent serviceIntent = new Intent(MainActivity.this, AppService.class);
        startService(serviceIntent);
        bindService();
    }

    private void bindService(){
        Intent serviceIntent = new Intent(MainActivity.this, AppService.class);
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
    }
}
