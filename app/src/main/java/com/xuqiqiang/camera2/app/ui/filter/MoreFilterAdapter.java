package com.xuqiqiang.camera2.app.ui.filter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuqiqiang.camera2.app.R;
import com.xuqiqiang.camera2.app.utils.Filters;

import org.wysaid.filter.base.FiltersConstant;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */
public class MoreFilterAdapter extends RecyclerView.Adapter<MoreFilterAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    public MoreFilterAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (position >= FiltersConstant.NAME_FILTERS.length &&
                position < Filters.IMG_FILTERS.length + FiltersConstant.NAME_FILTERS.length - 1)
            holder.img.setImageResource(Filters.IMG_FILTERS[position + 1 - FiltersConstant.NAME_FILTERS.length]);
        else
            holder.img.setImageResource(Filters.IMG_FILTERS[0]);
        String name = position < FiltersConstant.NAME_FILTERS.length ?
                FiltersConstant.NAME_FILTERS[position] : ("filter" + position);
        holder.tvName.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
        holder.tvName.setText(name);
////        if (position > ConstantFilters.NAME_FILTERS.length &&
////                position < ConstantFilters.IMG_FILTERS.length + ConstantFilters.NAME_FILTERS.length)
////            holder.img.setImageResource(ConstantFilters.IMG_FILTERS[position - ConstantFilters.NAME_FILTERS.length]);
////        else
////            holder.img.setImageResource(ConstantFilters.IMG_FILTERS[0]);
//        holder.img.setImageResource(AllFilters.IMG_FILTERS[position]);
//        holder.img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mOnItemClickListener)
//                    mOnItemClickListener.onItemClickListener(position);
//            }
//        });
//
////        String name = position > 0 && position - 1 < AllFilters.NAME_FILTERS.length ?
////                AllFilters.NAME_FILTERS[position - 1] : null;
////        holder.tvName.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
//        holder.tvName.setText(AllFilters.FILTERS[position].getName());
    }

    @Override
    public int getItemCount() {
        return FiltersConstant.FILTERS.length - 1;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener)
                        mOnItemClickListener.onItemClickListener(getAdapterPosition());
                }
            });
        }
    }
}
