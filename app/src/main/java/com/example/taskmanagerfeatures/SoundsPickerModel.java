package com.example.taskmanagerfeatures;

import java.util.ArrayList;

public class SoundsPickerModel {
    private int id;
    private String name;
    private static ArrayList<SoundsPickerModel> soundsPickerModelArrayList;

    public static void initSounds(){
        soundsPickerModelArrayList = new ArrayList<>();

        SoundsPickerModel ding = new SoundsPickerModel(0, "Ding");
        soundsPickerModelArrayList.add(ding);

        SoundsPickerModel radar = new SoundsPickerModel(1, "Radar");
        soundsPickerModelArrayList.add(radar);

        SoundsPickerModel alarm = new SoundsPickerModel(2, "Alarm");
        soundsPickerModelArrayList.add(alarm);

        SoundsPickerModel high_pitch = new SoundsPickerModel(3, "High Pitch");
        soundsPickerModelArrayList.add(high_pitch);

        SoundsPickerModel high_pitch_2 = new SoundsPickerModel(4, "High Pitch 2");
        soundsPickerModelArrayList.add(high_pitch_2);

        SoundsPickerModel music_box = new SoundsPickerModel(5, "Music Box");
        soundsPickerModelArrayList.add(music_box);

        SoundsPickerModel ringtone = new SoundsPickerModel(6, "Ringtone");
        soundsPickerModelArrayList.add(ringtone);

        SoundsPickerModel ringtone_new = new SoundsPickerModel(7, "Ringtone New");
        soundsPickerModelArrayList.add(ringtone_new);

        SoundsPickerModel ship_bell = new SoundsPickerModel(8, "Ship Bell");
        soundsPickerModelArrayList.add(ship_bell);
    }

    public SoundsPickerModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ArrayList<SoundsPickerModel> getSoundsPickerModelArrayList() {
        return soundsPickerModelArrayList;
    }

    public static String[] soundNames(){
        String[] names = new String[soundsPickerModelArrayList.size()];
        for (int i = 0; i < soundsPickerModelArrayList.size(); i++){
            names[i] = soundsPickerModelArrayList.get(i).name;
        }
        return names;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
