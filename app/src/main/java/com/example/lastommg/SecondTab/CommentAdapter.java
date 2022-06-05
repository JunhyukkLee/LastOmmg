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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lastommg.Login.App;
import com.example.lastommg.MyPage.MypageActivity;
import com.example.lastommg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Comment> comments=new ArrayList<Comment>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        App local = (App) mContext.getApplicationContext();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row_rl);
        ((ViewHolder) holder).itemView.startAnimation(animation);
        Comment comment=comments.get(position);
        holder.setItem(comment);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getName().equals(local.getNickname())){
                    final DocumentReference sfDocRef = db.collection("items").document(comment.getStorename());
                    comments.remove(position);
                    notifyDataSetChanged();
                    ((ViewHolder) holder).itemView.startAnimation(animation);
                    db.collection("items").document(comment.getStorename()).collection("Comment").document(comment.getComment())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("1화긴", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("1화긴", "Error deleting document", e);
                                }
                            });
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                            // Note: this could be done without a transaction
                            //       by updating the population using FieldValue.increment()
                            double newcomment = snapshot.getDouble("comment") - 1;
                            transaction.update(sfDocRef, "comment", newcomment);
                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Tag", "Transaction success!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Transaction failure.", e);
                                }
                            });
                }
                else{
                    Toast.makeText(mContext, "내 댓글만 지울 수 있습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        public ImageView image_profile,delete;
        public TextView username, ccomment;
        Uri profileUri;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            ccomment = itemView.findViewById(R.id.comment);
            delete=itemView.findViewById(R.id.delete);
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
