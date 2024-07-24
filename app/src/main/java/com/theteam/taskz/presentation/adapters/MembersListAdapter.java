package com.theteam.taskz.presentation.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.entities.Member;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.MyHolder> {

    private ArrayList<UserModel> users;

    public MembersListAdapter(ArrayList<UserModel> members){
        this.users = members;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_item_layout, null, false);
        return new MyHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        private CircleImageView profile;
        private TextView name_text, description;
        private Context context;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();

            profile = itemView.findViewById(R.id.member_icon);
            name_text = itemView.findViewById(R.id.member_name);
            description = itemView.findViewById(R.id.job_description);
        }

        public void bind(final UserModel user){
            if(user.hasProfile()){
                Glide.with(context)
                        .load(user.profile())
                        .placeholder(R.drawable.avatar)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                profile.setScaleX(1);
                                profile.setScaleY(1);
                                profile.setImageDrawable(drawable);
                                return false;
                            }
                        })
                        .into(profile);

                name_text.setText(user.fullName());
                description.setText(user.jobTitle());
            }
        }

    }
}
