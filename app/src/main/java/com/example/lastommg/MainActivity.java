package com.example.lastommg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lastommg.FirstTab.MyAdapter;
import com.example.lastommg.Login.App;
import com.example.lastommg.Login.LoginActivity;
import com.example.lastommg.Login.User;
import com.example.lastommg.MyPage.AlbumAdapter;
import com.example.lastommg.MyPage.MypageActivity;
import com.example.lastommg.MyPage.ScrapAdapter;
import com.example.lastommg.SecondTab.GpsTracker;
import com.example.lastommg.SecondTab.Item;
import com.example.lastommg.SecondTab.ItemAdapter;
import com.example.lastommg.SecondTab.Upload.PostActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.grpc.internal.DnsNameResolver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final AuthorizationResult.RESULT_CODE SUCCESS = null;
    private BackPressCloseHandler backPressCloseHandler;
    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Toolbar toolbar;

    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;

    public RecyclerView recyclerView;
    private View yoon;
    private ItemAdapter itemAdapter;
    AlbumAdapter mAlbumAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton plus;
    private FirebaseStorage storage;

    double latitude;
    double longitude;
    private GpsTracker gpsTracker;
    GeoPoint u_GeoPoint;
    public static Object context_main;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        App local = (App) getApplicationContext();

        Log.d("좃같다", "ㅇㅇㅇㅇ");
        db.collection("User").whereEqualTo("uid", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("들어오기 성공", "ㅉ");
                        User user = document.toObject(User.class);
                        local.setNickname(user.getNickname());
                        local.setPro_img(user.getPro_img());
                        local.setEmail(user.getId());
                        local.setIntro(user.getIntro());
                        local.setName(user.getUsername());

                        Log.d("uri세팅", local.getPro_img());
                        Log.d("닉네임세팅", local.getNickname());
                        Log.d("이름 세팅", local.getName());
                    }
                } else {
                    Log.d("실패", "응 실패야", task.getException());
                }
            }
        });

        mAlbumAdapter = new AlbumAdapter();

        gpsTracker = new GpsTracker(MainActivity.this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        u_GeoPoint = new GeoPoint(latitude, longitude);
        yoon = findViewById(R.id.plus);
        context_main = this;

        mAuth = FirebaseAuth.getInstance();

        backPressCloseHandler = new BackPressCloseHandler(this);

        //1번 탭 미리 표시(2번도 활성화)
        firstView();
        secondView();
        //2번 탭 구성요소 가리기
        yoon.setVisibility((View.INVISIBLE));
        recyclerView.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setVisibility((View.INVISIBLE));
//        sortDistance.setVisibility(View.INVISIBLE);
//        sortTime.setVisibility(View.INVISIBLE);
        //Tab implant
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
    }

    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mypage_button:
                Intent intent = new Intent(MainActivity.this, MypageActivity.class);
                startActivity(intent);
                break;
            case R.id.sorttime:
                itemAdapter.sortTime();
                itemAdapter.notifyDataSetChanged();
                recyclerView.startLayoutAnimation();
                break;
            case R.id.sortdistance:
                itemAdapter.setDistance(u_GeoPoint);
                itemAdapter.notifyDataSetChanged();
                recyclerView.startLayoutAnimation();
                int i = itemAdapter.getItemCount();
                Log.d("개수", Integer.toString(i));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void changeView(int index) {
        switch (index) {
            case 0:
                mPager.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                yoon.setVisibility((View.INVISIBLE));
                swipeRefreshLayout.setVisibility((View.INVISIBLE));
                break;
            case 1:
                recyclerView.setVisibility(View.VISIBLE);
                yoon.setVisibility((View.VISIBLE));
                swipeRefreshLayout.setVisibility((View.VISIBLE));
                mPager.setVisibility(View.INVISIBLE);
                itemAdapter.notifyDataSetChanged();
                recyclerView.startLayoutAnimation();
                break;
        }
    }

    private void firstView() {
        //ViewPager2
        mPager = findViewById(R.id.viewpager);
        //Adapter
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);
        //ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        mPager.setCurrentItem(500);
        mPager.setOffscreenPageLimit(3);
        //Change in position -> change in view page
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }
        });
    }

    private void secondView() {
        recyclerView = findViewById(R.id.recycler_view);
        itemAdapter = new ItemAdapter();
        recyclerView.setAdapter(itemAdapter);
        //조회 전 화면 클리어
        //itemAdapter.removeAllItem();
        //샘플 데이터 생성

        //새로고침
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemAdapter.removeAllItem();
                itemAdapter.notifyDataSetChanged();
                recyclerView.startLayoutAnimation();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //윤수
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        plus = (FloatingActionButton) findViewById(R.id.plus);

        storage = FirebaseStorage.getInstance();

        plus.setOnClickListener((View.OnClickListener) this);
        //152까지

        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        itemAdapter.addItem(item);
                        Log.d("확인", document.getId() + "=>" + document.getData());
                    }
                } else {
                    Log.d("실패", "응 실패야", task.getException());
                }
            }
        });
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.plus:
                anim();
                Intent post = new Intent(getApplicationContext(), PostActivity.class);
                startActivity(post);
                break;
        }
    }

    private void anim() {
        if (isFabOpen) {
            isFabOpen = false;
        } else {
            isFabOpen = true;
        }
    }
}

