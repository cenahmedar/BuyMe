package com.cenah.buyme.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cenah.buyme.Models.User;
import com.cenah.buyme.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {
    private EditText ed_phone, ed_password,ed_name;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/AbrilFatface-Regular.otf").setFontAttrId(R.attr.fontPath).build());


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        FirebaseApp.initializeApp(this);



        ed_phone = findViewById(R.id.ed_phone); // id sini verdiğimiz yer.
        ed_password = findViewById(R.id.ed_password);
        ed_name = findViewById(R.id.ed_name);

        Button btn_save = findViewById(R.id.btnLogin);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tel. isim ve parolanın boş olup olmadığını kontrol ediyor.
                if (ed_password.getText().toString().isEmpty() || ed_phone.getText().toString().isEmpty()|| ed_name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields..", Toast.LENGTH_LONG).show();
                    return;
                }

                // Dönen bekleme şeklini gösterme yeri.
                final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setMessage("Searching..");
                progressDialog.show();


                // firebase den çektiğimiz user verilerini table_user a getir.
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        // kişi telefonunun kontrol edip aynı nımara varsa kayedetmiyor.
                        if (dataSnapshot.child(ed_phone.getText().toString()).exists()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Phone number found, use another number..", Toast.LENGTH_LONG).show();

                        }else{
                            progressDialog.dismiss();
                            User user = new User(ed_name.getText().toString().trim(),ed_password.getText().toString().trim());
                            table_user.child(ed_phone.getText().toString()).setValue(user);
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }
}
