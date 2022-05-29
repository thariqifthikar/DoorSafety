package com.codehavok.doorsafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase fBase = FirebaseDatabase.getInstance();
    DatabaseReference DbRef = fBase.getReference();

    Button btnBuzzerSwitch;
    CardView cardLogs, cardPower, cardState, cardBuzzerSwitch;
    ImageView btnPower, btnLogs;
    TextView txtState, txtDate, txtTime,txtDeviceState;

    String LastLogMsg, LastState, LastDate, LastTime;
    long alarmState, alarmStateNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBuzzerSwitch = findViewById(R.id.btnBuzzerSwitch);

        cardLogs = findViewById(R.id.closest);
        cardPower = findViewById(R.id.cardPower);
        cardState = findViewById(R.id.cardState);
        cardBuzzerSwitch = findViewById(R.id.cardBuzzerSwitch);

        btnPower = findViewById(R.id.btnPower);
        btnLogs = findViewById(R.id.btnLogs);

        txtState = findViewById(R.id.txtState);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtDeviceState = findViewById(R.id.txtDeviceState);

        DbRef.child("Logs").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot: snapshot.getChildren()){
                    LastLogMsg = childSnapshot.getValue().toString();
                    LastState = LastLogMsg.substring(5,11);
                    LastDate = LastLogMsg.substring(LastLogMsg.lastIndexOf("on"));
                    LastTime = LastLogMsg.substring(LastLogMsg.lastIndexOf("at"),LastLogMsg.lastIndexOf("on") - 1);

                    if (LastState.equals("OPENED")){
                        txtState.setText("Door\nOpened");
                        cardState.setCardBackgroundColor(Color.parseColor("#E53935"));
                    }
                    else{
                        txtState.setText("Door\nClosed");
                        cardState.setCardBackgroundColor(Color.parseColor("#43A047"));
                    }

                    txtDate.setText(LastDate);
                    txtTime.setText(LastTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DbRef.child("Misc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alarmState = (long)snapshot.child("alarmState").getValue();
                alarmStateNow = (long)snapshot.child("alarmStateNow").getValue();

                if(alarmStateNow == 1){
                    btnBuzzerSwitch.setEnabled(true);
                    cardBuzzerSwitch.setCardBackgroundColor(Color.parseColor("#E53935"));
                }
                else{
                    btnBuzzerSwitch.setEnabled(false);
                    cardBuzzerSwitch.setCardBackgroundColor(Color.parseColor("#5C5C5C"));
                }

                if (alarmState == 1){
                    cardPower.setCardBackgroundColor(Color.parseColor("#43A047"));
                    txtDeviceState.setText("On");
                    txtDeviceState.setTextColor(Color.parseColor("#43A047"));
                }
                else{
                    cardPower.setCardBackgroundColor(Color.parseColor("#E53935"));
                    txtDeviceState.setText("Off");
                    txtDeviceState.setTextColor(Color.parseColor("#E53935"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBuzzerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbRef.child("Misc").child("alarmStateNow").setValue(0);
            }
        });

        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarmState == 1){
                    DbRef.child("Misc").child("alarmState").setValue(0);
                }
                else{
                    DbRef.child("Misc").child("alarmState").setValue(1);
                }
            }
        });

        btnLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Logs.class));
            }
        });
    }


}