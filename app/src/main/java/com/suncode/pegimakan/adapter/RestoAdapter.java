package com.suncode.pegimakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suncode.pegimakan.R;
import com.suncode.pegimakan.model.Restaurant;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestoAdapter extends RecyclerView.Adapter<RestoAdapter.ViewHolder> {

    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<Restaurant> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    private View mEmptyView;

    public RestoAdapter(Context context, ArrayList<Restaurant> data, ArrayList<String> dataId, View emptyView, ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mEmptyView = emptyView;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();

    }

    @NonNull
    @Override
    public RestoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_restaurant, parent, false);
        return new RestoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestoAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = mData.get(position);
        holder.nameResto.setText(restaurant.getName());
        holder.descResto.setText(restaurant.getDescription());
        if (holder.descResto.length() > 20){
            holder.descResto.setTextSize(14);
            holder.descResto.setText(restaurant.getDescription());
        }
        holder.openResto.setText(restaurant.getOpenHours() + " - ");
        holder.closeResto.setText(restaurant.getClosingHours());
        holder.itemView.setSelected(mSelectedId.contains(mDataId.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void toggleSelection(String dataId) {
        if (mSelectedId.contains(dataId))
            mSelectedId.remove(dataId);
        else
            mSelectedId.add(dataId);
        notifyDataSetChanged();
    }

    public int selectionCount() {
        return mSelectedId.size();
    }

    public void resetSelection() {
        mSelectedId = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedId() {
        return mSelectedId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView nameResto;
        final TextView descResto;
        final TextView openResto;
        final TextView closeResto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameResto = itemView.findViewById(R.id.tv_name_list_food_resto);
            descResto = itemView.findViewById(R.id.tv_desc_resto);
            openResto = itemView.findViewById(R.id.tv_jam_open);
            closeResto = itemView.findViewById(R.id.tv_jam_close);

            itemView.setFocusable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            mClickHandler.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return mClickHandler.onItemLongClick(getAdapterPosition());
        }
    }

    public interface ClickHandler {
        void onItemClick(int position);

        boolean onItemLongClick(int position);
    }
}