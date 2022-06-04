package com.example.lastommg.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lastommg.MainActivity;
import com.example.lastommg.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.w3c.dom.Text;
//

public class LoginActivity extends AppCompatActivity {
    private View login_K, logout_K;
    private ImageView profileImage;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    public String facebook_name;
    public String facebook_email;
    public String facebook_uid;
    TextView FindPW;
    TextView sb;
    public static Context context5;

    private CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context5 = this;
        AppEventsLogger.activateApp(getApplication());
        //login_K = findViewById(R.id.loginK); //카카오로그인
        //logout_K = findViewById(R.id.logoutK);
        Button loginE = findViewById(R.id.loginB);
        TextView loginK = findViewById(R.id.btnKako);
        loginK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity1.class);
                startActivity(intent);
            }
        });
        FindPW = findViewById(R.id.findPW);
        FindPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(LoginActivity.this, FindPW.class);
                startActivity(intent2);
            }
        });

        mAuth = FirebaseAuth.getInstance(); //파이어베이스어스객체의 공유 인스턴스를 가져옴
        findViewById(R.id.loginB).setOnClickListener(onClickListener);
        findViewById(R.id.sb).setOnClickListener(onClickListener);
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.loginF);
        loginButton.setPermissions("email", "public_profile");
        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override

            public void onSuccess(LoginResult login_result) {
                handleFacebookAccessToken(login_result.getAccessToken());
                final Profile profile = Profile.getCurrentProfile();

                GraphRequest request = GraphRequest.newMeRequest(
                        login_result.getAccessToken(),
                        (object, response) -> {
                            try {
                                Intent intent = new Intent(LoginActivity.this, FacebookUserInfo.class);
                                Log.d("이름",object.getString("name"));
                                Log.d("email",object.getString("email"));
                                facebook_name=object.getString("name");
                                facebook_email=object.getString("email");




                                startActivity(intent);





                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name, birthday,picture,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            //파이어베이스는 싱글톤패턴을 이용하기 때문에 로그아웃을 하지않으면 계속 인증상태임
            //인증되어있을때는 로그인화면에서 그냥 바로 메인으로 이동하려면 OnConnectionFailedListener 사용
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //현재 유저가 로그인 되어있는걸 확인한 후 그냥 바로 메인으로 연결해줌
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                } else {
                    //로그아웃 상태
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if (currentUser != null) {
            currentUser.reload();
        }
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);

    }
    @Override
    protected void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 페이스북 콜백 등록
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log . d (TAG, "FFAACCEEBBOOKK");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log . d (TAG, "FFAAIILL");
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }
                    }
                });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sb:
                    startSignupActivity();
                case R.id.loginB:
                    login();
                    break;
            }
        }
    };

    private void login() {
        String email = ((EditText) findViewById(R.id.ID2)).getText().toString();
        String password = ((EditText) findViewById(R.id.pw2)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인 성공!");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                updateUI(user);
                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                    updateUI(null);
                                    startToast("아이디 또는 비밀번호를 확인하세요.");
                                }
                            }
                        }
                    });
        } else {
            startToast("아이디 또는 비밀번호를 입력해주세요");
        }


    }//파베관련

    private void startSignupActivity() {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
    }//파베관련

    //파베관련
    private void updateUI(FirebaseUser user) {
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


}


