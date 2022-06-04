 package com.example.lastommg.SecondTab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lastommg.Login.App;
import com.example.lastommg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    ArrayList<Item> items = new ArrayList<Item>();
    int lastPosition = -1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    Context context;
    AlertDialog dialog;
    static String TAG = "Adapter";
    int index = 0;

    public int pos = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Random random = new Random();
        if ((random.nextInt(100)) % 2 == 1) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = inflater.inflate(R.layout.item_layout_rl, viewGroup, false);
            context = viewGroup.getContext();
            return new ViewHolder(itemView);
        } else {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = inflater.inflate(R.layout.item_layout_lr, viewGroup, false);
            context = viewGroup.getContext();
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RelativeLayout parentLayout;
        TextView gcount,ccount,scount;
        Uri u;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.b_image);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            gcount=itemView.findViewById(R.id.goodcount);
            ccount=itemView.findViewById(R.id.commentcount);
            scount=itemView.findViewById(R.id.scrapcount);
        }

        public void setItem(Item item) {
            u = Uri.parse(item.getUri());
            Glide.with(context).load(u).into(imageView);
            gcount.setText(String.valueOf(item.getGood()));
            ccount.setText(String.valueOf(item.getComment()));
            scount.setText(String.valueOf(item.getScrap()));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        Random random = new Random();
        if (viewHolder.getAdapterPosition() > lastPosition) {
            if ((random.nextInt(100)) % 2 == 1) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row_rl);
                ((ViewHolder) viewHolder).itemView.startAnimation(animation);
                Item item = items.get(position);
                viewHolder.setItem(item);
            } else {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row_lr);
                ((ViewHolder) viewHolder).itemView.startAnimation(animation);

                Item item = items.get(position);
                viewHolder.setItem(item);
            }
        }

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupXml(items.get(position).getNickname(), items.get(position).getName(),items.get(position).getDecripthion(), items.get(position).getUri(), items.get(position).getPhoneNumber(), items.get(position).getAddress(),position);
            }
        });
    }


    public void popupXml(String nickname, String name, String decription,String uri, String phoneNumber, String address, int position) {
        //Log.d(TAG, "okay");
        App local = (App) context.getApplicationContext();
        Map<String, Object> good_id = new HashMap<>();
        Map<String, Object> scrap_id = new HashMap<>();
        final Map<String, Object>[] aa = new Map[]{new HashMap<>()};
        Uri u = Uri.parse(uri);
        Uri pro;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup1, null);
        recyclerView = view.findViewById(R.id.recycler_view);
        commentAdapter = new CommentAdapter();
        recyclerView.setAdapter(commentAdapter);
        EditText add_comment=view.findViewById(R.id.add_comment);
        TextView post=view.findViewById(R.id.post);
        final DocumentReference sfDocRef = db.collection("items").document(name);
        DocumentReference docRef = db.collection("items").document(name).collection("Good").document(local.getNickname());
        DocumentReference docRef_scrap = db.collection("items").document(name).collection("Scrap").document(local.getNickname());
        DocumentReference docRef_user = db.collection("User").document(local.getNickname()).collection("Scrap").document(name);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment(add_comment.getText().toString(),local.getNickname(),local.getPro_img());
                Log.d("id확인",local.getNickname());
                commentAdapter.addComment(comment);
                db.collection("items").document(name).collection("Comment").document(add_comment.getText().toString()).set(comment);
                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);
                        // Note: this could be done without a transaction
                        //       by updating the population using FieldValue.increment()
                        double newcomment = snapshot.getDouble("comment") + 1;
                        transaction.update(sfDocRef, "comment", newcomment);
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
        Glide.with(context).load(pro).into(image_profile);

        ImageView imageView = view.findViewById(R.id.imageView);
        ImageButton good = view.findViewById(R.id.good);
        ImageButton scrap = view.findViewById(R.id.scrap);

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

        ImageButton commentBtn = view.findViewById(R.id.commentBtn);
        LinearLayout commentView = view.findViewById(R.id.comment_box);
        ScrollView infoView = view.findViewById(R.id.info_box);
        commentView.setVisibility(View.INVISIBLE);
        infoView.setVisibility(View.VISIBLE);
        commentBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (pos){
                    case 0:
                        commentView.setVisibility(View.VISIBLE);
                        infoView.setVisibility(View.INVISIBLE);
                        pos = 1;
                        break;
                    case 1:
                        commentView.setVisibility(View.INVISIBLE);
                        infoView.setVisibility(View.VISIBLE);
                        pos = 0;
                        break;
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
                    db.collection("User").document(local.getNickname()).collection("Scrap").document(name).set(items.get(position));
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
        Glide.with(context).load(u).into(imageView);

        textView.setTextSize(35);
        textView.setText(name + "\n");
        textView.append(decription + "\n");
        textView.append(address + "\n");
        textView.append(phoneNumber + "\n");



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void delItem(Item item) {
        items.clear();
        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        addItem(item);
                        Log.d("확인", document.getId() + "=>" + document.getData());
                    }
                } else {
                    Log.d("실패", "응 실패야", task.getException());
                }
            }
        });
        int i = getItemCount();
        Log.d("아이템개수", Integer.toString(i));
        notifyDataSetChanged();

    }

    public void addItem(Item item) {
        items.add(0, item);
        notifyDataSetChanged();
    }

    public void removeAllItem() {
        items.clear();
        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        addItem(item);
                        Log.d("확인", document.getId() + "=>" + document.getData());
                    }
                } else {
                    Log.d("실패", "응 실패야", task.getException());
                }
            }
        });
    }

    public void setDistance(GeoPoint B) {
        int i = 0;
        int j = getItemCount();
        double distance;
        GeoPoint A;
        Location locationA = new Location("Point A");
        Location locationB = new Location("Point B");
        locationB.setLatitude((B.getLatitude()));
        locationB.setLongitude((B.getLongitude()));
        for (i = 0; i < j; i++) {
            A = items.get(i).getGeoPoint();
            locationA.setLatitude(A.getLatitude());
            locationA.setLongitude((A.getLongitude()));
            distance = locationA.distanceTo(locationB);
            items.get(i).distance = distance;
        }
        Collections.sort(items, new itemDistanceComparator());
    }


    public void sortTime() {
        items.sort(Comparator.comparing(Item::getTimestamp));
        Collections.reverse(items);
    }

    class itemDistanceComparator implements Comparator<Item> {
        @Override
        public int compare(Item i1, Item i2) {
            if (i1.distance > i2.distance) {
                return 1;
            } else if (i1.distance < i2.distance) {
                return -1;
            }
            return 0;
        }
    }



}
