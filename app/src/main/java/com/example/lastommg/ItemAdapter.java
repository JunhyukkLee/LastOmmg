package com.example.lastommg;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        Uri u;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.b_image);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }

        public void setItem(Item item) {
            u = Uri.parse(item.getUri());
            Glide.with(context).load(u).into(imageView);
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
                popupXml(items.get(position).getNickname(), items.get(position).getName(),items.get(position).getDecripthion(), items.get(position).getUri(), items.get(position).getPhoneNumber(), items.get(position).getAddress());
            }
        });
    }


    public void popupXml(String nickname, String name, String decription,String uri, String phoneNumber, String address) {
        //Log.d(TAG, "okay");
        App nickName = (App) context.getApplicationContext();
        Map<String, Object> good_id = new HashMap<>();
        final Map<String, Object>[] aa = new Map[]{new HashMap<>()};
        Uri u = Uri.parse(uri);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup1, null);
        recyclerView = view.findViewById(R.id.recycler_view);
        commentAdapter = new CommentAdapter();
        recyclerView.setAdapter(commentAdapter);
        EditText add_comment=view.findViewById(R.id.add_comment);
        TextView post=view.findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment(add_comment.getText().toString(),nickName.getNickname());
                Log.d("id확인",nickName.getNickname());
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
        //ImageView image_profile = view.findViewById(R.id.image_profile); 이건 나중에 프로필 사진 해결되면
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.yes);//이것도 프로필사진으로 변경해야함
        ImageButton good = view.findViewById(R.id.good);
        final DocumentReference sfDocRef = db.collection("items").document(name);
        DocumentReference docRef = db.collection("items").document(name).collection("Good").document(nickName.getNickname());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("체크", String.valueOf(document.getData()));
                    if (String.valueOf(document.getData()).equals("{nickname=" + nickName.getNickname() + "}")) {
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
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (good.getTag().equals("like")) {
                    good.setTag("liked");
                    good.setImageResource(R.drawable.yes);
                    good_id.put("nickname", nickName.getNickname());
                    db.collection("items").document(name).collection("Good").document(nickName.getNickname()).set(good_id);
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
