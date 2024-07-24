package com.theteam.taskz.presentation.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.UserRepository;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;
import com.theteam.taskz.presentation.adapters.MembersListAdapter;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MembersTabFragment extends Fragment {

    private Workspace workspace;
    private LinearLayout empty_layout, loading_layout;
    private RecyclerView members_recycler_view;


    public MembersTabFragment(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.members_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        empty_layout = view.findViewById(R.id.empty_layout);
        loading_layout = view.findViewById(R.id.loading_layout);
        members_recycler_view = view.findViewById(R.id.members_recycler_view);

        members_recycler_view.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

        empty_layout.setVisibility(View.VISIBLE);
        loading_layout.setVisibility(View.GONE);
        members_recycler_view.setVisibility(View.GONE);
        load();
    }

    private void load(){
        ArrayList<UserModel> userModels = new ArrayList<>();
        new UserRepository(requireActivity()).getUsers(
                null,
                jsonArray -> {
                    for (int i = 0; i<jsonArray.length(); i++){
                        try {
                            final JSONObject object = jsonArray.getJSONObject(i);
                            if(workspace.teamMembers().contains(object.get("id").toString())){
                                userModels.add(new UserModel(JsonUtils.convertToHashMap(object)));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(!userModels.isEmpty()){
                        empty_layout.setVisibility(View.GONE);
                        loading_layout.setVisibility(View.GONE);
                        members_recycler_view.setVisibility(View.VISIBLE);
                        members_recycler_view.setAdapter(new MembersListAdapter(userModels));
                        members_recycler_view.getAdapter().notifyDataSetChanged();
                    }
                },
                volleyError -> {
                    Log.v("API_RESPONSE", volleyError.toString());
                }
        );
    }

}