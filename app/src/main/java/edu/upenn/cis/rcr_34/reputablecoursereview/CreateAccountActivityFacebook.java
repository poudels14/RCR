package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


public class CreateAccountActivityFacebook extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_facebook);
    }

    public void registerClicked(View v) {

        EditText emailField = (EditText) findViewById(R.id.createaccountFB_email);
        EditText yearField = (EditText) findViewById(R.id.createaccountFB_year);
        EditText majorField = (EditText) findViewById(R.id.createaccountFB_major);


        String firstName = getIntent().getStringExtra("first");
        String lastName = getIntent().getStringExtra("last");
        String email = emailField.getText().toString();
        String year = yearField.getText().toString();
        String major = majorField.getText().toString();
        String password = "password";

        //Make sure valid email used and the email is from upenn
        if(!email.equals("")){
            String[] emailSplitatAt = email.split("@");
            if (emailSplitatAt.length < 2){
                Toast.makeText(getApplicationContext(), "Please enter a valid email!",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                String[] emailSplitAtDot = emailSplitatAt[1].split("\\.");
                int length = emailSplitAtDot.length;

                if (length < 2 || !emailSplitAtDot[length - 1].equals("edu") || !emailSplitAtDot[length - 2].equals("upenn")){
                    Toast.makeText(getApplicationContext(), "Only Penn emails are accepted!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (email.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Email",
                    Toast.LENGTH_SHORT).show();
        }
        else if (firstName.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter First Name",
                    Toast.LENGTH_SHORT).show();
        } else if (lastName.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Last Name",
                    Toast.LENGTH_SHORT).show();
        }else if (major.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Major",
                    Toast.LENGTH_SHORT).show();
        }  else if (year.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter Year of Graduation",
                    Toast.LENGTH_SHORT).show();
        }
        else{
             createNewUser(firstName, lastName, email, major, year, password);
        }
    }

    public void cancelClicked(View v) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    private void createNewUser(String firstName, String lastName, final String email, String major,
                               String year, final String password) {
        final ParseUser user = new ParseUser();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        String userName = firstName + " " + lastName;
        userName = userName.toUpperCase();
        user.put("userName", userName);

        user.put("major", major);
        user.put("year", year);
        user.put("facebook", true);
        user.setEmail(email);
        ////////////////////////////////////////////////////////
        user.setUsername(getIntent().getStringExtra("email"));
        user.setPassword(password);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Toast.makeText(getApplicationContext(), "User Already Exists!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                Toast.makeText(getApplicationContext(), "Account Created!",
                                        Toast.LENGTH_SHORT).show();
                                Intent i = new Intent();
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }
}