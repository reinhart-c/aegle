package com.healthtracker.aegle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    DocumentReference userData;

    DecimalFormat timeFormat = new DecimalFormat("00");

    TextView nameText;
    TextView progressNum;
    ProgressBar progressBar;
    ImageView calendarButton;
    ImageView nextDayButton;
    ImageView previousDayButton;
    ImageView addFood;
    ImageView addSleep;
    TextView foodNum;
    ProgressBar foodCalorie;
    TextView sleepStartNum;
    TextView sleepEndNum;
    TextView sleepNum;
    TextView pickedDay;
    TextView pickedDate;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat theYear = new SimpleDateFormat("yyyy");
    SimpleDateFormat theMonth = new SimpleDateFormat("MM");
    SimpleDateFormat theDay = new SimpleDateFormat("dd");
    SimpleDateFormat showDateMainFormat = new SimpleDateFormat("dd MMMM yyyy");
    SimpleDateFormat showDayMainFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HHmm");
    SimpleDateFormat showTimeFormat = new SimpleDateFormat("HH : mm");

    String date;
    int stepCount;
    SensorManager sensorManager;
    Sensor stepCounterSensor;
    StepDetector stepDetector;
    Sensor accel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] { Manifest.permission.ACTIVITY_RECOGNITION}, 1002);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey("savedDate") && bundle.get("savedDate") != null){
            date = bundle.get("savedDate").toString();
        }else{
            date = simpleDateFormat.format(new Date());
        }

        nextDayButton = findViewById(R.id.nextDayButton);
        previousDayButton = findViewById(R.id.previousDayButton);
        calendarButton = findViewById(R.id.calendarButton);

        progressNum = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        foodNum = findViewById(R.id.calorieNum);
        foodCalorie = findViewById(R.id.calorieBar);

        sleepStartNum = findViewById(R.id.sleepStartNum);
        sleepEndNum = findViewById(R.id.sleepEndNum);
        sleepNum = findViewById(R.id.sleepNum);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            getDataFromFirebase(date);

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            stepDetector = new StepDetector();
            stepDetector.registerListener(this);

            nameText = findViewById(R.id.nameText);
            nameText.setText(currentUser.getDisplayName());

            addFood = findViewById(R.id.foodPlus);
            addSleep = findViewById(R.id.sleepPlus);

            calendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCalendar();
                }
            });

            nextDayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    date = Integer.toString(Integer.parseInt(date)+1);
                    getDataFromFirebase(date);
                }
            });

            previousDayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    date = Integer.toString(Integer.parseInt(date)-1);
                    getDataFromFirebase(date);
                }
            });

            addFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, UpdateFoodActivity.class);
                    i.putExtra("savedDate", date);
                    startActivity(i);
                    finish();
                }
            });

            addSleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, UpdateSleepActivity.class);
                    i.putExtra("savedDate", date);
                    startActivity(i);
                    finish();
                }
            });

            TextView logOutButton = findViewById(R.id.logOutButton);
            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
    }

    public void updateDate(){
        pickedDay = findViewById(R.id.dayName);
        try {
            pickedDay.setText(showDayMainFormat.format(simpleDateFormat.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pickedDate = findViewById(R.id.dateText);
        try {
            pickedDate.setText(showDateMainFormat.format(simpleDateFormat.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        addFood = findViewById(R.id.foodPlus);
        addSleep = findViewById(R.id.sleepPlus);
        if(Integer.parseInt(simpleDateFormat.format(new Date())) < Integer.parseInt(date)){
            addSleep.setEnabled(false);
            addFood.setEnabled(false);
            addSleep.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.disabled_add_button)));
            addFood.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.disabled_add_button)));
        }else{
            addSleep.setEnabled(true);
            addFood.setEnabled(true);
            addSleep.setImageTintList(null);
            addFood.setImageTintList(null);
        }
    }

    public void openCalendar(){
        Date useDate = null;
        try {
            useDate = simpleDateFormat.parse(date);
            int useYear = Integer.parseInt(theYear.format(useDate));
            int useMonth = Integer.parseInt(theMonth.format(useDate));
            int useDay = Integer.parseInt(theDay.format(useDate));
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    date = Integer.toString(year) + timeFormat.format(month) + timeFormat.format(day);
                    getDataFromFirebase(date);
                }
            }, useYear, useMonth, useDay);
            datePickerDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateProgress(int stepNum){
        progressNum.setText(Integer.toString(stepNum));
        int progress = (stepNum*100)/10000;
        progressBar.setProgress(progress);
    }

    public void updateFood(int calories){
        foodNum.setText(Integer.toString(calories));
        foodCalorie.setProgress((calories*100)/2000);
    }

    public void updateSleep(String sleepStart, String sleepEnd){
        long diff = 0;
        try {
            Date sEnd = simpleTimeFormat.parse(sleepEnd);
            Date sStart = simpleTimeFormat.parse(sleepStart);

            Log.d("TestGetBundle", sEnd.toString());
            Log.d("TestGetBundle", sStart.toString());
            diff = sEnd.getTime() - sStart.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            sleepStartNum.setText(showTimeFormat.format(sStart));
            sleepEndNum.setText(showTimeFormat.format(sEnd));
            sleepNum.setText(timeFormat.format(Math.toIntExact(diffHours))+" : "+timeFormat.format(Math.toIntExact(diffMinutes)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getDataFromFirebase(String date){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userData = db.collection("users").document(currentUser.getUid());
        userData.collection(date).document("sleep").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    Log.d("TestDB", "db not null, reading data");
                    userData.collection(date).document("step").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            stepCount = Integer.parseInt(task.getResult().getData().get("progress").toString());
                            updateProgress(stepCount);
                        }
                    });
                    userData.collection(date).document("food").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Map<String, Object> foodDatas = task.getResult().getData();
                            updateFood(Integer.parseInt(foodDatas.get("breakfast").toString())
                                    +Integer.parseInt(foodDatas.get("lunch").toString())
                                    +Integer.parseInt(foodDatas.get("dinner").toString())
                                    +Integer.parseInt(foodDatas.get("snack").toString()));
                        }
                    });
                    userData.collection(date).document("sleep").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            updateSleep(task.getResult().getData().get("sleepStart").toString(), task.getResult().getData().get("sleepEnd").toString());
                        }
                    });
                }else {
                    Log.d("TestDB", "db null, creating data");
                    Map<String, Integer> foodData = new HashMap<>();
                    foodData.put("breakfast", 0);
                    foodData.put("lunch", 0);
                    foodData.put("dinner", 0);
                    foodData.put("snack", 0);
                    userData.collection(date).document("food").set(foodData);

                    Map<String, String> sleepData = new HashMap<>();
                    sleepData.put("sleepStart", "0000");
                    sleepData.put("sleepEnd", "0000");
                    userData.collection(date).document("sleep").set(sleepData);

                    Map<String, Integer> stepData = new HashMap<>();
                    stepData.put("progress", 0);
                    userData.collection(date).document("step").set(stepData);

                    updateProgress(0);
                    updateFood(0);
                    updateSleep("0000", "0000");
                }
            }
        });
        updateDate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(stepCounterSensor != null){
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(stepCounterSensor != null){
            sensorManager.unregisterListener(this);
        }
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNs) {
        if(date.equals(simpleDateFormat.format(new Date()))){
            stepCount++;
            db = FirebaseFirestore.getInstance();
            DocumentReference userDataSensor = db.collection("users").document(currentUser.getUid()).collection(date).document("step");
            Map<String, Integer> stepData = new HashMap<>();
            stepData.put("progress", stepCount);
            userDataSensor.set(stepData);
            updateProgress(stepCount);
        }
    }
}