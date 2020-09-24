package com.example.android2project.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android2project.model.ChatMessage;
import com.example.android2project.model.Conversation;
import com.example.android2project.model.Comment;
import com.example.android2project.model.Post;
import com.example.android2project.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Repository {
    private FirebaseAuth mAuth;

    private Context mContext;

    private static Repository repository;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDBChats = mDatabase.getReference("chats");

    private FirebaseFirestore mCloudDB = FirebaseFirestore.getInstance();
    private CollectionReference mCloudUsers = mCloudDB.collection("users");
    private CollectionReference mCloudChats = mCloudDB.collection("chats");

    private final String TAG = "Repository";

    /**<-------Posts Interfaces------->**/
    /**<-------Post Downloading interface------->**/
    public interface RepositoryPostDownloadInterface {
        void onPostDownloadSucceed(List<Post> posts);

        void onPostDownloadFailed(String error);
    }

    private RepositoryPostDownloadInterface mPostDownloadListener;

    public void setPostDownloadListener(RepositoryPostDownloadInterface repositoryPostDownloadInterface) {
        this.mPostDownloadListener = repositoryPostDownloadInterface;
    }

    /**<-------Post Uploading interface------->**/
    public interface RepositoryPostUploadInterface {
        void onPostUploadSucceed(Post post);

        void onPostUploadFailed(String error);
    }

    private RepositoryPostUploadInterface mPostUploadListener;

    public void setPostUploadListener(RepositoryPostUploadInterface repositoryPostUploadInterface) {
        this.mPostUploadListener = repositoryPostUploadInterface;
    }

    /**<-------Post Updating interface------->**/
    public interface RepositoryPostUpdatingInterface {
        void onPostUpdatingSucceed(Post updatedPost);

        void onPostUpdatingFailed(String error);
    }

    private RepositoryPostUpdatingInterface mPostUpdatingListener;

    public void setPostUpdatingListener(RepositoryPostUpdatingInterface repositoryPostUpdatingInterface) {
        this.mPostUpdatingListener = repositoryPostUpdatingInterface;
    }

    /**<-------Post Likes Updating interface------->**/
    public interface RepositoryPostLikesUpdatingInterface {
        void onPostLikesUpdateSucceed(Post post);

        void onPostLikesUpdateFailed(String error);
    }

    private RepositoryPostLikesUpdatingInterface mPostLikesUpdatingListener;

    public void setPostLikesUpdatingListener(RepositoryPostLikesUpdatingInterface repositoryPostLikesUpdatingInterface) {
        this.mPostLikesUpdatingListener = repositoryPostLikesUpdatingInterface;
    }

    /**<-------Post Deleting interface------->**/
    public interface RepositoryPostDeletingInterface {
        void onPostDeletingSucceed(String postId);

        void onPostDeletingFailed(String error);
    }

    private RepositoryPostDeletingInterface mPostDeletingListener;

    public void setPostDeletingListener(RepositoryPostDeletingInterface repositoryPostDeletingInterface) {
        this.mPostDeletingListener = repositoryPostDeletingInterface;
    }

    /**<-------Comments Interfaces------->**/
    /**<-------Comment Downloading interface------->**/
    public interface RepositoryCommentDownloadInterface {
        void onCommentDownloadSucceed(List<Comment> comments);

        void onCommentDownloadFailed(String error);
    }

    private RepositoryCommentDownloadInterface mCommentDownloadListener;

    public void setCommentDownloadListener(RepositoryCommentDownloadInterface repositoryCommentDownloadInterface) {
        this.mCommentDownloadListener = repositoryCommentDownloadInterface;
    }

    /**<-------Comment Uploading interface------->**/
    public interface RepositoryCommentUploadInterface {
        void onCommentUploadSucceed(Comment comment);

        void onCommentUploadFailed(String error);
    }

    private RepositoryCommentUploadInterface mCommentUploadListener;

    public void setCommentUploadListener(RepositoryCommentUploadInterface repositoryCommentUploadInterface) {
        this.mCommentUploadListener = repositoryCommentUploadInterface;
    }

    /**<-------Comment Updating interface------->**/
    public interface RepositoryCommentUpdatingInterface {
        void onCommentUpdatingSucceed(String updatedCommentContent);

        void onCommentUpdatingFailed(String error);
    }

    private RepositoryCommentUpdatingInterface mCommentUpdatingListener;

    public void setCommentUpdatingListener(RepositoryCommentUpdatingInterface repositoryCommentUpdatingInterface) {
        this.mCommentUpdatingListener = repositoryCommentUpdatingInterface;
    }

    /**<-------Comment Deleting interface------->**/
    public interface RepositoryCommentDeletingInterface {
        void onCommentDeletingSucceed(String commentId);

        void onCommentDeletingFailed(String error);
    }

    private RepositoryCommentDeletingInterface mCommentDeletingListener;

    public void setCommentDeletingListener(RepositoryCommentDeletingInterface repositoryCommentDeletingInterface) {
        this.mCommentDeletingListener = repositoryCommentDeletingInterface;
    }

    /**<-------Settings Interfaces------->**/
    /**<-------Update User Name interface------->**/
    public interface RepositoryUpdateUserNameInterface {
        void onUpdateUserNameSucceed(String newUserName);

        void onUpdateUserNameFailed(String error);
    }

    private RepositoryUpdateUserNameInterface mUpdateUserNameListener;

    public void setUpdateUserNameListener(RepositoryUpdateUserNameInterface repositoryUpdateUserNameInterface) {
        this.mUpdateUserNameListener = repositoryUpdateUserNameInterface;
    }

    /**<-------Update User Image interface------->**/
    public interface RepositoryUpdateUserImageInterface {
        void onUpdateUserImageSucceed(String newUserProfilePic);

        void onUpdateUserImageFailed(String error);
    }

    private RepositoryUpdateUserImageInterface mUpdateUserImageListener;

    public void setUpdateUserImageListener(RepositoryUpdateUserImageInterface repositoryUpdateUserImageInterface) {
        this.mUpdateUserImageListener = repositoryUpdateUserImageInterface;
    }

    /**<-------Update User Cover Image interface------->**/
    public interface RepositoryUpdateUserCoverImageInterface {
        void onUpdateUserCoverImageSucceed(String newUserProfileCoverPic);

        void onUpdateUserCoverImageFailed(String error);
    }

    private RepositoryUpdateUserCoverImageInterface mUpdateUserCoverImageListener;

    public void setUpdateUserCoverImageListener(RepositoryUpdateUserCoverImageInterface repositoryUpdateUserCoverImageInterface) {
        this.mUpdateUserCoverImageListener = repositoryUpdateUserCoverImageInterface;
    }

    /**<-------User Deletion interface------->**/
    public interface RepositoryUserDeletionInterface {
        void onUserDeletionSucceed(String userId);

        void onUserDeletionFailed(String error);
    }

    private RepositoryUserDeletionInterface mUserDeletionListener;

    public void setUserDeletionListener(RepositoryUserDeletionInterface repositoryUserDeletionInterface) {
        this.mUserDeletionListener = repositoryUserDeletionInterface;
    }

    /**<-------Chats Interfaces------->**/
    /**<-------Download Conversation interface------->**/
    public interface RepositoryDownloadConversationInterface {
        void onDownloadConversationSucceed(List<ChatMessage> conversation);

        void onDownloadConversationFailed(String error);
    }

    private RepositoryDownloadConversationInterface mDownloadConversationListener;

    public void setDownloadConversationListener(RepositoryDownloadConversationInterface repositoryDownloadConversationInterface) {
        this.mDownloadConversationListener = repositoryDownloadConversationInterface;
    }

    /**<-------Upload Message interface------->**/
    public interface RepositoryUploadMessageInterface {
        void onUploadMessageSucceed(ChatMessage message, boolean isMine);

        void onUploadMessageFailed(String error);
    }

    private RepositoryUploadMessageInterface mUploadMessageListener;

    public void setUploadMessageListener(RepositoryUploadMessageInterface repositoryUploadMessageInterface) {
        this.mUploadMessageListener = repositoryUploadMessageInterface;
    }

    public static Repository getInstance(final Context context) {
        if (repository == null) {
            repository = new Repository(context);
        }
        return repository;
    }

    private Repository(final Context context) {
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
    }

    /**<-------Posts methods------->**/
    public void downloadPosts() {
        final List<Post> posts = new ArrayList<>();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mCloudDB.collectionGroup("posts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    posts.add(document.toObject(Post.class));
                                    Log.d(TAG, "onComplete: " + document.toObject(Post.class).toString());
                                }
                                if (mPostDownloadListener != null) {
                                    Collections.sort(posts);
                                    mPostDownloadListener.onPostDownloadSucceed(posts);
                                }
                            } else {
                                if (mPostDownloadListener != null) {
                                    Log.wtf(TAG, "onComplete: ", task.getException());
                                    mPostDownloadListener.onPostDownloadFailed(Objects
                                            .requireNonNull(task.getException()).getMessage());
                                }
                            }
                        }
                    });
        }
    }

    public void uploadNewPost(String postContent) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final Post post = new Post(user.getEmail(), user.getDisplayName(),
                    Objects.requireNonNull(user.getPhotoUrl()).toString(),
                    postContent);

            post.setPostId(user.getEmail() + System.nanoTime());

            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .collection("posts")
                    .document(post.getPostId())
                    .set(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mPostUploadListener != null) {
                                mPostUploadListener.onPostUploadSucceed(post);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mPostUploadListener != null) {
                                mPostUploadListener.onPostUploadFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updatePost(final Post updatedPost) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = updatedPost.getPostId();
            final String updatedPostContent = updatedPost.getAuthorContent();

            Map<String, Object> updatePostMap = new HashMap<>();
            updatePostMap.put("authorContent", updatedPostContent);

            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .collection("posts")
                    .document(postId)
                    .update(updatePostMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mPostUpdatingListener != null) {
                                mPostUpdatingListener.onPostUpdatingSucceed(updatedPost);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mPostUpdatingListener != null) {
                                mPostUpdatingListener.onPostUpdatingFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updatePostLikes(final Post post, final boolean isLike) {
        int likesAmount = post.getLikesCount() + (isLike ? 1 : -1);
        post.setLikesCount(likesAmount);

        if (isLike) {
            post.getLikesMap().put(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), true);
        } else {
            post.getLikesMap().remove(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        }

        Map<String, Object> updateLikesMap = new HashMap<>();
        updateLikesMap.put("likesCount", likesAmount);
        updateLikesMap.put("likesMap", post.getLikesMap());

        mCloudUsers.document(post.getAuthorEmail())
                .collection("posts")
                .document(post.getPostId())
                .update(updateLikesMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (mPostLikesUpdatingListener != null) {
                            mPostLikesUpdatingListener.onPostLikesUpdateSucceed(post);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (mPostLikesUpdatingListener != null) {
                            mPostLikesUpdatingListener.onPostLikesUpdateFailed(e.getMessage());
                        }
                    }
                });
    }

    public void deletePost(final String postId) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .collection("posts")
                    .document(postId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mPostDeletingListener != null) {
                                mPostDeletingListener.onPostDeletingSucceed(postId);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mPostDeletingListener != null) {
                                mPostDeletingListener.onPostDeletingFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void downloadComments(final Post post) {
        final List<Comment> comments = new ArrayList<>();
        final FirebaseUser user = mAuth.getCurrentUser();

        final String postId = post.getPostId();
        final String authorEmail = post.getAuthorEmail();

        if (user != null) {
            mCloudUsers.document(authorEmail)
                    .collection("posts")
                    .document(postId)
                    .collection("comments")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    comments.add(document.toObject(Comment.class));
                                    Log.d(TAG, "onComplete: " + document.toObject(Post.class).toString());
                                }
                                if (mCommentDownloadListener != null) {
                                    Collections.sort(comments);
                                    mCommentDownloadListener.onCommentDownloadSucceed(comments);
                                }
                            } else {
                                if (mCommentDownloadListener != null) {
                                    Log.wtf(TAG, "onComplete: ", task.getException());
                                    mCommentDownloadListener.onCommentDownloadFailed(Objects
                                            .requireNonNull(task.getException()).getMessage());
                                }
                            }
                        }
                    });
        }
    }

    /**<-------Comments methods------->**/
    public void uploadComment(final Post post, final String commentContent) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = post.getPostId();
            final String authorEmail = post.getAuthorEmail();

            final Comment comment = new Comment(user.getEmail(), user.getDisplayName(),
                    Objects.requireNonNull(user.getPhotoUrl()).toString(),
                    commentContent);

            comment.setCommentId(user.getEmail() + System.nanoTime());

            mCloudUsers.document(authorEmail)
                    .collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document(comment.getCommentId())
                    .set(comment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mCommentUploadListener != null) {
                                mCommentUploadListener.onCommentUploadSucceed(comment);
                            }
                            post.setCommentsCount(post.getCommentsCount() + 1);
                            updateCommentsAmountOfPost(post);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mCommentUploadListener != null) {
                                mCommentUploadListener.onCommentUploadFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updateComment(final Post post, final String commentId, final String updatedComment) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = post.getPostId();
            final String postAuthorEmail = post.getAuthorEmail();

            Map<String, Object> updateCommentMap = new HashMap<>();
            updateCommentMap.put("authorContent", updatedComment);

            mCloudUsers.document(postAuthorEmail)
                    .collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document(commentId)
                    .update(updateCommentMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mCommentUpdatingListener != null) {
                                mCommentUpdatingListener.onCommentUpdatingSucceed(updatedComment);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mCommentUpdatingListener != null) {
                                mCommentUpdatingListener.onCommentUpdatingFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    private void updateCommentsAmountOfPost(final Post post) {
        final String postId = post.getPostId();
        final String authorEmail = post.getAuthorEmail();
        final int commentsAmount = post.getCommentsCount();

        Map<String, Object> updateCommentsAmountMap = new HashMap<>();
        updateCommentsAmountMap.put("commentsCount", commentsAmount);

        mCloudUsers.document(authorEmail)
                .collection("posts")
                .document(postId)
                .update(updateCommentsAmountMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (mPostUpdatingListener != null) {
                            mPostUpdatingListener.onPostUpdatingSucceed(post);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (mPostUpdatingListener != null) {
                            mPostUpdatingListener.onPostUpdatingFailed(e.getMessage());
                        }
                    }
                });
    }

    public void deleteComment(final Post post, final String commentId) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String postId = post.getPostId();
            final String postAuthorEmail = post.getAuthorEmail();

            mCloudUsers.document(postAuthorEmail)
                    .collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document(commentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mCommentDeletingListener != null) {
                                mCommentDeletingListener.onCommentDeletingSucceed(commentId);
                            }
                            post.setCommentsCount(post.getCommentsCount() - 1);
                            updateCommentsAmountOfPost(post);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mCommentDeletingListener != null) {
                                mCommentDeletingListener.onCommentDeletingFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void updateUserName(String newUserName) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String firstName = newUserName.split(" ")[0];
            final String lastName = newUserName.split(" ")[1];

            Map<String, Object> updateUserNameMap = new HashMap<>();
            updateUserNameMap.put("firstName", firstName);
            updateUserNameMap.put("lastName", lastName);

            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .update(updateUserNameMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mUpdateUserNameListener != null) {
                                mUpdateUserNameListener.onUpdateUserNameSucceed(user.getUid());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mUpdateUserNameListener != null) {
                        mUpdateUserNameListener.onUpdateUserNameFailed(e.getMessage());
                    }
                }
            });
        }
    }

    public void updateUserProfileImage(final String newProfilePic) {
        final FirebaseUser user = mAuth.getCurrentUser();
        final boolean[] isImageUploaded = {false};
        final boolean[] isImageUpdated = {false};

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(newProfilePic))
                .build();

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                isImageUpdated[0] = true;
                                if (mUpdateUserImageListener != null && isImageUploaded[0]) {
                                    mUpdateUserImageListener.onUpdateUserImageSucceed(newProfilePic);
                                }
                            }
                        }
                    });

            Map<String, Object> updateUserProfilePicMap = new HashMap<>();
            updateUserProfilePicMap.put("photoUri", newProfilePic);

            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .update(updateUserProfilePicMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            isImageUploaded[0] = true;
                            if (mUpdateUserImageListener != null && isImageUpdated[0]) {
                                mUpdateUserImageListener.onUpdateUserImageSucceed(newProfilePic);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mUpdateUserImageListener != null) {
                        mUpdateUserImageListener.onUpdateUserImageFailed(e.getMessage());
                    }
                }
            });
        }
    }

    public void deleteUser() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mCloudUsers.document(Objects.requireNonNull(user.getEmail()))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (mUserDeletionListener != null) {
                                mUserDeletionListener.onUserDeletionSucceed(user.getUid());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mUserDeletionListener != null) {
                                mUserDeletionListener.onUserDeletionFailed(e.getMessage());
                            }
                        }
                    });
        }
    }

    public void downloadConversation(final String chatId) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final List<ChatMessage> conversation = new ArrayList<>();

            mCloudChats.document(chatId)
                    .collection("messages")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    conversation.add(document.toObject(ChatMessage.class));
                                }

                                Collections.sort(conversation);

                                if (mDownloadConversationListener != null) {
                                    mDownloadConversationListener.onDownloadConversationSucceed(conversation);
                                }
                            } else {
                                if (mDownloadConversationListener != null) {
                                    mDownloadConversationListener
                                            .onDownloadConversationFailed(Objects.requireNonNull(task
                                            .getException()).getMessage());
                                }
                            }
                        }
                    });

            mCloudChats.document(chatId)
                    .collection("messages")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "onEvent: ", error);
                            }

                            if (value != null && !value.isEmpty()) {
                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        ChatMessage message = dc.getDocument()
                                                .toObject(ChatMessage.class);

                                        if (mUploadMessageListener != null) {
                                            mUploadMessageListener.onUploadMessageSucceed(message, false);
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    public void uploadChatMessage(final User userRecipient, final String messageContent) {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String tempChatId = userRecipient.getEmail() + "&" + user.getEmail();
            if (Objects.requireNonNull(user.getEmail()).compareTo(userRecipient.getEmail()) < 0) {
                tempChatId = user.getEmail() + "&" + userRecipient.getEmail();
            }
            final String chatId = tempChatId;

            mCloudChats.document(chatId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                uploadMessageToCloud(chatId, messageContent, userRecipient);
                            } else {
                                final Conversation conversation = new Conversation(user.getEmail(),
                                        userRecipient.getEmail());

                                mCloudChats.document(chatId)
                                        .set(conversation)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                uploadMessageToCloud(chatId, messageContent, userRecipient);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (mUploadMessageListener != null) {
                                                    mUploadMessageListener.onUploadMessageFailed(e.getMessage());
                                                }
                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void uploadMessageToCloud(final String chatId,
                                      final String messageContent,
                                      final User userRecipient) {
        final ChatMessage message = new ChatMessage(messageContent,
                userRecipient.getEmail());//lo koers kloom

        mCloudChats.document(chatId)
                .collection("messages")
                .document(message.getTime().toString())
                .set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (mUploadMessageListener != null) {
                            mUploadMessageListener.onUploadMessageSucceed(message, true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (mUploadMessageListener != null) {
                            mUploadMessageListener.onUploadMessageFailed(e.getMessage());
                        }
                    }
                });
    }



    public void uploadMessageToDB(final String messageContent,
                                 final String sender,
                                 final String recipient) {
        final ChatMessage chatMessage=new ChatMessage(messageContent,recipient);
        

        final String id1 = sender.replace(".", "");
        final String id2 = recipient.replace(".", "");

        String tempChatId = id2 + "&" + id1;
        if (Objects.requireNonNull(id1).compareTo(id2) < 0) {
            tempChatId = id1+ "&" + id2;
        }
        final String chatId = tempChatId;

        mDBChats.child(chatId)
                .child(String.valueOf(System.currentTimeMillis()))
                .setValue(chatMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    public void downloadMessagesFromDB(String recipient,String sender){
        final List<ChatMessage> chatList = new ArrayList<>();
        final String id1 = sender.replace(".", "");
        final String id2 = recipient.replace(".", "");

        String tempChatId = id2 + "&" + id1;
        if (Objects.requireNonNull(id1).compareTo(id2) < 0) {
            tempChatId = id1+ "&" + id2;
        }
        final String chatId = tempChatId;


        mDBChats.child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dc:snapshot.getChildren()){
                    ChatMessage message = dc.getValue(ChatMessage.class);
                    chatList.add(message);
                }
              mDownloadConversationListener.onDownloadConversationSucceed(chatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
}
