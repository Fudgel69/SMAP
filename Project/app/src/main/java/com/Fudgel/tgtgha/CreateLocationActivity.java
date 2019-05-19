package com.Fudgel.tgtgha;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Fudgel.tgtgha.Database.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

<<<<<<< HEAD
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

=======
import java.io.ByteArrayOutputStream;
>>>>>>> master
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CreateLocationActivity extends AppCompatActivity {

    private TextView txt_addAge;
    private TextView txt_addName;
    private Button btn_Gender;
    private Button btn_Location;
    private Button btn_Search;
    private ImageButton image_profile;
    private User user;

    private String userName;
    private FirebaseUser firebaseUser;
    private String userID;
    private String userImageUrl;
    private String userGender;
    private String userAge;
    private int checkedGender;
    private String[] Genders = {"Male", "Female", "Other"};
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

<<<<<<< HEAD
        mProgress = new ProgressDialog(this);

=======
        SetupID();
        databaseListener();
>>>>>>> master
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

            if (ds.child(userID).child("userAge").getValue() == null){
                return;
            }
            else{
                user.setUserAge(ds.child(userID).child("userAge").getValue().toString());
                txt_addAge.setText(user.getUserAge());
            }
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
//                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
//                } else {
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//                }
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                startActivityForResult(intent, CAMERA_PIC_REQUEST);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_PIC_REQUEST);
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
                .child("userImageURL");
        ref.setValue(imageEncoded).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgress.dismiss();
                Toast.makeText(CreateLocationActivity.this, "New Profile Picture saved", Toast.LENGTH_SHORT).show();
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance();
        databaseRef = databaseUser.getReference("Users/" + firebaseUser.getUid());


        userName = firebaseUser.getDisplayName();
        String [] names = userName.split(" ");
        txt_addName.setText(names[0]);

        userID = firebaseUser.getUid();

        image_profile.setImageResource(R.drawable.ic_camera);

        setUserImage();

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

