package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class CreateCategory extends AppCompatActivity {
    ImageView goBackAddCategory, colourRedShadow, colourOrangeShadow, colourYellowShadow, colourLimeShadow, colourSkyBlueShadow, colourBlueShadow, colourPinkShadow, colourPurpleShadow;
    FrameLayout colourRed, colourOrange, colourYellow, colourLime, colourSkyBlue, colourBlue, colourPink, colourPurple;
    EditText addCategoryName;
    TextView cancelAddCategory, saveAddCategory;
    SelectedCategory selectedCategory;
    String categoryName, categoryColour, categoryAmount, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        goBackAddCategory = findViewById(R.id.goBackAddCategory);
        addCategoryName = findViewById(R.id.addCategoryName);
        colourRed = findViewById(R.id.colourRed);
        colourOrange = findViewById(R.id.colourOrange);
        colourYellow = findViewById(R.id.colourYellow);
        colourLime = findViewById(R.id.colourLime);
        colourSkyBlue = findViewById(R.id.colourSkyBlue);
        colourBlue = findViewById(R.id.colourBlue);
        colourPink = findViewById(R.id.colourPink);
        colourPurple = findViewById(R.id.colourPurple);
        cancelAddCategory = findViewById(R.id.cancelAddCategory);
        saveAddCategory = findViewById(R.id.saveAddCategory);
        colourRedShadow = findViewById(R.id.colourRedShadow);
        colourOrangeShadow = findViewById(R.id.colourOrangeShadow);
        colourYellowShadow = findViewById(R.id.colourYellowShadow);
        colourLimeShadow = findViewById(R.id.colourLimeShadow);
        colourSkyBlueShadow = findViewById(R.id.colourSkyBlueShadow);
        colourBlueShadow = findViewById(R.id.colourBlueShadow);
        colourPinkShadow = findViewById(R.id.colourPinkShadow);
        colourPurpleShadow = findViewById(R.id.colourPurpleShadow);

        colourRedShadow.setVisibility(View.VISIBLE);
        Colour();

        saveAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryName = addCategoryName.getText().toString();
                categoryAmount = String.valueOf(categoryName.length());

                startActivity(new Intent(CreateCategory.this, SelectedCategory.class));
            }
        });
    }

    public void PreviousPage(View view){
        finish();
    }

    public void Colour(){
        colourRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Red";
                FrameLayoutsVisibility();
                colourRedShadow.setVisibility(View.VISIBLE);
            }
        });
        colourOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Orange";
                FrameLayoutsVisibility();
                colourOrangeShadow.setVisibility(View.VISIBLE);
            }
        });
        colourYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Yellow";
                FrameLayoutsVisibility();
                colourYellowShadow.setVisibility(View.VISIBLE);
            }
        });
        colourLime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Lime";
                FrameLayoutsVisibility();
                colourLimeShadow.setVisibility(View.VISIBLE);
            }
        });
        colourSkyBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Sky_Blue";
                FrameLayoutsVisibility();
                colourSkyBlueShadow.setVisibility(View.VISIBLE);
            }
        });
        colourBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Blue";
                FrameLayoutsVisibility();
                colourBlueShadow.setVisibility(View.VISIBLE);
            }
        });
        colourPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Pink";
                FrameLayoutsVisibility();
                colourPinkShadow.setVisibility(View.VISIBLE);
            }
        });
        colourPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryColour = "Purple";
                FrameLayoutsVisibility();
                colourPurpleShadow.setVisibility(View.VISIBLE);
            }
        });
    }

    public void FrameLayoutsVisibility(){
        colourRedShadow.setVisibility(View.INVISIBLE);
        colourOrangeShadow.setVisibility(View.INVISIBLE);
        colourYellowShadow.setVisibility(View.INVISIBLE);
        colourLimeShadow.setVisibility(View.INVISIBLE);
        colourSkyBlueShadow.setVisibility(View.INVISIBLE);
        colourBlueShadow.setVisibility(View.INVISIBLE);
        colourPinkShadow.setVisibility(View.INVISIBLE);
        colourPurpleShadow.setVisibility(View.INVISIBLE);;
    }
}