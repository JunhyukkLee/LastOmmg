package com.example.lastommg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

public class RoundImageView extends AppCompatImageButton {

    public static float radius = 360.0f;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        RectF rect = new RectF(25, 25, this.getWidth()-25, this.getHeight()-25);
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}