package com.example.ignas.fime;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.ParseException;


public class SignUpActivity extends Activity {


    private Button loginButton;
    private Button signUpButton;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText passwordConfirmationField;
    private TextView singUpNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signupButton);
        usernameField = (EditText) findViewById(R.id.loginUsername);
        passwordField  = (EditText) findViewById(R.id.loginPassword);
        passwordConfirmationField  = (EditText) findViewById(R.id.loginPassword);
        emailField = (EditText) findViewById(R.id.loginEmail);
        singUpNotice = (TextView) findViewById(R.id.singUpNotice);
    }

    public void sendSingUpMessage(View view){
        final Intent intent = new Intent(this, BasicMeniuActivity.class);
        if (validateRegistrationForm()){
            ParseUser user = new ParseUser();
            user.setUsername(usernameField.getText().toString());
            user.setPassword(passwordField.getText().toString());
            user.setEmail(emailField.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.registration_success)
                                , Toast.LENGTH_LONG).show();
                        startService(new Intent(getApplicationContext(), MessageService.class)); // Starts messaging service
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "There was an error signing up."
                                , Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }

    private boolean validateRegistrationForm(){
        boolean valid = validatePersonalData();
        singUpNotice.setText("");

        if (valid) {
            valid = validatePasswordFields();
        }

        return valid;
    }

    private boolean validatePersonalData(){
        boolean valid = true;
        String email = emailField.getText().toString();
        String username = usernameField.getText().toString();

        if (username == null || username.trim().equals("")){
            valid = false;
            singUpNotice.setText("Username cannot be blank");
        }

        // Email validation
        if (valid && !email.matches("^[\\w0-9._%+-]+@[\\w0-9.-]+\\.[\\w]{2,4}$")){
            valid = false;
            singUpNotice.setText("Email is not Valid");
        }

        if (valid){
            try {
                ParseQuery<ParseUser> queryByUsername = ParseUser.getQuery();
                queryByUsername.whereEqualTo("username", username);
                if (!queryByUsername.find().isEmpty()){
                    singUpNotice.setText("Username Is Not Unique");
                    valid = false;
                }
            } catch (com.parse.ParseException e) {

            }
        }

        return valid;
    }
    private boolean validatePasswordFields(){
        boolean valid = true;
        String originalPassword = passwordField.getText().toString();
        String confirmationPassword = passwordConfirmationField.getText().toString();

        if (originalPassword.length() <= 5 ){
            singUpNotice.setText("Password Is Too short");
            valid = false;
        }
        if (!originalPassword.contentEquals(confirmationPassword)){
            valid = false;
            singUpNotice.setText("Passwords Don't Match");
        }

        return valid;
    }
}
