package com.example.ignas.fime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends Activity {

    private EditText usernameField;
    private EditText passwordField;
    private TextView noticeTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.loginUsername);
        passwordField  = (EditText) findViewById(R.id.loginPassword);
        noticeTextField = (TextView) findViewById(R.id.loginNotice);
    }


    public void sendLoginMessage(final View view){
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.login_connection_notice),
                            Toast.LENGTH_LONG).show();
        } else {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, com.parse.ParseException e) {
                    if (user != null) {
                        Intent intent = new Intent(view.getContext(), BasicMeniuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        noticeTextField.setText(getString(R.string.login_data_notice));
                    }
                }
            });
        }

    }

    public void sendRegisterMessage(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
