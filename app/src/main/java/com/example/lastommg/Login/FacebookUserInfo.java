package com.example.lastommg.Login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lastommg.MainActivity;
import com.example.lastommg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FacebookUserInfo extends AppCompatActivity {
    private static final String TAG = "dkssudgktpdy";
    public static Context context2;

    TextView email2 = null;
    TextView name2 = null;
    String profile_name;
    String profile_email;
    String profile_uid;

    ImageView profile2 = null;
    StorageReference storageReference;
    Button sign;
    Button sign2;
    String nickname;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    ArrayList<User> userList2=new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage2);
        mAuth=FirebaseAuth.getInstance();
        context2 = this;
        //
        storageReference = FirebaseStorage.getInstance().getReference();
        sign = findViewById(R.id.sign);
        sign2 = findViewById(R.id.sign2);
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        profile_name = ((LoginActivity) LoginActivity.context5).facebook_name;
        profile_email = ((LoginActivity) LoginActivity.context5).facebook_email;
        Log.d("페에에에북","이동");


        //유저리스트 불러오는 코드
        storageReference = FirebaseStorage.getInstance().getReference();
        db2.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        User user=document.toObject(User.class);
                        userList2.add(user);
                        checkNick();
                    }
                }
                else
                {
                    Log.d("실패","응 실패야",task.getException());
                }
            }

        });




        //페북유저가  유저리스트에 있는지 확인 하는 코드



    }
    public int getUserCount() {
        return userList2.size();
    }

    private void checkNick(){
        int j=getUserCount();
        int i;
        Log.d("제이는 ", String.valueOf(j));

        if(j==0)
        {//유저가 없을때 , 없으므로 닉네임 생성
            kkk();

        }
        for(i=0;i<j;i++)
        {
            if(userList2.get(i).getUid().equals(mAuth.getUid()))
            {//해당 닉네임이 이미 존재 -> 페북 닉네임이 존재  메인으로 이동
                Log.d("페북 닉 존재","메인으로 이동");
                Intent intent4 = new Intent(FacebookUserInfo.this, MainActivity.class);
                startActivity(intent4);
                break;
            }
            if(i==(j-1)){//없으므로 닉네임 생성
                Log.d("페북 닉 없음","이동");
                kkk();
            }
        }
    }
    private void userAdd(){
        //얘는 유저 닉넴,이메일,이름 유저에 추가하는 코드
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
                StorageReference submitProfile = storageReference.child("profile/" + "user.PNG");
                submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("성공1", uri.toString());
                        Log.d("이메일1", profile_email);
                        Log.d("닉네임1", nickname);
                        Log.d("이름1", profile_name);
                        User user= new User(profile_email,"한줄소개를입력하세요",profile_name,nickname,uri.toString(), mAuth.getUid());


                        db.collection("User").document(nickname).set(user);
                        Intent intent3 = new Intent(FacebookUserInfo.this, MainActivity.class);
                        startActivity(intent3);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("실패", "ㅠㅠ");
                    }
                });

            }
        });
    }
    private void checkNick2(){
        int j=getUserCount();
        int i;
        String nickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
        if(j==0)
        {
            Toast.makeText(FacebookUserInfo.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
            sign2.setVisibility(View.GONE);
            sign.setVisibility(View.VISIBLE);
            userAdd();

        }
        for(i=0;i<j;i++)
        {
            Log.d("왜안되지", userList2.get(i).getNickname()+"+++++"+nickname);
            if(userList2.get(i).getNickname().equals(nickname))
            {
                Toast.makeText(FacebookUserInfo.this, "닉네임 중복!", Toast.LENGTH_SHORT).show();
                break;
            }
            if(i==(j-1)){
                Toast.makeText(FacebookUserInfo.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                sign2.setVisibility(View.GONE);
                sign.setVisibility(View.VISIBLE);
                userAdd();
            }
        }
    }
    private void kkk(){
        sign2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNick2();

            }
        });
    }

}