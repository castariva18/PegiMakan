package com.suncode.pegimakan.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterName, etRegisterEmail, etRegisterNoHP, etRegisterPassword, etRegisterCoPassword;
    private ImageButton btnRegister;
    private TextView tvGoback;
    private CheckBox cbShowPassword;
    private String lvl;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String mNameUser;
    private String currentUserId;
    private ProgressDialog loadingBar;

    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);

        etRegisterEmail = (EditText) findViewById(R.id.et_register_email);
        etRegisterName = (EditText) findViewById(R.id.et_register_name);
        etRegisterPassword = (EditText) findViewById(R.id.et_register_password);
        etRegisterNoHP = findViewById(R.id.et_register_no_hp);
        etRegisterCoPassword = (EditText) findViewById(R.id.et_register_Copassword);
        btnRegister = (ImageButton) findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LetUserToRegister();
            }
        });
    }

    private void LetUserToRegister() {
        final String username = etRegisterName.getText().toString();
        final String email = etRegisterEmail.getText().toString();
        final String noHp = etRegisterNoHP.getText().toString();
        final String password = etRegisterPassword.getText().toString();
        final String coPassword = etRegisterCoPassword.getText().toString();
        final String lvl = "0";

        CheckEmail();

        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(coPassword)) {
            Toast.makeText(RegisterActivity.this, "Field cannot be empty....", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(username)) {
            etRegisterName.setError("Name cannot be empty");
            etRegisterName.requestFocus();

        } else if (username.length() > 26) {
            etRegisterName.setError("Name too long");
            etRegisterName.requestFocus();

        } else if (TextUtils.isEmpty(email)) {
            etRegisterEmail.setError("Email cannot be empty");
            etRegisterEmail.requestFocus();

        } else if (TextUtils.isEmpty(password)) {
            etRegisterPassword.setError("Password cannot be empty");
            etRegisterPassword.requestFocus();

        } else if (TextUtils.isEmpty(coPassword)) {
            etRegisterCoPassword.setError("Please confirm password");
            etRegisterCoPassword.requestFocus();

        } else if (TextUtils.isEmpty(noHp)) {
            etRegisterCoPassword.setError("Phone Number cannot be empty");
            etRegisterCoPassword.requestFocus();

        } else if (noHp.length() < 6) {
            etRegisterCoPassword.setError("Phone Number too short");
            etRegisterCoPassword.requestFocus();

        } else if (noHp.length() > 16) {
            etRegisterCoPassword.setError("Phone Number too long");
            etRegisterCoPassword.requestFocus();

        } else if (!coPassword.equals(password)) {
            etRegisterCoPassword.setError("Password not match");
            etRegisterCoPassword.requestFocus();

        } else if (password.length() < 6 && coPassword.length() < 6) {
            etRegisterPassword.setError("Password too short");
            etRegisterCoPassword.setError("Password too short");
            etRegisterPassword.requestFocus();
            etRegisterCoPassword.requestFocus();

        } else if (password.length() > 18 && coPassword.length() > 18) {
            etRegisterPassword.setError("Password too long");
            etRegisterCoPassword.setError("Password too long");
            etRegisterPassword.requestFocus();
            etRegisterCoPassword.requestFocus();

        } else if (!isEmailValid(email)) {
            etRegisterEmail.setError("Email invalid");
            Toast.makeText(RegisterActivity.this, "Please enter your valid email", Toast.LENGTH_LONG).show();
        } else {
            loadingBar.setTitle("Registering account");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    } else {

                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            mNameUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            loadingBar.dismiss();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                        } else {
                            loadingBar.dismiss();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }

                        RootRef = FirebaseDatabase.getInstance().getReference("Users").child(mNameUser);
                        String uid = mAuth.getUid();
                        User user = new User(username, email, password, coPassword, noHp, lvl, uid);
                        newUserLvl();
                        RootRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Register is successful!", Toast.LENGTH_SHORT).show();
                                SendToLogin();
                            }
                        });
                    }
                }
            });
        }
    }

    private void CheckEmail() {
        mAuth.fetchSignInMethodsForEmail(etRegisterEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check = !task.getResult().getSignInMethods().isEmpty();

                if (check) {
                    etRegisterEmail.setError("Email is already register");
                    etRegisterEmail.requestFocus();
                }
            }
        });
    }

    private void SendToLogin() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
    }

    private void newUserLvl() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference UserId = databaseReference.child(mAuth.getCurrentUser().getUid());
        UserId.child("lvl").setValue(0);
    }

    public void itemClicked(View view) {
        CheckBox checkBox = (CheckBox) view;
        if (!checkBox.isChecked()) {
            etRegisterCoPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            etRegisterCoPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }
}