package com.Fudgel.tgtgha;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.Fudgel.tgtgha.Fragment.ChatFragment;
import com.Fudgel.tgtgha.Fragment.MapFragment;

public class ChatActivity extends AppCompatActivity {

    private Button btnMap, btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnMap = findViewById(R.id.btnMap);
        btnChat = findViewById(R.id.btnChat);

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
}
