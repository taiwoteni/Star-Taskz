package com.example.taskmanagerfeatures;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewholder> {
    ArrayList<CategoryModel> categoryModels;
    ItemClickListener itemClickListener;
    Context context;
    SharedPreferences reminderPreference, defaultPreference;
    SharedPreferences.Editor editor;
    int selectedPosition, previousPosition;
    String colour, previousPositionName;
    Toast toast, toast2;

    public CategoryAdapter(ArrayList<CategoryModel> categoryModels, Context context, ItemClickListener itemClickListener){
        this.categoryModels = categoryModels;
        this.context = context;
        this.itemClickListener = itemClickListener;

        defaultPreference = context.getSharedPreferences("Selected_Reminder", MODE_PRIVATE);
        previousPositionName = defaultPreference.getString("category_name", "Postpone");
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        for (int i = 0; i < categoryModels.size(); i++){
            if (previousPositionName.equals(categoryModels.get(i).reminderName)){
                selectedPosition = i;
            }
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_add_category, parent, false);
        return new CategoryViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewholder holder, int position) {
        reminderPreference = context.getSharedPreferences("Selected_Reminder", MODE_PRIVATE);
        editor = reminderPreference.edit();

        holder.reminderTitle.setText(categoryModels.get(position).reminderName);
        holder.reminderAmount.setText(categoryModels.get(position).reminderAmount);
        colour = categoryModels.get(position).reminderColour;
        if (colour.equals("Red")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.redO)));
        }else if (colour.equals("Orange")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.orange)));
        }else if (colour.equals("Yellow")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.yellow)));
        }else if (colour.equals("Lime")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.lime)));
        }else if (colour.equals("Sky_Blue")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.skyBlue)));
        }else if (colour.equals("Blue")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.blue3)));
        }else if (colour.equals("Pink")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.purPink)));
        }else if (colour.equals("Purple")){
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.purple)));
        }else {
            holder.reminderColour.setColorFilter((ContextCompat.getColor(context, R.color.blue)));
        }

        holder.radioButton1.setChecked(position == selectedPosition);
        holder.radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    selectedPosition = holder.getAdapterPosition();
                    itemClickListener.onClick(categoryModels.get(position).reminderName);
                    editor.putString("category_name", categoryModels.get(position).reminderName);
                    editor.putString("category_colour", categoryModels.get(position).reminderColour);
                    editor.commit();
                }
            }
        });
    }

    @Override public long getItemId(int position) {
        return position;
    }
    @Override public int getItemViewType(int position){
        return position;
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryViewholder extends RecyclerView.ViewHolder{
        RadioButton radioButton1;
        TextView reminderTitle, reminderAmount;
        ImageView reminderColour;
        public CategoryViewholder(@NonNull View itemView) {
            super(itemView);

            radioButton1 = itemView.findViewById(R.id.radioButton1);
            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            reminderAmount = itemView.findViewById(R.id.reminderAmount);
            reminderColour = itemView.findViewById(R.id.reminderColour);
        }
    }
}
