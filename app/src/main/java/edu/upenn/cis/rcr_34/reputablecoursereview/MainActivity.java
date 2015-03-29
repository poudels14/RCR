package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView friendList = (TextView)findViewById(R.id.friendList);
        String[] friends = {"Andrew Remec", "Daniel McCann", "Tahmid Shahriar", "Sagar Poudel",
                "Alex Harelick", "Chris Murphy", "Amy Gutmann", "James Kirk", "John Rambo",
                "Alan \"Dutch\" Schaefer", "Michael Scott", "Jack Bauer", "Creed Bratton",
                "Troy Barnes", "Abed Nadir", "Dr. Ján Ïtor", "Loch Ness Monster"};
        for(int x = 0; x < friends.length; x++){
            friendList.append(friends[x]);
            if(x != (friends.length - 1)){
                friendList.append("\n");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void searchClassesClicked(View view){
        EditText courseName = (EditText)findViewById(R.id.course_search);
        String course = courseName.getText().toString();
        Toast.makeText(getApplicationContext(), "Searching for course: " + course, Toast.LENGTH_SHORT).show();
    }

    public void manageAccountClicked(View view){
        Toast.makeText(getApplicationContext(), "Manage account selected", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(this, ManageAccountActivity.class);
        startActivity(intent2);
    }

    public void signOutClicked(View view){
        Toast.makeText(getApplicationContext(), "Signing out", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}