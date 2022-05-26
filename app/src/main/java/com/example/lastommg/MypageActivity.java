package com.example.lastommg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
// 앨범 클릭 추가할때 ,setOnclick implements해야함
public class MypageActivity extends AppCompatActivity implements AlbumAdapter.OnItemClickListener, Serializable {

    private Context mContext;
    private RecyclerView my_album;
    private AlbumAdapter mAlbumAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);
        App local = (App) getApplicationContext();
        mAlbumAdapter = new AlbumAdapter();
        mAuth = FirebaseAuth.getInstance();
        //프로필 이미지 띄우기(동그랗게)
        Uri a;
        a = Uri.parse(local.getPro_img());
        RoundImageView mPressProfileImg = findViewById(R.id.round_profile_image);
        Glide.with(MypageActivity.this).load(a).into(mPressProfileImg);
       // mPressProfileImg.setOnClickListener(this);

//        RoundImageView riv = findViewById(R.id.round_profile_image);
//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profile_img);
//        riv.setImageBitmap(bm);
        //프로필 정보 띄우기
        TextView nameSlot = findViewById(R.id.name);
        TextView emailSlot = findViewById(R.id.email);
        TextView introduction = findViewById(R.id.intro);
        nameSlot.setText("MinHyugi");
        emailSlot.setText("minhyuk9803@gmail.com");
        introduction.setText("Hi, im cute");
        //
        mContext = this;
        //밑에 사진 띄우기
        init();

        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        Item item=document.toObject(Item.class);
                        if(item.getNickname().equals(local.getNickname())) {
                            mAlbumAdapter.addItem(item);
                        }
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
    //프로필 이미지 설정 methods/////////////////////////////////////////////////////////////////
    /**
     * 앨범에서 이미지 가져오기
     * private void doTakeAlbumAction()
     *     {
     *         // 앨범 호출
     *         Intent intent = new Intent(Intent.ACTION_PICK);
     *         intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
     *         startActivityForResult(intent, PICK_FROM_ALBUM);
     *     }
     *
     *     @Override
     *     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *         super.onActivityResult(requestCode, resultCode, data);
     *         if (resultCode != RESULT_OK) {
     *             return;
     *         }
     *
     *         switch (requestCode) {
     *             case CROP_FROM_CAMERA: {
     *                 // 크롭이 된 이후의 이미지를 넘겨 받습니다.
     *                 // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
     *                 // 임시 파일을 삭제합니다.
     *                 final Bundle extras = data.getExtras();
     *
     *                 if (extras != null) {
     *                     Bitmap photo = extras.getParcelable("data");
     *                     mPressProfileImg.setImageBitmap(photo);
     *                 }
     *
     *                 // 임시 파일 삭제
     *                 File f = new File(mImageCaptureUri.getPath());
     *                 if (f.exists()) {
     *                     f.delete();
     *                 }
     *
     *                 break;
     *             }
     *
     *             case PICK_FROM_ALBUM: {
     *                 // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
     *                 // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
     *
     *                 mImageCaptureUri = data.getData();
     *             }
     *
     *             case PICK_FROM_CAMERA: {
     *
     *
     *                 // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
     *                 // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
     *
     *                 Intent intent = new Intent("com.android.camera.action.CROP");
     *                 intent.setDataAndType(mImageCaptureUri, "image/*");
     *
     *                 intent.putExtra("outputX", 90);
     *                 intent.putExtra("outputY", 90);
     *                 intent.putExtra("aspectX", 1);
     *                 intent.putExtra("aspectY", 1);
     *                 intent.putExtra("scale", true);
     *                 intent.putExtra("return-data", true);
     *                 startActivityForResult(intent, CROP_FROM_CAMERA);
     *                 break;
     *             }
     *         }
     *     }
     *
     *     //프로필 이미지 눌렀을 때
     *     @Override
     *     public void onClick(View view) {
     *         DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
     *         {
     *             @Override
     *             public void onClick(DialogInterface dialog, int which)
     *             {
     *                 doTakeAlbumAction();
     *             }
     *         };
     *
     *         DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
     *         {
     *             @Override
     *             public void onClick(DialogInterface dialog, int which)
     *             {
     *                 dialog.dismiss();
     *             }
     *         };
     *
     *         new AlertDialog.Builder(this)
     *                 .setTitle("업로드할 이미지 선택")
     *                 .setNeutralButton("앨범선택", albumListener)
     *                 .setNegativeButton("취소", cancelListener)
     *                 .show();
     *     }
     */


    //밑에 사진 그리드 띄우기/////////////////////////////////////////////////////////////////////////////////////////

    private void init() {
        my_album = findViewById(R.id.my_album);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
        my_album.setLayoutManager(mLayoutManager);
        my_album.addItemDecoration(new ItemDecoration(this));
        mAlbumAdapter.setOnItemClickListener(this);
        my_album.setAdapter(mAlbumAdapter);
    }
    // 각 이미지들 눌렀을때 인터랙션
    @Override
    public void onItemClick(View view, myItem item) {
        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra("item", item);

        View thumbView = view.findViewById(R.id.img_thumb);
        Pair<View, String> pair_thumb = Pair.create(thumbView, thumbView.getTransitionName());
        ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this, pair_thumb);

        startActivity(intent, optionsCompat.toBundle());
    }



}

