package com.suncode.pegimakan.view.normalUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.adapter.FoodItemDetailAdapter;
import com.suncode.pegimakan.model.Makanan;

import java.util.ArrayList;

public class FoodListActivity extends AppCompatActivity {

    private static final String TAG = "FoodListActivity";
    private RecyclerView mRecycleViewDetailStore;

    private FoodItemDetailAdapter mAdapter;

    private ArrayList<Makanan> mData;
    private ArrayList<String> mDataId;

    private ActionMode mActionMode;

    private DatabaseReference mDatabase, rootRef;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Makanan.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Makanan.class));
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mDataId.remove(pos);
            mData.remove(pos);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        mRecycleViewDetailStore = findViewById(R.id.rv_food_list);

        mShowItem(savedInstanceState);
    }

    private void mShowItem(final Bundle bundle) {
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        rootRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        final String restoId = getRestoId(bundle);

        mDatabase = FirebaseDatabase.getInstance().getReference("Restaurant").child(restoId).child("category").child("food");
        mDatabase.addChildEventListener(childEventListener);

        mRecycleViewDetailStore.setHasFixedSize(true);
        Log.d(TAG, "mShowItem: " + mData.size());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycleViewDetailStore.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mRecycleViewDetailStore.addItemDecoration(divider);

        mAdapter = new FoodItemDetailAdapter(FoodListActivity.this, mData, mDataId, new FoodItemDetailAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                String itemId = mDataId.get(position).toString();
                String restoId = getRestoId(bundle);

                Intent intent = new Intent(FoodListActivity.this, BookingFoodActivity.class);

                intent.putExtra("itemId", itemId);
                intent.putExtra("restoId", restoId);

                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        mRecycleViewDetailStore.setAdapter(mAdapter);

    }

    private String getRestoId(Bundle savedInstanceState) {
        String restoId;
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                return restoId = null;
            } else {
                return restoId = bundle.getString("restoId");
            }
        } else {
            return restoId = (String) savedInstanceState.getSerializable("restoId");
        }
    }

}