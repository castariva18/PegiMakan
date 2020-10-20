package com.suncode.pegimakan.view;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.utils.BaseActivity;

public class ForgotPasswordActivity extends BaseActivity {

    private EditText etForgotPass;
    private ImageButton btnSendEmail;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etForgotPass = findViewById(R.id.et_forgot_email);
        btnSendEmail = findViewById(R.id.btn_forgot_password);
        firebaseAuth = FirebaseAuth.getInstance();

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useremail = etForgotPass.getText().toString().trim();

                if (useremail.equals("")) {
                    showLongToast("Enter your email registered in this app");

                } else {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showLongToast("Email sent!");
                                finish();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            } else {
                                showLongToast("Email failed to send");
                            }
                        }
                    });
                }

            }
        });
    }
}