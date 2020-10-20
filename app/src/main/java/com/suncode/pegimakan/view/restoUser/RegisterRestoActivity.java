package com.suncode.pegimakan.view.restoUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.model.Restaurant;
import com.suncode.pegimakan.utils.BaseActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterRestoActivity extends BaseActivity {

    private EditText etName, etDesc, etOpen, etClose;
    private ImageButton btnRegisterResto;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_resto);

        etName = findViewById(R.id.et_name_restaurant);
        etDesc = findViewById(R.id.et_desc_restaurant);
        etOpen = findViewById(R.id.et_opening_hours);
        etClose = findViewById(R.id.et_closing_hours);
        btnRegisterResto = findViewById(R.id.btn_register_resto);

        buttonClicked();
    }

    private void buttonClicked() {
        btnRegisterResto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadFile();
                saveStore();
            }
        });

        etOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker(etOpen);
            }
        });

        etClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker(etClose);
            }
        });

    }

    private void timePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        final int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int timeHours, int timeMinutes) {
                String minutesString = "";
                String hourString = "";

                if (timeMinutes == 0) {
                    minutesString = "00";
                } else {
                    minutesString = String.valueOf(timeMinutes);
                }

                if (timeHours == 0) {
                    hourString = "00";
                } else {
                    hourString = String.valueOf(timeHours);
                }

                editText.setText(hourString + ":" + minutesString);
            }
        }, hours, minutes, true);

        timePickerDialog.setTitle("Set hours");
        timePickerDialog.show();
    }

    private void saveStore() {
        String name = etName.getText().toString().trim();
        String description = etDesc.getText().toString().trim();
        String open = etOpen.getText().toString().trim();
        String close = etClose.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(open) || TextUtils.isEmpty(close)) {
            showLongToast("Field cannot be empty");
            return;
        }

        rootRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        final String dataID = rootRef.push().getKey();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("restoId", dataID);
        Restaurant restaurant = new Restaurant(
                etName.getText().toString().trim(),
                etDesc.getText().toString().trim(),
                etOpen.getText().toString().trim(),
                etClose.getText().toString().trim()
        );
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Create restaurant");
        progressDialog.setMessage("Please wait for a moment..");
        progressDialog.show();

        rootRef.child(userId).setValue(restaurant).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                showLongToast("Restaurant is created");
                finish();
            }
        });

        ref.updateChildren(values);
    }
}