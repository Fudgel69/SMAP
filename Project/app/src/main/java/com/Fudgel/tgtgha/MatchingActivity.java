package com.Fudgel.tgtgha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.Fudgel.tgtgha.Service.MatchingService;

public class MatchingActivity extends AppCompatActivity {

    Button btn_matchin_accept;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        btn_matchin_accept = findViewById(R.id.btn_matchin_accept);

        btn_matchin_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchingActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }
}
