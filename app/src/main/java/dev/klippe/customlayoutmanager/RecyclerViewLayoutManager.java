package dev.klippe.customlayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vlad on 22.08.17.
 */

public class RecyclerViewLayoutManager extends RecyclerView.LayoutManager {
    private int mHiddenOffset = 0;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mHiddenOffset = 0;
        int itemCount = getItemCount();

        if (itemCount == 0) {
            return;
        }

        boolean fill = true;
        int iCurrItem = 0;
        int currYPos = 0;
        while (fill && iCurrItem < itemCount) {
            View v = recycler.getViewForPosition(iCurrItem);
            addView(v, 0);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.UNSPECIFIED);
            v.measure(widthMeasureSpec, heightMeasureSpec);
            layoutDecorated(v, 0, currYPos, getDecoratedMeasuredWidth(v), currYPos + getDecoratedMeasuredHeight(v));

            currYPos = getDecoratedBottom(v);
            iCurrItem++;
            fill = currYPos < getHeight();
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offset = -limitOffset(dy);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childOffset = offset;
            if (childOffset < 0) {
                if (getPosition(child) == 1) {
                    int absOffsetLimit = Math.abs(Math.min(0, -getDecoratedTop(child)));
                    int absChildOffset = Math.abs(childOffset);
                    if (absChildOffset > absOffsetLimit) {
                        mHiddenOffset += absChildOffset - absOffsetLimit;
                        childOffset = -absOffsetLimit;
                    }
                }
            } else {
                if (getPosition(child) == 1) {
                    if (mHiddenOffset > 0) {
                        if (mHiddenOffset >= childOffset) {
                            mHiddenOffset -= childOffset;
                            childOffset = 0;
                        } else {
                            childOffset -= mHiddenOffset;
                            mHiddenOffset = 0;
                        }
                    }
                }
            }
            child.offsetTopAndBottom(childOffset);
        }
        fillTopWithViews(recycler);
        fillBottomWithViews(recycler);
        return dy;
    }

    private void fillTopWithViews(RecyclerView.Recycler recycler) {
        View topView = getChildAt(getChildCount() - 1);
        if (getDecoratedTop(topView) > 0) {
            /* Появилось пространсво сверху */
            int top = getDecoratedTop(topView);
            int pos = getPosition(topView) - 1;
            while (pos >= 0 && top > 0) {
                View v = recycler.getViewForPosition(pos);
                addView(v);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.UNSPECIFIED);
                v.measure(widthMeasureSpec, heightMeasureSpec);
                layoutDecorated(v, 0, top - getDecoratedMeasuredHeight(v), getDecoratedMeasuredWidth(v), top);
                top = getDecoratedTop(v);
                pos--;
            }
        } else if (getDecoratedBottom(topView) < 0) {
            /* Вьюха сверху улетела далеко вверх */
            while (getDecoratedBottom(getChildAt(0)) < 0) {
                View v = getChildAt(getChildCount() - 1);
                detachView(v);
                recycler.recycleView(v);
            }
        }
    }

    private void fillBottomWithViews(RecyclerView.Recycler recycler) {
        View bottomView = getChildAt(0);
        if (getDecoratedBottom(bottomView) < getHeight()) {
            /* Появилось пространство снизу */
            int bottom = getDecoratedBottom(bottomView);
            int pos = getPosition(bottomView) + 1;
            while (pos < getItemCount() && bottom < getHeight()) {
                View v = recycler.getViewForPosition(pos);
                addView(v, 0);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.UNSPECIFIED);
                v.measure(widthMeasureSpec, heightMeasureSpec);
                layoutDecorated(v, 0, bottom, getDecoratedMeasuredWidth(v), bottom + getDecoratedMeasuredHeight(v));
                bottom = getDecoratedBottom(v);
                pos++;
            }
        } else if (getDecoratedTop(bottomView) > getHeight()) {
            /* Вьюха снизу улетела далеко вниз */
            while (getDecoratedTop(getChildAt(getChildCount() - 1)) > getHeight()) {
                View v = getChildAt(0);
                detachView(v);
                recycler.recycleView(v);
            }
        }
    }

    private int limitOffset(int dy) {
        View topView = getChildAt(getChildCount() - 1);
        View bottomView = getChildAt(0);

        int allViewsSize = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (allViewsSize < getHeight()) {
            return 0;
        }

        if (dy < 0) {
            if (getPosition(topView) == 0) {
                return Math.max(dy, getDecoratedTop(topView));
            } else {
                return dy;
            }
        } else if (dy > 0) {
            if (getPosition(bottomView) == getItemCount() - 1) {
                return Math.min(dy, getDecoratedBottom(bottomView) - getHeight());
            } else {
                return dy;
            }
        } else {
            return 0;
        }
    }
}
