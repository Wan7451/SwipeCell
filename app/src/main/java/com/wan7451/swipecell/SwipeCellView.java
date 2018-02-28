package com.wan7451.swipecell;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by wan7451 on 2018/2/27.
 * desc: 滑动删除View
 */

public class SwipeCellView extends FrameLayout {


    private ViewDragHelper dragHelper;
    private ViewDragHandler dragHandler;


    public SwipeCellView(Context context) {
        this(context, null, 0);
    }

    public SwipeCellView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeCellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    protected void initView() {
        dragHandler = new ViewDragHandler(this);
        dragHelper = ViewDragHelper.create(this, 0.8f, dragHandler);
        dragHandler.setDragHelper(dragHelper);
    }

    public void setContentView(@LayoutRes int layoutId) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(getContext())
                .inflate(layoutId, this, false);
        view.setLayoutParams(params);
        view.setClickable(true);
        addView(view);

        dragHandler.setCaptureView(view);
    }


    public void setDelView(View delView, int width) {
        int slide = dip2px(getContext(), width);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                slide,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, dip2px(getContext(), 1),
                0, dip2px(getContext(), 1));
        params.gravity = Gravity.RIGHT;
        delView.setLayoutParams(params);
        addView(delView, 0);

        dragHandler.setMaxSlide(slide);
    }

    public View getDelView() {
        return getChildAt(0);
    }


    enum State {
        CLOSE,
        SPREAD
    }


    static class ViewDragHandler extends ViewDragHelper.Callback {
        private State state;

        private ViewDragHelper dragHelper;
        private View captureView;
        private ViewGroup parent;
        private int maxSlide;

        public ViewDragHandler(ViewGroup parent) {
            this.parent = parent;
        }

        public void setCaptureView(View captureView) {
            this.captureView = captureView;
        }

        public void setMaxSlide(int maxSlide) {
            this.maxSlide = maxSlide;
        }

        public void setDragHelper(ViewDragHelper dragHelper) {
            this.dragHelper = dragHelper;
            float density = parent.getContext().getResources().getDisplayMetrics().density;
            this.dragHelper.setMinVelocity(density * 400);
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (child.getLeft() < 0) {
                state = State.SPREAD;
            } else {
                state = State.CLOSE;
            }
            return child == captureView;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (dx != 0) {
                parent.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //屏蔽点击操作
            if (left > -5) {
                return 0;
            }
            //控制最大滑动距离
            if (left < -maxSlide) {
                return -maxSlide;
            }
            //只能向左划
            if (left < 0) {
                return left;
            }
            return 0;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return maxSlide;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (releasedChild != captureView) return;

            int left = Math.abs(releasedChild.getLeft());
            if (state == State.SPREAD) { //直接关闭
                if (left <= maxSlide / 5 * 4) {
                    dragHelper.settleCapturedViewAt(0, 0);
                } else {
                    dragHelper.settleCapturedViewAt(-maxSlide, 0);
                }
            }
            if (state == State.CLOSE) {
                if (left >= maxSlide / 5) {
                    dragHelper.settleCapturedViewAt(-maxSlide, 0);
                } else {
                    dragHelper.settleCapturedViewAt(0, 0);
                }
            }
            parent.invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }


    float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offX = getX() - x;
                float offY = getY() - y;
//                if (Math.abs(offX / offY) > 0.9f) {
                if (Math.abs(offX) > Math.abs(offY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                x = getX();
                y = getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
