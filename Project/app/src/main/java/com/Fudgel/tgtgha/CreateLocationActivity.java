package com.Fudgel.tgtgha;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Fudgel.tgtgha.Database.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CreateLocationActivity extends AppCompatActivity {

    private TextView txt_addAge;
    private TextView txt_addName;
    private Button btn_Gender;
    private Button btn_Location;
    private Button btn_Search;
    private ImageButton image_profile;
    private User user;

    private String userName;
    private String userID;
    private String userImageUrl;
    private String userGender;
    private String userAge;
    private int checkedGender;
    private String[] Genders = {"Male", "Female", "Other"};
    private int checkedLocation;
    private String[] Locations = {"Aarhus C", "Skejby", "Aarhus N", "Aarhus S", "Aarhus V", "Viby J"};

    private DatabaseReference databaseRef;
    private FirebaseDatabase databaseUser;

    private static final int CAMERA_PIC_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    //FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        SetupDatabase();
        SetupView();
        SetupClick();
    }


    private void SetupDatabase() {

        databaseUser = FirebaseDatabase.getInstance();
        databaseRef = databaseUser.getReference("users");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLargeSizeNotification();

                updateUserDatabase();
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
        btn_Search = findViewById(R.id.btn_createlocation_search);

        btn_Search = findViewById(R.id.btn_createlocation_search);

        image_profile = findViewById(R.id.img_createlocation_userImage);


        //set text to user current data
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userName = firebaseUser.getDisplayName();
        userID = firebaseUser.getUid();
        txt_addName.setText(userName);
        image_profile.setImageResource(R.drawable.ic_camera);
        userImageUrl = firebaseUser.getPhotoUrl().toString();
        Picasso.get().load(userImageUrl).into(image_profile);

        saveUserToDatabase();


    }



    private void saveUserToDatabase(){
        user = new User();



        if(!databaseRef.child("userAge").equals(null)){


        }


        user.setUserName(userName);
        user.setUserImageURL(userImageUrl);

        databaseRef.child("id").setValue(userID);
        databaseRef.child("userName").setValue(user.getUserName());
        databaseRef.child("userImageURL").setValue(user.getUserImageURL());

    }

    private void updateUserDatabase(){

        userAge = txt_addAge.getText().toString();
        userGender = btn_Gender.getText().toString();

        databaseUser = FirebaseDatabase.getInstance();
        databaseRef = databaseUser.getReference("users");

        user.setUserAge(userAge);
        user.setUserImageURL(userImageUrl);
        user.setUserGender(userGender);

        databaseRef.child("userAge").setValue(user.getUserAge());
        databaseRef.child("userImageURL").setValue(user.getUserImageURL());
        databaseRef.child("userGender").setValue(user.getUserGender());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendLargeSizeNotification() {
        // Sets an ID for the notification
        Toast.makeText(this, "Lets go", Toast.LENGTH_LONG).show();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("myChannel", "Visible myChannel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent notificationIntent = new Intent(getApplicationContext(), MatchingActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = getApplicationContext().getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_new_launcher)
                .setContentTitle("Message")
                .setContentText("Try this out")
                .setChannelId("myChannel");
        Notification n = builder.getNotification();

        n.defaults |= Notification.DEFAULT_ALL;
        nm.notify(0, n);
    }
}

