package com.example.orderfoods;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.orderfoods.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Sign extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        btnSignIn = findViewById(R.id.btnSignIn);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInterner(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(Sign.this);
                    mDialog.setMessage("please waiting...");
                    mDialog.show();


                    table_user.addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    {
                                        Intent i = new Intent(Sign.this, Kbc.class);
                                        Common.currentUser = user;
                                        startActivity(i);
                                        finish();
                                    }


                                } else {
                                    Toast.makeText(Sign.this, "wrong password !!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(Sign.this, "user not exist", Toast.LENGTH_SHORT).show();
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(Sign.this,"please check your connection !!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });
    }
}
