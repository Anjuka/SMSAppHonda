package com.anjukakoralage.smsapphonda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference dbref;
    private String Name = "";
    private String Tp = "";
    private LinearLayout coordinatorLayout;
    String smsName, sendNumber,lastID , ID, msg;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (LinearLayout) findViewById(R.id.coordinatorLayout);


        lastID = "0";

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {

        }
        editor = getSharedPreferences("PhoneNumberSP", MODE_PRIVATE).edit();
        editor.putString("lastID", lastID);
        editor.apply();

        //sendSMS();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        }, 0, 30000);



    }

    private void getData() {

        dbref = FirebaseDatabase.getInstance().getReference().child("user");
        Query lastQuery = dbref.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    //if you call methods on dataSnapshot it gives you the required values
                    String number = data.child("tp").getValue().toString();
                    String name = data.child("name").getValue().toString();

                    SharedPreferences prefs = getSharedPreferences("PhoneNumberSP", Activity.MODE_PRIVATE);
                    String last = prefs.getString("lastID", "");

                    if (!last.equalsIgnoreCase(number)) {
                        sendNumber = number;
                        sendSMS();
                        editor.putString("lastID", number).apply();
                        //editor.putString("lastID", name).apply();
                        coordinatorLayout.setBackgroundColor(Color.BLACK);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Same Number", Toast.LENGTH_LONG).show();
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});

    }

    public void sendSMS() {

        msg = "We honda familly, leaders in safety respectfully dedicate our honour to you.";

        //smsName = "Text Honda";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage(sendNumber, null, smsName, null, null);
            smsManager.sendTextMessage(sendNumber, null, msg, null, null);
            Toast.makeText(this, "SMS Send...", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed...", Toast.LENGTH_LONG).show();
        }


    }
}
