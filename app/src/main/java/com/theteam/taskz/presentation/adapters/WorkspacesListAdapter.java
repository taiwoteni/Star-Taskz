package com.theteam.taskz.presentation.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theteam.taskz.R;
import com.theteam.taskz.domain.entities.Workspace;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkspacesListAdapter extends RecyclerView.Adapter<WorkspacesListAdapter.WorkspacesViewHolder> {

    private Context context;
    private ArrayList<Workspace> workspaces;

    public WorkspacesListAdapter(final ArrayList<Workspace> workspaces, final Context context){
        this.workspaces = workspaces;
        this.context = context;
    }


    @NonNull
    @Override
    public WorkspacesListAdapter.WorkspacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View layout = LayoutInflater.from(context).inflate(R.layout.workspace_item_layout, viewGroup, false);
        return new WorkspacesViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkspacesListAdapter.WorkspacesViewHolder workspacesViewHolder, int position) {
        workspacesViewHolder.bind(workspaces.get(position));
    }

    @Override
    public int getItemCount() {
        return workspaces.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void update(final ArrayList<Workspace> workspaces){
        this.workspaces = workspaces;
        notifyDataSetChanged();
    }

    public class WorkspacesViewHolder extends RecyclerView.ViewHolder{

        private TextView workspaceTitle,workspaceDescription;
        private CircleImageView workspaceProfile;
        private ImageView workspaceIcon;
        private View root;

        public WorkspacesViewHolder(@NonNull View itemView) {
            super(itemView);
            workspaceTitle = (TextView) itemView.findViewById(R.id.workspace_title);
            workspaceIcon = (ImageView) itemView.findViewById(R.id.workspace_icon);
            workspaceDescription = (TextView) itemView.findViewById(R.id.workspace_description);
            workspaceProfile = (CircleImageView) itemView.findViewById(R.id.workspace_profile_icon);
            root = itemView;
        }

        public void bind(Workspace workspace){
            workspaceTitle.setText(workspace.workspaceTitle());
            workspaceDescription.setText(workspace.workspaceDescription());

            if(workspace.hasPhoto()){
                workspaceIcon.setVisibility(View.GONE);
                Glide.with(context)
                        .load(workspace.workspacePhoto())
                        .placeholder(R.color.workspacePlaceholder)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                workspaceProfile.setImageDrawable(drawable);
                                workspaceIcon.setVisibility(View.GONE);
                                return true;
                            }
                        }).into(workspaceProfile);

            }

        }
    }
}
