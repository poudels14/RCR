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
import com.parse.SaveCallback;

import java.text.ParseException;
import java.util.List;


public class CreateAccountActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        // Check if password matches
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(getApplicationContext(), "Password doesn't match!", Toast.LENGTH_SHORT).show();
            passwordField.setText("");
            passwordConfirmField.setText("");
        }
        else if (firstName.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter first name", Toast.LENGTH_SHORT).show();
        } else if (lastName.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter last name", Toast.LENGTH_SHORT).show();
        } else if (email.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter email", Toast.LENGTH_SHORT).show();
        } else if (year.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter year of graduation", Toast.LENGTH_SHORT).show();
        } else if (major.equals("")){
            Toast.makeText(getApplicationContext(), "Please Enter major", Toast.LENGTH_SHORT).show();
        } else {
            createNewUser(firstName, lastName, email, major, year, password);
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void cancelClicked(View v) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    private void createNewUser(String firstName, String lastName, String email, String major, String year, final String password) {
        final ParseObject newObject = new ParseObject("userDetails");
        newObject.put("firstName", firstName);
        newObject.put("lastName", lastName);
        newObject.put("email", email);
        newObject.put("major", major);
        newObject.put("year", year);
        newObject.put("password", password);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("userDetails");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Toast.makeText(getApplicationContext(), "User Already Exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        newObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
//                                logIn();
                            }
                        });
                    }
                }
            }
        });
    }
}