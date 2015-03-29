package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by sagar on 3/29/15.
 */
public class CreateAccountActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "W5JnCKDZoSYR2AHncCRPZ7TZ94e3x9RJcAQjoc0a", "9vWs3BE45BCEtigsvl9ezo14wAg2ECoPxADTxtoC");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    protected void registerClicked() {
        EditText firstName = (EditText) findViewById(R.id.createaccount_firstname);
        EditText lastName = (EditText) findViewById(R.id.createaccount_lastname);
        EditText email = (EditText) findViewById(R.id.createaccount_email);
        EditText year = (EditText) findViewById(R.id.createaccount_year);
        EditText major = (EditText) findViewById(R.id.createaccount_major);
        EditText password = (EditText) findViewById(R.id.createaccount_password);
        EditText passwordConfirm = (EditText) findViewById(R.id.createaccount_passwordconfirmation);
//        Button register = (Button) findViewById(R.id.createacccout_register_button);

        // Check if password matches
        if (!password.getText().equals(passwordConfirm.getText())) {
            Toast.makeText(getApplicationContext(), "Password doesn't match!", Toast.LENGTH_SHORT).show();
            password.setText("");
            passwordConfirm.setText("");
        } else {
            createNewUser(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(), year.getText().toString(), password.getText().toString());
        }
    }

    private void createNewUser(String firstName, String lastName, String email, String year, final String password) {
        final ParseObject newObject = new ParseObject("userDetails");
        newObject.put("firstName", firstName);
        newObject.put("lastName", lastName);
        newObject.put("email", email);
        newObject.put("year", year);
        newObject.put("password", password);

        ParseQuery query = ParseQuery.getQuery("userDetails");
        query.whereEqualTo("username", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Toast.makeText(getApplicationContext(), "User Already Exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        newObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
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
