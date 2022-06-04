package com.example.lastommg.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lastommg.MainActivity;
import com.example.lastommg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class NicknameForSNSuser extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private View sign_up;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<User> userList=new ArrayList<>();
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_sns);
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        User user=document.toObject(User.class);
                        userList.add(user);
                    }
                }
                else
                {
                    Log.d("실패","응 실패야",task.getException());
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        sign_up = findViewById(R.id.clear);
        String pro_uri="drawable://"+R.drawable.yes;
        findViewById(R.id.nickdc).setOnClickListener(onClickListener3);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
                String name = ((EditText) findViewById(R.id.name)).getText().toString();
                String email = ((EditText) findViewById(R.id.ID)).getText().toString();
                StorageReference submitProfile = storageReference.child("image/" + "user.PNG");
                submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User user= new User(email,"한줄소개를입력하세요",name,nickname,uri.toString(),mAuth.getUid());
                        db.collection("User").document(nickname).set(user);
                        userList.add(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                Intent intent = new Intent(NicknameForSNSuser.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 사용자가 현재 로그인되어있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }
    View.OnClickListener onClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nickdc:
                    checkNick();
                    break;
            }
        }
    };
    public int getUserCount() {
        return userList.size();
    }
    private void checkNick(){
        int j=getUserCount();
        int i;
        String nickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
        if(j==0)
        {
            Toast.makeText(NicknameForSNSuser.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
            sign_up.setVisibility(View.VISIBLE);
        }
        for(i=0;i<j;i++)
        {
            if(userList.get(i).getNickname().equals(nickname))
            {
                Toast.makeText(NicknameForSNSuser.this, "닉네임 중복!", Toast.LENGTH_SHORT).show();
                break;
            }
            if(i==(j-1)){
                Toast.makeText(NicknameForSNSuser.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                sign_up.setVisibility(View.VISIBLE);
            }
        }
    }


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
