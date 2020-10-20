package com.suncode.pegimakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.model.Makanan;
import com.suncode.pegimakan.view.normalUser.FoodListActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodItemDetailAdapter extends RecyclerView.Adapter<FoodItemDetailAdapter.ViewHolder> {

    private ClickHandler mClickHandler;
    private Context mContext;
    private ArrayList<Makanan> mData;
    private ArrayList<String> mDataId;
    private ArrayList<String> mSelectedId;

    public FoodItemDetailAdapter(Context context, ArrayList<Makanan> data, ArrayList<String> dataId, ClickHandler handler) {
        mContext = context;
        mData = data;
        mDataId = dataId;
        mClickHandler = handler;
        mSelectedId = new ArrayList<>();
    }

    @NonNull
    @Override
    public FoodItemDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_food_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemDetailAdapter.ViewHolder holder, int position) {
        Makanan item = mData.get(position);
        holder.nameTextView.setText(item.getNamaMakanan());
        holder.priceTextView.setText("Rp. "+item.getHargaMakanan());
        holder.descTextView.setText(item.getDescMakanan());
        Picasso.get().load(item.getGambar()).into(holder.itemImageView);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnLongClickListener {
        final TextView nameTextView;
        final TextView priceTextView;
        final TextView descTextView;
        final ImageView itemImageView;


        ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.textView_store_item_detail_nama);
            priceTextView = itemView.findViewById(R.id.textView_store_item_detail_harga);
            descTextView = itemView.findViewById(R.id.tv_desc_item);
            itemImageView = itemView.findViewById(R.id.imageView6);


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
