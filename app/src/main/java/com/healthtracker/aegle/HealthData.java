package com.healthtracker.aegle;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HealthData {
    Map<String, Object> food;
    Map<String, Object> sleep;
    Map<String, Object> step;

    protected void onCreate(){
        food = new HashMap<>();
        sleep = new HashMap<>();
        step = new HashMap<>();

        food.put("breakfast", 0);
        food.put("lunch", 0);
        food.put("dinner", 0);
        food.put("snack", 0);

        sleep.put("sleepStart", "0000");
        sleep.put("sleepEnd", "0000");

        step.put("progress", 0);
    }

    public void setFood(int breakfast, int lunch, int dinner, int snack){
        food.put("breakfast", breakfast);
        food.put("lunch", lunch);
        food.put("dinner", dinner);
        food.put("snack", snack);
    }

    public void setSleep(String sleepStart, String sleepEnd){
        sleep.put("sleepStart", sleepStart);
        sleep.put("sleepEnd", sleepEnd);
    }

    public void setStep(int progress){
        step.put("progress", progress);
    }
}
