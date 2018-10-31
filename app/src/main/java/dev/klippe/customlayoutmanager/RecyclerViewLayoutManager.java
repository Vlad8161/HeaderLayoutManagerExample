package dev.klippe.customlayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vlad on 22.08.17.
 */

public class RecyclerViewLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "RVLM";
    private int mHiddenOffset = 0;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mHiddenOffset = 0;
        int itemCount = getItemCount();

        if (itemCount == 0) {
            return;
        }

        View headerView = recycler.getViewForPosition(1);
        addView(headerView);
        int headerViewWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
        int headerViewHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.UNSPECIFIED);
        headerView.measure(headerViewWidthMeasureSpec, headerViewHeightMeasureSpec);
        layoutDecorated(headerView, 0, getHeight() - getDecoratedMeasuredHeight(headerView),
                getDecoratedMeasuredWidth(headerView), getHeight());

        View mapView = recycler.getViewForPosition(0);
        addView(mapView);
        int mapViewWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
        int mapViewHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getHeight() - getDecoratedMeasuredHeight(headerView), View.MeasureSpec.EXACTLY);
        mapView.measure(mapViewWidthMeasureSpec, mapViewHeightMeasureSpec);
        layoutDecorated(mapView, 0, 0, getDecoratedMeasuredWidth(mapView), getDecoratedMeasuredHeight(mapView));
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offset = -limitOffset(dy);
        View headerView = findViewByPosition(1);
        int headerOffset = offset;
        if (headerOffset < 0) {
            int absOffsetLimit = Math.abs(Math.min(0, -getDecoratedTop(headerView)));
            int absChildOffset = Math.abs(headerOffset);
            if (absChildOffset > absOffsetLimit) {
                mHiddenOffset += absChildOffset - absOffsetLimit;
                headerOffset = -absOffsetLimit;
            }
        } else {
            if (mHiddenOffset > 0) {
                if (mHiddenOffset >= headerOffset) {
                    mHiddenOffset -= headerOffset;
                    headerOffset = 0;
                } else {
                    headerOffset -= mHiddenOffset;
                    mHiddenOffset = 0;
                }
            }
        }

        headerView.offsetTopAndBottom(headerOffset);

        View mapView = recycler.getViewForPosition(0);
        addView(mapView);
        int mapViewWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
        int mapViewHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getDecoratedTop(headerView), View.MeasureSpec.EXACTLY);
        mapView.measure(mapViewWidthMeasureSpec, mapViewHeightMeasureSpec);
        layoutDecorated(mapView, 0, 0, getDecoratedMeasuredWidth(mapView), getDecoratedMeasuredHeight(mapView));

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = getPosition(child);
            if (position == 0 || position == 1) {
                continue;
            }
            child.offsetTopAndBottom(offset);
        }
        fillTopWithViews(recycler);
        fillBottomWithViews(recycler);
        return dy;
    }

    private void fillTopWithViews(RecyclerView.Recycler recycler) {
        if (getChildCount() <= 2) {
            return;
        }
        View topView = getChildAt(getChildCount() - 3);
        View headerView = getChildAt(getChildCount() - 2);
        if (getDecoratedTop(topView) > getDecoratedBottom(headerView)) {
            /* Появилось пространсво сверху */
            int top = getDecoratedTop(topView);
            int pos = getPosition(topView) - 1;
            while (pos >= 2 && top > getDecoratedBottom(headerView)) {
                View v = recycler.getViewForPosition(pos);
                addView(v, getChildCount() - 2);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.UNSPECIFIED);
                v.measure(widthMeasureSpec, heightMeasureSpec);
                layoutDecorated(v, 0, top - getDecoratedMeasuredHeight(v), getDecoratedMeasuredWidth(v), top);
                top = getDecoratedTop(v);
                pos--;
            }
        } else if (getDecoratedBottom(topView) < getDecoratedBottom(headerView)) {
            /* Вьюха сверху улетела далеко вверх */
            while (getDecoratedBottom(getChildAt(getChildCount() - 3)) < 0) {
                View v = getChildAt(getChildCount() - 3);
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
            while (getDecoratedTop(getChildAt(0)) > getHeight()) {
                View v = getChildAt(0);
                detachView(v);
                recycler.recycleView(v);
            }
        }
    }

    private int limitOffset(int dy) {
        View headerView = findViewByPosition(1);
        View bottomView = getChildAt(0);

        if (dy < 0) {
            /* Скролл вниз */
            return Math.max(dy, getDecoratedBottom(headerView) - getHeight());
        } else if (dy > 0) {
            /* Скролл вверх */
            if (getItemCount() > 2 && getPosition(bottomView) == getItemCount() - 1) {
                return Math.min(dy, getDecoratedBottom(bottomView) - getHeight());
            } else {
                return dy;
            }
        } else {
            return 0;
        }
    }
}
