package com.suncode.pegimakan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suncode.pegimakan.model.User;
import com.suncode.pegimakan.utils.BaseActivity;
import com.suncode.pegimakan.view.LoginActivity;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivityTAG";

    private EditText mUsername;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private Button btnSaveSetting, btnChangePass;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUsername = findViewById(R.id.et_setting_nama);
        btnSaveSetting = findViewById(R.id.btn_save_name);
       // btnChangePass = findViewById(R.id.btn_change_pass);

        RootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                Log.d(TAG, "onDataChange: " + user.getUsername());
                Log.d(TAG, "onDataChange: " + user.getEmail());
                Log.d(TAG, "onDataChange: " + user.getNoHP());

                String nama = dataSnapshot.child("username").getValue().toString();

                mUsername.setText(nama);


                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                } else {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }

                btnSaveSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nama = mUsername.getText().toString().trim();

                        mAuth = FirebaseAuth.getInstance();

                        try {
                            if (TextUtils.isEmpty(nama)) {
                                showLongToast("Field cannot be empty");
                            } else if (nama.length() > 26) {
                                showLongToast("Name is too long");

                            } else {
                                RootRef.child("username").setValue(mUsername.getText().toString());
                                showLongToast("Saving Success");
                                finish();
                            }
                        } catch (Exception e) {
                            showLongToast("Gagal Parsing JSON : " + e.getMessage());
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}