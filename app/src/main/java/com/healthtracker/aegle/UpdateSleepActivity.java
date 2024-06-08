package com.healthtracker.aegle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class UpdateSleepActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference userData;

    DecimalFormat timeFormat = new DecimalFormat("00");

    Button setSleep;
    TextInputLayout sleepStart;
    TextInputLayout sleepEnd;
    ImageView setSleepStart;
    ImageView setSleepEnd;

    String sleepStartTime;
    String sleepEndTime;

    String savedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sleep);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        setSleep = findViewById(R.id.setSleepButton);
        sleepStart = findViewById(R.id.inputSleepStart);
        sleepEnd = findViewById(R.id.inputSleepEnd);
        setSleepStart = findViewById(R.id.selectSleepStartButton);
        setSleepEnd = findViewById(R.id.selectSleepEndButton);
        sleepStartTime = "0000";
        sleepEndTime = "0000";

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            savedDate = bundle.get("savedDate").toString();
        }

        db = FirebaseFirestore.getInstance();
        userData = db.collection("users").document(currentUser.getUid()).collection(savedDate).document("sleep");

        userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                sleepStartTime = task.getResult().get("sleepStart").toString();
                sleepEndTime = task.getResult().get("sleepEnd").toString();
                String sleepStartTemp = sleepStartTime.substring(0,2) +" : "+sleepStartTime.substring(2,4);
                String sleepEndTemp = sleepEndTime.substring(0,2) +" : "+sleepEndTime.substring(2,4);
                sleepStart.getEditText().setText(sleepStartTemp);
                sleepEnd.getEditText().setText(sleepEndTemp);
            }
        });

        setSleepStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSleepStartDialog();
            }
        });

        setSleepEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSleepEndDialog();
            }
        });

        setSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> sleepData = new HashMap<>();
                sleepData.put("sleepStart", sleepStartTime);
                sleepData.put("sleepEnd", sleepEndTime);
                userData.set(sleepData);
                Intent i = new Intent(UpdateSleepActivity.this, MainActivity.class);
                i.putExtra("savedDate", savedDate);
                startActivity(i);
                finish();
            }
        });
    }

    public void openSleepStartDialog(){
        sleepStart = findViewById(R.id.inputSleepStart);
        TimePickerDialog setSleepStartDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                sleepStartTime = timeFormat.format(hour) + timeFormat.format(minute);
                String tempStart = timeFormat.format(hour) + " : " + timeFormat.format(minute);
                sleepStart.getEditText().setText(tempStart);
            }
        }, 00, 00, true);
        setSleepStartDialog.show();
    }

    public void openSleepEndDialog(){
        sleepEnd = findViewById(R.id.inputSleepEnd);
        TimePickerDialog setSleepEndDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                sleepEndTime = timeFormat.format(hour) + timeFormat.format(minute);
                String tempEnd = timeFormat.format(hour) + " : " + timeFormat.format(minute);
                sleepEnd.getEditText().setText(tempEnd);
            }
        }, 00, 00, true);
        setSleepEndDialog.show();
    }
}