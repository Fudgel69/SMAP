package com.Fudgel.tgtgha;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreateLocationActivity extends AppCompatActivity {

    private TextView txt_addAge;
    private TextView txt_addName;
    private Button btn_Gender;
    private Button btn_Location;

    private int checkedGender;
    private String[] Genders = {"Man", "Woman", "Other"};

    private int checkedLocation;
    private String[] Locations = {"Aarhus C", "Skejby", "Aarhus N", "Aarhus S", "Aarhus V", "Viby J"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        SetupView();
        SetupClick();

    }

    private void SetupClick() {
        btn_Gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGenderOptions();
            }
        });
        btn_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocationOptions();
            }
        });
    }

    private void displayLocationOptions() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose Location");
        dialogBuilder.setSingleChoiceItems(Locations, checkedLocation,
                (dialogInterface, which) -> {
                    checkedLocation = which;
                });
        dialogBuilder.setPositiveButton("Done", (dialog, which) -> showSelectedLocation());
        dialogBuilder.create().show();
        btn_Location.setText(Locations[checkedLocation]);
    }

    private void showSelectedLocation() {
        Toast.makeText(this, "You selected: " + Locations[checkedLocation], Toast.LENGTH_SHORT).show();
    }

    private void displayGenderOptions() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose Genders");
        dialogBuilder.setSingleChoiceItems(Genders, checkedGender,
                (dialogInterface, which) -> {
                    checkedGender = which;
                });
        dialogBuilder.setPositiveButton("Done", (dialog, which) -> showSelectedGender());
        dialogBuilder.create().show();
        btn_Gender.setText(Genders[checkedGender]);
    }

    private void showSelectedGender() {
        Toast.makeText(this, "You selected: " + Genders[checkedGender], Toast.LENGTH_SHORT).show();
    }

    private void SetupView() {
        txt_addAge = findViewById(R.id.edtxt_createlocation_addAge);
        txt_addName = findViewById(R.id.edtxt_createlocation_addName);

        btn_Gender = findViewById(R.id.btn_createlocation_addGender);
        btn_Location = findViewById(R.id.btn_createlocation_addLocation);

        //set text to user current data
    }
}
