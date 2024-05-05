// Generated by view binder compiler. Do not edit!
package com.theteam.taskz.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.theteam.taskz.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class TaskRecyclerItemBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView ageTxt;

  @NonNull
  public final ImageView checkBtn;

  @NonNull
  public final TextView nameTxt;

  @NonNull
  public final TextView placeTxt;

  private TaskRecyclerItemBinding(@NonNull LinearLayout rootView, @NonNull TextView ageTxt,
      @NonNull ImageView checkBtn, @NonNull TextView nameTxt, @NonNull TextView placeTxt) {
    this.rootView = rootView;
    this.ageTxt = ageTxt;
    this.checkBtn = checkBtn;
    this.nameTxt = nameTxt;
    this.placeTxt = placeTxt;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static TaskRecyclerItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static TaskRecyclerItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.task_recycler_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static TaskRecyclerItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ageTxt;
      TextView ageTxt = ViewBindings.findChildViewById(rootView, id);
      if (ageTxt == null) {
        break missingId;
      }

      id = R.id.checkBtn;
      ImageView checkBtn = ViewBindings.findChildViewById(rootView, id);
      if (checkBtn == null) {
        break missingId;
      }

      id = R.id.nameTxt;
      TextView nameTxt = ViewBindings.findChildViewById(rootView, id);
      if (nameTxt == null) {
        break missingId;
      }

      id = R.id.placeTxt;
      TextView placeTxt = ViewBindings.findChildViewById(rootView, id);
      if (placeTxt == null) {
        break missingId;
      }

      return new TaskRecyclerItemBinding((LinearLayout) rootView, ageTxt, checkBtn, nameTxt,
          placeTxt);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
