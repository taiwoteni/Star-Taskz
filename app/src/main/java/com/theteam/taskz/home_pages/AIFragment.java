package com.theteam.taskz.home_pages;

import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ai.client.generativeai.Chat;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.SafetyRating;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.AlarmManager;
import com.theteam.taskz.BuildConfig;
import com.theteam.taskz.CreateTask;
import com.theteam.taskz.DateComparator;
import com.theteam.taskz.R;
import com.theteam.taskz.TaskManager;
import com.theteam.taskz.TaskModel;
import com.theteam.taskz.view_models.TypeWriterTextView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class AIFragment extends Fragment {

    private TextToSpeech speech;
    private LinearLayout record_button;
    private ImageView record_icon;
    private LottieAnimationView ai_lottie;

    private TypeWriterTextView ai_text;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private GenerativeModel star_taskz;
    private GenerativeModelFutures aiModel;
    private ChatFutures chat;

    private boolean isListening = false;

    private MediaPlayer recordMp, stopMp;

    private
    ArrayList<HashMap<String, String>> speeches = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ai_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        record_button = view.findViewById(R.id.record_button);
        record_icon = view.findViewById(R.id.record_icon);
        ai_lottie = view.findViewById(R.id.ai_lottie);
        ai_text = view.findViewById(R.id.ai_text);
        ai_text.setStartDelay(20);
        ai_text.setTypingInterval(50);
        ai_text.animateText("Hey there! need assistance?");
        recordMp = MediaPlayer.create(requireActivity().getApplicationContext(), R.raw.hangup);


        // Initialize speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity());
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        List<SafetySetting> safetySettings = new ArrayList<>();
        safetySettings.add(new SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH));
        safetySettings.add(new SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE));
        safetySettings.add(new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE));

        GenerationConfig generationConfig = new GenerationConfig.Builder().build();
        RequestOptions requestOptions = new RequestOptions();


        final Calendar today = Calendar.getInstance();
        star_taskz = new GenerativeModel(
                "gemini-1.5-pro-latest",
                getResources().getString(R.string.Google_AI_KEY),
                generationConfig,
                safetySettings,
                requestOptions,
                null,
                null,
                new Content.Builder()
                        .addText("Today is " + new SimpleDateFormat("EEEE, MMM dd, yyyy.", Locale.getDefault()).format(today.getTime()) + " And the current time is " + new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(today.getTime()))
                        .addText("You now know the time !")
                        .addText(getResources().getString(R.string.STAR_AI_INSTRUCTIONS)).build()
        );
        aiModel = GenerativeModelFutures.from(star_taskz);


        List<Content> chatHistory = new ArrayList<>();
        chat = aiModel.startChat(chatHistory);


        // Set recognition listener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                isListening = false;
                record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                record_icon.setImageResource(R.drawable.record);
            }

            @Override
            public void onError(int i) {
                Log.v("AI", String.valueOf(i));
                if(i == SpeechRecognizer.ERROR_AUDIO){
                    ai_text.animateText("Could not hear you :(");
                }
                if(i == SpeechRecognizer.ERROR_NO_MATCH){
                    ai_text.animateText("Try to be more clear :(");
                }
                if(i== SpeechRecognizer.ERROR_CLIENT){
                    ai_text.animateText("Something went wrong :(");
                }
                if(i == SpeechRecognizer.ERROR_NETWORK){
                    ai_text.animateText("Timeout ! :(");
                }
                isListening = false;
                record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                record_icon.setImageResource(R.drawable.record);
            }

            @Override
            public void onResults(Bundle bundle) {
                isListening = false;
                record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                record_icon.setImageResource(R.drawable.record);
                if(!isListening){
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (results != null && !results.isEmpty()) {
                        String recognizedText = results.get(0);
                        askAi(recognizedText);

                    }
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

        // Initialize speech
        speech = new TextToSpeech(requireActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    speech.setLanguage(new Locale("en", "US"));
                    final Voice voice = new Voice("en-us-x-tpf-localmale",new Locale("en", "US"), Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_LOW,true, null);
                    final int couldSetVoice = speech.setVoice(voice);

                    speech.setPitch(0.3f);
                    speech.setSpeechRate(1.5f);

                    speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onRangeStart(String utteranceId, int start, int end, int frame) {
                            super.onRangeStart(utteranceId, start, end, frame);
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ai_text.setText(speeches.get(Integer.parseInt(utteranceId)-1).get("speech").substring(0,end));

                                }
                            });
                        }

                        @Override
                        public void onDone(String s) {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(s.trim().toLowerCase(Locale.getDefault()).equals(speeches.get(speeches.size()-1).get("utteranceId"))){
                                        speechStop();
                                    }
                                }
                            });


                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                    record_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isListening){
                                stopListening();
                                return;
                            }
                            startListening();
                        }
                    });


                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(speechRecognizer != null){
            speechRecognizer.destroy();
        }
    }

    private void askAi(String question){
        ai_text.animateText("Give me a minute...");
        Content content = new Content.Builder()
                .addText(question)
                .build();


        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> aiResponse = chat.sendMessage(content);
        Futures.addCallback(aiResponse, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse generateContentResponse) {
                String response = generateContentResponse.getText();
                decodeAIResponse(response);

            }

            @Override
            public void onFailure(Throwable throwable) {
                final String message = throwable.getMessage();
                if(message.contains("safety")){
                    speechStart("I've been instructed by my creators not to attest to explicit, dangerous or private content.");
                }
                else if(throwable.getMessage().equalsIgnoreCase("Resource has been exhausted (e.g. check quota).")){
                    speechStart("Oops, an unprecedented error occurred. Please be more clear");
                }
                else {
                    speechStart(throwable.getMessage());
                }
                Log.v("AI", throwable.getMessage());
            }
        }, executor);
    }
    private void decodeAIResponse(String aiString){
        String string = aiString.trim();
        //That is if the ai didn't understand the question or fully utilize the given parameters.
//        if(string.startsWith("```json") || string.startsWith("```")){
//            speechStart("Please specify the task name and time properly");
//            return;
//        }
        int startIndex = string.indexOf("{");
        int endIndex = string.lastIndexOf("}");
        if(string.contains("{") && string.contains("}") && startIndex != -1 && endIndex != -1 && startIndex < endIndex){
            string=string.substring(startIndex,endIndex+1);
        }
        if(isJSON(string)){
            Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
            HashMap<String,Object> rawJson = new Gson().fromJson(string, mapType);
            HashMap<String,Object> taskJson = new HashMap<>();
            rawJson.forEach((key,value)->{
                taskJson.put(key.toLowerCase(), value);
            });
            if(taskJson.get("request-type").equals("create-task")){
                createTask(taskJson);
                return;
            }
            if(taskJson.get("request-type").equals("list-tasks")){
                listTasks(taskJson);
                return;
            }
            Log.v("AI", string);

        }
        else{
            speechStart(string);
        }
    }
    private void createTask(HashMap<String,Object> taskJson){
        if(!taskJson.containsKey("time") || taskJson.get("time") == null){
            speechStart("What time should i set your task?");
            return;
        }
        TaskManager holder = new TaskManager(requireActivity());

        HashMap<String,Object> _taskJson = new HashMap<>();
        //To set the is to "#TASK-(The index the model would have when it inserted into the list)

        Calendar time = Calendar.getInstance();
        //If at all the AI misbehaves and passes the date as null, we set it to today on our end also
        Calendar g = taskJson.get("date") == null? Calendar.getInstance(): getCalendarFromDate(taskJson.get("date").toString());
        time.setTimeInMillis(g.getTimeInMillis());
        Calendar h = getCalendarFromTime(taskJson.get("time").toString());
        time.set(Calendar.HOUR_OF_DAY, h.get(Calendar.HOUR_OF_DAY));
        time.set(Calendar.MINUTE, h.get(Calendar.MINUTE));
        final String now = new SimpleDateFormat("EEEE, MMM dd, HH:mm a", Locale.getDefault()).format(time.getTime());
        Log.d("AI", now);

        Calendar calendar = Calendar.getInstance();
        if(calendar.getTime().after(time.getTime())){
            speechStart("Creating a task at an expired date or time is not possible");
            return;
        }
        final String fr = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(taskJson.get("date") == null ? Calendar.getInstance().getTime(): getCalendarFromDate(taskJson.get("date").toString()).getTime());
        speechStart("Creating task : "+ "'" +  taskJson.get("name") + "' at " + taskJson.get("time") + " on " + fr);

//            //If the time given is not older or past.
//            //This is checked due to the AIs current set time
//            //If the set time is greater than the current time.
//            if(g.get(Calendar.YEAR)>=time.get(Calendar.YEAR)){
//                time.set(Calendar.YEAR, g.get(Calendar.YEAR));
//                //If the set month is greater than or equals to the current month.
//                if(g.get(Calendar.MONTH)>=time.get(Calendar.MONTH)){
//                    time.set(Calendar.MONTH, g.get(Calendar.MONTH));
//                    //If the set day is greater than or equals to the current day.
//                    if(g.get(Calendar.DAY_OF_MONTH)>=time.get(Calendar.DAY_OF_MONTH)){
//                        time.set(Calendar.DAY_OF_MONTH, g.get(Calendar.DAY_OF_MONTH));
//                    }
//                }
//            }


//            //If the time given is not older or past.
//            //This is checked due to the AIs current set time
//            if(g.get(Calendar.DAY_OF_YEAR)>=time.get(Calendar.DAY_OF_YEAR)){
//                //If the day is the same
//                if(g.get(Calendar.DAY_OF_YEAR)==time.get(Calendar.DAY_OF_YEAR)){
//                    //If the hour is equals to or after the current hour
//                    if(g.get(Calendar.HOUR_OF_DAY) >= time.get(Calendar.HOUR_OF_DAY)){
//                        time.set(Calendar.HOUR_OF_DAY, g.get(Calendar.HOUR_OF_DAY));
//                        // if the hour is equal to the current hour
//                        if(g.get(Calendar.HOUR_OF_DAY) == time.get(Calendar.HOUR_OF_DAY)){
//                            // if the minute is greater than or equals to the current minute
//                            if(g.get(Calendar.MINUTE) >= time.get(Calendar.MINUTE)){
//                                time.set(Calendar.MINUTE, g.get(Calendar.MINUTE));
//                            }
//                        }else{
//                            // We can set the minute since they are in the same day but the hour is after the current hour
//                            time.set(Calendar.MINUTE, g.get(Calendar.MINUTE));
//                        }
//                    }
//                    //We do nothing since the its the same day but the hour set has been past.
//                }
//                //else set the hour and the minute since the days are not the same but the tasks day is higher than the current day.
//                else{
//                    time.set(Calendar.HOUR_OF_DAY, g.get(Calendar.HOUR_OF_DAY));
//                    time.set(Calendar.MINUTE, g.get(Calendar.MINUTE));
//                }
//            }



        _taskJson.put("id", "#TASK-" + holder.getTasks().size());
        _taskJson.put("name", taskJson.get("name"));
        _taskJson.put("time", time.getTimeInMillis());
        _taskJson.put("category", taskJson.get("category"));
        _taskJson.put("notifId", String.valueOf((int) AlarmManager.NOTIF_ID));

        TaskModel model = new TaskModel(_taskJson);
        holder.addTask(model);

        final AlarmManager taskReminder = new AlarmManager(requireActivity().getApplicationContext(), requireActivity());
        taskReminder.setAlarm(model, false);
    }
    private void listTasks(HashMap<String,Object> criteria){
        TaskManager manager = new TaskManager(requireActivity());
        List<TaskModel> tasks = manager.getTasks();
        List<TaskModel> filteredTasks = new ArrayList<>();
        // First we filter based on date.
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());

        for(final TaskModel task: tasks){
            final Calendar dateCalendar = getCalendarFromDate(criteria.get("date").toString());
            Log.v("AI", dateFormat.format(task.date.getTime()));

            if(dateFormat.format(task.date.getTime()).equals(criteria.get("date"))){

                //if the time is not null we filter by time as well
                if(criteria.get("time") != null){
                    if(timeFormat.format(task.date.getTime()).equalsIgnoreCase(criteria.get("time").toString())){

                        //If category is null i.e (not specified meaning all) we add the task by default
                        if(criteria.get("category")==null){
                            filteredTasks.add(task);
                        }
                        // else we only add it if the category is the same with the specified category
                        else{
                            if(criteria.get("category").toString().equalsIgnoreCase(task.category)){
                                filteredTasks.add(task);
                            }
                        }
                    }
                }
                //else we go and check for the date
                else{
                    //If category is null i.e (not specified meaning all) we add the task by default
                    if(criteria.get("category")==null){
                        filteredTasks.add(task);
                    }
                    // else we only add it if the category is the same with the specified category
                    else{
                        if(criteria.get("category").toString().equalsIgnoreCase(task.category)){
                            filteredTasks.add(task);
                        }
                    }
                }

            }


        }
        filteredTasks.sort(new DateComparator());

        if(tasks.isEmpty()){
            speechStart("You have do not have any tasks");
        }
        else if(filteredTasks.isEmpty()){
            speechStart("No tasks concerning your descriptions were found");
        }
        else{
            StringBuilder str = new StringBuilder("I was able to find " + filteredTasks.size() + " task" + (filteredTasks.size() == 1 ? "" : "s") + " matching your description.\n");
            for (int i=0; i<filteredTasks.size(); i++){
                final TaskModel task = filteredTasks.get(i);
                boolean hasExpired = Calendar.getInstance().getTime().after(task.date.getTime());
                String s = String.valueOf(i+1) + ") " + filteredTasks.get(i).name + " which start" + (hasExpired? "ed":"s") + " by ";
                s = s + timeFormat.format(task.date.getTime());
                // If it is not today i want to specify the day with the text as well
                if(!dateFormat.format(task.date.getTime()).equals(criteria.get("date"))){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
                    s = s + " " + simpleDateFormat.format(task.date.getTime());
                }
                s = s + ".\n";
                str.append(s);
            }
            speechStart(str.toString());

        }

    }
    private Calendar getCalendarFromTime(String time){
        //We are using 'hh' for the hour since the AI by default uses 12 hour format
        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(Objects.requireNonNull(sdf.parse(time)));
            calendar.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        } catch (Exception e) {
            Log.e("AI", Objects.requireNonNull(e.getMessage()));
            showMessage("Something went wrong");
        }
        return calendar;

    }
    private Calendar getCalendarFromDate(String time){
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(Objects.requireNonNull(sdf.parse(time)));
            calendar.set(Calendar.YEAR, c.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, c.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        } catch (Exception e) {
            Log.e("AI", Objects.requireNonNull(e.getMessage()));
            showMessage("Something went wrong");
        }
        return calendar;

    }

    private void startListening(){
        speechStop();
        isListening = true;
        speechRecognizer.startListening(speechRecognizerIntent);
        isListening = true;
        ai_text.animateText("Listening...");
        record_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        record_icon.setImageResource(R.drawable.stop);



    }
    private void stopListening(){
        isListening = false;
        speechRecognizer.stopListening();
//        ai_text.animateText("Hold on, give me a minute");

    }
    private void speechStart(String speech){
        speak(speech);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ai_lottie.setAnimation(R.raw.call_audio_wave);
                ai_lottie.playAnimation();
            }
        });

    }
    private void speechStop(){
        speech.stop();
        ai_text.animateText("Need more assistance?");
        ai_lottie.setAnimation(R.raw.ai_not_speaking);
        ai_lottie.setColorFilter(null);
        ai_lottie.playAnimation();

    }
    private void speak(String textSpeech){
        if(speech.isSpeaking()){
            speech.stop();
        }
        speeches.clear();
        final String text = textSpeech.replaceAll("\\*", "");
        final String[] splitTexts =  text.split("\\.");


        for (int i = 0; i < splitTexts.length; i++) {
            StringBuilder resultBuilder = new StringBuilder();
            String part = splitTexts[i].trim();
            resultBuilder.append(part.trim()); // Trim to remove any leading or trailing spaces
            if (i < splitTexts.length - 1) {
                if(!splitTexts[i + 1].isEmpty()){
                    resultBuilder.append(splitTexts[i + 1].charAt(0)); // Append the first character of the next part
                }
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("speech", splitTexts[i].toString().trim());
            map.put("utteranceId", String.valueOf(speeches.size()+1));
            speeches.add(map);
        }

        for(int i=0;i<speeches.size();i++) {
            speech.speak(speeches.get(i).get("speech"), i == 0 ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, speeches.get(i).get("utteranceId"));
        }
    }

    private boolean isJSON(String jsonString) {
        Log.v("AI", jsonString);
        try {
            Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
            new Gson().fromJson(jsonString, mapType);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }
    private void speak(ArrayList<HashMap<String,String>> array){
        if(speech.isSpeaking()){
            speech.stop();
        }
        speeches.clear();
        speeches = array;
        for(int i=0;i<speeches.size();i++) {
            speech.speak(speeches.get(i).get("speech"), i == 0 ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, speeches.get(i).get("utteranceId"));
        }
    }

    void showMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
