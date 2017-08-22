package dev.klippe.customlayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vlad on 22.08.17.
 */

public class RecyclerViewLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int itemCount = getItemCount();

        if (itemCount == 0) {
            return;
        }

        boolean fill = true;
        int iCurrItem = 0;
        int currYPos = 0;
        while (fill && iCurrItem < itemCount) {
            View v = recycler.getViewForPosition(iCurrItem);
            addView(v);
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
        dy = limitOffset(dy);
        offsetChildrenVertical(-dy);
        fillTopWithViews(recycler);
        fillBottomWithViews(recycler);
        return dy;
    }

    private void fillTopWithViews(RecyclerView.Recycler recycler) {
        View topView = getChildAt(0);
        if (getDecoratedTop(topView) > 0) {
            /* Появилось пространсво сверху */
            int top = getDecoratedTop(topView);
            int pos = getPosition(topView) - 1;
            while (pos >= 0 && top > 0) {
                View v = recycler.getViewForPosition(pos);
                addView(v, 0);
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
                View v = getChildAt(0);
                detachView(v);
                recycler.recycleView(v);
            }
        }
    }

    private void fillBottomWithViews(RecyclerView.Recycler recycler) {
        View bottomView = getChildAt(getChildCount() - 1);
        if (getDecoratedBottom(bottomView) < getHeight()) {
            /* Появилось пространство снизу */
            int bottom = getDecoratedBottom(bottomView);
            int pos = getPosition(bottomView) + 1;
            while (pos < getItemCount() && bottom < getHeight()) {
                View v = recycler.getViewForPosition(pos);
                addView(v);
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
                View v = getChildAt(getChildCount() - 1);
                detachView(v);
                recycler.recycleView(v);
            }
        }
    }

    private int limitOffset(int dy) {
        View topView = getChildAt(0);
        View bottomView = getChildAt(getChildCount() - 1);

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
