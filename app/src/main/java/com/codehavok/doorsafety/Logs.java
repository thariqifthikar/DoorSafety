package com.codehavok.doorsafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Logs extends AppCompatActivity {

    FirebaseDatabase fBase = FirebaseDatabase.getInstance();
    DatabaseReference DbRef = fBase.getReference();

    ArrayList<String> LogsArray;

    ImageView btnBack, btnBuzzer;
    CardView cardBack, cardBuzzer, cardIndicator;
    RecyclerView recyclerView;

    long alarmStateNow;
    String LastLogMsg,LastState;

    LogAdapter logAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        LogsArray = new ArrayList<>();

        btnBack = findViewById(R.id.btnBack);
        btnBuzzer = findViewById(R.id.btnBuzzer);
        cardBack = findViewById(R.id.cardBack);
        cardBuzzer = findViewById(R.id.cardBuzzer);
        cardIndicator = findViewById(R.id.cardIndicator);
        recyclerView = findViewById(R.id.recyclerView);

        DbRef.child("Logs").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot: snapshot.getChildren()){
                    LastLogMsg = childSnapshot.getValue().toString();
                    LastState = LastLogMsg.substring(5,11);

                    if (LastState.equals("OPENED")){
                        cardIndicator.setCardBackgroundColor(Color.parseColor("#E53935"));
                    }
                    else{
                        cardIndicator.setCardBackgroundColor(Color.parseColor("#43A047"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DbRef.child("Misc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alarmStateNow = (long)snapshot.child("alarmStateNow").getValue();

                if(alarmStateNow == 1){
                    btnBuzzer.setEnabled(true);
                    cardBuzzer.setCardBackgroundColor(Color.parseColor("#E53935"));
                }
                else{
                    btnBuzzer.setEnabled(false);
                    cardBuzzer.setCardBackgroundColor(Color.parseColor("#5C5C5C"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbRef.child("Misc").child("alarmStateNow").setValue(0);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DbRef.child("Logs").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                LogsArray.clear();

                for (DataSnapshot childSnapshot: snapshot.getChildren()){
                    LogsArray.add(childSnapshot.getValue().toString());


                }
                Collections.reverse(LogsArray);
                recyclerView.setLayoutManager(new LinearLayoutManager(Logs.this));
                logAdapter = new LogAdapter(Logs.this, LogsArray,LastLogMsg);
                logAdapter.setClickListener(null);
                recyclerView.setAdapter(logAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}