package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.Arrays;
import java.util.List;
import android.support.v4.app.*;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Toast.makeText(getApplicationContext(), "I made it to 1", Toast.LENGTH_SHORT).show();

                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override

                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        Toast.makeText(getApplicationContext(), "I made it to end", Toast.LENGTH_SHORT).show();
                                        // Application code
                                        try {
                                            Toast.makeText(getApplicationContext(), (String) object.get("email"), Toast.LENGTH_SHORT).show();
                                            String a = (String) object.get("email");
                                        } catch (Exception e) {

                                        }


                                    }
                                });

                        Toast.makeText(getApplicationContext(), "I made it to somewhere", Toast.LENGTH_SHORT).show();

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(getApplicationContext(), "I made it to 2", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(getApplicationContext(), "I made it to 3", Toast.LENGTH_SHORT).show();

                    }
                });

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "W5JnCKDZoSYR2AHncCRPZ7TZ94e3x9RJcAQjoc0a",
                "9vWs3BE45BCEtigsvl9ezo14wAg2ECoPxADTxtoC");

        //Check if user is already logged in
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            logIn();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
}
