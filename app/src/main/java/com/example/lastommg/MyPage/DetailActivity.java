package com.example.lastommg.MyPage;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lastommg.Login.App;
import com.example.lastommg.Login.LoginActivity;
import com.example.lastommg.Login.User;
import com.example.lastommg.SecondTab.Comment;
import com.example.lastommg.SecondTab.CommentAdapter;
import com.example.lastommg.SecondTab.Item;
import com.example.lastommg.SecondTab.ItemAdapter;
import com.example.lastommg.R;
import com.example.lastommg.SecondTab.myItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView,recyclerView2;
    private Context mContext;
    private myItem mitem;
    private AlbumAdapter mAlbumAdapter;
    private ItemAdapter itemAdapter;
    private ImageView btn_del;
    private ImageView btn_back;
    private ImageView img_thumb;
    private TextView txt_title;
    private TextView txt_description;
    private TextView txt_date;
    private TextView txt_type;
    private TextView txt_introduce;
    private RecyclerView rcc_song;

    private CommentAdapter.ViewHolder commentHolder;
    GeoPoint geoPoint;
    Timestamp timestamp;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        setInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Button btn_del = findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delItem();
            }
        });
        mContext = this;
        getData();
        geoPoint=new GeoPoint(mitem.getLat(), mitem.getLon());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        Date date;
        try {
            date = formatter.parse(mitem.getTimestamp());
            timestamp=new Timestamp(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void getData() {
        Intent intent = getIntent();
        mitem = (myItem) intent.getSerializableExtra("item");

    }



    private void delItem(){
        App local = (App) getApplicationContext();
        mAlbumAdapter = new AlbumAdapter();
        supportFinishAfterTransition();
        Item aitem=new Item(mitem.getGood(),mitem.getComment(),mitem.getScrap(),mitem.getNickname(),mitem.getName(), mitem.getDecripthion(),mitem.getUri(), mitem.getPhoneNumber(), geoPoint,mitem.getAddress(),mitem.getDistance(), timestamp);
        Map<String, Object> scrap_id = new HashMap<>();
        db.collection("items").document(aitem.getName()).collection("Scrap").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        scrap_id.put("nickname",document.getData());
                        String nickname=scrap_id.get("nickname").toString().substring(10);
                        String nnickname=nickname.substring(0,nickname.length()-1);
                        Log.d("스크랩유저", nnickname);
                        db.collection("User").document(nnickname).collection("Scrap").document(aitem.getName())
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


                    }
                } else {
                    Log.d("실패", "응 실패야", task.getException());
                }
            }
        });


        db.collection("items").document(aitem.getName())
                .delete()
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

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("image");
        if (pathReference == null) {
            Toast.makeText(DetailActivity.this, "저장소에사진이없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference submitProfile = storageReference.child("image/" + aitem.getName());
            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    submitProfile.delete();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
        mAlbumAdapter.removeAllItem(local.getNickname());
        mAlbumAdapter.notifyDataSetChanged();
        Intent intent5 = new Intent(DetailActivity.this, MypageActivity.class);
        //로그아웃누르면  로그인화면으로 이동
        startActivity(intent5);


    }

    private void setInit() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        img_thumb = (ImageView) findViewById(R.id.img_thumb);
        Glide.with(mContext)
                .load(mitem.getUri())
                .into(img_thumb);
//가게 이름
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(mitem.getName());
        //한줄평
        txt_description = (TextView) findViewById(R.id.txt_description);
        txt_description.setText(mitem.getDecripthion());
        //주소
        txt_date = (TextView) findViewById(R.id.txt_Address);
        txt_date.setText(mitem.getAddress());
        //전화번호
        txt_type = (TextView) findViewById(R.id.txt_PhoneNum);
        if (mitem.getPhoneNumber() == "") {
            txt_type.setText(mitem.getPhoneNumber());
        }
        else {
            txt_type.setText("tel: " + mitem.getPhoneNumber());
        }
        //코멘트
        txt_introduce = (TextView) findViewById(R.id.txt_introduce);
        txt_introduce.setText("comments");
        //코멘트 칸
        rcc_song = (RecyclerView) findViewById(R.id.rcc_song);
        CommentAdapter mCommentAdapter = new CommentAdapter();
        rcc_song.setAdapter(mCommentAdapter);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rcc_song.setLayoutManager(mLayoutManager);
        rcc_song.setNestedScrollingEnabled(false);
        db.collection("items").document(mitem.getName()).collection("Comment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        Comment comment = document.toObject(Comment.class);
                        mCommentAdapter.addComment(comment);
                        Log.d("확인",document.getId()+"=>"+document.getData());
                    }
                }
                else
                {
                    Log.d("실패","응 실패야",task.getException());
                }
            }
        });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                supportFinishAfterTransition();
                break;
        }
    }
}