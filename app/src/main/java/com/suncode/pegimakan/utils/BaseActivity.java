package com.suncode.pegimakan.utils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.suncode.pegimakan.LoginActivity;
import com.suncode.pegimakan.model.Session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected BaseActivity mActivity;
    protected Session mSession;
    protected FirebaseAuth firebaseAuth;
    protected FirebaseDatabase rootreff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mSession = new Session(this);
        rootreff = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    protected void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected void getFirebaseId() {
    }

    protected String showDate(String text) {
        Locale id = new Locale("in", "ID");
        String pattern = "EEEE";
        Date today = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        return sdf.format(today);
    }

    protected String getTimeNow() {
        Locale id = new Locale("in", "ID");
        String pattern = "EEEE, dd MMMM yyyy";
        Date today = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        return sdf.format(today);
    }

    protected String getHour() {
        Date dateNow = Calendar.getInstance().getTime();
        String hour = (String) android.text.format.DateFormat.format("HH:mm", dateNow);
        return hour;
    }

    protected void logoutApps() {
        mSession.removeSession();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}