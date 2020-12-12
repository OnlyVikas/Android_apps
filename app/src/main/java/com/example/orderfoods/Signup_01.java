package com.example.orderfoods;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.orderfoods.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Signup_01 extends AppCompatActivity {
    MaterialEditText edtPhone,edtPassword,edtName;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_01);
        edtName=findViewById(R.id.edtName);

        edtPassword=findViewById(R.id.edtPassword);
        edtPhone=findViewById(R.id.edtPhone);

        btnSignUp=findViewById(R.id.btnSignUp);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInterner(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(Signup_01.this);
                    mDialog.setMessage("pleas waiting...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(Signup_01.this, "phone no. already registered", Toast.LENGTH_SHORT).show();

                            } else {
                                mDialog.dismiss();
                                User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                Toast.makeText(Signup_01.this, "Sign Up successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    Toast.makeText(Signup_01.this,"please check your connection !!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }





        });
    }
}
