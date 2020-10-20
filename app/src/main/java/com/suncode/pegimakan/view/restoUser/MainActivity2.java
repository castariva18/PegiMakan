package com.suncode.pegimakan.view.restoUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.adapter.FoodItemRestoAdapter;
import com.suncode.pegimakan.model.Makanan;
import com.suncode.pegimakan.utils.BaseActivity;
import com.suncode.pegimakan.view.LoginActivity;
import com.suncode.pegimakan.view.restoUser.food.FoodRestoActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends BaseActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserId;
    private LinearLayout btnCreatePage;

    private FloatingActionButton mAddItemFloatingButton;
    //private View mEmptyView;
    private static final int PICK_IMAGE_REQUEST = 101;

    private StorageTask uploadTask;
    private StorageReference storageProfileImageReference;


    private RecyclerView mStoreItemRecycleView;
    private View mEmptyView;
    private FoodItemRestoAdapter mAdapter;


    private ArrayList<Makanan> mData;
    private ArrayList<String> mDataId;

    private Uri mImageUri;

    private ImageButton image;

    private ActionMode mActionMode;

    private DatabaseReference mDatabase;
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mData.add(dataSnapshot.getValue(Makanan.class));
            mDataId.add(dataSnapshot.getKey());
            // mAdapter.updateEmptyView();
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
            // mAdapter.updateEmptyView();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    private DatabaseReference mDatabaseRef;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btnCreatePage = findViewById(R.id.cardCreate);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mStoreItemRecycleView = findViewById(R.id.rv_food_resto);
        mAddItemFloatingButton = findViewById(R.id.fab_add_food_resto);
        mButtonClicked();

        mShowItem();

        btnCreatePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCreatePage.setEnabled(false);
                startActivity(new Intent(MainActivity2.this, RegisterRestoActivity.class));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        btnCreatePage.setEnabled(true);
        checkResto();
    }

    private void mShowItem() {
        mData = new ArrayList<>();
        mDataId = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Restaurant").child(userId).child("category").child("food");;
        mDatabase.addChildEventListener(childEventListener);

        mStoreItemRecycleView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mStoreItemRecycleView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        mStoreItemRecycleView.addItemDecoration(divider);

        mAdapter = new FoodItemRestoAdapter(this, mData, mDataId, new FoodItemRestoAdapter.ClickHandler() {
            @Override
            public void onItemClick(int position) {
                if (mActionMode != null) {
                    mAdapter.toggleSelection(mDataId.get(position));
                    if (mAdapter.selectionCount() == 0)
                        mActionMode.finish();
                    else
                        mActionMode.invalidate();
                    return;
                }
                showShortToast( "Click little bit longer");
                String item = mData.get(position).toString();
            }

            @Override
            public boolean onItemLongClick(int position) {
                if (mActionMode != null) return false;

                mAdapter.toggleSelection(mDataId.get(position));
                mActionMode = MainActivity2.this.startSupportActionMode(mActionModeCallback);
                return true;
            }
        });

        mStoreItemRecycleView.setAdapter(mAdapter);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(String.valueOf(mAdapter.selectionCount()));
            menu.findItem(R.id.action_edit).setVisible(mAdapter.selectionCount() == 1);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    editItem();
                    return true;

                case R.id.action_delete:
                    deleteItem();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mAdapter.resetSelection();
        }
    };

    private void editItem() {
        final String currentItemId = mAdapter.getSelectedId().get(0);
        Makanan selectedItem = mData.get(mDataId.indexOf(currentItemId));

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.dialog_item_food_resto, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Edit Item Toko");

        final EditText name = (EditText) view.findViewById(R.id.et_name_food_item);
        name.setText(selectedItem.getNamaMakanan());
        final EditText desc = (EditText) view.findViewById(R.id.et_desc_food_item);
        desc.setText(selectedItem.getDescMakanan());
        final EditText price = (EditText) view.findViewById(R.id.et_harga_food_item);
        price.setText(selectedItem.getHargaMakanan());
        final TextView textView = (TextView) view.findViewById(R.id.textView4);
        textView.setVisibility(View.GONE);
        image = (ImageButton) view.findViewById(R.id.imageButton_dialog_item_gambar);
        image.setVisibility(View.GONE);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(userId).child("category").child("food");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Restaurant").child(userId).child("category").child("food").child(currentItemId);

                Map<String, Object> values = new HashMap<String, Object>();
                values.put("namaMakanan", name.getText().toString());
                values.put("descMakanan", desc.getText().toString());
                values.put("hargaMakanan", price.getText().toString());

                reference.updateChildren(values);
                mActionMode.finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mActionMode.finish();
            }
        });

        builder.show();
    }

    private void deleteItem() {
        final ArrayList<String> selectedIds = mAdapter.getSelectedId();
        int message = selectedIds.size() == 1 ? R.string.delete_item : R.string.delete_items;

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(userId).child("category").child("food");

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String currentPetId : selectedIds) {
                            mDatabaseRef.child(currentPetId).removeValue();
                        }
                        mActionMode.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mActionMode.finish();
                    }
                });
        builder.create().show();
    }

    private void mButtonClicked() {
        mAddItemFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    private void saveItem() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference reference = storage.getReference();

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.dialog_item_food_resto, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Item Restaurant");

        final EditText name = (EditText) view.findViewById(R.id.et_name_food_item);
        final EditText desc = (EditText) view.findViewById(R.id.et_desc_food_item);
        final EditText price = (EditText) view.findViewById(R.id.et_harga_food_item);
        image = (ImageButton) view.findViewById(R.id.imageButton_dialog_item_gambar);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(userId).child("category").child("food");
        final String dataID = mDatabaseRef.push().getKey();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload Item");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                if (mImageUri == null || name == null || price == null) {
                    showShortToast( "Field cannot be empty");
                    dialogInterface.dismiss();
                    return;
                } else {
                    progressDialog.show();
                    mDatabaseRef.child(dataID).setValue(new Makanan(name.getText().toString(),desc.getText().toString(),price.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    final StorageReference ref = reference.child("Items/" + name.getText().toString() + System.currentTimeMillis() + "." + getFileExtention(mImageUri));
                                    ref.putFile(mImageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Restaurant").child(userId).child("category").child("food").child(dataID);

                                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String imageUrl = uri.toString();
                                                            Map<String, Object> values = new HashMap<String, Object>();
                                                            values.put("gambar", imageUrl);
                                                            databaseReference.updateChildren(values);
                                                            progressDialog.dismiss();
                                                            showShortToast( "Upload success");
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    showLongToast("Upload failed : " + e.toString());
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                    progressDialog.setMessage("Uploading " + (int)progress + "%");
                                                }
                                            });
                                    dialogInterface.dismiss();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOpenFilePicker();
            }
        });
    }

    private void mOpenFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sIntent, 0) != null) {
            chooserIntent = Intent.createChooser(sIntent, "Open File");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }
        try {
            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        } catch (android.content.ActivityNotFoundException e) {

        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    mImageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                        image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        showLongToast(e.toString());
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void checkResto() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            startActivity(new Intent(new Intent(MainActivity2.this, LoginActivity.class)));
        }
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("restoId");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("please wait for a moment..");
        progressDialog.show();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressDialog.dismiss();
                    btnCreatePage.setVisibility(View.GONE);
                } else {
                    btnCreatePage.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLongToast("Error: " + error);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                logoutApps();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}