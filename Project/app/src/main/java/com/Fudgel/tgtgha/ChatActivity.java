package com.Fudgel.tgtgha;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.Fudgel.tgtgha.Fragment.ChatFragment;
import com.Fudgel.tgtgha.Fragment.MapFragment;
import com.Fudgel.tgtgha.Service.AppService;

public class ChatActivity extends AppCompatActivity {

    private AppService appService;
    private ServiceConnection appServiceConnection;

    private Button btnMap, btnChat;

    private Boolean bound;
    private Boolean serviceStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnMap = findViewById(R.id.btnMap);
        btnChat = findViewById(R.id.btnChat);

        bound = false;
        serviceStarted = false;

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new MapFragment(), false, "one");
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ChatFragment(), false, "one");
            }
        });

        setupServiceConnection();
        bindAppService();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (bound) { unbindAppService(); }
    }

    public void replaceFragment(Fragment fragment, boolean backStack, String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (backStack) {
            ft.addToBackStack(tag);
        }

        ft.replace(R.id.layoutFragment, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    private void setupServiceConnection() {

        appServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                appService = ((AppService.AppBinder) service).getService();
                if(!serviceStarted)
                {
                    Log.d("Service: ", "Trying to start service...");
                    appService.startService(new Intent(ChatActivity.this, AppService.class));
                    serviceStarted = true;
                }
                Log.d("Success: ", "AppService connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                bound = false;
                Log.d("Error: ", "AppService disconnected");
            }
        };
    }

    public void bindAppService(){
        if (!bound){
            bindService(new Intent(ChatActivity.this, AppService.class), appServiceConnection, Context.BIND_AUTO_CREATE);
            bound = true;
        }
    }

    public void unbindAppService() {
        if (bound) {
            unbindService(appServiceConnection);
            bound = false;
        }
    }
}
