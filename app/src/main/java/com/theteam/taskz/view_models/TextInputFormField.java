package com.theteam.taskz.view_models;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.theteam.taskz.R;

public class TextInputFormField extends LinearLayout {
    private ImageView prefixIcon, suffixIcon;

    //boolean value to know if password is currently visible (readable) or not
    private boolean passwordVisible = false;
    private EditText editText;

    public TextInputFormField(Context context) {
        super(context);
        init(null);
    }

    public TextInputFormField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    private void init(AttributeSet attr){

        LayoutInflater.from(getContext()).inflate(R.layout.text_form_field, this, true);


        // Find views within the layout and configure theme

        prefixIcon = (ImageView) findViewById(R.id.prefix_icon);
        suffixIcon = (ImageView) findViewById(R.id.suffix_icon);
        editText = (EditText) findViewById(R.id.edit_text);

        // That is, if there's need to initialize views
        if(attr != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.TextInputFormField);

            final Drawable prefixDrawable = typedArray.getDrawable(R.styleable.TextInputFormField_prefixIcon);
            final String hint = typedArray.getString(R.styleable.TextInputFormField_hint);
            final int hintColor = typedArray.getColor(R.styleable.TextInputFormField_hintColor, Color.parseColor("#FF000000"));

            //To get know if it is a password edit text or not
            final boolean isPasswordType = typedArray.getBoolean(R.styleable.TextInputFormField_isPassword, false);
            //To get thr input type
            final int inputType = typedArray.getInt(R.styleable.TextInputFormField_android_inputType, InputType.TYPE_CLASS_TEXT);
            editText.setHint(hint);
            editText.setHintTextColor(hintColor);
            editText.setInputType(inputType);
            if(prefixDrawable == null){
                prefixIcon.setVisibility(View.GONE);
            }
            else{
                prefixIcon.setImageDrawable(prefixDrawable);
            }

            // If it is not a password edit text, we do not want to show the suffix
            // icon.
            if(!isPasswordType){
                if(suffixIcon != null){
                    suffixIcon.setVisibility(View.GONE);
                }
            }
            else{

                // by default we set the password type to non-readable mode
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                suffixIcon.setOnClickListener(view -> {
                    if(passwordVisible){

                        // if password is readable, we make it unreadable
                        // and change the icon to the icon that is for "read mode"
                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                        suffixIcon.setImageDrawable(getResources().getDrawable(R.drawable.eyes_opened));
                    }
                    else{

                        // if password is not readable, we make it readable
                        // and change the icon to the icon that is for "not-read mode"
                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_CLASS_TEXT);
                        suffixIcon.setImageDrawable(getResources().getDrawable(R.drawable.eyes_closed));
                    }

                    // Then we switch the passwordVisible variable back to
                    // It's inverse form
                    passwordVisible = !passwordVisible;
                });

            }



            typedArray.recycle();

        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(suffixIcon != null){
            suffixIcon.setEnabled(enabled);
        }
        editText.setEnabled(enabled);
    }

    public String getText(){
        return editText.getText().toString().trim();
    }
    public void setText(String text){
        editText.setText(text);
    }
}
