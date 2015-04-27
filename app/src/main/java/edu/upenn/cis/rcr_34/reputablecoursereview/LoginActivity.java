package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity {
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //Check if user is already logged in
        if (isLoggedIn()) {
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


    //User has clicked sign in button
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

    //User has successfully logged in
    protected void logIn(){
        Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //User clicked register button
    public void registerClicked(View view){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }


    // try to log the user in when 'sign in' is pressed
    private void checkLoginDetails(final String email, final String password){
        Log.d("Came", "didnt crash 99");

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    boolean isEmailVerified = (Boolean) user.get("emailVerified");
                    if (!isEmailVerified){
                        Toast.makeText(getApplicationContext(),
                                "Please verify your email first",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d("LOGIN", "Login successful!");
                        logIn(); // go to the main activity
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Invalid username or wrong password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Check if hte user is logged in
    private boolean isLoggedIn(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return true;
        }
        return false;
    }

    //User is trying to log in with Facebook
    public void loginWithFacebook(View v){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override

                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {
                                            String a = (String) object.get("email");
                                            String first = (String) object.get("name");
                                            checkLoginDetailsFB(a, first);
                                        } catch (Exception e) {

                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {

                        Toast.makeText(getApplicationContext(), "There was an error.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // try to log the user in when 'sign in' is pressed
    private void checkLoginDetailsFB(final String email, final String name){
        final ParseUser user = new ParseUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Log.d("Came", "didnt crash 1");
                        ParseUser a = (ParseUser) list.get(0);
                        Toast.makeText(getApplicationContext(), "checking boolean",
                                Toast.LENGTH_SHORT).show();

                        Log.d("Came", "didnt crash 2");
                        if ((boolean) a.get("facebook") == true) {
                            Log.d("Came", "didnt crash 3");
                            checkLoginDetails(email, "password");
                        } else {
                            Log.d("Came", "didnt crash 4");
                            Toast.makeText(getApplicationContext(), "Account not created through Facebook",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d("Came", "didnt crash 5");
                        Toast.makeText(getApplicationContext(), "Account creating through Facebook",
                                Toast.LENGTH_SHORT).show();
                        registerWithFB(email, name);
                    }
                }
            }
        });
    }

    //User clicked register button
    public void registerWithFB(String email, String name){
        Intent intent = new Intent(this, CreateAccountActivityFacebook.class);
        intent.putExtra("email", email);
        String[] nameStore = name.split(" ");
        Log.d("Came", name);

        intent.putExtra("first", nameStore[0]);
        if (nameStore.length == 1) {
            intent.putExtra("last", " ");
        } else {
            intent.putExtra("last", nameStore[nameStore.length - 1]);
        }
        startActivity(intent);
        LoginManager.getInstance().logOut();

    }

}