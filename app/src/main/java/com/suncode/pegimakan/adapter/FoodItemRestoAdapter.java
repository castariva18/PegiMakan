package com.suncode.pegimakan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.model.Makanan;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodItemRestoAdapter extends RecyclerView.Adapter<FoodItemRestoAdapter.ViewHolder> {

    private static final String TAG = "FoodItemRestoAdapter";
    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<Makanan> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;
    private View tvEmptyView;

    public FoodItemRestoAdapter(Context context, ArrayList<Makanan> data, ArrayList<String> dataId, View tvEmpty,ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
        tvEmptyView = tvEmpty;
    }

    public void updateEmptyView() {
        if (mData.size() == 0) {
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            tvEmptyView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public FoodItemRestoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_food_resto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemRestoAdapter.ViewHolder holder, int position) {
        Makanan makanan = mData.get(position);
        holder.tvItemNameFoodResto.setText(makanan.getNamaMakanan());
        holder.tvItemPriceFoodResto.setText("Rp. " +makanan.getHargaMakanan());
        Picasso.get().load(makanan.getGambar()).into(holder.imgItemFoodResto);
        //Log.d(TAG, "onBindViewHolder: " + makanan.getGambar());
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

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {
        final TextView tvItemNameFoodResto;
        final TextView tvItemPriceFoodResto;
        final ImageView imgItemFoodResto;

        ViewHolder(View itemView) {
            super(itemView);
            tvItemNameFoodResto = itemView.findViewById(R.id.tv_name_list_food_resto);
            tvItemPriceFoodResto = itemView.findViewById(R.id.tv_price_list_food_resto);
            imgItemFoodResto = itemView.findViewById(R.id.img_list_food_resto);

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