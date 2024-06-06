package com.healthtracker.aegle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class UpdateSleepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sleep);

        Button setSleep = findViewById(R.id.setSleepButton);
        TextInputLayout sleepStart = findViewById(R.id.inputSleepStart);
        TextInputLayout sleepEnd = findViewById(R.id.inputSleepEnd);

        setSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UpdateSleepActivity.this, MainActivity.class);
                i.putExtra("from", "updateSleep");
                i.putExtra("sleepStart", sleepStart.getEditText().getText());
                i.putExtra("sleepEnd", sleepEnd.getEditText().getText());
                startActivity(i);
            }
        });

    }
}