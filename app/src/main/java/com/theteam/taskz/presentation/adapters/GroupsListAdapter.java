package com.theteam.taskz.presentation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.R;
import com.theteam.taskz.domain.entities.Group;
import com.theteam.taskz.presentation.views.ChatScreen;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Group> groups;

    public GroupsListAdapter(final ArrayList<Group> groups, final Context context){
        this.context = context;
        this.groups = groups;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View layout = LayoutInflater.from(context).inflate(R.layout.groups_item_layout, viewGroup, false);

        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(groups.get(i));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void update(ArrayList<Group> groups){
        this.groups = groups;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView group_name,group_description,new_messages;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            group_name = (TextView) itemView.findViewById(R.id.group_name);
            group_description = (TextView) itemView.findViewById(R.id.group_description);
            new_messages = (TextView) itemView.findViewById(R.id.new_messages);
            view = itemView;
        }

        public void bind(Group group){
            group_name.setText(group.groupName());
            group_description.setText(group.groupDescription());
            new_messages.setVisibility(View.GONE);

            view.setOnClickListener(view1 -> {
                Type hashType = new TypeToken<HashMap<String,Object>>(){}.getType();

                final Intent chatScreen = new Intent(context, ChatScreen.class);
                chatScreen.putExtra("group", new Gson().toJson(group.toJson(), hashType));
                context.startActivity(chatScreen);
            });
        }

    }
}
