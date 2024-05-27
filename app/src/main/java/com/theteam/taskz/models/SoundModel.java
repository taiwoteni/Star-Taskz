package com.theteam.taskz.models;

import android.media.MediaPlayer;
import android.net.Uri;

public class SoundModel {
    public String name;
    public Uri url;

    public SoundModel(String name, Uri url) {
        this.name = name;
        this.url = url;
    }
}
