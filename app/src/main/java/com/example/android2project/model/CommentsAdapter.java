package com.example.android2project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android2project.R;
import com.example.android2project.repository.AuthRepository;
import com.skyhope.showmoretextview.ShowMoreTextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<Comment> mComments;

    private Context mContext;

    private String mUserEmail;

    public CommentsAdapter(List<Comment> comments, Context context) {
        this.mComments = comments;
        this.mContext = context;
        this.mUserEmail = AuthRepository.getInstance(context).getUserEmail();
    }

    public interface CommentListener {
        void onAuthorImageClicked(int position, View view);
        void onEditOptionClicked(int position, View view);
        void onDeleteOptionClicked(int position, View view);
    }

    private CommentListener listener;

    public void setCommentListener(CommentListener commentListener) {
        this.listener = commentListener;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView authorPicIv;
        TextView authorNameTv;
        TextView postTimeAgo;
        ShowMoreTextView contentTv;
        ImageButton optionsBtn;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            authorPicIv = itemView.findViewById(R.id.author_pic_iv);
            authorNameTv = itemView.findViewById(R.id.author_name_tv);
            postTimeAgo = itemView.findViewById(R.id.time_ago_tv);
            contentTv = itemView.findViewById(R.id.comment_content_tv);
            optionsBtn = itemView.findViewById(R.id.comment_options_menu);

            setContentTvProperties();

            optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUpMenu(v);
                }
            });

            authorPicIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAuthorImageClicked(getAdapterPosition(), v);
                    }
                }
            });
        }

        private void setContentTvProperties() {
            contentTv.setShowingLine(3);
            contentTv.setShowMoreColor(mContext.getColor(R.color.colorPrimary));
            contentTv.setShowLessTextColor(mContext.getColor(R.color.colorPrimary));
            contentTv.addShowMoreText(mContext.getString(R.string.show_more));
            contentTv.addShowLessText(mContext.getString(R.string.show_less));
        }

        private void showPopUpMenu(final View view) {
            PopupMenu popupMenu = new PopupMenu(mContext, optionsBtn);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.option_edit:
                            if (listener != null) {
                                listener.onEditOptionClicked(getAdapterPosition(), view);
                            }
                            break;
                        case R.id.option_delete:
                            if (listener != null) {
                                listener.onDeleteOptionClicked(getAdapterPosition(), view);
                            }
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_cardview, parent, false);
        return new CommentViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = mComments.get(position);

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(mContext)
                .load(comment.getAuthorImageUri())
                .apply(options)
                .into(holder.authorPicIv);

        holder.authorNameTv.setText(comment.getAuthorName());

        holder.postTimeAgo.setText(timestampToTimeAgo(comment.getTime()));

        if (comment.getAuthorEmail().equals(mUserEmail)) {
            holder.optionsBtn.setVisibility(View.VISIBLE);
        } else {
            holder.optionsBtn.setVisibility(View.GONE);
        }

        holder.contentTv.setText(comment.getAuthorContent());
        holder.setContentTvProperties();
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    private String timestampToTimeAgo(Date date) {
        String language = Locale.getDefault().getLanguage();
        PrettyTime prettyTime = new PrettyTime(new Locale(language));
        return prettyTime.format(date);
    }
}
