package com.cenah.buyme.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cenah.buyme.Models.Common;
import com.cenah.buyme.Models.User;
import com.cenah.buyme.R;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 9999;
    private DatabaseReference users;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/AbrilFatface-Regular.otf")
                .setFontAttrId(R.attr.fontPath).build());

       // prinKeyHash();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        Button btn_continue = findViewById(R.id.btn_continue);//Buton veya textin adres id sini tanıyan kısım
        TextView tx_appname = findViewById(R.id.tx_appname);
        tx_appname.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF"));

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),LoginActivity.class)); // Bu Sayfadan butona bastıktan sonra gitmek istediğin sayfaya gider.
                startLoginSystem();
            }
        });

        if(AccountKit.getCurrentAccessToken() !=null){
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait..");
            progressDialog.show();

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    progressDialog.dismiss();
                    users.child(account.getPhoneNumber().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User localUser = dataSnapshot.getValue(User.class);
                                    finish();
                                    Common.currnetUser = localUser;
                                    Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }


    }

    private void startLoginSystem() {
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void prinKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.cenah.buyme",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            if (data != null) {
                AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
                if (result.getError() != null) {
                    Toast.makeText(this, "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                } else if (result.wasCancelled()) {
                    Toast.makeText(this, "CANCEL", Toast.LENGTH_SHORT).show();
                } else {

                    if (result.getAccessToken() != null) {
                        //show dialog
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Please wait..");
                        progressDialog.show();


                        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                            @Override
                            public void onSuccess(Account account) {
                                final String userPhone = account.getPhoneNumber().toString();

                                //check if exist on firebase
                                users.orderByKey().equalTo(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.child(userPhone).exists()){
                                             User newUser = new User();
                                             newUser.setPhone(userPhone);
                                             newUser.setName("");
                                             newUser.setIsStaff("false");
                                             newUser.setPassword("");

                                             users.child(userPhone)
                                                     .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if(task.isSuccessful())
                                                         Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                                     users.child(userPhone)
                                                             .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                 @Override
                                                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                     User localUser = dataSnapshot.getValue(User.class);
                                                                     finish();
                                                                     Common.currnetUser = localUser;
                                                                     Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                                                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                     startActivity(intent);

                                                                     progressDialog.dismiss();
                                                                 }

                                                                 @Override
                                                                 public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                     progressDialog.dismiss();

                                                                 }
                                                             });
                                                 }
                                             });

                                        }else{

                                            // if exist just login
                                            users.child(userPhone)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            User localUser = dataSnapshot.getValue(User.class);
                                                            finish();
                                                            Common.currnetUser = localUser;
                                                            Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);

                                                            progressDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            progressDialog.dismiss();

                                                        }
                                                    });



                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progressDialog.dismiss();

                                    }
                                });
                            }

                            @Override
                            public void onError(AccountKitError accountKitError) {
                                Toast.makeText(MainActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }
                        });
                    }
                }
            }
        }
    }
}
