package com.theteam.taskz.presentation.views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.data.repositories.WorkspaceDataRepository;
import com.theteam.taskz.data.repositories.WorkspacePreferences;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;
import com.theteam.taskz.presentation.adapters.WorkspacesListAdapter;
import com.theteam.taskz.presentation.transformers.NonScrollableLinearLayoutManager;
import com.theteam.taskz.presentation.viewmodels.SplashViewModel;
import com.theteam.taskz.presentation.viewmodels.WorkspacesViewModel;
import com.theteam.taskz.utils.others.JsonUtils;
import com.theteam.taskz.utils.others.ThemeManager;

import org.json.JSONException;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CollaborationFragment extends Fragment {

    private LottieAnimationView create_workspace_lottie;
    private LinearLayout create_workspace_layout;
    private ScrollView workspace_layout;
    private RecyclerView workspaces_recycler_view;
    private WorkspacesListAdapter workspacesListAdapter;
    private EditText search_bar;
    private TextView title_text;
    private UserModel user;
    private CircleImageView profile_image;
    private SplashViewModel splashViewModel;
    private FloatingActionButton fab;

    public CollaborationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collaboration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // First we want to instantiate the views
        create_workspace_lottie = (LottieAnimationView) view.findViewById(R.id.create_workspace_lottie);
        create_workspace_layout = (LinearLayout) view.findViewById(R.id.create_workspace_layout);
        workspace_layout = (ScrollView) view.findViewById(R.id.workspace_layout);
        workspaces_recycler_view = (RecyclerView) view. findViewById(R.id.workspaces_recycler_view);
        search_bar = (EditText) view.findViewById(R.id.search_bar);
        title_text = (TextView) view.findViewById(R.id.title_text);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        profile_image = view.findViewById(R.id.profile_image);

        initializeUseCases();
        initializeUi();
        addListeners();
        addObservers();



    }
    private void initializeUseCases(){
        splashViewModel = new ViewModelProvider(requireActivity()).get(SplashViewModel.class);
        user = new UserModel(requireActivity());
    }
    private void initializeUi(){
        WorkspacePreferences workspacePreferences = new WorkspacePreferences(requireActivity());

        LinearLayoutManager layoutManager =new NonScrollableLinearLayoutManager(requireActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        workspaces_recycler_view.setLayoutManager(layoutManager);

        if(user.hasProfile()){
            Glide.with(this)
                    .load(user.profile())
                    .placeholder(R.drawable.avatar)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .error(R.drawable.avatar)
                    .addListener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            profile_image.setImageResource(R.drawable.avatar);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            profile_image.setImageDrawable(drawable);
                            profile_image.setScaleX(1f);
                            profile_image.setScaleY(1f);
                            return false;
                        }
                    })
                    .into(profile_image);
        }
        // We want to change the lottie to it's appropriate lottie when in dark mode or light mode
        if(new ThemeManager(requireActivity()).isDarkMode()){
            create_workspace_lottie.setAnimation(R.raw.create_worskpace_dark);
        }
        else{
            create_workspace_lottie.setAnimation(R.raw.create_worskpace);
        }
        create_workspace_lottie.loop(true);
        create_workspace_lottie.playAnimation();
        workspace_layout.setVisibility(View.GONE);

        // If there are locally saved workspaces:
        if(!workspacePreferences.getCachedWorkspaces().isEmpty()){
            workspace_layout.setVisibility(View.VISIBLE);
            create_workspace_layout.setVisibility(View.GONE);

            workspacesListAdapter = new WorkspacesListAdapter(workspacePreferences.getCachedWorkspaces(), requireActivity());
            workspaces_recycler_view.setAdapter(workspacesListAdapter);
            workspacesListAdapter.notifyDataSetChanged();
        }

    }
    private void addListeners(){
        fab.setOnClickListener(view1 -> {
            createWorkspace();
        });

    }

    private void addObservers(){
        WorkspaceDataRepository.getInstance().getWorkspaces().observe(getViewLifecycleOwner(), workspaces -> {
            create_workspace_layout.setVisibility(workspaces.isEmpty()?View.VISIBLE:View.GONE);
            workspace_layout.setVisibility(workspaces.isEmpty()? View.GONE:View.VISIBLE);
            if(workspacesListAdapter == null){
                workspacesListAdapter = new WorkspacesListAdapter(workspaces,requireActivity());
                workspaces_recycler_view.setAdapter(workspacesListAdapter);
                workspacesListAdapter.notifyDataSetChanged();
            }else{
                workspacesListAdapter.update(workspaces);
            }


        });
    }

    private void createWorkspace(){
        RoundedBottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(requireActivity());
        View workspaceView = requireActivity().getLayoutInflater().inflate(R.layout.create_workspace_sheet, null);
        // Configurations and listeners to be made...

        final TextInputFormField workspaceName = workspaceView.findViewById(R.id.workspace_name_form);
        final TextInputFormField workspaceDescription = workspaceView.findViewById(R.id.workspace_description_form);
        final LoadableButton createWorkspaceButton = workspaceView.findViewById(R.id.create_workspace_button);


        createWorkspaceButton.setOnClickListener(view -> {
            if(workspaceName.getText().trim().length() < 4 || workspaceDescription.getText().trim().length() < 4){
                return;
            }
            HashMap<String,Object> hash = new HashMap<>();
            hash.put("workSpaceTitle", workspaceName.getText().trim());
            hash.put("workSpaceDescription", workspaceDescription.getText().trim());
            Workspace workspace = Workspace.fromJson(new Gson().toJson(hash));
            createWorkspaceButton.startLoading();
            new Handler().postDelayed(() -> {
                createWorkspaceButton.stopLoading();
                bottomSheetDialog.dismiss();
                requireActivity().runOnUiThread(() -> {
                    splashViewModel.getHomeSplashLayout().getValue().startAnimating();
                    new WorkspaceRepository(requireActivity()).createWorkspace(
                            workspace,
                            jsonObject -> {
                                splashViewModel.getHomeSplashLayout().getValue().stopAnimating();
                                Log.v("API_RESPONSE", "Final response: " + JsonUtils.prettyPrint(jsonObject.toString()));
                                try {
                                    jsonObject.put("workSpaceDescription", workspace.workspaceDescription());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            volleyError -> {
                                splashViewModel.getHomeSplashLayout().getValue().stopAnimating();
                                Log.e("API_RESPONSE", volleyError.toString());
//                                workspacesViewModel.addWorkspace(workspace);
                            }
                    );

                });
            },2000);
        });



        bottomSheetDialog.setContentView(workspaceView);
        bottomSheetDialog.setDismissWithAnimation(true);
        bottomSheetDialog.show();
    }
}