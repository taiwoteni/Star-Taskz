package com.theteam.taskz.presentation.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.TaskManager;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.data.repositories.TasksPreferences;
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.TaskRepository;
import com.theteam.taskz.utils.enums.AccountType;
import com.theteam.taskz.utils.enums.TaskStatus;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CreateTask extends AppCompatActivity{

    private EditText taskName;
    private LinearLayout startDateLayout, startTimeLayout, endDateLayout, endTimeLayout;
    private SplashRefreshLayout refresh_layout;

    private LinearLayout workLayout, personalLayout, uncategorizedLayout,studyLayout, collaborators_list;
    private LoadableButton button;
    private TextView startDateText,startTimeText,endDateText,endTimeText,add_collaborators, titl_text;

    private TextView workText,personalText,uncategorizedText,studyText,collaborators_text;

    private String category = "uncategorized";
    private EditText collaborator;

    private Calendar startDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endCalendar = null;
    private Calendar endTime = null;
    private Workspace workspace;

    private TaskRepository taskRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());



    private UserModel user;
    private boolean fromWorkspace;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = new UserModel(this);
        fromWorkspace = getIntent().getBooleanExtra("fromWorkspace",false);
        taskRepository = new TaskRepository(getApplicationContext());
        if (fromWorkspace){
            workspace = Workspace.fromJson(getIntent().getStringExtra("taskWorkspace"));
        }

        startDateLayout = findViewById(R.id.start_date_layout);
        startTimeLayout = findViewById(R.id.start_time_layout);
        endDateLayout = findViewById(R.id.end_date_layout);
        endTimeLayout = findViewById(R.id.end_time_layout);
        startDateText = findViewById(R.id.start_date_text);
        startTimeText = findViewById(R.id.start_time_text);
        endDateText = findViewById(R.id.end_date_text);
        endTimeText = findViewById(R.id.end_time_text);
        taskName = findViewById(R.id.task_name);
        workLayout = findViewById(R.id.work_category);
        personalLayout = findViewById(R.id.personal_category);
        uncategorizedLayout = findViewById(R.id.uncategorized_category);
        studyLayout = findViewById(R.id.study_category);
        workText = findViewById(R.id.work_text);
        personalText = findViewById(R.id.personal_text);
        uncategorizedText = findViewById(R.id.uncategorized_text);
        studyText = findViewById(R.id.study_text);
        button = findViewById(R.id.loadable_button);
        add_collaborators = findViewById(R.id.add_collaborators);
        titl_text = findViewById(R.id.title_text);
        collaborators_text = findViewById(R.id.collaborators_text);
        collaborators_list = findViewById(R.id.collaborators_list);
        refresh_layout = findViewById(R.id.splash_layout);
        collaborator = findViewById(R.id.collaborator);

        findViewById(R.id.back).setOnClickListener(view -> {
            onBackPressed();
        });

        final Calendar presetTime = Calendar.getInstance();
        if (presetTime.get(Calendar.MINUTE)<55){
            presetTime.set(Calendar.MINUTE, presetTime.get(Calendar.MINUTE)+5);
        } else{
            presetTime.set(Calendar.MINUTE, (presetTime.get(Calendar.MINUTE)+5)%60);
            presetTime.set(Calendar.HOUR, presetTime.get(Calendar.HOUR)+1);
        }

        final String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(presetTime.getTime());
        startTime.setTime(presetTime.getTime());
        startTimeText.setText(time.toUpperCase());

        startDateLayout.setOnClickListener(view -> {
            showDatePickerDialog(true);
        });
        startTimeLayout.setOnClickListener(view -> {
            showTimePickerDialog(true);
        });
        endDateLayout.setOnClickListener(view -> {
            showDatePickerDialog(false);
        });
        endTimeLayout.setOnClickListener(view -> {
            showTimePickerDialog(false);
        });

        workLayout.setOnClickListener(view -> {
            category = "work";
            refreshCategories();
        });
        personalLayout.setOnClickListener(view -> {
            category = "personal";
            refreshCategories();
        });
        uncategorizedLayout.setOnClickListener(view -> {
            category = "uncategorized";
            refreshCategories();
        });
        studyLayout.setOnClickListener(view -> {
            category = "study";
            refreshCategories();
        });

        titl_text.setText("Create new " + (user.accountType() == AccountType.Business?(fromWorkspace?"Project":"Task"):"Task"));
        if(user.accountType() == AccountType.Personal || !fromWorkspace){
            collaborators_text.setVisibility(View.GONE);
            collaborators_list.setVisibility(View.GONE);
        }

        button.setOnClickListener(view -> createTask());


