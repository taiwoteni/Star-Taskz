// Generated by view binder compiler. Do not edit!
package com.theteam.taskz.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ViewholderMainActivityViewerPagerBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LottieAnimationView lottie;

  @NonNull
  public final LinearLayout noTaskLayout;

  @NonNull
  public final TextView noTasksText;

  @NonNull
  public final RecyclerView taskRecyclerView;

  private ViewholderMainActivityViewerPagerBinding(@NonNull LinearLayout rootView,
      @NonNull LottieAnimationView lottie, @NonNull LinearLayout noTaskLayout,
      @NonNull TextView noTasksText, @NonNull RecyclerView taskRecyclerView) {
    this.rootView = rootView;
    this.lottie = lottie;
    this.noTaskLayout = noTaskLayout;
    this.noTasksText = noTasksText;
    this.taskRecyclerView = taskRecyclerView;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ViewholderMainActivityViewerPagerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ViewholderMainActivityViewerPagerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.viewholder_main_activity_viewer_pager, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ViewholderMainActivityViewerPagerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.lottie;
      LottieAnimationView lottie = ViewBindings.findChildViewById(rootView, id);
      if (lottie == null) {
        break missingId;
      }

      id = R.id.noTaskLayout;
      LinearLayout noTaskLayout = ViewBindings.findChildViewById(rootView, id);
      if (noTaskLayout == null) {
        break missingId;
      }

      id = R.id.no_tasks_text;
      TextView noTasksText = ViewBindings.findChildViewById(rootView, id);
      if (noTasksText == null) {
        break missingId;
      }

      id = R.id.taskRecyclerView;
      RecyclerView taskRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (taskRecyclerView == null) {
        break missingId;
      }

      return new ViewholderMainActivityViewerPagerBinding((LinearLayout) rootView, lottie,
          noTaskLayout, noTasksText, taskRecyclerView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
