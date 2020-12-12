package com.example.orderfoods;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.orderfoods.Model.User;
import com.example.orderfoods.Remote.APIServices;
import com.example.orderfoods.Remote.RetrofitClient;

public class Common {
    public static User currentUser;

    private static final String BASE_URL="https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static APIServices getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIServices.class);

    }


    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static final String DELETE ="Delete";

    public static boolean isConnectedToInterner(Context context)
    {

        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager !=null)
        {
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if (info !=null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if (info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
