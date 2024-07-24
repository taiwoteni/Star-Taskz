package com.theteam.taskz.presentation.views;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.L;
import com.android.volley.NetworkResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.data.repositories.WorkspaceDataRepository;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.UserRepository;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;
import com.theteam.taskz.presentation.adapters.ViewPagerAdapter;
import com.theteam.taskz.utils.others.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkspaceDescriptionScreen extends AppCompatActivity {

    private String imagePath;

    final private int PERMISSION_CODE = 100;
    final private int PICK_IMAGE_REQ = 1;

    private ViewPager2 view_pager;
    private ImageView back_icon,leave, edit_group, add_member, workspace_icon,edit_icon;

    private FloatingActionButton fab;

    private TabLayout tabLayout;

    private CircleImageView workspace_profile;
    private TextView workspace_name, workspace_description, groups, members, tasks;

    private Workspace workspace;
    private WorkspaceRepository workspaceRepository;
    private SplashRefreshLayout refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workspace_description_screen);

        workspaceRepository = new WorkspaceRepository(getApplicationContext());

        workspace = Workspace.fromJson(JsonUtils.convertToHashMap(getIntent().getStringExtra("workspace")));

        workspace_name = findViewById(R.id.workspace_name);
        workspace_description = findViewById(R.id.workspace_description);
        groups = findViewById(R.id.groups);
        members = findViewById(R.id.members);
        tasks = findViewById(R.id.projects);
        workspace_profile = findViewById(R.id.workspace_profile_icon);
        workspace_icon = findViewById(R.id.workspace_icon);
        back_icon = findViewById(R.id.back);
        add_member = findViewById(R.id.member_icon);
        edit_icon = findViewById(R.id.sub_icon);
        leave = findViewById(R.id.logout_icon);
        edit_group = findViewById(R.id.edit_icon);
        tabLayout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);
        refresh_layout = findViewById(R.id.main);
        fab = findViewById(R.id.fab);

        showDetails();

        tabLayout.getTabAt(0).setText("Projects");
        tabLayout.getTabAt(1).setText("Members");
        tabLayout.getTabAt(2).setText("Groups");

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateTask.class);
            intent.putExtra("fromWorkspace", true);
            intent.putExtra("taskWorkspace", new Gson().toJson(workspace.toJson()));
            startActivity(intent);
        });



        back_icon.setOnClickListener(view -> {
            finish();
        });
        Log.e("API_RESPONSE", "W : "+ workspace.workspaceId());


        edit_group.setOnClickListener(view -> editWorkspace());
        leave.setOnClickListener(view -> leaveGroup());
        add_member.setOnClickListener(view -> addMember());
        workspace_profile.setOnClickListener(view -> editProfile());

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new ProjectsTabFragment());
        fragments.add(new MembersTabFragment(workspace));
        fragments.add(new GroupsTabFragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        view_pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        view_pager.setCurrentItem(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition(), true);

            }
        });
        view_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position){
                tabLayout.getTabAt(position).select();
            }
        });

        WorkspaceDataRepository.getInstance().getWorkspaces().observe(this, workspaces -> {
            for (final Workspace workspace : workspaces){
                if(workspace.workspaceId().equals(this.workspace.workspaceId())){
                    this.workspace = workspace;
                    showDetails();
                    break;
                }
            }
        });


    }
    private void showDetails(){
        edit_icon.setColorFilter(Color.WHITE);
        edit_icon.setImageResource(workspace.hasPhoto()? R.drawable.edit_icon:R.drawable.add_icon);
        workspace_name.setText(workspace.workspaceTitle());
        workspace_description.setText(workspace.workspaceDescription());
        groups.setText(String.valueOf(workspace.groups().size()));
        members.setText(String.valueOf(workspace.teamMembers().size()));
        tasks.setText("0");
        if(!workspace.isCreator(getApplicationContext())){
            edit_icon.setVisibility(View.GONE);
            edit_group.setVisibility(View.GONE);
        }

        if(workspace.hasPhoto()){
            Glide.with(this)
                    .load(workspace.workspacePhoto())
                    .placeholder(R.color.workspacePlaceholder)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            workspace_icon.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(workspace_profile);
        }
    }

    private void editProfile(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQ);
    }

    private void addMember(){
        final UserRepository userRepository = new UserRepository(getApplicationContext());

        final RoundedBottomSheetDialog dialog = new RoundedBottomSheetDialog(this);
        final View groupView = getLayoutInflater().inflate(R.layout.add_collaborator_sheet,null);
        final TextInputFormField memberEmail = groupView.findViewById(R.id.collaborator_name_form);
        final LoadableButton addMemberButton = groupView.findViewById(R.id.add_collaborator_button);
        final TextView titleText = groupView.findViewById(R.id.title_text);

        addMemberButton.setOnClickListener(view -> {
            addMemberButton.startLoading();
            userRepository.getUserByMail(
                    memberEmail.getText().trim(),
                    jsonObject -> {
                        Log.v("API_RESPONSE", jsonObject.toString());
                        dialog.dismiss();
                        refresh_layout.startAnimating();
                        final String id = jsonObject.optString("id", "");

                        workspaceRepository.addMemberToWorkspace(
                                workspace,
                                id,
                                jsonObject1 -> {
                                    Log.v("API_RESPONSE", jsonObject1.toString());
                                    members.setText(String.valueOf(workspace.teamMembers().size()+1));
                                    refresh_layout.stopAnimating();
                                },
                                volleyError -> {
                                    Log.v("API_RESPONSE", volleyError.toString());
                                }
                        );
                    },
                    volleyError -> {
                        ((TextView)groupView.findViewById(R.id.warning)).setTextColor(getColor(R.color.red));
                        Log.v("API_RESPONSE", volleyError.toString());

                    }
            );
        });

        dialog.setContentView(groupView);
        dialog.setDismissWithAnimation(true);
        dialog.show();






    }

    private void editWorkspace(){
        final RoundedBottomSheetDialog dialog = new RoundedBottomSheetDialog(this);
        final View workspaceView = getLayoutInflater().inflate(R.layout.create_workspace_sheet,null);

        final TextInputFormField workspaceName = workspaceView.findViewById(R.id.workspace_name_form);
        final TextInputFormField workspaceDescription = workspaceView.findViewById(R.id.workspace_description_form);
        final LoadableButton createWorkspaceButton = workspaceView.findViewById(R.id.create_workspace_button);
        final TextView titleText = workspaceView.findViewById(R.id.title_text);

        titleText.setText("Edit Workspace");
        workspaceName.setText(workspace.workspaceTitle());
        workspaceDescription.setText(workspace.workspaceDescription());
        createWorkspaceButton.setText("Edit");

        createWorkspaceButton.setOnClickListener(view -> {
            if(workspaceName.getText().trim().length() < 4 || workspaceDescription.getText().trim().length() < 4){
                return;
            }
            HashMap<String,Object> hash = workspace.toJson();
            hash.replace("workSpaceTitle", workspaceName.getText().trim());
            hash.replace("workSpaceDescription", workspaceDescription.getText().trim());
            Workspace workspace = Workspace.fromJson(new Gson().toJson(hash));

            createWorkspaceButton.startLoading();
            refresh_layout.startAnimating();
            dialog.dismiss();
            workspaceRepository.editWorkspace(
                    workspace,
                    jsonObject -> {
                        Log.v("API_RESPONSE", JsonUtils.prettyPrint(jsonObject.toString()));
                        final Workspace newWorkspace = Workspace.fromJson(jsonObject.toString());
                        this.workspace = newWorkspace;
                        this.workspace_name.setText(newWorkspace.workspaceTitle());
                        this.workspace_description.setText(newWorkspace.workspaceDescription());

                        refresh_layout.stopAnimating();


                    },
                    volleyError -> {
                        Log.e("API_RESPONSE", volleyError.toString());
                        refresh_layout.stopAnimating();
                    }
            );


        });

        dialog.setContentView(workspaceView);
        dialog.setDismissWithAnimation(true);
        dialog.show();



    }
    private void leaveGroup(){
        refresh_layout.startAnimating();
        if(workspace.isCreator(getApplicationContext())){
            workspaceRepository.deleteWorkspace(
                    workspace,
                    jsonObject -> {
                        refresh_layout.stopAnimating();
                        Log.v("API_RESPONSE", jsonObject.toString());
                        new Handler().postDelayed(() -> finish(), 2500);
                    },
                    volleyError -> {
                        refresh_layout.stopAnimating();
                        Log.e("API_RESPONSE", volleyError.toString());
                    }
            );
        }else {
            workspaceRepository.removeMemberFromWorkspace(
                    workspace,
                    new UserModel(getApplicationContext()).uid(),
                    jsonObject -> {
                        refresh_layout.stopAnimating();
                        Log.v("API_RESPONSE", jsonObject.toString());

                    },
                    volleyError -> {
                        refresh_layout.stopAnimating();
                        Log.e("API_RESPONSE", volleyError.toString());
                    }
            );
        }
    }

    private void updatePhoto(){
        refresh_layout.startAnimating();
        workspaceRepository.uploadWorkspacePhoto(
                workspace,
                imagePath,
                string -> {
                    refresh_layout.stopAnimating();
                    Glide.with(this)
                            .load(string.replace("http://", "https://"))
                            .placeholder(R.color.workspacePlaceholder)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                    workspace_icon.setVisibility(View.GONE);
                                    workspace_profile.setImageDrawable(drawable);
                                    return false;
                                }
                            })
                            .into(workspace_profile);
                },
                volleyError -> {
                    Log.e("API_RESPONSE", volleyError.toString());
                    Log.e("API_RESPONSE", new Gson().toJson(volleyError.networkResponse, NetworkResponse.class));

                    refresh_layout.stopAnimating();
                }
        );
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("tapped", "Got data");


        if (requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Handle the selected image URI (e.g., display it in an ImageView)
            imagePath = getRealPathFromURI(getApplicationContext(), imageUri);
            Log.v("tapped", imagePath);
            workspace_profile.setScaleY(1f);
            workspace_profile.setScaleX(1f);
            workspace_profile.setImageURI(imageUri);
            updatePhoto();
        }
    }

    private String getRealPathFromURI(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

}