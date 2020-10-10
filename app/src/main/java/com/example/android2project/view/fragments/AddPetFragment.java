package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android2project.R;
import com.example.android2project.model.Pet;
import com.example.android2project.model.PhotosPreviewRecyclerview;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.PetViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Objects;

public class AddPetFragment extends DialogFragment {

    private static final String TAG = "AddPetFragment";
    private PetViewModel mViewModel;
    private File mFile;
    private AlertDialog mLoadingDialog;
    private PhotosPreviewRecyclerview photosPreviewRecyclerview;

    private Pet mCurrentPet;

    private Observer<Integer> mDoneUploadingObserver;
    private Observer<String> mDeletePetFailedObserver;

    private final int WRITE_PERMISSION_REQUEST = 7;

    public AddPetFragment() {
    }

    public static AddPetFragment newInstance(Pet pet) {
        AddPetFragment fragment = new AddPetFragment();
        Bundle args = new Bundle();
        args.putSerializable("pet", pet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Pet)).get(PetViewModel.class);

        if (getArguments() != null) {
            mCurrentPet = (Pet) getArguments().getSerializable("pet");
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_pet, container, false);

        photosPreviewRecyclerview = rootView.findViewById(R.id.photos_preview_recycler);
        final TextInputEditText petNameEt = rootView.findViewById(R.id.pet_name_et);
        final TextInputEditText petTypeEt = rootView.findViewById(R.id.item_name_et);
        final TextInputEditText petDescriptionEt = rootView.findViewById(R.id.pet_description_et);
        final ImageButton addImageBtn = rootView.findViewById(R.id.add_image_btn);
        final Button addBtn = rootView.findViewById(R.id.add_pet_btn);


        if (mCurrentPet != null) {
            petNameEt.setText(mCurrentPet.getPetName());
            petTypeEt.setText(mCurrentPet.getAnimalType());
            petDescriptionEt.setText(mCurrentPet.getPetDescription());
            photosPreviewRecyclerview.initEdit(8, mCurrentPet);
            addBtn.setText(R.string.update);
        } else {
            photosPreviewRecyclerview.init(8);
        }

        mDoneUploadingObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                final String petName = petNameEt.getText().toString().trim();
                final String petType = petTypeEt.getText().toString().trim();
                final String description = petDescriptionEt.getText().toString().trim();
                if (mCurrentPet != null) {
                    mCurrentPet.setPetName(petName);
                    mCurrentPet.setAnimalType(petType);
                    mCurrentPet.setPetDescription(description);
                    mViewModel.addPetToUser(mCurrentPet);
                } else {
                    mCurrentPet = new Pet(petName, petType, description);
                    mViewModel.addPetToUser(mCurrentPet);
                }
                if (mLoadingDialog!=null) {
                    mLoadingDialog.dismiss();
                    dismiss();
                }
            }
        };

        mDeletePetFailedObserver = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mViewModel.getOnPetUploadPhotoLiveData().observe(getViewLifecycleOwner(), mDoneUploadingObserver);
        mViewModel.getOnDeletePetFailedLiveData().observe(getViewLifecycleOwner(),mDeletePetFailedObserver);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                Uri uri = FileProvider.getUriForFile(requireContext(),
                        "com.example.android2project.provider", mFile);
                CropImage.activity()
                        .setAspectRatio(4, 3)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(uri)
                        .start(requireContext(), AddPetFragment.this);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String petName = petNameEt.getText().toString().trim();
                final String petType = petTypeEt.getText().toString().trim();

                if (petName.length() > 0 && petType.length() > 0) {
                    showLoadingDialog();
                    mViewModel.uploadPetPhotos(photosPreviewRecyclerview.getSelectedImageList());
                } else {
                    if (petName.length() < 1) {
                        petNameEt.setError(getContext().getString(R.string.enter_pet_name));
                    } else {
                        petNameEt.setError(null);
                    }
                    if (petType.length() < 1) {
                        petTypeEt.setError(getContext().getString(R.string.enter_pet_type));
                    } else {
                        petTypeEt.setError(null);
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFile = new File(requireContext().
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "petclan" + System.nanoTime() + "pic.jpg");
                Uri uri = FileProvider.getUriForFile(requireContext(),
                        "com.example.android2project.provider", mFile);
                CropImage.activity()
                        .setAspectRatio(4, 3)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputUri(uri)
                        .start(requireContext(), AddPetFragment.this);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                photosPreviewRecyclerview.addPhoto(result.getUri());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dog_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(false);
        mLoadingDialog = builder.create();
        mLoadingDialog.show();
        mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

}
