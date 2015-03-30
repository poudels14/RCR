package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        TextView friendList = (TextView)findViewById(R.id.friendList);
        String[] friends = {"Andrew Remec", "Daniel McCann", "Tahmid Shahriar", "Sagar Poudel",
                "Alex Harelick", "Chris Murphy", "Amy Gutmann", "James Kirk", "Spock",
                "Leonard McCoy", "Montgomery Scott", "Nyota Uhura", "Hikaru Sulu", "Pavel Chekov",
                "Christine Chapel", "Janice Rand", "Jean-Luc Picard", "William Riker",
                "Geordi La Forge", "Benjamin Sisko", "Kathryn Janeway", "Jonathan Archer",
                "Sterling Archer", "John Rambo", "Alan \"Dutch\" Schaefer", "Michael Scott",
                "Dwight Schrute", "Jim Halpert", "Pam Halpert", "Creed Bratton", "Jack Bauer",
                "Troy Barnes", "Abed Nadir", "Dr. Ján Ïtor", "John Dorian", "Christopher Turk",
                "Percival Cox", "Bruce Wayne", "Brennan Huff", "Dale Doback", "Ricky Bobby",
                "Cal Naughton, Jr.", "Aragorn II Elessar", "Frodo Baggins", "Bilbo Baggins",
                "Samwise Gamgee", "Peregrin Took" ,"Meriadoc Brandybuck", "Loch Ness Monster"};
        for(int x = 0; x < friends.length; x++){
            friendList.append(friends[x]);
            if(x != (friends.length - 1)){
                friendList.append("\n");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.manage_account_item) {
            manageAccountClicked();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.sign_out) {
            signOutClicked();
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchClassesClicked(View view){
        EditText courseName = (EditText)findViewById(R.id.course_search);
        String course = courseName.getText().toString();
        Toast.makeText(getApplicationContext(), "Searching for course: " + course, Toast.LENGTH_SHORT).show();
    }

    public void manageAccountClicked(){
        Toast.makeText(getApplicationContext(), "Manage account selected", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(this, ManageAccountActivity.class);
        startActivity(intent2);
    }

    public void signOutClicked(){
        ParseUser.logOut();

        Toast.makeText(getApplicationContext(), "Signing out", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}