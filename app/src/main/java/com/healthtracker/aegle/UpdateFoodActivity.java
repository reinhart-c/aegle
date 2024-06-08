package com.healthtracker.aegle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateFoodActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference userData;

    Button setFood;
    TextInputLayout breakfast;
    TextInputLayout lunch;
    TextInputLayout dinner;
    TextInputLayout snack;

    String savedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_food);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        setFood = findViewById(R.id.setFoodButton);
        breakfast = findViewById(R.id.inputBreakfast);
        lunch = findViewById(R.id.inputLunch);
        dinner = findViewById(R.id.inputDinner);
        snack = findViewById(R.id.inputSnack);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            savedDate = bundle.get("savedDate").toString();
        }
        db = FirebaseFirestore.getInstance();
        userData = db.collection("users").document(currentUser.getUid()).collection(savedDate).document("food");

        userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                breakfast.getEditText().setText(task.getResult().get("breakfast").toString());
                lunch.getEditText().setText(task.getResult().get("lunch").toString());
                dinner.getEditText().setText(task.getResult().get("dinner").toString());
                snack.getEditText().setText(task.getResult().get("snack").toString());
            }
        });

        setFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Integer> foodData = new HashMap<>();
                foodData.put("breakfast", Integer.parseInt(breakfast.getEditText().getText().toString()));
                foodData.put("lunch", Integer.parseInt(lunch.getEditText().getText().toString()));
                foodData.put("dinner", Integer.parseInt(dinner.getEditText().getText().toString()));
                foodData.put("snack", Integer.parseInt(snack.getEditText().getText().toString()));
                userData.set(foodData);
                Intent i = new Intent(UpdateFoodActivity.this, MainActivity.class);
                i.putExtra("savedDate", savedDate);
                startActivity(i);
                finish();
            }
        });
    }
}