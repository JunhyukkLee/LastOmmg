package com.example.lastommg.SecondTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lastommg.Login.App;
import com.example.lastommg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Comment> comments=new ArrayList<Comment>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.comment, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,  @SuppressLint("RecyclerView") final int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row_rl);
        ((ViewHolder) holder).itemView.startAnimation(animation);
        Comment comment=comments.get(position);
        holder.setItem(comment);

    }

    public void addComment(Comment comment) {
        comments.add(0, comment);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return comments.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile;
        public TextView username, ccomment;
        Uri profileUri;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            ccomment = itemView.findViewById(R.id.comment);
        }
        public void setItem(Comment comment) {
            username.setText(comment.getName());
            ccomment.setText(comment.getComment());
            //원래코드
            profileUri = Uri.parse(comment.getProfile_img());
            Log.d("댓글사진", profileUri.toString());
            Glide.with(mContext).load(profileUri).into(image_profile);
        }
    }
}
