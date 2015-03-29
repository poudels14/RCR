package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;


public class LoginActivity extends ActionBarActivity {

    private HashMap<String, String> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        users = new HashMap<String, String>();
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
        if(users.containsKey(name)){
            if(users.get(name).equals(word)){
                Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
                username.setText("");
                logIn();
            }
            else{
                Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Invalid username", Toast.LENGTH_SHORT).show();
        }
        password.setText("");
    }

    private void logIn(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void registerClicked(View view){
        EditText username = (EditText)findViewById(R.id.username);
        EditText password = (EditText)findViewById(R.id.password);
        String name = username.getText().toString();
        String word = password.getText().toString();
        if(users.containsKey(name)){
            Toast.makeText(getApplicationContext(), "That username is already taken", Toast.LENGTH_SHORT).show();
        }
        else{
            if(name.length() == 0){
                Toast.makeText(getApplicationContext(), "You must enter a username", Toast.LENGTH_SHORT).show();
            }
            else if(word.length() == 0){
                Toast.makeText(getApplicationContext(), "You must enter a password", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                users.put(name, word);
                username.setText("");
                logIn();
            }
        }
        password.setText("");
    }
}
