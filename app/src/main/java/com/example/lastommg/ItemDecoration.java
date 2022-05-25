package com.example.lastommg;

import android.app.Activity;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemDecoration extends RecyclerView.ItemDecoration{
    private int spanCount;
    private int spacing;
    private int outerMargin;

    public ItemDecoration(Activity mActivity) {
        spanCount = 3;

        spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12, mActivity.getResources().getDisplayMetrics());
        outerMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50, mActivity.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int maxCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        int row = position / spanCount;
        int lastRow = (maxCount - 1) / spanCount;

        outRect.left = column * spacing / spanCount;
        outRect.right = spacing - (column + 1) * spacing / spanCount;
        outRect.top = spacing * 2;

        if (row == lastRow) {
            outRect.bottom = outerMargin;
        }
    }
}