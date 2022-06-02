package com.example.lastommg.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lastommg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPW extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pw);
        String editText_email = ((EditText) findViewById(R.id.editText_email)).getText().toString();
        Button Find = (Button) findViewById(R.id.find_PW);
        Button BtL = (Button) findViewById(R.id.backToLogin);
        Find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
        BtL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindPW.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    private void resetPassword() {
        String editText_email = ((EditText) findViewById(R.id.editText_email)).getText().toString();

        FirebaseAuth.getInstance().sendPasswordResetEmail(editText_email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FindPW.this, "비밀번호 재설정 메세지 전송", Toast.LENGTH_LONG).show();

                        } else {
                        }
                    }
                });

    }

}
