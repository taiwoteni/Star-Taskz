package com.theteam.taskz.presentation.views;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import android.Manifest;
import android.widget.Toast;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.repositories.AuthenticationRepository;
import com.theteam.taskz.domain.repositories.UserRepository;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileSection extends Fragment {

    private LoadableButton loadableButton;
    private FrameLayout profile_layout;
    private CircleImageView profile_icon;

    private SplashRefreshLayout refreshLayout;


    private String imagePath;

    final private int PERMISSION_CODE = 100;
    final private int PICK_IMAGE_REQ = 1;

    public ProfileSection() {
        // Required empty public constructor
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("tapped", "Got data");
        Log.v("tapped", "Data is null:"+(data.getData()==null));


        if (requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Handle the selected image URI (e.g., display it in an ImageView)
            imagePath = getRealPathFromURI(requireActivity().getApplicationContext(), imageUri);
            Log.v("tapped", imagePath);
            profile_icon.setScaleY(1f);
            profile_icon.setScaleX(1f);
            profile_icon.setImageURI(imageUri);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profile_icon = (CircleImageView) view.findViewById(R.id.profile_icon);
        profile_layout = (FrameLayout) view.findViewById(R.id.profile_layout);
        loadableButton = (LoadableButton) view.findViewById(R.id.loadable_button);
        refreshLayout = (SplashRefreshLayout) view.findViewById(R.id.refresh_layout);

        loadableButton.setOnClickListener(view1 -> {
            go();
        });

        profile_layout.setOnClickListener(view1 -> {
            pickImage();
        });


    }

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireActivity(), "Permission to pick image was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQ);
    }


    private void pickImage(){
        Log.v("tapped","Image tapped");
        openGallery();
    }

    private void go(){
        if(imagePath == null){
            // we are skipping
        }
        else{
            final UserModel user = new UserModel(requireActivity().getApplicationContext());
            UserRepository userRepo = new UserRepository(requireActivity().getApplicationContext());
            userRepo.uploadProfilePic(
                    user.uid(),
                    imagePath,
                    null,
                    networkResponse -> {
                        Log.v("API_RESPONSE", networkResponse.data.toString());
                    },
                    volleyError -> {
                        Log.e("API_RESPONSE", volleyError.toString());
                    }
            );

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