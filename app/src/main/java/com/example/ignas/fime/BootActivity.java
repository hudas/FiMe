package com.example.ignas.fime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class BootActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        Parse.initialize(this, "CK76FfLEcXbnPKTMgceNsod80V8lGJqe8dQuERLK", "e8MqaWz21ZT05lA3JNCXwK1NGMNgOSzMoylMN4r0");

        ParseUser currentUser = ParseUser.getCurrentUser();
        final Intent openLoginActivity;

        if (currentUser != null) {
            openLoginActivity = new Intent(this, BasicMeniuActivity.class);
//            Toast.makeText(getApplicationContext(),
//                    "You are Logged in!",
//                    Toast.LENGTH_LONG).show();

        } else {
            openLoginActivity = new Intent(this, LoginActivity.class);
//            Toast.makeText(getApplicationContext(),
//                    "You are Not Logged in!",
//                    Toast.LENGTH_LONG).show();
        }

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    startActivity(openLoginActivity);
                    finish();
                }
            }
        };

        timer.start();
    }
}
