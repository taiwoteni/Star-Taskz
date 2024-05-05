// Generated by view binder compiler. Do not edit!
package com.theteam.taskz.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LoadableButtonBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView buttonText;

  @NonNull
  public final LottieAnimationView loadingLottie;

  private LoadableButtonBinding(@NonNull LinearLayout rootView, @NonNull TextView buttonText,
      @NonNull LottieAnimationView loadingLottie) {
    this.rootView = rootView;
    this.buttonText = buttonText;
    this.loadingLottie = loadingLottie;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LoadableButtonBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LoadableButtonBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.loadable_button, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LoadableButtonBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_text;
      TextView buttonText = ViewBindings.findChildViewById(rootView, id);
      if (buttonText == null) {
        break missingId;
      }

      id = R.id.loading_lottie;
      LottieAnimationView loadingLottie = ViewBindings.findChildViewById(rootView, id);
      if (loadingLottie == null) {
        break missingId;
      }

      return new LoadableButtonBinding((LinearLayout) rootView, buttonText, loadingLottie);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
