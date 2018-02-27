package com.wan7451.swipecell;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

/**
 * Created by wan7451 on 2018/2/27.
 * desc:
 */

public class SwipeCellAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public SwipeCellAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );


        ImageView delView = new ImageView(context);
        delView.setBackgroundColor(0xFFE52C1A);
        delView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        delView.setImageResource(R.drawable.icon_bag_draft);

        SwipeCellView swipeCellView = new SwipeCellView(context);
        swipeCellView.setContentView(R.layout.view_shopping_bag);
        swipeCellView.setDelView(delView, 80);
        swipeCellView.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swipeCellView.setOutlineProvider(new OutLine());
            swipeCellView.setClipToOutline(true);
            swipeCellView.setElevation(5);
        }
        return new SwipeCellHolder(swipeCellView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }


    static class SwipeCellHolder extends RecyclerView.ViewHolder {

        public SwipeCellHolder(View itemView) {
            super(itemView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static class OutLine extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
        }
    }
}
