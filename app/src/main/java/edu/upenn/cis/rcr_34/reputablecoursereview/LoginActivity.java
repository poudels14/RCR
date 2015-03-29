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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class LoginActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "W5JnCKDZoSYR2AHncCRPZ7TZ94e3x9RJcAQjoc0a", "9vWs3BE45BCEtigsvl9ezo14wAg2ECoPxADTxtoC");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void signInClicked(View view){
        EditText username = (EditText)findViewById(R.id.username);
        EditText password = (EditText)findViewById(R.id.password);
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


    private void checkLoginDetails(final String username, final String password){
        ParseQuery query = ParseQuery.getQuery("userDetails");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0){
                        ParseObject po = (ParseObject) list.get(0);
                        String cloudUsername = (String) po.get("username");
                        String cloudPassword = (String) po.get("password");
                        if (cloudPassword.equals(password) && cloudUsername.equals(username)){
                            logIn();
                            Log.d("Database", "Logged in");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Invalid username or wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Invalid username or wrong password", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d("Database", "Error: " + e.getMessage());
                }
            }
        });
    }


    private void createNewUser(final String username, final String password){
        final ParseObject newObject = new ParseObject("userDetails");
        newObject.put("username", username);
        newObject.put("password", password);

        ParseQuery query = ParseQuery.getQuery("userDetails");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0){
                        Toast.makeText(getApplicationContext(), "User Already Exists!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        newObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                                logIn();
                            }
                        });
                    }
                }
            }
        });
    }
}
