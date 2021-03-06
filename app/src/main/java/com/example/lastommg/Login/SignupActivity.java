package com.example.lastommg.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "dkssudgktpdy";
    private FirebaseAuth mAuth;
    private View sign_up;
    EditText phone;
    EditText nickName;
    EditText name;
    Button nickcheck;
    Button checking;
    Button sb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<User> userList=new ArrayList<>();
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        phone =findViewById(R.id.phoneNumber);
        nickName =findViewById(R.id.nickName);
        name =findViewById(R.id.name);
        nickcheck = findViewById(R.id.nickdc);
        db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        User user=document.toObject(User.class);
                        Log.d("???????????????", user.getNickname());
                        userList.add(user);
                    }
                }
                else
                {
                    Log.d("??????","??? ?????????",task.getException());
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();

        sign_up = findViewById(R.id.clear);
        checking=findViewById(R.id.checking);
        checking.setOnClickListener(onClickListener2);
        sb=findViewById(R.id.sb);
        sb.setOnClickListener(onClickListener);
        findViewById(R.id.nickdc).setOnClickListener(onClickListener3);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
                String name = ((EditText) findViewById(R.id.name)).getText().toString();
                String email = ((EditText) findViewById(R.id.ID)).getText().toString();
                StorageReference submitProfile = storageReference.child("profile/" + "user.PNG");
                submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("??????", uri.toString());
                        User user= new User(email,"??????????????????????????????",name,nickname,uri.toString(),mAuth.getUid());
                        db.collection("User").document(nickname).set(user);
                        userList.add(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("??????", "??????");
                    }
                });
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);

                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // ???????????? ?????? ???????????????????????? ??????
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
                    sb.setVisibility(View.GONE);
                    checking.setVisibility(View.VISIBLE);
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
        //?????? ??????
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> verifitask) {
                if (verifitask.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(SignupActivity.this,
                            "????????????????????????" + user.getEmail() + "??? ?????????????????????.",
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
        //?????? ??????????????????
        String email = ((EditText) findViewById(R.id.ID)).getText().toString();
        String password = ((EditText) findViewById(R.id.pw)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.pwc)).getText().toString();

        //?????????????????? ?????????????????? ???????????? ??????


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
                                    sendVerifi();//???????????? ????????? ??? ???????????? ?????? ??????

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
                startToast("??????????????? ????????????????????????");
            }
        } else {
            startToast("???????????? ??????????????? ?????? ???????????????");

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
            Toast.makeText(SignupActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            sign_up.setVisibility(View.VISIBLE);
        }
        for(i=0;i<j;i++)
        {
            Log.d("????????????", userList.get(i).getNickname()+"+++++"+nickname);
            if(userList.get(i).getNickname().equals(nickname))
            {
                Toast.makeText(SignupActivity.this, "????????? ??????!", Toast.LENGTH_SHORT).show();
                break;
            }
            if(i==(j-1)){
                Toast.makeText(SignupActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                sign_up.setVisibility(View.VISIBLE);
            }
        }
    }
    private void checkings() {
        //???????????? ?????? ??? ???????????? ??????
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                if (user.isEmailVerified() == true) {
                    Log . d (TAG, "LoginActivity - onStart() called");
                    Toast.makeText(SignupActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    startToast("?????????????????????");
                    phone.setVisibility(View.VISIBLE);
                    nickName.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    nickcheck.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(SignupActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
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
                    //Toast.makeText(SignupActivity.this, "??????", Toast.LENGTH_LONG).show();
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


    }
    /*
    @Override
    protected void onStop() {
        super.onStop();

        if(user != null)
            deleteUser();

    }*/

    public void onBackPressed() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null)
            deleteUser();
        super.onBackPressed();
    }
}

