package com.example.ignas.fime;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Ignas on 10/26/2014.
 */
public class UserNotification {

    public static void notifyUserOfMessage(Context context, String type){
        String notification = type.replace('_',' ').toUpperCase() + " " + "Has been succesfully Sent!";
        Toast.makeText(context, notification, Toast.LENGTH_LONG ).show();
    }

}
