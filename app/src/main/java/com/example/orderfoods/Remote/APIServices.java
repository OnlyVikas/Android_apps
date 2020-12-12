package com.example.orderfoods.Remote;

import com.example.orderfoods.Model.MyResponse;
import com.example.orderfoods.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {
    @Headers(

            {
                  "Content-Type:application/json",
                    "Authorization:key=AAAAmkUg9Sk:APA91bHXHFEyXN9o7kSLvrUulU42uAdh5dZp1bbIXr5BFUZ0qiUyGWfD4DZ1h689fD5yqSIDKfCGZnZqNKLd7Jt14zLo4lJixyiT-MT_V6TrVzGwwUZDVyiJQ6jszQh2qxquyySSI5me"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
