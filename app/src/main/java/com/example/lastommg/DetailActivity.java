package com.example.lastommg;


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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    private TextView txt_artist;
    private TextView txt_date;
    private TextView txt_type;
    private TextView txt_introduce;
    private RecyclerView rcc_song;
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
        supportFinishAfterTransition();
        Item aitem=new Item(mitem.getGood(),mitem.getNickname(),mitem.getName(), mitem.getDecripthion(),mitem.getUri(), mitem.getPhoneNumber(), geoPoint,mitem.getAddress(),mitem.getDistance(), timestamp);
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

    }

    private void setInit() {

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        img_thumb = (ImageView) findViewById(R.id.img_thumb);
        Glide.with(mContext)
                .load(mitem.getUri())
                .into(img_thumb);

        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(mitem.getName());
        txt_artist = (TextView) findViewById(R.id.txt_artist);
        txt_artist.setText(mitem.getAddress());
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_date.setText(mitem.getPhoneNumber());
        txt_type = (TextView) findViewById(R.id.txt_type);
        txt_type.setText(mitem.getName());
        txt_introduce = (TextView) findViewById(R.id.txt_introduce);
        txt_introduce.setText(mitem.getPhoneNumber());
        rcc_song = (RecyclerView) findViewById(R.id.rcc_song);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rcc_song.setLayoutManager(mLayoutManager);
        rcc_song.setNestedScrollingEnabled(false);
//        SongAdapter mSongAdapter = new SongAdapter(mContext, albumVO.getSong());
//        rcc_song.setAdapter(mSongAdapter);
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