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
import com.example.lastommg.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

public class fragment_3 extends Fragment {
    ImageButton imgBtn3;
    TextView text3;
    String link3, image3, title3;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_3, container, false);
        imgBtn3 = rootView.findViewById(R.id.imgBanner3);
        text3 = rootView.findViewById(R.id.tvName3);

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
            Integer count = 0;
            for (Element elem : link) {
                if(count == 12){
                    title3 = elem.attr("aria-label");
                    link3 = elem.attr("href");
                    count = 0;
                    break;
                }
                count++;
            }
            //이미지 링크 뽑기 (1,3,5)
            for (Element elem : image_link) {
                if(count == 4) {
                    image3 = elem.attr("data-srcset");
                    count = 0;
                    break;
                }
                count++;
            }

            Message msg = handler.obtainMessage();
            handler.sendMessage(msg);

        }).start();



        imgBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://guide.michelin.com" + link3));
                startActivity(browserIntent);
            }
        });

        return rootView;
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                text3.setText(title3.substring(16, title3.length()));
                Glide.with(getActivity()).load(image3).into(imgBtn3);
            }
            catch (Exception e){
                Log.d("로드 에러", e+getMessageName(msg));
            }
        }
    };
}