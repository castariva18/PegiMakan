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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suncode.pegimakan.ProfileActivity;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.adapter.RestoAdapter;
import com.suncode.pegimakan.model.Restaurant;
import com.suncode.pegimakan.utils.BaseActivity;
import com.suncode.pegimakan.view.LoginActivity;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    int[] sampleImages = {
            R.drawable.burger,
            R.drawable.chicken,
            R.drawable.kopi,
            R.drawable.drink
    };
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private RecyclerView mStoreRecycleView;
    private View mEmptyView;
    private RestoAdapter mAdapter;

    private ArrayList<Restaurant> mData;
    private ArrayList<String> mDataId;

    private ActionMode mActionMode;

    private DatabaseReference mDatabaseRef;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Restaurant.class));
            mDataId.add(dataSnapshot.getKey());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            int pos = mDataId.indexOf(dataSnapshot.getKey());
            mData.set(pos, dataSnapshot.getValue(Restaurant.class));
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
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStoreRecycleView = findViewById(R.id.rv_resto);

        showCarousel();
        showRestaurant();
    }

    private void showRestaurant() {
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        mDatabaseRef.addChildEventListener(childEventListener);

        mStoreRecycleView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mStoreRecycleView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mStoreRecycleView.addItemDecoration(divider);

        mAdapter = new RestoAdapter(this, mData, mDataId, mEmptyView, new RestoAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                String item = mDataId.get(position).toString();

                Intent intent = new Intent(MainActivity.this, RestoProfileActivity.class);
                intent.putExtra("restoId", item);
                Log.d(TAG, "onItemClick: " + item);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        mStoreRecycleView.setAdapter(mAdapter);
    }

    private void mDetailStore(Restaurant restaurant) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Restaurant");
    }


    private void showCarousel() {
        CarouselView carouselView;

        carouselView = (CarouselView) findViewById(R.id.carousel_main);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_profile:
                Openprofile();
                break;
            case R.id.action_logout:
                logoutApps();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void Openprofile() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

}
