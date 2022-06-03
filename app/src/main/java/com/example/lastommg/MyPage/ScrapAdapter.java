package com.example.lastommg.MyPage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lastommg.Login.App;
import com.example.lastommg.R;
import com.example.lastommg.SecondTab.Comment;
import com.example.lastommg.SecondTab.CommentAdapter;
import com.example.lastommg.SecondTab.Item;
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
import java.util.HashMap;
import java.util.Map;

public class ScrapAdapter extends RecyclerView.Adapter<ScrapAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Item> scraps=new ArrayList<Item>();
    AlertDialog dialog;
    public RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CommentAdapter commentAdapter;
    static String TAG = "ScrapAdapter";
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.uploaded_items, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,  @SuppressLint("RecyclerView") final int position) {
        Item scrap=scraps.get(position);
        holder.setItem(scrap);
        holder.layout_album_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupXml(scraps.get(position).getNickname(), scraps.get(position).getName(),scraps.get(position).getDecripthion(), scraps.get(position).getUri(), scraps.get(position).getPhoneNumber(), scraps.get(position).getAddress(),position);
            }
        });
    }

    public void popupXml(String nickname, String name, String decription,String uri, String phoneNumber, String address, int position) {
        //Log.d(TAG, "okay");
        App local = (App) mContext.getApplicationContext();
        Map<String, Object> good_id = new HashMap<>();
        Map<String, Object> scrap_id = new HashMap<>();
        final Map<String, Object>[] aa = new Map[]{new HashMap<>()};
        Uri u = Uri.parse(uri);
        Uri pro;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.popup1, null);
        recyclerView = view.findViewById(R.id.recycler_view);
        commentAdapter = new CommentAdapter();
        recyclerView.setAdapter(commentAdapter);
        EditText add_comment=view.findViewById(R.id.add_comment);
        TextView post=view.findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment(add_comment.getText().toString(),local.getNickname(),local.getPro_img());
                Log.d("id확인",local.getNickname());
                commentAdapter.addComment(comment);
                db.collection("items").document(name).collection("Comment").document(add_comment.getText().toString()).set(comment);
                add_comment.setText(null);
                commentAdapter.notifyDataSetChanged();
            }
        });
        db.collection("items").document(name).collection("Comment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        Comment comment = document.toObject(Comment.class);
                        commentAdapter.addComment(comment);
                        Log.d("확인",document.getId()+"=>"+document.getData());
                    }
                }
                else
                {
                    Log.d("실패","응 실패야",task.getException());
                }
            }
        });
        ImageView image_profile = view.findViewById(R.id.image_profile);
        pro = Uri.parse(local.getPro_img());
        Glide.with(mContext).load(pro).into(image_profile);

        ImageView imageView = view.findViewById(R.id.imageView);
        ImageButton good = view.findViewById(R.id.good);
        ImageButton scrap = view.findViewById(R.id.scrap);
        final DocumentReference sfDocRef = db.collection("items").document(name);
        DocumentReference docRef = db.collection("items").document(name).collection("Good").document(local.getNickname());
        DocumentReference docRef_scrap = db.collection("items").document(name).collection("Scrap").document(local.getNickname());
        DocumentReference docRef_user = db.collection("User").document(local.getNickname()).collection("Scrap").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("체크", String.valueOf(document.getData()));
                    if (String.valueOf(document.getData()).equals("{nickname=" + local.getNickname() + "}")) {
                        good.setImageResource(R.drawable.yes);
                        good.setTag("liked");
                    } else {
                        good.setImageResource(R.drawable.no);
                        good.setTag("like");
                    }


                } else {
                    good.setImageResource(R.drawable.no);

                }
            }
        });
        docRef_scrap.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("체크", String.valueOf(document.getData()));
                    if (String.valueOf(document.getData()).equals("{nickname=" + local.getNickname() + "}")) {
                        scrap.setImageResource(R.drawable.scrap);
                        scrap.setTag("is_scrap");
                    } else {
                        scrap.setImageResource(R.drawable.non_scrap);
                        scrap.setTag("non_scrap");
                    }


                } else {
                    scrap.setImageResource(R.drawable.non_scrap);

                }
            }
        });
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (good.getTag().equals("like")) {
                    good.setTag("liked");
                    good.setImageResource(R.drawable.yes);
                    good_id.put("nickname", local.getNickname());
                    db.collection("items").document(name).collection("Good").document(local.getNickname()).set(good_id);
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                            // Note: this could be done without a transaction
                            //       by updating the population using FieldValue.increment()
                            double newgood = snapshot.getDouble("good") + 1;
                            transaction.update(sfDocRef, "good", newgood);

                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Transaction success!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Transaction failure.", e);
                                }
                            });
                } else {
                    good.setImageResource(R.drawable.no);
                    good.setTag("like");
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                            // Note: this could be done without a transaction
                            //       by updating the population using FieldValue.increment()
                            double newgood = snapshot.getDouble("good") - 1;
                            transaction.update(sfDocRef, "good", newgood);

                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Transaction success!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Transaction failure.", e);
                                }
                            });
                    docRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("화긴", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("화긴", "Error deleting document", e);
                                }
                            });
                }
            }
        });
        scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scrap.getTag().equals("non_scrap")) {
                    scrap.setTag("is_scrap");
                    scrap.setImageResource(R.drawable.scrap);
                    scrap_id.put("nickname", local.getNickname());
                    db.collection("items").document(name).collection("Scrap").document(local.getNickname()).set(scrap_id);
                    db.collection("User").document(local.getNickname()).collection("Scrap").document(name).set(scraps.get(position));
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                            // Note: this could be done without a transaction
                            //       by updating the population using FieldValue.increment()
                            double newscrap = snapshot.getDouble("scrap") + 1;
                            transaction.update(sfDocRef, "scrap", newscrap);

                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Transaction success!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Transaction failure.", e);
                                }
                            });
                } else {
                    scrap.setImageResource(R.drawable.non_scrap);
                    scrap.setTag("non_scrap");
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                            // Note: this could be done without a transaction
                            //       by updating the population using FieldValue.increment()
                            double newscrap = snapshot.getDouble("scrap") - 1;
                            transaction.update(sfDocRef, "scrap", newscrap);

                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Transaction success!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Transaction failure.", e);
                                }
                            });
                    docRef_scrap.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("화긴", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("화긴", "Error deleting document", e);
                                }
                            });
                    docRef_user.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("화긴", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("화긴", "Error deleting document", e);
                                }
                            });
                }
            }
        });

        TextView textView = view.findViewById(R.id.textView);
        Glide.with(mContext).load(u).into(imageView);

        textView.setTextSize(35);
        textView.setText(name + "\n");
        textView.append(decription + "\n");
        textView.append(address + "\n");
        textView.append(phoneNumber + "\n");



        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("음식점정보").setView(view);
        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }


    public void addScrap(Item scrap) {
        scraps.add(0, scrap);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return scraps.size();

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_thumb;
        private TextView txt_title;
        Uri u;
        private LinearLayout layout_album_panel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_thumb = itemView.findViewById(R.id.img_thumb);
            txt_title = itemView.findViewById(R.id.txt_title);
            layout_album_panel = (LinearLayout) itemView.findViewById(R.id.layout_album_panel);
        }
        public void setItem(Item scrap) {

            u = Uri.parse(scrap.getUri());
            Glide.with(mContext).load(u).into(img_thumb);
            txt_title.setText(scrap.getName());
        }
    }
}
