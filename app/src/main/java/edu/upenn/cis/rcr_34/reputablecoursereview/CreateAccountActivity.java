package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.text.ParseException;
import java.util.List;


public class CreateAccountActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void registerClicked(View v) {

        EditText firstNameField = (EditText) findViewById(R.id.createaccount_firstname);
        EditText lastNameField = (EditText) findViewById(R.id.createaccount_lastname);
        EditText emailField = (EditText) findViewById(R.id.createaccount_email);
        EditText yearField = (EditText) findViewById(R.id.createaccount_year);
        EditText majorField = (EditText) findViewById(R.id.createaccount_major);
        EditText passwordField = (EditText) findViewById(R.id.createaccount_password);
        EditText passwordConfirmField = (EditText) findViewById(R.id.createaccount_passwordconfirmation);


        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String email = emailField.getText().toString();
        String year = yearField.getText().toString();
        String major = majorField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordConfirm = passwordConfirmField.getText().toString();
//        Button register = (Button) findViewById(R.id.createacccout_register_button);

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
            // Check if password matches
            if (password.length() < 5) {
                Toast.makeText(getApplicationContext(),
                        "Password should be at least 5 characters long", Toast.LENGTH_SHORT).show();
                passwordField.setText("");
                passwordConfirmField.setText("");
            }
            else if (!password.equals(passwordConfirm)) {
                Toast.makeText(getApplicationContext(), "Password doesn't match!",
                        Toast.LENGTH_SHORT).show();
                passwordField.setText("");
                passwordConfirmField.setText("");
            }else {
                createNewUser(firstName, lastName, email, major, year, password);
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    public void cancelClicked(View v) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    private void createNewUser(String firstName, String lastName, String email, String major,
                               String year, final String password) {
        final ParseUser user = new ParseUser();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("major", major);
        user.put("year", year);

        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(password);

        ParseQuery<ParseUser> query = ParseQuery.getQuery("userDetails");
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

                            }
                        });
                    }
                }
            }
        });
    }
}