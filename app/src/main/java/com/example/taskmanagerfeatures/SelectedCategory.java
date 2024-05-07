package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectedCategory extends AppCompatActivity{
    TextView noCategoryAvailable;
    RelativeLayout addCategory;
    RecyclerView reminderRecycler;
    CategoryAdapter categoryAdapter;
    ExpandorCollapseLayout expandorCollapseLayout;
    ArrayList<CategoryModel> categoryArrayList;
    LinearLayoutManager linearLayoutManager;
    ImageView goBackSelectedCategory;
    String name, amount, colour;
    Map<String, String> remindersAmount, remindersColour;
    ItemClickListener itemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_category);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        noCategoryAvailable = findViewById(R.id.noCategoryAvailable);
        addCategory = findViewById(R.id.addCategory);
        reminderRecycler = findViewById(R.id.reminderRecycler);
        goBackSelectedCategory = findViewById(R.id.goBackSelectedCategory);
        setItemClickListener();
        ReminderDetails();

        expandorCollapseLayout = new ExpandorCollapseLayout();
        categoryArrayList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryArrayList, this, itemClickListener);
        linearLayoutManager = new LinearLayoutManager(this);
        reminderRecycler.setAdapter(categoryAdapter);
        reminderRecycler.setLayoutManager(linearLayoutManager);
        sendCategoryDetails();

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectedCategory.this, CreateCategory.class));
            }
        });

        goBackSelectedCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //expandorCollapseLayout.recreate();
                startActivity(new Intent(SelectedCategory.this, ExpandorCollapseLayout.class));
            }
        });
    }

    public void ReminderDetails(){
        remindersAmount = new HashMap<>();
        remindersAmount.put("Work", "2");
        remindersAmount.put("School", "10");
        remindersAmount.put("Food", "3");
        remindersAmount.put("Postpone", "20");

        remindersColour = new HashMap<>();
        remindersColour.put("Work", "Red");
        remindersColour.put("School", "Red");
        remindersColour.put("Food", "Lime");
        remindersColour.put("Postpone", "Pink");
    }

    public void setItemClickListener(){
        itemClickListener = new ItemClickListener() {
            @Override public void onClick(String s) {
                reminderRecycler.post(new Runnable() {
                    @Override public void run() {
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
                Toast.makeText(getApplicationContext(), "Selected : " + s, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void sendCategoryDetails(){
        for (Map.Entry<String, String> reminderValues : remindersAmount.entrySet()){
            name = reminderValues.getKey();
            amount = reminderValues.getValue();
            colour = remindersColour.get(name);

            CategoryModel model = new CategoryModel(name, amount, colour);
            model.setReminderName(name);
            model.setReminderAmount(amount);
            model.setReminderColour(colour);
            categoryArrayList.add(model);
            categoryAdapter.notifyDataSetChanged();
        }
    }
}