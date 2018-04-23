package com.lsl.recycleviewsource;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class TestLayoutManager extends RecyclerView.LayoutManager {

    private Context mContext;
    private int mVerticalOffset;
    private int mFirstVisiPos;
    private int mLastVisiPos;

    public TestLayoutManager(Context context) {
        mContext = context;
        mItemRects = new SparseArray<>();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    /**
     * 该方法是LayoutManager的入口。它会在如下情况下被调用：
     * 1 在RecyclerView初始化时，会被调用两次。
     * 2 在调用adapter.notifyDataSetChanged()时，会被调用。
     * 3 在调用setAdapter替换Adapter时,会被调用。
     * 4 在RecyclerView执行动画时，它也会被调用。
     * 即RecyclerView 初始化 、 数据源改变时 都会被调用。
     *
     * @param recycler Recycler to use for fetching potentially cached views for a
     *                 position
     * @param state    Transient state of RecyclerView
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        mVerticalOffset = 0;
        mFirstVisiPos = 0;
        mLastVisiPos = getItemCount();
        fill(recycler, state, 0);

    }

    private SparseArray<Rect> mItemRects;//key 是View的position，保存View的bounds ，


    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        int topOffset = getPaddingTop();
        int leftOffset = getPaddingLeft();
        int lineMaxHeight = 0;
        int minPos = mFirstVisiPos;
        mLastVisiPos = getItemCount() - 1;

        for (int i = minPos; i <= mLastVisiPos; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChild(child, 0, 0);
            if (leftOffset + getDecoratedMeasurementHorizontal(child) <= getHorizontalSpace()) {
                layoutDecoratedWithMargins(child, leftOffset, topOffset,
                        leftOffset + getDecoratedMeasurementHorizontal(child),
                        topOffset + getDecoratedMeasurementVertical(child));

                //保存Rect供逆序layout用
                Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);
                mItemRects.put(i, rect);

                leftOffset += getDecoratedMeasurementHorizontal(child);
                topOffset += getDecoratedMeasurementVertical(child);
            } else {
                leftOffset = getPaddingLeft();
                topOffset += lineMaxHeight;
                lineMaxHeight = 0;

                if (topOffset - dy > getHeight() - getPaddingBottom()) {
                    removeAndRecycleView(child, recycler);
                    mLastVisiPos = i - 1;
                } else {
                    layoutDecoratedWithMargins(child, leftOffset, topOffset,
                            leftOffset + getDecoratedMeasurementHorizontal(child),
                            topOffset + getDecoratedMeasurementVertical(child));

                    //保存Rect供逆序layout用
                    Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(child), topOffset + getDecoratedMeasurementVertical(child) + mVerticalOffset);
                    mItemRects.put(i, rect);


                    leftOffset += getDecoratedMeasurementHorizontal(child);
                    lineMaxHeight = Math.max(lineMaxHeight, getDecoratedMeasurementVertical(child));
                }
            }
        }

        View lastChild = getChildAt(getChildCount() - 1);
        if (getPosition(lastChild) == getItemCount() - 1) {
            int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
            if (gap > 0) {
                dy -= gap;
            }
        } else {
            int maxPos = getItemCount() - 1;
            mFirstVisiPos = 0;
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                maxPos = getPosition(firstView) - 1;
            }

            for (int i = maxPos; i >= mFirstVisiPos; i--) {
                Rect rect = mItemRects.get(i);

                if (rect.bottom - mVerticalOffset - dy < getPaddingTop()) {
                    mFirstVisiPos = i + 1;
                    break;
                } else {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);//将View添加至RecyclerView中，childIndex为1，但是View的位置还是由layout的位置决定
                    measureChildWithMargins(child, 0, 0);

                    layoutDecoratedWithMargins(child, rect.left, rect.top - mVerticalOffset, rect.right, rect.bottom - mVerticalOffset);
                }
            }
        }

        return dy;

    }


    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically();
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (dy == 0 || getChildCount() == 0) {
            return 0;
        }

        int realOffset = dy;
        if (mVerticalOffset + realOffset < 0) {
            realOffset = -mVerticalOffset;
        } else if (realOffset > 0) {
            View lastChild = getChildAt(getChildCount() - 1);
            if (getPosition(lastChild) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
                if (gap > 0) {
                    realOffset = -gap;
                } else if (gap == 0) {
                    realOffset = 0;
                } else {
                    realOffset = Math.min(realOffset, -gap);
                }
            }
        }

        realOffset = fill(recycler, state, realOffset);

        mVerticalOffset += realOffset;
        offsetChildrenVertical(-realOffset);

        return realOffset;
    }


    /**
     * 获取某个childView在水平方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    /**
     * 获取某个childView在竖直方向所占的空间
     *
     * @param view
     * @return
     */
    public int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


}
