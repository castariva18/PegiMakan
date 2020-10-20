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
import android.widget.Button;
import android.widget.LinearLayout;
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

public class RestoProfileActivity extends AppCompatActivity {

    private TextView tvNama, tvDesc, tvOpen;
    private RecyclerView mRecycleViewDetailStore;

   // private LinearLayout btnFood, btnDrink, btnPack;

    private FoodItemDetailAdapter mAdapter;

    private ArrayList<Makanan> mData;
    private ArrayList<String> mDataId;

    private ActionMode mActionMode;

    private DatabaseReference mDatabase;

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
        setContentView(R.layout.activity_resto_profile);
        mRecycleViewDetailStore = findViewById(R.id.rv_list_resto);
        tvNama = findViewById(R.id.tv_resto_detail_name);
        tvDesc = findViewById(R.id.tv_resto_detail_desc);
        tvOpen = findViewById(R.id.tv_resto_detail_open);

//        btnFood = findViewById(R.id.card_food_detail);
//        btnDrink = findViewById(R.id.card_drink_detail);
//        btnPack = findViewById(R.id.card_package_detail);

//        btnFood.setOnClickListener(view -> SendToFoodList());
//        btnDrink.setOnClickListener(view -> SendToDrinkList());
//        btnPack.setOnClickListener(view -> SendToPackList());
        mShowItem(savedInstanceState);
        mShowHeader(savedInstanceState);
    }

    private void mShowItem(final Bundle bundle) {
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Restaurant").child(getRestoId(bundle)).child("category").child("food");
        mDatabase.addChildEventListener(childEventListener);

        mRecycleViewDetailStore.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycleViewDetailStore.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mRecycleViewDetailStore.addItemDecoration(divider);

        mAdapter = new FoodItemDetailAdapter(RestoProfileActivity.this, mData, mDataId, new FoodItemDetailAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                String itemId = mDataId.get(position).toString();
                String restoId = getRestoId(bundle);

                Intent intent = new Intent(RestoProfileActivity.this, BookingFoodActivity.class);

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

//    private void SendToPackList() {
//        btnPack.setEnabled(false);
//        Intent pack = new Intent(RestoProfileActivity.this, PaketListActivity.class);
//        startActivity(pack);
//    }
//
//    private void SendToDrinkList() {
//        btnDrink.setEnabled(false);
//        Intent drink = new Intent(RestoProfileActivity.this, DrinkListActivity.class);
//        startActivity(drink);
//    }
//
//    private void SendToFoodList() {
//        btnFood.setEnabled(false);
//        Intent food = new Intent(RestoProfileActivity.this, FoodListActivity.class);
//        startActivity(food);
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        btnFood.setEnabled(true);
//        btnDrink.setEnabled(true);
//        btnPack.setEnabled(true);
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

    private void mShowHeader(Bundle bundle) {
        String restoId = getRestoId(bundle);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Restaurant").child(restoId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String deskripsi = dataSnapshot.child("description").getValue(String.class);
                String nama = dataSnapshot.child("name").getValue(String.class);
                String jamBuka = dataSnapshot.child("openHours").getValue(String.class);
                String jamTutup = dataSnapshot.child("closingHours").getValue(String.class);

                tvNama.setText(nama);
                tvDesc.setText(deskripsi);
                tvOpen.setText("Open : " + jamBuka + " - " + jamTutup);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}