package com.theteam.taskz.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.theteam.taskz.domain.entities.Workspace;

import java.util.ArrayList;

public class WorkspaceDataRepository {

    private static WorkspaceDataRepository instance;
    private MutableLiveData<ArrayList<Workspace>> data;

    private WorkspaceDataRepository() {
        data = new MutableLiveData<>();
    }

    public static synchronized WorkspaceDataRepository getInstance() {
        if (instance == null) {
            instance = new WorkspaceDataRepository();
        }
        return instance;
    }

    public void setWorkspaces(final ArrayList<Workspace> workspaces) {
        data.postValue(workspaces);
    }

    public MutableLiveData<ArrayList<Workspace>> getWorkspaces() {
        return data;
    }
}
