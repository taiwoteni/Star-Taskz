package com.theteam.taskz.presentation.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.entities.Message;
import com.theteam.taskz.presentation.adapters.GroupsListAdapter;
import com.theteam.taskz.utils.others.ThemeManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListsAdapter extends RecyclerView.Adapter<ChatListsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Message> messages;

    private UserModel user;

    public ChatListsAdapter(final ArrayList<Message> messages, final Context context){
        this.messages = messages;
        this.context = context;
        user = new UserModel(context);
    }

    @NonNull
    @Override
    public ChatListsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View layout = LayoutInflater.from(context).inflate(R.layout.chats_item_layout, viewGroup, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListsAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(messages.get(i), i);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void update(final ArrayList<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileIcon;
        private TextView messageText;
        private LinearLayout messageLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIcon = itemView.findViewById(R.id.profile_icon);
            messageText = itemView.findViewById(R.id.message);
            messageLayout = itemView.findViewById(R.id.root);

        }

        void bind(Message message, int position){

            final boolean isSender = message.senderId().equals(user.uid());
            final boolean isLastMessage = position == messages.size()-1;
            final boolean isFirstMessage = position == 0;
            boolean lastTimeSender = false;
            boolean firstTimeSender = false;

            if(isFirstMessage){
                if(isLastMessage){
                    lastTimeSender = true;
                }
                else{

                }
            }

            messageText.setText(message.messageContent());
            messageText.setBackgroundResource(isSender? R.drawable.chat_background_sender:R.drawable.chat_background_receiver);
            messageText.setTextColor(isSender? Color.WHITE:new ThemeManager(context).primary);
            // Using bottom because of profile icon
            messageLayout.setGravity((isSender? Gravity.END:Gravity.START)|Gravity.BOTTOM);

            if(isSender){
                profileIcon.setVisibility(View.GONE);
                return;
            }
            if(message.hasProfile()){
                Glide.with(context)
                        .load(message.senderProfile())
                        .placeholder(R.drawable.avatar)
                        .transition(DrawableTransitionOptions.withCrossFade(1500))
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                profileIcon.setImageDrawable(drawable);
                                profileIcon.setScaleX(1);
                                profileIcon.setScaleY(1);
                                return false;
                            }
                        }).into(profileIcon);
            }


        }


    }
}
