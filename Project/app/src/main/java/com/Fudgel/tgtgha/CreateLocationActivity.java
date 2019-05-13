package com.Fudgel.tgtgha;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateLocationActivity extends AppCompatActivity {

    private TextView txt_addAge;
    private TextView txt_addName;
    private Button btn_Gender;
    private Button btn_Location;
    private ImageButton image_profile;

    private int checkedGender;
    private String[] Genders = {"Male", "Female", "Other"};
    private int checkedLocation;
    private String[] Locations = {"Aarhus C", "Skejby", "Aarhus N", "Aarhus S", "Aarhus V", "Viby J"};

    private static final int CAMERA_PIC_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

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
        image_profile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            image_profile.setImageBitmap(image);
        }
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
    }

    private void showSelectedLocation() {
        Toast.makeText(this, "You selected: " + Locations[checkedLocation], Toast.LENGTH_SHORT).show();
        btn_Location.setText(Locations[checkedLocation]);
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
    }

    private void showSelectedGender() {
        Toast.makeText(this, "You selected: " + Genders[checkedGender], Toast.LENGTH_SHORT).show();
        btn_Gender.setText(Genders[checkedGender]);
    }

    private void SetupView() {
        txt_addAge = findViewById(R.id.edtxt_createlocation_addAge);
        txt_addName = findViewById(R.id.edtxt_createlocation_addName);

        btn_Gender = findViewById(R.id.btn_createlocation_addGender);
        btn_Location = findViewById(R.id.btn_createlocation_addLocation);

        image_profile = findViewById(R.id.img_createlocation_userImage);
        //set text to user current data

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        txt_addName.setText(name);
        image_profile.setImageResource(R.drawable.ic_camera);
        String newurl = user.getPhotoUrl().toString();
        Picasso.get().load(newurl).into(image_profile);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}

