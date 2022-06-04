package com.example.lastommg.Login;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lastommg.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity3 extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int MULTIPLE_PERMISSIONS = 1111;
    private String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        checkPermission();
        Button bt = findViewById((R.id.loginBB));
        try {
            Thread.sleep(2000); //대기 초 설정
            startActivity(new Intent(MainActivity3.this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            Log.e("Error", "SplashActivity ERROR", e);
        }
        //그냥 로그인 버튼
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void checkPermission() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equals(this.PERMISSIONS[0])) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[0]))) {
                                new AlertDialog.Builder(this)
                                        .setTitle("알림")
                                        .setMessage("카메라 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }
                                        })
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }
                        }
                    } else if (permissions[i].equals(this.PERMISSIONS[1])) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[1]))) {
                                new AlertDialog.Builder(this)
                                        .setTitle("알림")
                                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }
                                        })
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }
                        }
                    } else if (permissions[i].equals(this.PERMISSIONS[2])) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[2]))) {
                                new AlertDialog.Builder(this)
                                        .setTitle("알림")
                                        .setMessage("위치 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }
                                        })
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }
                        } else if (permissions[i].equals(this.PERMISSIONS[3])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                if ((ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS[3]))) {
                                    new AlertDialog.Builder(this)
                                            .setTitle("알림")
                                            .setMessage("위치 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                                            .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                                    startActivity(intent);
                                                }
                                            })
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            })
                                            .setCancelable(false)
                                            .create()
                                            .show();
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }
    }
}

