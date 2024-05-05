// Generated by view binder compiler. Do not edit!
package com.theteam.taskz.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.theteam.taskz.R;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NameSectionBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final TextInputFormField dobForm;

  @NonNull
  public final TextInputFormField firstNameForm;

  @NonNull
  public final LinearLayout googleSignInButton;

  @NonNull
  public final UnderlineTextView haveAccount;

  @NonNull
  public final TextInputFormField lastNameForm;

  @NonNull
  public final LoadableButton loadableButton;

  private NameSectionBinding(@NonNull CoordinatorLayout rootView,
      @NonNull TextInputFormField dobForm, @NonNull TextInputFormField firstNameForm,
      @NonNull LinearLayout googleSignInButton, @NonNull UnderlineTextView haveAccount,
      @NonNull TextInputFormField lastNameForm, @NonNull LoadableButton loadableButton) {
    this.rootView = rootView;
    this.dobForm = dobForm;
    this.firstNameForm = firstNameForm;
    this.googleSignInButton = googleSignInButton;
    this.haveAccount = haveAccount;
    this.lastNameForm = lastNameForm;
    this.loadableButton = loadableButton;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NameSectionBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NameSectionBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.name_section, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NameSectionBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.dob_form;
      TextInputFormField dobForm = ViewBindings.findChildViewById(rootView, id);
      if (dobForm == null) {
        break missingId;
      }

      id = R.id.first_name_form;
      TextInputFormField firstNameForm = ViewBindings.findChildViewById(rootView, id);
      if (firstNameForm == null) {
        break missingId;
      }

      id = R.id.google_sign_in_button;
      LinearLayout googleSignInButton = ViewBindings.findChildViewById(rootView, id);
      if (googleSignInButton == null) {
        break missingId;
      }

      id = R.id.have_account;
      UnderlineTextView haveAccount = ViewBindings.findChildViewById(rootView, id);
      if (haveAccount == null) {
        break missingId;
      }

      id = R.id.last_name_form;
      TextInputFormField lastNameForm = ViewBindings.findChildViewById(rootView, id);
      if (lastNameForm == null) {
        break missingId;
      }

      id = R.id.loadable_button;
      LoadableButton loadableButton = ViewBindings.findChildViewById(rootView, id);
      if (loadableButton == null) {
        break missingId;
      }

      return new NameSectionBinding((CoordinatorLayout) rootView, dobForm, firstNameForm,
          googleSignInButton, haveAccount, lastNameForm, loadableButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}