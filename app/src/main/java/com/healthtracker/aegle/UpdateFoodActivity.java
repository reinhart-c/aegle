package com.healthtracker.aegle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UpdateFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_food);

        Button setFood = findViewById(R.id.setFoodButton);
        TextInputLayout breakfast = findViewById(R.id.inputBreakfast);
        TextInputLayout lunch = findViewById(R.id.inputLunch);
        TextInputLayout dinner = findViewById(R.id.inputDinner);
        TextInputLayout snack = findViewById(R.id.inputSnack);


        setFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UpdateFoodActivity.this, MainActivity.class);
                i.putExtra("from", "updateFood");
                i.putExtra("breakfast", breakfast.getEditText().getText());
                i.putExtra("lunch", lunch.getEditText().getText());
                i.putExtra("dinner", dinner.getEditText().getText());
                i.putExtra("snack", snack.getEditText().getText());
                startActivity(i);
            }
        });
    }
}