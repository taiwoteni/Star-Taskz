package com.example.taskmanagerfeatures;

public class CategoryModel {
    String reminderName, reminderAmount, reminderColour;

    public CategoryModel(String reminderName, String reminderAmount, String reminderColour) {
        this.reminderName = reminderName;
        this.reminderAmount = reminderAmount;
        this.reminderColour = reminderColour;
    }

    public CategoryModel(String reminderName, String reminderColour) {
        this.reminderName = reminderName;
        this.reminderColour = reminderColour;
    }

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public String getReminderAmount() {
        return reminderAmount;
    }

    public void setReminderAmount(String reminderAmount) {
        this.reminderAmount = reminderAmount;
    }

    public String getReminderColour() {
        return reminderColour;
    }

    public void setReminderColour(String reminderColour) {
        this.reminderColour = reminderColour;
    }
}
