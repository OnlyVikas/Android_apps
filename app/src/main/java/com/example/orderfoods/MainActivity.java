package com.example.orderfoods;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn,btnSignUp;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn=findViewById(R.id.btnSignIn);
        btnSignUp=findViewById(R.id.btnSignUp);
        txtSlogan=findViewById(R.id.txtslogan);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/Goodlight-Light.otf");
        txtSlogan.setTypeface(face);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(MainActivity.this,Sign.class);
                startActivity(i);

            }
        });
    btnSignUp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i= new Intent(MainActivity.this,Signup_01.class);
            startActivity(i);
        }
    });

    }
}
