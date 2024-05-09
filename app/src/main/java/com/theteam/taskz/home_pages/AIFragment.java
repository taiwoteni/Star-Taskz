package com.theteam.taskz.home_pages;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.BuildConfig;
import com.theteam.taskz.R;
import com.theteam.taskz.view_models.TypeWriterTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AIFragment extends Fragment {

    private TextToSpeech speech;
    private LottieAnimationView ai_lottie,speak_lottie;

    private TypeWriterTextView ai_text;
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
        speak_lottie = view.findViewById(R.id.speak_lottie);
        ai_lottie = view.findViewById(R.id.ai_lottie);
        ai_text = view.findViewById(R.id.ai_text);
        ai_text.setStartDelay(20);
        ai_text.setTypingInterval(50);

        speeches.add(new HashMap<String, String>() {{
            put("speech", "Hi there,");
            put("utteranceId", "1");
        }});
        speeches.add(new HashMap<String, String>() {{
            put("speech", "My Name is Star Tasks.");
            put("utteranceId", "2");
        }});
        speeches.add(new HashMap<String, String>() {{
            put("speech", "Good Morning!");
            put("utteranceId", "3");
        }});
        speeches.add(new HashMap<String, String>() {{
            put("speech", "I'm an AI built to help assist with your tasks.");
            put("utteranceId", "4");
        }});
        speeches.add(new HashMap<String, String>() {{
            put("speech", "Feel free to communicate with me anytime!!");
            put("utteranceId", "5");
        }});
        speeches.add(new HashMap<String, String>() {{
            put("speech", "Star Tasks, At your service.");
            put("utteranceId", "6");
        }});


        speech = new TextToSpeech(requireActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(speech.getEngines().size() == 0){
                    showMessage("No Engines Installed");
                }
                if(i==TextToSpeech.SUCCESS){
                    speech.setLanguage(new Locale("en", "US"));
                    final Voice voice = new Voice("en-us-x-tpf-localmale",new Locale("en", "US"), Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_LOW,true, null);
                    final int couldSetVoice = speech.setVoice(voice);

                    speech.setPitch(0.3f);
                    speech.setSpeechRate(1.5f);
                    ai_text.animateText("Engage me :)");

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
                    speak_lottie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            speechStart();

//                speech.stop();
                        }
                    });


                }
            }
        });


    }

    private void speechStart(){
        speak("Manchester City Football Club has a rich history that took a significant turn with the advent of new ownership in the 21st century. The club, founded in 1880 as St. Mark's (West Gorton), underwent several name changes before settling on Manchester City in 1894. Over the years, City experienced varying degrees of success, including winning the English league title in 1937 and 1968, as well as securing FA Cup victories in 1904, 1934, and 1956.\n" +
                "However, the most transformative period in Manchester City's history began in 2008 when the club was acquired by the Abu Dhabi United Group, a conglomerate owned by Sheikh Mansour bin Zayed Al Nahyan, a member of the ruling family of Abu Dhabi. The acquisition marked the beginning of a new era of unprecedented investment and ambition for the club.\n" +
                "Under the ownership of Sheikh Mansour, Manchester City underwent a massive transformation both on and off the pitch. The club invested heavily in world-class players, managers, and state-of-the-art facilities, aiming to establish itself as a dominant force in English and European football.\n" +
                "In the 2010s, Manchester City achieved remarkable success, winning multiple Premier League titles, FA Cups, League Cups, and Community Shields. The club's success was fueled by a combination of astute managerial appointments, including Roberto Mancini, Manuel Pellegrini, and Pep Guardiola, as well as the signings of top-tier players such as Sergio Agüero, David Silva, Kevin De Bruyne, and Raheem Sterling.\n" +
                "On the European stage, Manchester City's quest for continental dominance led to consistent participation in the UEFA Champions League, with deep runs in the tournament becoming a regular occurrence. While the club faced setbacks and challenges along the way, the unwavering financial backing from the ownership ensured that Manchester City remained a formidable force in world football.\n" +
                "Off the pitch, Manchester City's investment extended beyond player transfers and stadium upgrades. The club focused on community engagement, youth development, and sustainability initiatives, aiming to have a positive impact both locally and globally.\n" +
                "Overall, the period of new ownership marked a transformative chapter in Manchester City's history, propelling the club to unprecedented heights of success and establishing it as one of the elite football institutions in the world.");

        ai_lottie.setAnimation(R.raw.call_audio_wave);
        ai_lottie.playAnimation();
    }
    private void speechStop(){
        ai_text.setText("Engage me :)");
        ai_lottie.setAnimation(R.raw.ai_not_speaking);
        ai_lottie.setColorFilter(null);
        ai_lottie.playAnimation();
    }
    private void speak(String text){
        if(speech.isSpeaking()){
            speech.stop();
        }
        speeches.clear();
        final String[] splitTexts =  text.split("[,\\\\.](?![^!]*!)");


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
            map.put("speech", resultBuilder.toString());
            map.put("utteranceId", String.valueOf(speeches.size()+1));
            speeches.add(map);
        }

        for(int i=0;i<speeches.size();i++) {
            speech.speak(speeches.get(i).get("speech"), i == 0 ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null, speeches.get(i).get("utteranceId"));
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
