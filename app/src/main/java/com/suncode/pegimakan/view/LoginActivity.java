package com.suncode.pegimakan.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.suncode.pegimakan.view.normalUser.MainActivity;
import com.suncode.pegimakan.view.restoUser.MainActivity2;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.model.User;
import com.suncode.pegimakan.utils.BaseActivity;
import com.suncode.pegimakan.utils.LoginViewCallback;

import androidx.annotation.NonNull;

public class LoginActivity extends BaseActivity implements LoginViewCallback {

    private EditText etEmail, etPassword;
    private ProgressDialog loadingBar;
    private ImageButton btnLogin;
    TextView tvForgotPass;
    CheckBox cbLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login_button);
        cbLogin = findViewById(R.id.cb_login_show_pw);
        tvForgotPass = findViewById(R.id.tv_forgot_button);
        tvRegister = findViewById(R.id.tv_register_button);

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToForgot();
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LetUserToRegister();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUser();
            }
        });
        checkUserLogin(mSession.getPreferences().getString("KEY_IS_LOGIN", ""));
    }

    private void CheckUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password cannot be empty");
            etPassword.requestFocus();
        } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            showLongToast("Field cannot be empty");
        } else {
            getAuthFirebase(email, password);
        }
    }

    private void getAuthFirebase(String email, String password) {
        final ProgressDialog loadingBar = new ProgressDialog(LoginActivity.this);
        loadingBar.setTitle("Login");
        loadingBar.setMessage("Please wait...");
        loadingBar.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().getCurrentUser();
                    onSuccessAuthFirebase(firebaseAuth.getUid());
//                    if (loadingBar != null){
                    loadingBar.dismiss();
                    // }
                } else {
                    loadingBar.dismiss();
                    onFailedAuthFirebase();
                }
            }
        });
    }

    private void getRealtimeData(final String userId) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userLvl = dataSnapshot.child("lvl").getValue().toString();
                saveSession(userId, userLvl, dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveSession(String uid, String userLvl, DataSnapshot dataSnapshot) {
        if (userLvl.equals("1")) {
            User admin = new User();
            admin.setLvl(dataSnapshot.child("lvl").getValue().toString());
            admin.setuId(uid);
            mSession.setLoginAdmin(admin);

        } else if (userLvl.equals("0")) {
            User user = new User();
            user.setLvl(dataSnapshot.child("lvl").getValue().toString());
            user.setuId(uid);
            mSession.setLoginUser(user);
        }
        checkUserLogin(mSession.getPreferences().getString("KEY_IS_LOGIN", ""));
    }

    private void checkUserLogin(String user_lvl_login) {
        if (user_lvl_login.equals("1")) {
            Intent j = new Intent(LoginActivity.this, MainActivity2.class);
            startActivity(j);
            finish();
        } else if (user_lvl_login.equals("0")) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    private void SendUserToForgot() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    private void LetUserToRegister() {
        Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intentRegister);
    }

    @Override
    public void onSuccessAuthFirebase(String uid) {
        getRealtimeData(uid);
    }

    @Override
    public void onFailedAuthFirebase() {
        showLongToast("Login failed, please check your password or connection");
    }

    @Override
    public void onShowProgress() {

    }

    @Override
    public void onHideProgress() {

    }

    public void cbClicked(View view) {
        CheckBox checkBox = (CheckBox) view;
        if (!checkBox.isChecked()) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }
}