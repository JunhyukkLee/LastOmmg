package com.example.lastommg.FirstTab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lastommg.MainActivity;
import com.example.lastommg.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class fragment_1 extends Fragment {
    ImageButton imgBtn1;
    TextView text1;
    String link1, image1, title1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);
        imgBtn1 = rootView.findViewById(R.id.imgBanner1);
        text1 = rootView.findViewById(R.id.tvName1);

        //web crawling thread
        new Thread(() -> {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://guide.michelin.com/kr/ko").get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements image_link = doc.select("div.card-post picture img");
            Elements link = doc.select("div.card-post a");

            //제목 뽑기 (1,7,13)
            for (Element elem : link) {
                title1 = elem.attr("aria-label");

                link1 = elem.attr("href");
                Log.d("제목뽑기",title1);
                break;
            }
            //이미지 링크 뽑기 (1,3,5)
            for (Element elem : image_link) {
                image1 = elem.attr("data-srcset");
                Log.d("제목뽑기",image1);
                break;
            }

            Message msg = handler.obtainMessage();
            handler.sendMessage(msg);

        }).start();



        imgBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://guide.michelin.com" + link1));
                startActivity(browserIntent);
            }
        });

        return rootView;
    }
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                text1.setText(title1.substring(16, title1.length()));
                Glide.with(getActivity()).load(image1).into(imgBtn1);
            }
            catch (Exception e){
                Log.d("로드 에러", e+getMessageName(msg));
            }
        }
    };
}