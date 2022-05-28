package com.example.lastommg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class fragment_1 extends Fragment {
    ImageView img;
    TextView text;
    String my_link, bit, title;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);
        img = rootView.findViewById(R.id.imgBanner1);
        text = rootView.findViewById(R.id.tvName1);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect("https://guide.michelin.com/kr/ko").get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Elements image = doc.select("div.card-post picture source");
                    Elements link = doc.select("div.card-post a");

                    for(Element elem : link){
                        String my_title = elem.select("dt[class=tit] a").text();
                    }
                    my_link = link.attr("href");
                    bit = image.attr("data-srcset");
                    title = link.attr("aria-label");

                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }).start();
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://guide.michelin.com/"+my_link));
                startActivity(browserIntent);
            }
        });

        return rootView;
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            text.setText(title);
            Glide.with(getActivity()).load(bit).into(img);
        }
    };
}