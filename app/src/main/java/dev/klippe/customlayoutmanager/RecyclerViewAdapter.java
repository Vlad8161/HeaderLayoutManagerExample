package dev.klippe.customlayoutmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by vlad on 22.08.17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_1 = 0;
    private static final int TYPE_2 = 1;
    private static final int TYPE_3 = 2;

    private Context mContext;

    RecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_1:
                return new ViewHolderType1(LayoutInflater.from(mContext).inflate(R.layout.item_1, parent, false));
            case TYPE_2:
                return new ViewHolderType2(LayoutInflater.from(mContext).inflate(R.layout.item_2, parent, false));
            case TYPE_3:
                return new ViewHolderType3(LayoutInflater.from(mContext).inflate(R.layout.item_3, parent, false));
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_1;
            case 1:
                return TYPE_2;
            default:
                return TYPE_3;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    private static class ViewHolderType1 extends RecyclerView.ViewHolder {
        ViewHolderType1(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolderType2 extends RecyclerView.ViewHolder {
        ViewHolderType2(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolderType3 extends RecyclerView.ViewHolder {
        ViewHolderType3(View itemView) {
            super(itemView);
        }
    }
}
