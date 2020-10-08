package com.example.android2project.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.android2project.model.RotateBitmap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class StorageRepository {
    private static StorageRepository storageRepository;

    private StorageReference mStorage;

    private Context mContext;

    private final int COMPRESS_PERCENTAGE = 20;
    private final String TAG = "StorageRepository";


    /**<-------Picture Download interface------->**/
    public interface StorageDownloadPicInterface {
        void onDownloadPicSuccess(Uri uri);

        void onDownloadPicFailed(String error);
    }

    StorageDownloadPicInterface mDownloadPicListener;

    public void setDownloadPicListener(StorageDownloadPicInterface storageDownloadPicInterface) {
        this.mDownloadPicListener = storageDownloadPicInterface;
    }

    /**<-------Picture Upload interface------->**/
    public interface StorageUploadPicInterface {
        void onUploadPicSuccess(String imagePath);

        void onUploadPicFailed(String error);
    }

    StorageUploadPicInterface mUploadPicListener;

    public void setUploadPicListener(StorageUploadPicInterface storageUploadPicInterface) {
        this.mUploadPicListener = storageUploadPicInterface;
    }

    /**<-------Pet Picture Upload interface------->**/
    public interface StoragePetUploadPicInterface {
        void onPetUploadPicSuccess(String imagePath, int iteration);

        void onPetUploadPicFailed(String error);
    }

    StoragePetUploadPicInterface mPetUploadPicListener;

    public void setPetUploadPicListener(StoragePetUploadPicInterface storagePetUploadPicInterface) {
        this.mPetUploadPicListener = storagePetUploadPicInterface;
    }

    /**<-------Ad Picture Upload interface------->**/
    public interface StorageAdUploadPicInterface {
        void onAdUploadPicSuccess(String imagePath, int iteration);

        void onAdUploadPicFailed(String error);
    }

    StorageAdUploadPicInterface mAdUploadPicListener;

    public void setAdUploadPicListener(StorageAdUploadPicInterface storageAdUploadPicInterface) {
        this.mAdUploadPicListener = storageAdUploadPicInterface;
    }

    /**<-------Post Picture Upload interface------->**/
    public interface StoragePostUploadPicInterface {
        void onPostUploadPicSuccess(String imagePath);

        void onPostUploadPicFailed(String error);
    }

    StoragePostUploadPicInterface mPostUploadPicListener;

    public void setPostUploadPicListener(StoragePostUploadPicInterface storagePostUploadPicInterface) {
        this.mPostUploadPicListener = storagePostUploadPicInterface;
    }

    /**<-------Picture Deletion interface------->**/
    public interface StorageDeletePicInterface {
        void onDeletePicSuccess(String imagePath);

        void onDeletePicFailed(String error);
    }

    StorageDeletePicInterface mDeletePicListener;

    public void setDeletePicListener(StorageDeletePicInterface storageDeletePicInterface) {
        this.mDeletePicListener = storageDeletePicInterface;
    }

    /**<-------Picture Deletion interface------->**/
    public interface StorageDeleteAdPicInterface {
        void onDeleteAdPicSuccess(String imagePath);

        void onDeleteAdPicFailed(String error);
    }

    StorageDeleteAdPicInterface mDeleteAdPicListener;

    public void setDeleteAdPicListener(StorageDeleteAdPicInterface storageDeleteAdPicInterface) {
        this.mDeleteAdPicListener = storageDeleteAdPicInterface;
    }

    /**<-------Singleton------->**/
    public static StorageRepository getInstance(Context context) {
        if (storageRepository == null) {
            storageRepository = new StorageRepository(context);
        }
        return storageRepository;
    }

    private StorageRepository(final Context context) {
        this.mStorage = FirebaseStorage.getInstance().getReference();
        this.mContext = context;
    }

    public void uploadFile(final Uri uri, final String userId) {
        StorageReference fileToUpload = mStorage.child("users_profile_picture/" + userId + ".jpg");

        try {
            RotateBitmap rotateBitmap = new RotateBitmap();
            Bitmap bitmap = rotateBitmap.HandleSamplingAndRotationBitmap(mContext, uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_PERCENTAGE, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            fileToUpload.putBytes(bytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Objects.requireNonNull(Objects.requireNonNull(
                                    Objects.requireNonNull(taskSnapshot.getMetadata())
                                            .getReference())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (mUploadPicListener != null) {
                                                mUploadPicListener.onUploadPicSuccess(uri.toString());
                                            }
                                            Log.d(TAG, "onSuccess: " + uri.toString());
                                        }
                                    }));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mUploadPicListener != null) {
                                mUploadPicListener.onUploadPicFailed(e.getMessage());
                            }
                        }
                    });
            byteArrayOutputStream.close();
        } catch (IOException e) {
            if (mUploadPicListener != null) {
                mUploadPicListener.onUploadPicFailed(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void uploadPhoto(final String path, final Uri uri, final String userEmail, final int iteration) {
        try {
            RotateBitmap rotateBitmap = new RotateBitmap();
            Bitmap bitmap = rotateBitmap.HandleSamplingAndRotationBitmap(mContext, uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_PERCENTAGE, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            mStorage.child(userEmail + "/" + path).child(userEmail + System.nanoTime() + ".jpg").putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Objects.requireNonNull(Objects.requireNonNull(
                            Objects.requireNonNull(taskSnapshot.getMetadata())
                                    .getReference())
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (path.equals("pets")) {
                                        if (mPetUploadPicListener != null) {
                                            mPetUploadPicListener.onPetUploadPicSuccess(uri.toString(), iteration);
                                        }
                                    } else {
                                        if (mAdUploadPicListener != null) {
                                            Log.d(TAG, "onSuccess: qwerty " + uri.toString());
                                            mAdUploadPicListener.onAdUploadPicSuccess(uri.toString(), iteration);
                                        }
                                    }
                                }
                            }));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mPetUploadPicListener != null) {
                        mPetUploadPicListener.onPetUploadPicFailed(e.getMessage());
                    }
                }
            });
            byteArrayOutputStream.close();
        } catch (IOException e) {
            if (mPetUploadPicListener != null) {
                mPetUploadPicListener.onPetUploadPicFailed(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void uploadPostFile(final Uri uri, final String userEmail, final String postId) {
        StorageReference fileToUpload = mStorage.child(userEmail+"/posts/" + postId + ".jpg");
        try {
            RotateBitmap rotateBitmap = new RotateBitmap();
            Bitmap bitmap = rotateBitmap.HandleSamplingAndRotationBitmap(mContext, uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_PERCENTAGE, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            fileToUpload.putBytes(bytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Objects.requireNonNull(Objects.requireNonNull(
                                    Objects.requireNonNull(taskSnapshot.getMetadata())
                                            .getReference())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (mPostUploadPicListener != null) {
                                                mPostUploadPicListener.onPostUploadPicSuccess(uri.toString());
                                            }
                                            Log.d(TAG, "onSuccess: " + uri.toString());
                                        }
                                    }));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mPostUploadPicListener != null) {
                                mPostUploadPicListener.onPostUploadPicFailed(e.getMessage());
                            }
                        }
                    });
            byteArrayOutputStream.close();
        } catch (IOException e) {
            if (mPostUploadPicListener != null) {
                mPostUploadPicListener.onPostUploadPicFailed(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void downloadFile(final String imageUri) {
        StorageReference fileToDownload = mStorage.child(imageUri);

        fileToDownload.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (mDownloadPicListener != null) {
                            mDownloadPicListener.onDownloadPicSuccess(uri);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        if (mDownloadPicListener != null) {
                            mDownloadPicListener.onDownloadPicFailed(ex.getMessage());
                        }
                    }
                });

    }

    public void deleteFile(final String userId) {
        final StorageReference fileToDelete = mStorage.child("users_profile_picture/" + userId + ".jpg");

        fileToDelete.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (mDeletePicListener != null) {
                            mDeletePicListener.onDeletePicSuccess(fileToDelete.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (mDeletePicListener != null) {
                    mDeletePicListener.onDeletePicFailed(e.getMessage());
                }
            }
        });
    }

    public void deletePhotoFromStorage(final String photoUri) {
        final StorageReference fileToDelete = mStorage.child(photoUri);

        fileToDelete.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: " + photoUri);
                        if (mDeleteAdPicListener != null) {
                            mDeleteAdPicListener.onDeleteAdPicSuccess(fileToDelete.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + photoUri +"_" + e.getMessage());
                if (mDeleteAdPicListener != null) {
                    mDeleteAdPicListener.onDeleteAdPicFailed(e.getMessage());
                }
            }
        });
    }
}
