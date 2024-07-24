package com.theteam.taskz.presentation.views;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.theteam.taskz.R;
import com.theteam.taskz.data.repositories.WorkspaceDataRepository;
import com.theteam.taskz.domain.entities.Group;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;
import com.theteam.taskz.presentation.adapters.GroupsListAdapter;
import com.theteam.taskz.presentation.viewmodels.WorkspacesViewModel;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsScreen extends AppCompatActivity {

    private RecyclerView groups_recycler_view;
    private ImageView back,workspace_icon;
    private CircleImageView workspace_profile_icon;
    private TextView workspace_title;

    private GroupsListAdapter groups_list_adapter;
    private SplashRefreshLayout splash_layout;
    private FloatingActionButton fab;


    private Workspace workspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.group_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        workspace_icon = (ImageView) findViewById(R.id.workspace_icon);
        workspace_profile_icon = (CircleImageView) findViewById(R.id.workspace_profile_icon);
        workspace_title = (TextView) findViewById(R.id.name_text);
        groups_recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        splash_layout = (SplashRefreshLayout) findViewById(R.id.splash_layout);
        back = (ImageView) findViewById(R.id.back);

        initializeUseCases();
        initializeUi();
        addListeners();
        addObservers();



    }
    private void initializeUseCases(){
        workspace = Workspace.fromJson(JsonUtils.convertToHashMap(getIntent().getStringExtra("workspace")));

        if(!workspace.isCreator(getApplicationContext())){
            fab.setVisibility(View.GONE);
        }
    }

    private void initializeUi(){
        if(workspace.hasPhoto()){

            Glide.with(this)
                    .load(workspace.workspacePhoto())
                    .placeholder(R.color.workspacePlaceholder)
                    .transition(DrawableTransitionOptions.withCrossFade(1500))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            workspace_icon.setVisibility(View.GONE);
                            workspace_profile_icon.setImageDrawable(drawable);
                            return false;
                        }
                    })
                    .into(workspace_profile_icon);
        }
        workspace_profile_icon.setOnClickListener(view -> {
            final Intent intent = new Intent(getApplicationContext(), WorkspaceDescriptionScreen.class);
            intent.putExtra("workspace", getIntent().getStringExtra("workspace"));
            startActivity(intent);
        });
        fab.setOnClickListener(view -> createGroup());
        workspace_title.setText(workspace.workspaceTitle());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,true);
        groups_recycler_view.setLayoutManager(layoutManager);

        groups_list_adapter = new GroupsListAdapter(workspace.groups(), this);
        groups_recycler_view.setAdapter(groups_list_adapter);
        groups_list_adapter.notifyDataSetChanged();

    }

    private void addListeners(){
         back.setOnClickListener(view -> {
             finish();
         });
    }
    private void addObservers(){
        WorkspaceDataRepository.getInstance().getWorkspaces().observe(this, workspaces -> {
            final Workspace workspace = getWorkspace(workspaces);

            if(workspace == null){
                Toast.makeText(getApplicationContext(), "Workspace has been deleted", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // We only want to rebuild UI if the json structure changes
            if(new Gson().toJson(workspace.toJson()).equals(new Gson().toJson(this.workspace.toJson()))){
                if(groups_list_adapter != null){
                    return;
                }
            }

            this.workspace = workspace;
            if(groups_list_adapter == null){
                groups_list_adapter = new GroupsListAdapter(workspace.groups(), this);
                groups_recycler_view.setAdapter(groups_list_adapter);
                groups_list_adapter.notifyDataSetChanged();
            }
            else{
                groups_list_adapter.update(workspace.groups());
            }
        });
    }

    private Workspace getWorkspace(ArrayList<Workspace> workspaces){
        Workspace obj = null;
        final String workspaceId = this.workspace.workspaceId();
        for(final Workspace workspace:workspaces){
            if(workspace.workspaceId().trim().equals(workspaceId.trim())){
                obj = workspace;
                break;
            }
        }
        return obj;
    }

    private void createGroup(){
        RoundedBottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(this);
        View workspaceView = getLayoutInflater().inflate(R.layout.create_group_sheet, null);
        // Configurations and listeners to be made...

        final TextInputFormField workspaceName = workspaceView.findViewById(R.id.workspace_name_form);
        final TextInputFormField workspaceDescription = workspaceView.findViewById(R.id.workspace_description_form);
        final LoadableButton createWorkspaceButton = workspaceView.findViewById(R.id.create_workspace_button);
//        createWorkspaceButton.setEnabled(false);


        createWorkspaceButton.setOnClickListener(view -> {
            if(workspaceName.getText().trim().length() < 4 || workspaceDescription.getText().trim().length() < 4){
                return;
            }
            HashMap<String,Object> hash = new HashMap<>();
            hash.put("groupName", workspaceName.getText().trim());
            hash.put("groupDescription", workspaceDescription.getText().trim());
            Group group = Group.fromJson(hash);
            createWorkspaceButton.startLoading();
            new Handler().postDelayed(() -> {
                createWorkspaceButton.stopLoading();
                bottomSheetDialog.dismiss();
                runOnUiThread(() -> {
                    splash_layout.startAnimating();
                    new WorkspaceRepository(this).createGroup(
                            group,
                            workspace,
                            jsonObject -> {
                                splash_layout.stopAnimating();
                                Log.v("API_RESPONSE", "Final response: " + JsonUtils.prettyPrint(jsonObject.toString()));
                            },
                            volleyError -> {
                                splash_layout.stopAnimating();
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