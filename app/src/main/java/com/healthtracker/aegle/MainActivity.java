package com.healthtracker.aegle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Pedometer pedometer;
    TextView progressNum;
    ProgressBar progressBar;
    TextView foodNum;
    ProgressBar foodCalorie;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HHmm");
    SimpleDateFormat showTimeFormat = new SimpleDateFormat("HH : mm");
    FirebaseFirestore db;
    DocumentReference userData;
    DocumentReference step;
    DocumentReference food;
    DocumentReference sleep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String date = simpleDateFormat.format(new Date());
        progressNum = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        foodNum = findViewById(R.id.calorieNum);
        foodCalorie = findViewById(R.id.calorieBar);

        TextView sleepStartNum = findViewById(R.id.sleepStartNum);
        TextView sleepEndNum = findViewById(R.id.sleepEndNum);
        TextView sleepNum = findViewById(R.id.sleepNum);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        pedometer= new Pedometer();
        pedometer.setMainActivity(this);

        Bundle bundle = getIntent().getExtras();

        if(currentUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
//            db = FirebaseFirestore.getInstance();
//            userData = db.collection("users").document(currentUser.getUid());
//            if(userData.collection(date) == null){
//                HealthData dateData = new HealthData();
//                dateData.put(date);
//            }
//            food = userData.collection(date).document("food");
//            sleep = userData.collection(date).document("sleep");
//            step = userData.collection(date).document("food");

            TextView nameText = findViewById(R.id.nameText);
            nameText.setText(currentUser.getDisplayName());

            if(bundle != null){
                Log.d("TestGetBundle", bundle.get("from").toString());
                if(bundle.get("from").toString().equals("updateFood")){
                    Log.d("TestGetBundle", "Updating Food");
                    int calorie = Integer.parseInt(bundle.get("breakfast").toString())+Integer.parseInt(bundle.get("lunch").toString())+Integer.parseInt(bundle.get("dinner").toString())+Integer.parseInt(bundle.get("snack").toString());
                    foodNum.setText(Integer.toString(calorie));
                    foodCalorie.setProgress((calorie*100)/2000);
                    Log.d("TestGetBundle", Integer.toString(calorie));
                }else if(bundle.get("from").toString().equals("updateSleep")){
                    Log.d("TestGetBundle", "Updating Sleep");
                    long diff = 0;
                    try {
                        Date sEnd = simpleTimeFormat.parse(bundle.get("sleepEnd").toString());
                        Date sStart = simpleTimeFormat.parse(bundle.get("sleepStart").toString());

                        Log.d("TestGetBundle", sEnd.toString());
                        Log.d("TestGetBundle", sStart.toString());
                        diff = sEnd.getTime() - sStart.getTime();

                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000) % 24;
                        sleepStartNum.setText(showTimeFormat.format(sStart));
                        sleepEndNum.setText(showTimeFormat.format(sEnd));
                        sleepNum.setText(Integer.toString(Math.toIntExact(diffHours))+" : "+Integer.toString(Math.toIntExact(diffMinutes)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            ImageView addFood = findViewById(R.id.foodPlus);
            ImageView addSleep = findViewById(R.id.sleepPlus);

            addFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, UpdateFoodActivity.class));
                }
            });

            addSleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, UpdateSleepActivity.class));
                }
            });

            TextView logOutButton = findViewById(R.id.logOutButton);
            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });


        }
    }

    public void updateProgress(int stepNum, int progress){
        progressNum.setText(stepNum);
        progressBar.setProgress(progress);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}