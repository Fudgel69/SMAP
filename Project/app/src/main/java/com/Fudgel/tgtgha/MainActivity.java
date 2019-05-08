package com.Fudgel.tgtgha;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

//Refs:
//Authentication: https://www.youtube.com/watch?v=EO-_vwfVi7c


public class MainActivity extends AppCompatActivity {

    private Button chatButton;
    private static final int MY_REQUEST_CODE = 4004;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initProviders();
        setID();
        clickSetup();
    }


    private void setID() {
        chatButton = findViewById(R.id.btn_chat);
    }


    private void clickSetup() {
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initProviders() {
        //Init Providers:
        providers = Arrays.asList(
                //new AuthUI.IdpConfig.EmailBuilder().build(),
                //new AuthUI.IdpConfig.PhoneBuilder().build(),
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            }
        }
    }
}
