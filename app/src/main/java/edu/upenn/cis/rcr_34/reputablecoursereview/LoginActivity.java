package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class LoginActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);

        //Check if user is already logged in
        if (isLoggedIn()) {
            logIn();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onResume(){
        ParseAPI.init(this);

        super.onResume();


        //Check if user is already logged in
        if (isLoggedIn()) {
            logIn();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void signInClicked(View view){
        EditText username = (EditText)findViewById(R.id.login_email);
        EditText password = (EditText)findViewById(R.id.login_password);
        String name = username.getText().toString();
        String word = password.getText().toString();

        //username and password should be checked at same time to prevent guessing
        username.setText("");
        password.setText("");
        this.checkLoginDetails(name, word);
    }

    protected void logIn(){
        Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void registerClicked(View view){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    // try to log the user in when 'sign in' is pressed
    private void checkLoginDetails(final String email, final String password){
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    logIn(); // go to the main activity
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Invalid username or wrong password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isLoggedIn(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return true;
        }
        return false;
    }
}
