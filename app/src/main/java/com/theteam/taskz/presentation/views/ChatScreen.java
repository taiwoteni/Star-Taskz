package com.theteam.taskz.presentation.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.data.repositories.WorkspaceDataRepository;
import com.theteam.taskz.domain.entities.Group;
import com.theteam.taskz.domain.entities.Message;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;
import com.theteam.taskz.presentation.viewmodels.ChatsViewModel;
import com.theteam.taskz.presentation.viewmodels.WorkspacesViewModel;
import com.theteam.taskz.utils.others.JsonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreen extends AppCompatActivity {

    private CircleImageView group_image;
    private ImageView group_icon,back_icon, send_icon;
    private TextView group_name, group_description;
    private EditText message_edit;
    private Group group;

    private ChatListsAdapter chats_adapter;

    private WorkspaceRepository workspaceRepository;

    private RecyclerView chat_recycler_view;
    private UserModel user;

    private ArrayList<Message> messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        group = Group.fromJson(JsonUtils.convertToHashMap(getIntent().getStringExtra("group")));
        user = new UserModel(getApplicationContext());
        workspaceRepository = new WorkspaceRepository(getApplicationContext());

        group_image = findViewById(R.id.group_profile_icon);
        group_icon = findViewById(R.id.group_icon);
        back_icon = findViewById(R.id.back);
        send_icon = findViewById(R.id.send_icon);
        message_edit = findViewById(R.id.message_form);
        group_name = findViewById(R.id.group_name);
        group_description = findViewById(R.id.group_description);
        chat_recycler_view = findViewById(R.id.chats_recycler);

        initializeUI();
        addObservers();




    }

    private void initializeUI(){
        send_icon.setVisibility(View.GONE);
        group_name.setText(group.groupName());
        group_description.setText(group.members().size() + " Member" + (group.members().size()==1?"":"s"));

        back_icon.setOnClickListener(view -> {
            finish();
        });

        message_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(message_edit.getText().toString().trim().isEmpty()){
                    send_icon.setVisibility(View.GONE);
                }
                else {
                    send_icon.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        chat_recycler_view.setLayoutManager(layoutManager);

        chats_adapter = new ChatListsAdapter(group.getMessages(),this);

        chat_recycler_view.setAdapter(chats_adapter);
        chats_adapter.notifyDataSetChanged();

        send_icon.setOnClickListener(view -> sendMessage());


    }

    private void addObservers(){
        WorkspaceDataRepository.getInstance().getWorkspaces().observe(this, workspaces -> {
            final ArrayList<Message> gottenMessages = getMessages(workspaces);
            this.messages = gottenMessages;
            if(chats_adapter == null){
                chats_adapter = new ChatListsAdapter(messages, this);
                chat_recycler_view.setAdapter(chats_adapter);
                chats_adapter.notifyDataSetChanged();
            }
            else{
                chats_adapter.update(gottenMessages);
            }
        });
    }

    private void sendMessage(){
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());
        ArrayList<String> seen = new ArrayList<>();
        seen.add(user.uid());
        HashMap<String,Object> message = new HashMap<>();
        message.put("senderId", user.uid());
        message.put("id", user.uid());
        message.put("messageContent", message_edit.getText().toString().trim());
        message.put("dateTime", format.format(Calendar.getInstance().getTime()));
//            return;
        message.put("readBy", seen);

        message_edit.setText("");

        workspaceRepository.sendMessages(
                group,
                Message.fromJson(message),
                jsonObject -> {
                    Log.v("API_RESPONSE", JsonUtils.prettyPrint(jsonObject.toString()));
                },
                volleyError -> {
                    Log.e("API_RESPONSE", volleyError.toString());
                }
        );

//        final ArrayList<Message> newMessages = (ArrayList<Message>) messages.clone();
//        newMessages.add(Message.fromJson(message));


    }

    private Workspace getWorkspace(ArrayList<Workspace> workspaces){
        Workspace obj = null;
        final String workspaceId = group.workspaceId();
        for(final Workspace workspace:workspaces){
            if(workspace.workspaceId().trim().equals(workspaceId.trim())){
                obj = workspace;
                break;
            }
        }
        return obj;
    }
    private Group getGroup(ArrayList<Group> groups) {
        Group obj = null;
        final String groupId = this.group.groupId();
        for(final Group group:groups){
            if(group.groupId().trim().equals(groupId.trim())){
                obj = group;
                break;
            }
        }
        return obj;
    }

    private ArrayList<Message> getMessages(ArrayList<Workspace> workspaces){
        return getGroup(getWorkspace(workspaces).groups()).getMessages();
    }






}