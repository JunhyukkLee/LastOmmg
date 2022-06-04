package com.example.lastommg.MyPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lastommg.Login.LoginActivity;
import com.example.lastommg.MainActivity;
import com.example.lastommg.SecondTab.Comment;
import com.example.lastommg.SecondTab.Item;
import com.example.lastommg.Login.App;
import com.example.lastommg.R;
import com.example.lastommg.SecondTab.myItem;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

// 앨범 클릭 추가할때 ,setOnclick implements해야함
public class MypageActivity extends AppCompatActivity implements AlbumAdapter.OnItemClickListener, Serializable, View.OnClickListener {
    UploadTask uploadTask;
    private Context mContext;
    private RecyclerView my_album,my_scrap;
    private AlbumAdapter mAlbumAdapter;
    ScrapAdapter scrapAdapter;
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;
    RoundImageView mPressProfileImg;
    ImageView background_profile;
    ImageButton back;

    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);
        App local = (App) getApplicationContext();
        final DocumentReference sfDocRef = db.collection("User").document(local.getNickname());
        Log.d("유알아이", local.getPro_img());

        storageReference = FirebaseStorage.getInstance().getReference();
        mAlbumAdapter = new AlbumAdapter();
        scrapAdapter=new ScrapAdapter();
        mAuth = FirebaseAuth.getInstance();
        //프로필 이미지 띄우기(동그랗게)
        Uri a;
        a = Uri.parse(local.getPro_img());
        mPressProfileImg = findViewById(R.id.round_profile_image);
        Glide.with(MypageActivity.this).load(a).into(mPressProfileImg);
        mPressProfileImg.setOnClickListener(this);

        background_profile = findViewById(R.id.profile_image);
        Glide.with(MypageActivity.this).load(a).into(background_profile);

        back = findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MypageActivity.this, MainActivity.class));
                finish();
            }
        });


//        RoundImageView riv = findViewById(R.id.round_profile_image);
//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profile_img);
//        riv.setImageBitmap(bm);
        //프로필 정보 띄우기
        TextView nameSlot = findViewById(R.id.name);
        ImageButton logout = findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                Intent intent3 = new Intent(MypageActivity.this, LoginActivity.class);
                //로그아웃누르면  로그인화면으로 이동
                startActivity(intent3);

            }
        });

        TextView introduction = findViewById(R.id.intro);
        nameSlot.setText(local.getNickname());
        introduction.setText(local.getIntro());
        EditText editIntro=findViewById(R.id.edit_intro);
        ImageButton btn_intro=findViewById(R.id.btn_intro);
        btn_intro.setTag("edit");
        btn_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_intro.getTag().equals("edit")){
                    btn_intro.setTag("ok");
//                    btn_intro.setImageResource(R.drawable.setting);
                    editIntro.setText(local.getIntro());
                    editIntro.setVisibility(View.VISIBLE);
                    introduction.setVisibility(View.INVISIBLE);
                    nameSlot.setVisibility(View.INVISIBLE);

                }
                else{
                    btn_intro.setTag("edit");
//                    btn_intro.setImageResource(R.drawable.yes);
                    local.setIntro(editIntro.getText().toString());
                    introduction.setText(local.getIntro());
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);
                            // Note: this could be done without a transaction
                            //       by updating the population using FieldValue.increment()
                            transaction.update(sfDocRef, "intro", local.getIntro().toString());
                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Log", "Transaction success!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Log", "Transaction failure.", e);
                                }
                            });
                    editIntro.setVisibility(View.INVISIBLE);
                    introduction.setVisibility(View.VISIBLE);
                    nameSlot.setVisibility(View.VISIBLE);
                }

            }
        });
        //
        mContext = this;
        //밑에 사진 띄우기
        init();
        scrap();

        //Tab implant
        TabLayout tabLayout = (TabLayout) findViewById(R.id.mypage_tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                changeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });


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
        db.collection("User").document(local.getNickname()).collection("Scrap").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        Item item=document.toObject(Item.class);
                        scrapAdapter.addScrap(item);

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
    private void changeView(int index) {
        switch (index) {
            case 0:
                my_album.setVisibility(View.VISIBLE);
                my_scrap.setVisibility(View.INVISIBLE);
                mAlbumAdapter.notifyDataSetChanged();
                break;
            case 1:
                my_album.setVisibility(View.INVISIBLE);
                my_scrap.setVisibility(View.VISIBLE);
                scrapAdapter.notifyDataSetChanged();
                break;
        }
    }
    ///////////////프로필 이미지 설정 methods/////////////////////////////////////////////////////////////////
    private void signOut(){
        mAuth.signOut();
        if (isLoggedIn == true) {
            LoginManager.getInstance().logOut();
        }
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                FirebaseAuth.getInstance().signOut();
            }
        });
    }
    //앨범에서 이미지 가져오기
    private void doTakeAlbumAction()
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        App local = (App) getApplicationContext();
        super.onActivityResult(requestCode, resultCode, data);
        final DocumentReference sfDocRef = db.collection("User").document(local.getNickname());
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_FROM_CAMERA: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    mPressProfileImg.setImageBitmap(photo);
                    background_profile.setImageBitmap(photo);

                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                if (data.getData() != null) {
                    try {
                        mImageCaptureUri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                        mPressProfileImg.setImageBitmap(bitmap);
                        background_profile.setImageBitmap(bitmap);
                        StorageReference filereference = storageReference.child("profile/" + local.getNickname());
                        uploadTask = filereference.putFile(mImageCaptureUri);
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("Waiting");
                        progressDialog.show();

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MypageActivity.this, "프로필사진 업로드 실패", Toast.LENGTH_SHORT).show();
                                Log.v("알림", "사진 업로드 실패");
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference pathReference = storageReference.child("image");
                                if (pathReference == null) {
                                    Toast.makeText(MypageActivity.this, "저장소에 사진이 없습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    StorageReference submitProfile = storageReference.child("profile/" + local.getNickname());
                                    submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            local.setPro_img(uri.toString());
                                            db.runTransaction(new Transaction.Function<Void>() {
                                                @Override
                                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                    DocumentSnapshot snapshot = transaction.get(sfDocRef);
                                                    // Note: this could be done without a transaction
                                                    //       by updating the population using FieldValue.increment()
                                                    transaction.update(sfDocRef, "pro_img", uri.toString());
                                                    // Success
                                                    return null;
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Log", "Transaction success!");
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Log", "Transaction failure.", e);
                                                        }
                                                    });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                }
                                progressDialog.dismiss();
                            }
                        });



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            case PICK_FROM_CAMERA: {
//                 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
//                 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);
                break;
            }
        }
    }

    //프로필 이미지 눌렀을 때
    @Override
    public void onClick(View view) {
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    }

    private void scrap() {
        my_scrap = findViewById(R.id.my_scrap);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
        my_scrap.setLayoutManager(mLayoutManager);
        my_scrap.addItemDecoration(new ItemDecoration(this));
        my_scrap.setAdapter(scrapAdapter);
    }

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
