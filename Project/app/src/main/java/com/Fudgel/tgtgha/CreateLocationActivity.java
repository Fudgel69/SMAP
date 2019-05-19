package com.Fudgel.tgtgha;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Fudgel.tgtgha.Model.User;
import com.Fudgel.tgtgha.Service.AppService;
import com.Fudgel.tgtgha.Service.MatchingService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class CreateLocationActivity extends AppCompatActivity {

    private TextView txt_addAge;
    private TextView txt_addName;
    private Button btn_Gender;
    private Button btn_Location;
    private Button btn_Search;
    private ImageButton image_profile;
    private User user;
    private boolean serviceStarted;
    private boolean bound;
    private MatchingService matchingService;
    private ServiceConnection serviceConnection;

    private String userName;
    private FirebaseUser firebaseUser;
    private String userID;
    private String userImageUrl;
    private String userGender;
    private String userAge;
    private int checkedGender;
    private int checkedLocation;
    private String[] Locations = {"Aarhus C", "Skejby", "Aarhus N", "Aarhus S", "Aarhus V", "Viby J"};

    private ProgressDialog mProgress;
    private StorageReference userimageRef;
    private DatabaseReference databaseRef;
    private FirebaseDatabase databaseUser;

    private static final int CAMERA_PIC_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        // Start Loading progress, ends after all data is loaded from DB.
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading Information...");
        mProgress.show();

        serviceStarted = false;
        bound = false;

        SetupID();
        databaseListener();
        SetupView();
        SetupClick();
    }

    private void SetupID() {

        txt_addAge = findViewById(R.id.edtxt_createlocation_addAge);
        txt_addName = findViewById(R.id.edtxt_createlocation_addName);
        btn_Gender = findViewById(R.id.btn_createlocation_addGender);
        btn_Location = findViewById(R.id.btn_createlocation_addLocation);
        btn_Search = findViewById(R.id.btn_createlocation_search);
        image_profile = findViewById(R.id.img_createlocation_userImage);
    }


    private void databaseListener() {

        databaseUser = FirebaseDatabase.getInstance();
        databaseRef = databaseUser.getReference();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                showData(snapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot){

        for(DataSnapshot ds : dataSnapshot.getChildren()){

            user = new User();
            Object userAge = ds.child(userID).child("userAge").getValue();
            Object userImage = ds.child(userID).child("userImageBitmap").getValue();
            Object userGender = ds.child(userID).child("userGender").getValue();

            // Display User age
            if (userAge != null){
                user.setUserAge(userAge.toString());
                txt_addAge.setText(user.getUserAge());
            }

            // Profile Picture:
            // sets picture til Authentication profile image
            // unless the user has saved a new image
            if (userImage != null){
                String bitmap = (userImage.toString());

                byte[] decodeString = Base64.decode(bitmap, Base64.DEFAULT);
                Bitmap decoded = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                image_profile.setImageBitmap(decoded);
            }

            // Display User Gender
            if (userGender == null || userGender == "Choose"){
                return;
            }
            else{
                user.setUserGender(userGender.toString());
                btn_Gender.setText(user.getUserGender());
            }

            mProgress.dismiss();
        }
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

                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA_PIC_REQUEST);
                    }
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
                setupServiceConnection();
                bindMatchService();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {

            Log.e("onActivityResult", "Called" + data);

            //Loading message
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Uploading Image...");
            mProgress.show();

            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap)extras.get("data");
            image_profile.setImageBitmap(photo);

            saveImage(photo);
        }
    }


    private void saveImage(Bitmap bitmap){

        Log.e("saveImage", "Called");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userImageBitmap");
        ref.setValue(imageEncoded).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgress.dismiss();
                Toast.makeText(CreateLocationActivity.this, getString(R.string.New_Profile_Picture_saved), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayLocationOptions() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.Choose_Location));
        dialogBuilder.setSingleChoiceItems(Locations, checkedLocation,
                (dialogInterface, which) -> {
                    checkedLocation = which;
                });
        dialogBuilder.setPositiveButton(getString(R.string.Done), (dialog, which) -> showSelectedLocation());
        dialogBuilder.create().show();
    }

    private void showSelectedLocation() {
        Toast.makeText(this, getString(R.string.You_Selected) + ": " + Locations[checkedLocation], Toast.LENGTH_SHORT).show();
        btn_Location.setText(Locations[checkedLocation]);
    }

    private void displayGenderOptions() {

        String [] Genders = {getString(R.string.Male), getString(R.string.Female), getString(R.string.Other)};

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.Choose_Genders));
        dialogBuilder.setSingleChoiceItems(Genders, checkedGender,
                (dialogInterface, which) -> {
                    checkedGender = which;
                });
        dialogBuilder.setPositiveButton(getString(R.string.Done), (dialog, which) -> showSelectedGender(Genders));
        dialogBuilder.create().show();
    }

    private void showSelectedGender(String[] Genders) {
        Toast.makeText(this, getString(R.string.You_Selected) + ": " + Genders[checkedGender], Toast.LENGTH_SHORT).show();
        btn_Gender.setText(Genders[checkedGender]);
    }

    private void SetupView() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance();
        databaseRef = databaseUser.getReference("Users/" + firebaseUser.getUid());


        userName = firebaseUser.getDisplayName();
        String [] names = userName.split(" ");
        txt_addName.setText(names[0]);
        //userImageUrl = firebaseUser.getPhotoUrl().toString();
        userID = firebaseUser.getUid();

        image_profile.setImageResource(R.drawable.ic_camera);

        saveUserToDatabase();
    }

    private void setUserImage() {
        userImageUrl = firebaseUser.getPhotoUrl().toString();
        Picasso.get().load(userImageUrl).into(image_profile);
    }


    private void saveUserToDatabase(){

        databaseRef.child("id").setValue(userID);
        databaseRef.child("userName").setValue(userName);
        databaseRef.child("userImageURL").setValue(userImageUrl);
    }

    private void updateUserDatabase(){

        databaseRef.child("userAge").setValue(txt_addAge.getText().toString());
        databaseRef.child("userImageURL").setValue(userImageUrl);
        databaseRef.child("userGender").setValue(btn_Gender.getText().toString());

        databaseRef.child("Route").child("Time").setValue("test");
        databaseRef.child("Route").child("Goal").setValue(Locations[checkedLocation]);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.Camera_permission_granted), Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            } else {
                Toast.makeText(this, getString(R.string.Camera_permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendLargeSizeNotification() {
        // Sets an ID for the notification
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
                .setContentTitle(getString(R.string.You_got_matched))
                .setContentText(getString(R.string.Click_to_see_your_match))
                .setChannelId("myChannel");
        Notification n = builder.getNotification();

        n.defaults |= Notification.DEFAULT_ALL;
        nm.notify(0, n);
    }

    private void setupServiceConnection() {

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                matchingService = ((MatchingService.MatchBinder) service).getService();
                if(!serviceStarted)
                {
                    Log.d("Service: ", "Trying to start matching service...");
                    matchingService.startService(new Intent(CreateLocationActivity.this, MatchingService.class));
                    serviceStarted = true;
                }
                Log.d("Success: ", "MatchService connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                serviceStarted = false;
                Log.d("Error: ", "MatchService disconnected");
            }
        };
    }


    public void bindMatchService(){
        if (!bound){
            bindService(new Intent(CreateLocationActivity.this, MatchingService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            bound = true;
        }
    }

    public void unbindMatchService() {
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
    }
}