//        title_text = findViewById(R.id.title_text);
//        subtitle_text = findViewById(R.id.subtitle_text);
//        taskName = findViewById(R.id.task_name_form);
//        taskDate = findViewById(R.id.task_date_form);
//        taskTime = findViewById(R.id.task_time_form);
//        taskCategory = findViewById(R.id.task_category_form);
//        button = findViewById(R.id.loadable_button);

        // To check if user wants to edit a task:
        // This is done because 'data' is only passed when a user wants to edit a task.
//        if(getIntent().hasExtra("data")){
//            Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
//            HashMap<String,Object> map = new Gson().fromJson(getIntent().getStringExtra("data"), mapType);
//
//            TaskModel model = new TaskModel(map);
//            final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
//            final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
//
//            calendar = model.date;
//            taskName.setText(model.name);
//            taskTime.setText(timeFormat.format(calendar.getTime()));
//            taskDate.setText(dateFormat.format(calendar.getTime()));
//            taskCategory.setText(model.category);
//
//            title_text.setText("Edit your Task.");
//            subtitle_text.setText("Edit the following details");
//            button.setText("EDIT TASK");
//
//        }
//
//        taskDate.setOnClickListener(view -> {
//            showDatePickerDialog();
//        });
//        taskTime.setOnClickListener(view -> {
//            showTimePickerDialog();
//        });
//        taskCategory.setOnClickListener(view -> {
//            showCategoryDialog();
//        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                button.startLoading();
//                disableAll();
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        button.stopLoading();
//                        enableAll();
//                        if(taskName.getText().trim().isEmpty()){
//                            showMessage("Task Name is Empty");
//                            return;
//                        }
//                        if(taskTime.getText().trim().isEmpty()){
//                            showMessage("Time of task is Empty");
//                            return;
//                        }
//                        if(taskCategory.getText().trim().isEmpty()){
//                            showMessage("Task Category is Empty");
//                            return;
//                        }
//                        TaskManager holder = new TaskManager(CreateTask.this);
//
//                        // To check if user wants to edit a task:
//                        // This is done because 'data' is only passed when a user wants to edit a task.
//                        if(getIntent().hasExtra("data")){
//                            Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
//                            HashMap<String,Object> taskJson = new Gson().fromJson(getIntent().getStringExtra("data"), mapType);
//                            taskJson.put("name", taskName.getText().trim());
//                            taskJson.put("time", calendar.getTimeInMillis());
//                            taskJson.put("category", taskCategory.getText().trim());
//                            taskJson.put("status", TaskStatus.Pending.name());
//
//                            TaskModel model = new TaskModel(taskJson);
//
//                            holder.updateTask(model, true);
//
//                            finish();
//
//                        }
//                        else{
//                            HashMap<String,Object> taskJson = new HashMap<>();
//                            //To set the is to "#TASK-(The index the model would have when it inserted into the list)
//                            taskJson.put("id", "#TASK-" + holder.getTasks().size());
//                            taskJson.put("globalId", taskJson.get("id"));
//                            taskJson.put("name", taskName.getText().trim());
//                            taskJson.put("time", calendar.getTimeInMillis());
//                            taskJson.put("category", taskCategory.getText().trim());
//                            taskJson.put("notifId", String.valueOf((int) AlarmManager.NOTIF_ID));
//                            AlarmManager.NOTIF_ID++;
//
//                            TaskModel model = new TaskModel(taskJson);
//                            holder.addTask(model, true);
//                            finish();
//                        }
//
//
//
//
//
//                    }
//                }, 2500);
//            }
//        });


    }

    void disableAll(){
//        taskName.setEnabled(false);
//        taskCategory.setEnabled(false);
//        taskDate.setEnabled(false);
//        taskTime.setEnabled(false);
    }
    void enableAll(){
//        taskName.setEnabled(true);
//        taskCategory.setEnabled(true);
//        taskDate.setEnabled(true);
//        taskTime.setEnabled(true);
    }
    void showDatePickerDialog(boolean start){
        final TextView target = start? startDateText: endDateText;
        final Calendar startDate = Calendar.getInstance();
        final Calendar endDate = Calendar.getInstance();
        final Calendar targetCalendar = start? startDate: endDate;


        final SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());

        if(!target.getText().toString().contains("Today") && !target.getText().toString().contains("---")){
            try {
                targetCalendar.setTime(format.parse(target.getText().toString()));
                // Set the year to the current year. The SDF doesn't cover year.
                targetCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        final int year = targetCalendar.get(Calendar.YEAR);
        final int month = targetCalendar.get(Calendar.MONTH);
        final int day = targetCalendar.get(Calendar.DAY_OF_MONTH);

        final Calendar maxCal = Calendar.getInstance();
        maxCal.set(Calendar.MONTH, month ==12? 1: month+1);
        maxCal.set(Calendar.YEAR, month==12? year:year+1);


        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTask.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                final Calendar picked = Calendar.getInstance();
                final Calendar now = (Calendar) picked.clone();
                picked.set(Calendar.YEAR, _year);
                picked.set(Calendar.MONTH, _month);
                picked.set(Calendar.DAY_OF_MONTH, _day);

                final String date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(picked.getTime());

                target.setText(picked.get(Calendar.DAY_OF_YEAR)==now.get(Calendar.DAY_OF_YEAR)?"Today":date);

                setDateCal(start, picked);

                if(start){
                    endDateText.setText("---");
                    endTimeText.setText("---");
                }

            }
        }, year,month,day);
        if(!start){
            final Calendar _startDate = Calendar.getInstance();
            if(!startDateText.getText().toString().contains("Today") && !startDateText.getText().toString().contains("---")){
                try {
                    _startDate.setTime(format.parse(startDateText.getText().toString()));
                    // Set the year to the current year. The SDF doesn't cover year.
                    _startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            datePickerDialog.getDatePicker().setMinDate(_startDate.getTimeInMillis());
        }
        datePickerDialog.getDatePicker().setMaxDate(maxCal.getTimeInMillis());
        datePickerDialog.setOnCancelListener(dialogInterface -> {
            if(start){
                return;
            }
            endCalendar=null;
            target.setText("---");
        });

        datePickerDialog.show();
    }

    void showTimePickerDialog(boolean start){
        final Calendar _calendar = Calendar.getInstance();
        final int hour = _calendar.get(Calendar.HOUR);
        final int minute = _calendar.get(Calendar.MINUTE);


        final TextView target = start? startTimeText: endTimeText;

        if (!start && endCalendar == null){
            return;
        }


        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTask.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                final Calendar picked = Calendar.getInstance();
                picked.set(Calendar.HOUR_OF_DAY, hour);
                picked.set(Calendar.MINUTE, minute);

                final String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(picked.getTime()).toUpperCase();
                target.setText(time);

                setTimeCal(start, picked);

//                if(startDateText.getText().toString().equalsIgnoreCase(endDateText.getText().toString())){
//                    endTimeText.setText("---");
//                }
            }

        },hour,minute, false);
        timePickerDialog.setOnCancelListener(dialogInterface -> {
            if(start){
                return;
            }

            endTime=null;
            target.setText("---");
        });
        timePickerDialog.create();
        timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.themeColor));
        timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));

        timePickerDialog.show();
    }

    void refreshCategories(){
        workLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.redTransparent)));
        personalLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blueTransparent)));
        uncategorizedLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellowTransparent)));
        studyLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenTransparent)));

        workText.setTextColor(getResources().getColor(R.color.red));
        personalText.setTextColor(getResources().getColor(R.color.blue));
        uncategorizedText.setTextColor(getResources().getColor(R.color.yellow));
        studyText.setTextColor(getResources().getColor(R.color.themeColor));

        if(category.contains("uncategorized")){
            uncategorizedLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            uncategorizedText.setTextColor(getResources().getColor(R.color.white));
        }
        if(category.contains("work")){
            workLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            workText.setTextColor(getResources().getColor(R.color.white));
        }
        if(category.contains("personal")){
            personalLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
            personalText.setTextColor(getResources().getColor(R.color.white));
        }
        if(category.contains("study")){
            studyLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
            studyText.setTextColor(getResources().getColor(R.color.white));
        }


    }

    private static long parseStringToTime(String timeString) {
        try {
            // Try parsing without handling scientific notation
            return Long.parseLong(timeString);
        } catch (NumberFormatException e) {
            // If parsing fails, try parsing with scientific notation
            try {
                DecimalFormat df = new DecimalFormat("#");
                Number number = df.parse(timeString);
                assert number != null;
                return number.longValue();
            } catch (ParseException ex) {
                ex.printStackTrace();
                return 0; // or throw an exception as needed
            }
        }
    }

    private void addCollaborators(){
        final RoundedBottomSheetDialog roundedBottomSheetDialog = new RoundedBottomSheetDialog(this);
        View layout = getLayoutInflater().inflate(R.layout.add_collaborator_sheet, null);

        final TextInputFormField collaborator_email = layout.findViewById(R.id.collaborator_name_form);
        final LoadableButton add_collaborator = layout.findViewById(R.id.add_collaborator_button);

        add_collaborator.setOnClickListener(view -> {
            if(collaborator_email.getText().isEmpty()){
                return;
            }
            add_collaborator.startLoading();
            new Handler().postDelayed(() -> {
                add_collaborator.stopLoading();
                roundedBottomSheetDialog.dismiss();
                refresh_layout.startAnimating();
            },2500);
        });
    }

    void showMessage(final String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void searchCollaborator(final String email){
    }

    private void setDateCal(boolean start, Calendar calendar){
        if(start){
            startDate.setTime(calendar.getTime());
            return;
        }
        if (endCalendar==null){
            endCalendar = (Calendar) calendar.clone();
        } else {
            endCalendar.setTime(calendar.getTime());
        }


    }
    private void setTimeCal(boolean start, Calendar calendar){
        if(start){
            startTime.setTime(calendar.getTime());
            return;
        }
        if (endTime==null){
            endTime = (Calendar) calendar.clone();
        } else {
            endTime.setTime(calendar.getTime());
        }


    }

    private void createTask(){
        if (taskName.getText().toString().trim().isEmpty()){
            showMessage("Enter Task Name");
            return;
        }

        final Calendar now = Calendar.getInstance();
        final Calendar startCalendar = (Calendar) startDate;
        final Calendar endCalendar = (Calendar) this.endCalendar;
        final boolean hasDue = !endDateText.getText().toString().trim().equalsIgnoreCase("---");
        final boolean hasDueTime = !endTimeText.getText().toString().trim().equalsIgnoreCase("---");

        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        // Configure Time

        startCalendar.set(Calendar.HOUR, startTime.get(Calendar.HOUR));
        startCalendar.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));

        if (hasDue){
            if (!hasDueTime){
                showMessage("Select your due time");
                return;
            }
            endCalendar.set(Calendar.HOUR, endTime.get(Calendar.HOUR));
            endCalendar.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("startedAt", isoFormat.format(startCalendar.getTime()));
        if (hasDue){
            hashMap.put("endedAt", isoFormat.format(endCalendar.getTime()));
        }
        hashMap.put("taskName", taskName.getText().toString().trim());
        hashMap.put("taskCategory", category);
        hashMap.put("taskStatus", TaskStatus.Pending.name());
        if(workspace!=null){
            hashMap.put("assignedTo",collaborator.getText().toString().trim());
        }

        Task task = Task.fromJson(hashMap);

        button.startLoading();
        refresh_layout.startAnimating();
        new Handler().postDelayed(() -> {
            if(workspace != null){
                taskRepository.createTask(
                        workspace,
                        task,
                        null,
                        jsonObject -> {
                            Log.v("API_RESPONSE", JsonUtils.prettyPrint(jsonObject.toString()));
                            try {
                                jsonObject.put("alarmId", new TasksPreferences(getApplicationContext()).generateOfflineId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            new TaskManager(getApplicationContext()).addTask(Task.fromJson(jsonObject.toString()), true);
                            refresh_layout.stopAnimating();
                            runOnUiThread(() -> new Handler().postDelayed(() -> finish(),2000));

                        },
                        volleyError -> {
                            Log.v("API_RESPONSE", volleyError.toString());
                            refresh_layout.stopAnimating();
                            button.stopLoading();

                        }
                );
            } else {
                taskRepository.createTask(
                        task,
                        null,
                        jsonObject -> {
                            Log.v("API_RESPONSE", JsonUtils.prettyPrint(jsonObject.toString()));
                            try {
                                jsonObject.put("alarmId", new TasksPreferences(getApplicationContext()).generateOfflineId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            new TaskManager(getApplicationContext()).addTask(Task.fromJson(jsonObject.toString()), true);
                            refresh_layout.stopAnimating();
                            runOnUiThread(() -> new Handler().postDelayed(() -> finish(),2000));

                        },
                        volleyError -> {
                            Log.v("API_RESPONSE", volleyError.toString());
                            refresh_layout.stopAnimating();
                            button.stopLoading();

                        }
                );
            }
        }, 2000);









    }

}