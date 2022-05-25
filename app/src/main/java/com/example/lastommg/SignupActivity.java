package com.example.lastommg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "dkssudgktpdy";
    private FirebaseAuth mAuth;
    private View sign_up;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<User> userList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);


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
        findViewById(R.id.sb).setOnClickListener(onClickListener);
        sign_up = findViewById(R.id.clear);
        findViewById(R.id.checking).setOnClickListener(onClickListener2);
        findViewById(R.id.nickdc).setOnClickListener(onClickListener3);
        String pro_uri="drawable://"+R.drawable.yes;
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
                String name = ((EditText) findViewById(R.id.name)).getText().toString();
                String phonenumber = ((EditText) findViewById(R.id.phoneNumber)).getText().toString();
                String email = ((EditText) findViewById(R.id.ID)).getText().toString();

                User user= new User(email,name,nickname,Uri.parse(pro_uri));
                db.collection("User").document(nickname).set(user);
                userList.add(user);
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
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
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sb:
                    signup();
                    break;
            }
        }
    };
    View.OnClickListener onClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checking:
                    checkings();
                    break;
            }
        }
    };
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


    private void sendVerifi() {
        //링크 전송
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> verifitask) {
                if (verifitask.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(SignupActivity.this,
                            "이메일인증링크가" + user.getEmail() + "로 전송되었습니다.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Log.e(TAG, "sendEmailVerification", verifitask.getException());
                    Toast.makeText(SignupActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void signup() {
        //초기 회원가입처리
        String email = ((EditText) findViewById(R.id.ID)).getText().toString();
        String password = ((EditText) findViewById(R.id.pw)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.pwc)).getText().toString();

        //유효성검사후 신규사용자를 만드는걸 허락


        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sendVerifi();//회원가입 동시에 그 이메일로 링크 전송

                                } else {
                                    if (task.getException() != null) {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        startToast(task.getException().toString());
                                        deleteUser();

                                    }

                                }
                            }


                        });
            } else {
                startToast("비밀번호가 일치하지않습니다");
            }
        } else {
            startToast("이메일 또는 비밀번호를 입력해주세요");

        }


    }
    public int getUserCount() {
        return userList.size();
    }
    private void checkNick(){
        int j=getUserCount();
        int i;
        String nickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
        if(j==0)
        {
            Toast.makeText(SignupActivity.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
            sign_up.setVisibility(View.VISIBLE);
        }
        for(i=0;i<j;i++)
        {
            if(userList.get(i).getNickname().equals(nickname))
            {
                Toast.makeText(SignupActivity.this, "닉네임 중복!", Toast.LENGTH_SHORT).show();
                break;
            }
            if(i==(j-1)){
                Toast.makeText(SignupActivity.this, "사용가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                sign_up.setVisibility(View.VISIBLE);
            }
        }
    }
    private void checkings() {
        //인증결과 확인 후 회원가입 처리
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                if (user.isEmailVerified() == true) {
                    Log . d (TAG, "LoginActivity - onStart() called");
                    Toast.makeText(SignupActivity.this, "이메일인증완료", Toast.LENGTH_SHORT).show();
                    startToast("이메일인증완료");

                } else {
                    Toast.makeText(SignupActivity.this, "이메일인증실패", Toast.LENGTH_SHORT).show();
                    deleteUser();
                }
            }
        });
    }

    private void deleteUser() {
        FirebaseUser user = mAuth.getCurrentUser();

        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(SignupActivity.this, "오류", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null)
            deleteUser();

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null)
            deleteUser();

    }

    public void onBackPressed() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null)
            deleteUser();
        super.onBackPressed();
    }
}

