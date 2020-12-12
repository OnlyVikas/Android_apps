package com.example.orderfoods;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.orderfoods.Database.Database;
import com.example.orderfoods.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {


    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String categoryId="";

    FirebaseDatabase database;
    DatabaseReference category;

    Category  currentFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        database=FirebaseDatabase.getInstance();
        category=database.getReference("category");

        numberButton=findViewById(R.id.number_button);
        btnCart=findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new  Database(getBaseContext()).addToCart(new Order(
                        categoryId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()



                ));

                Toast.makeText(FoodDetails.this, "Added to Cart", Toast.LENGTH_SHORT).show();

            }
        });

        food_description=findViewById(R.id.food_description);
        food_name=findViewById(R.id.food_name);
        food_price=findViewById(R.id.food_price);
        food_image=findViewById(R.id.img_food);

        collapsingToolbarLayout=findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


        if (getIntent() !=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty())
        {
            if (Common.isConnectedToInterner(getApplicationContext()))
                 getDetailFood(categoryId);
            else
            {
                Toast.makeText(FoodDetails.this,"please check your connection !!",Toast.LENGTH_SHORT).show();
                return;
            }
        }



    }

    private void getDetailFood(String categoryId) {

        category.child(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentFood=dataSnapshot.getValue(Category.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_name.setText(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_description.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
