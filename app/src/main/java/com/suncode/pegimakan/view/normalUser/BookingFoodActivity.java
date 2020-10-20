package com.suncode.pegimakan.view.normalUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.suncode.pegimakan.R;
import com.suncode.pegimakan.utils.BaseActivity;

public class BookingFoodActivity extends BaseActivity {

    private EditText mAlamatEditText;
    private Button mTambahButton;
    private Button mKurangButton;
    private Button mOrderButton;
    private TextView mNamaTextView;
    private TextView mHargaTextView;
    private TextView mJumlahTextView;
    private TextView mTotalTextView;
    private TextView mOngkirTextView;
    private TextView mSubTotalTextView;
    private ImageView mItemImageView;
    private RadioButton mCODRadioButton;
    private RadioButton mTransferRadioButton;
    private RadioGroup mPembayaranRadioGroup;
    private int qty = 1;

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_food);

        mAlamatEditText = findViewById(R.id.editText_booking_address);
        mTambahButton = findViewById(R.id.button_booking_plus);
        mKurangButton = findViewById(R.id.button_booking_min);
        mOrderButton = findViewById(R.id.button_booking_order);
        mNamaTextView = findViewById(R.id.textView_booking_nama);
        mHargaTextView = findViewById(R.id.textView_booking_harga);
        mJumlahTextView = findViewById(R.id.textView_booking_qty);
        mTotalTextView = findViewById(R.id.textView_booking_total_price);
        mOngkirTextView = findViewById(R.id.textView_booking_ongkir);
        mSubTotalTextView = findViewById(R.id.textView_booking_sub_total);
        mItemImageView = findViewById(R.id.imageView_booking_imgage);
        mCODRadioButton = findViewById(R.id.radioButton);
        mTransferRadioButton = findViewById(R.id.radioButton2);
        mPembayaranRadioGroup = findViewById(R.id.radioGroup);

        mShowData(savedInstanceState);
    }

    private String mMetodePembayaran() {
        String pembayaran;
        if (mCODRadioButton.isChecked()) {
            return pembayaran = " Pembayaran Melalui Cash On Delivery ";
        } else {
            return pembayaran = " Pembayaran Melalui Transfer. ";
        }
    }

    private void initialPrice(String harga) {
        int total = Integer.valueOf(harga) * qty;
        mTotalTextView.setText(String.valueOf(total));
        int subtotal = total + 5000;
        mSubTotalTextView.setText(String.valueOf(subtotal));
    }

    private void mShowData(final Bundle savedInstanceState) {
        String itemId = getItemId(savedInstanceState);
        String restoId = getRestoId(savedInstanceState);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Restaurant").child(restoId).child("category").child("food").child(itemId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String harga = dataSnapshot.child("hargaMakanan").getValue(String.class);
                String nama = dataSnapshot.child("namaMakanan").getValue(String.class);
                String gambar = dataSnapshot.child("gambar").getValue(String.class);

                mNamaTextView.setText(nama);
                mHargaTextView.setText(harga);
                Picasso.get().load(gambar).into(mItemImageView);

                mButtonQtyClicked(harga, savedInstanceState);
                initialPrice(harga);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mButtonQtyClicked(final String harga, final Bundle bundle) {
        mTambahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qty = qty + 1;
                int hargaitem = Integer.parseInt(harga) * qty;
                int ongkir = Integer.parseInt(mOngkirTextView.getText().toString());
                int subTotal = hargaitem + ongkir;

                mJumlahTextView.setText(String.valueOf(qty));
                mTotalTextView.setText(String.valueOf(hargaitem));
                mSubTotalTextView.setText(String.valueOf(subTotal));
            }
        });

        mKurangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty <= 1) {
                    showLongToast("Cannot be null");
                } else {
                    qty = qty - 1;
                    int hargaitem = Integer.parseInt(mTotalTextView.getText().toString()) - Integer.parseInt(harga);
                    int ongkir = Integer.parseInt(mOngkirTextView.getText().toString());
                    int subTotal = hargaitem - ongkir;

                    mJumlahTextView.setText(String.valueOf(qty));
                    mTotalTextView.setText(String.valueOf(hargaitem));

                    if (subTotal < 0) {
                        mSubTotalTextView.setText("0");
                    } else {
                        mSubTotalTextView.setText(String.valueOf(subTotal));
                    }

                }
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(bundle);
            }
        });
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

    private String getItemId(Bundle savedInstanceState) {
        String itemId;
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                return itemId = null;
            } else {
                return itemId = bundle.getString("itemId");
            }
        } else {
            return itemId = (String) savedInstanceState.getSerializable("itemId");
        }
    }

    private void sendMessage(Bundle bundle) {
        String alamat = mAlamatEditText.getText().toString().trim();
        if (TextUtils.isEmpty(alamat)) {
            showLongToast("Field cannot be empty");
            mAlamatEditText.requestFocus();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(getRestoId(bundle));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phone = dataSnapshot.child("noHP").getValue(String.class);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String namaPemesan = dataSnapshot.child("username").getValue(String.class);
                        String alamat = mAlamatEditText.getText().toString();
                        String namaBarang = mNamaTextView.getText().toString();
                        String total = String.valueOf(qty);

                        final String message = "Saya pesan " + namaBarang + " sebanyak " + total + " porsi. dikirim ke : " + alamat + mMetodePembayaran() + "\n\n Penerima : " + namaPemesan;

                        boolean installed = isAppInstalled("com.whatsapp");
                        if (installed) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phone + "&text=" + message));
                            startActivity(i);
                        }else {
                            showLongToast("WhatsApp is not installed");
                        }
                    }
                    private boolean isAppInstalled(String s){
                        PackageManager packageManager = getPackageManager();
                        boolean is_installed;
                        try {
                            packageManager.getPackageInfo(s,PackageManager.GET_ACTIVITIES);
                            is_installed = true;
                        } catch (PackageManager.NameNotFoundException e) {
                            is_installed = false;
                            e.printStackTrace();
                        }
                        return is_installed;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}