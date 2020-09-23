package com.xuqiqiang.camera2.app.ui.filter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xuqiqiang.camera2.app.R;
import com.xuqiqiang.camera2.app.utils.Filters;

import org.wysaid.filter.base.FiltersConstant;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

public class DialogFilterAdapter extends RecyclerView.Adapter<DialogFilterAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    public DialogFilterAdapter(Context context) {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position > FiltersConstant.NAME_FILTERS.length &&
                position < Filters.IMG_FILTERS.length + FiltersConstant.NAME_FILTERS.length)
            holder.img.setImageResource(Filters.IMG_FILTERS[position - FiltersConstant.NAME_FILTERS.length]);
        else
            holder.img.setImageResource(Filters.IMG_FILTERS[0]);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemClickListener)
                    mOnItemClickListener.onItemClickListener(position);
            }
        });

        String name = position > 0 && position - 1 < FiltersConstant.NAME_FILTERS.length ?
                FiltersConstant.NAME_FILTERS[position - 1] : ("filter" + position);
        holder.tvName.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
        holder.tvName.setText(name);
    }

    @Override
    public int getItemCount() {
        return FiltersConstant.FILTERS.length;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
