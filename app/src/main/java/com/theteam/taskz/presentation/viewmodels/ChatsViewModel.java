package com.theteam.taskz.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.theteam.taskz.domain.entities.Message;

import java.util.ArrayList;

public class ChatsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Message>> chats = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<ArrayList<Message>> getMessages(){
        return chats;
    }

    public void setMessages(ArrayList<Message> messages){
        chats.setValue(messages);
    }

}
