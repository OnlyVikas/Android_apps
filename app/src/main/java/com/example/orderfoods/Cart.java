package com.example.orderfoods;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderfoods.Database.Database;
import com.example.orderfoods.Model.MyResponse;
import com.example.orderfoods.Model.Notification;
import com.example.orderfoods.Model.Order;
import com.example.orderfoods.Model.Request;
import com.example.orderfoods.Model.Sender;
import com.example.orderfoods.Model.Token;
import com.example.orderfoods.Remote.APIServices;
import com.example.orderfoods.ViewHolder.CartAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE =9999 ;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart=new ArrayList <>();
    CartAdapter adapter;

    APIServices mService;

    static PayPalConfiguration config=new PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId(com.example.orderfoods.config.PAYPAL_CLIENT_ID);
    String address,comment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);



        Intent i=new Intent(this, PayPalService.class);
        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(i);


        mService=Common.getFCMService();

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        txtTotalPrice=findViewById(R.id.total);
        btnPlace=findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cart.size()>0)
                   showAlertDialog();
                else
                    Toast.makeText(Cart.this,"your cart is empty !!",Toast.LENGTH_SHORT).show();

            }
        });
        
        loadListFood();
        

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step !");
        alertDialog.setMessage("Enter your address");


        final LayoutInflater inflater=this.getLayoutInflater();
        View order_address_comment=inflater.inflate(R.layout.order_address_comment,null);

        final MaterialEditText edtAddress=order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment=order_address_comment.findViewById(R.id.edtComment);

        final RadioButton rdiCOD=order_address_comment.findViewById(R.id.rdiCOD);
        final RadioButton rdiPaypal=order_address_comment.findViewById(R.id.rdiPaypal);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                address=edtAddress.getText().toString();
                comment=edtComment.getText().toString();
                
              if (!rdiPaypal.isChecked() && !rdiCOD.isChecked())
                {
                    Toast.makeText(Cart.this, "Please select payment option", Toast.LENGTH_SHORT).show();

                    getFragmentManager().beginTransaction()
                            .commit();
                    return;
                }
                else if (rdiPaypal.isChecked()) {

                    String formatAmount = txtTotalPrice.getText().toString()
                            .replace("$", "")
                            .replace(",", "");
                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                            "USD",
                            "Food Order",
                            PayPalPayment.PAYMENT_INTENT_SALE);
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                }
                else if (rdiCOD.isChecked())
                {

                    Request request=new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            txtTotalPrice.getText().toString(),
                            "0",
                            comment,
                            "COD",
                            "Unpaid",
                            cart

                    );

                    String order_number=String.valueOf(System.currentTimeMillis());
                    requests.child(order_number)
                            .setValue(request);

                    new Database(getBaseContext()).cleanCart();

                    sendNotificationOrder(order_number);

                   Toast.makeText(Cart.this, "Thank you,Order place", Toast.LENGTH_SHORT).show();
                   finish();
                }
            }


        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();


    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken=postSnapShot.getValue(Token.class);
                    Notification notification=new Notification("vikas","you have new order"+order_number);
                    Sender content=new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback <MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                   if (response.code()==200) {
                                       if (response.body().success == 1) {
                                           Toast.makeText(Cart.this, "Thank you,Order place", Toast.LENGTH_SHORT).show();
                                           finish();
                                       } else {
                                           Toast.makeText(Cart.this, "Failed", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                                }

                                @Override
                                public void onFailure(Call <MyResponse> call, Throwable t) {

                                    Log.e("ERROR",t.getMessage());
                                }
                            });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==PAYPAL_REQUEST_CODE)
        {
            if (resultCode==RESULT_OK)
            {
                PaymentConfirmation confirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation !=null)
                {
                    try{
                        String paymentDetail=confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject=new JSONObject(paymentDetail);


                        Request request=new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                comment,
                                "Paypal",
                                "Paid",
                                cart

                        );


                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);

                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this, "Thank you,Order place", Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            else if (requestCode== Activity.RESULT_CANCELED)
                Toast.makeText(this, "Payment cancel", Toast.LENGTH_SHORT).show();
            else if (requestCode==PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadListFood() {
        cart=new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total=0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for (Order item:cart)
            new Database(this).addToCart(item);
        loadListFood();
    }


}
