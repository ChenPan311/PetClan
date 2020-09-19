package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android2project.R;
import com.example.android2project.model.Post;
import com.example.android2project.model.PostsAdapter;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.viewmodel.FeedViewModel;
import com.example.android2project.viewmodel.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private FeedViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private PostsAdapter mPostsAdapter;
    private List<Post> mPosts = new ArrayList<>();

    private Observer<List<Post>> mOnPostDownloadSucceed;
    private Observer<String> mOnPostDownloadFailed;

    private Observer<Post> mOnPostUploadSucceed;
    private Observer<String> mOnPostUploadFailed;

    private Observer<Post> mOnPostLikesUpdateSucceed;
    private Observer<String> mOnPostLikesUpdateFailed;

    private int mPosition;

    private final String TAG = "FeedFragment";

    public FeedFragment() {}

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.Feed)).get(FeedViewModel.class);

        mOnPostDownloadSucceed = new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                mPosts.clear();
                mPosts.addAll(posts);
                mPostsAdapter.notifyDataSetChanged();
            }
        };

        mOnPostDownloadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostUploadSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                mPosts.add(0, post);
                mPostsAdapter.notifyItemInserted(0);
            }
        };

        mOnPostUploadFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnPostLikesUpdateSucceed = new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                mPostsAdapter.notifyItemChanged(mPosition);
            }
        };

        mOnPostLikesUpdateFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        startObservation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mRecyclerView = rootView.findViewById(R.id.feed_recycler_view);
        final RecyclerView commentsRecyclerView = rootView.findViewById(R.id.comments_recycler_view);
        final FloatingActionButton addPostBtn = rootView.findViewById(R.id.add_post_btn);

        final boolean[] isCommentsShown = {false};

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostAddingDialog();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPostsAdapter = new PostsAdapter(mPosts, getContext());

        mPostsAdapter.setPostListener(new PostsAdapter.PostListener() {
            @Override
            public void onAuthorImageClicked(int position, View view) {
                //TODO: Open the Author's Profile/big profile image
            }

            @Override
            public void onCommentsTvClicked(int position, View view) {
                isCommentsShown[0] = !isCommentsShown[0];
                commentsRecyclerView.setVisibility(isCommentsShown[0] ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onLikeBtnClicked(int position, View view, boolean isLike) {
                mPosition = position;
                Post post = mPosts.get(position);
                mViewModel.updatePostLikes(post, isLike);
            }

            @Override
            public void onCommentBtnClicked(int position, View view) {
                //TODO: Open a comment dialog and add the comment to the comments list
            }
        });

        mRecyclerView.setAdapter(mPostsAdapter);
        //commentsRecyclerView.setAdapter(commentsAdapter);

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getPostDownloadSucceed().observe(this, mOnPostDownloadSucceed);
            mViewModel.getPostDownloadFailed().observe(this, mOnPostDownloadFailed);
            mViewModel.getPostUploadSucceed().observe(this, mOnPostUploadSucceed);
            mViewModel.getPostUploadFailed().observe(this, mOnPostUploadFailed);
            mViewModel.getPostLikesUpdateSucceed().observe(this, mOnPostLikesUpdateSucceed);
            mViewModel.getPostLikesUpdateFailed().observe(this, mOnPostLikesUpdateFailed);
        }
    }

    private void showPostAddingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        ViewGroup root;
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.add_post_dialog,
                        (RelativeLayout) requireActivity().findViewById(R.id.layoutDialogContainer));

        builder.setView(view);
        builder.setCancelable(true);

        final EditText postContentEt = view.findViewById(R.id.new_post_content_et);
        final Button postBtn = view.findViewById(R.id.post_btn);
        postBtn.setEnabled(false);

        final AlertDialog alertDialog = builder.create();

        postContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postBtn.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.uploadNewPost(postContentEt.getText().toString());
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}