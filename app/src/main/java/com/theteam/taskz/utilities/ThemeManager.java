package com.theteam.taskz.utilities;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Resources;

import com.theteam.taskz.R;

public class ThemeManager {
    public int primary;
    public int secondary;
    public int tertiary;
    public int background;
    public int rootBackground;

    private Context context;
    public ThemeManager(Context context){
        Resources res = context.getApplicationContext().getResources();
        this.context = context;
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);

        if(uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES){
            //Dark Mode
            primary = res.getColor(R.color.primaryDark);
            secondary = res.getColor(R.color.secondaryDark);
            tertiary = res.getColor(R.color.tertiaryDark);
            background = res.getColor(R.color.backgroundDark);
            rootBackground = res.getColor(R.color.rootBackgroundDark);
        }else{
            //Light Mode
            primary = res.getColor(R.color.primaryLight);
            secondary = res.getColor(R.color.secondaryLight);
            tertiary = res.getColor(R.color.tertiaryLight);
            background = res.getColor(R.color.backgroundLight);
            rootBackground = res.getColor(R.color.rootBackgroundLight);
        }

    }

    public boolean isDarkMode(){
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        return uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES;
    }
}
