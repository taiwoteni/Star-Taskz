package com.theteam.taskz.presentation.views;

import android.content.Context;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theteam.taskz.R;

public class RoundedBottomSheetDialog extends BottomSheetDialog {

    public RoundedBottomSheetDialog(Context context) {
        super(context, R.style.CustomBottomSheetDialog);
    }

    public RoundedBottomSheetDialog(Context context, int theme) {
        super(context, theme);
    }


}
