package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import edu.upenn.cis.rcr_34.reputablecoursereview.util.StaticUtils;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        // fake friends to fill
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
        for(int i = 0; i < friends.length; i++){
            Button v2 = new Button(this);
            v2.setText(friends[i]);
            v2.setId(i);
            v2.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            v2.setGravity(Gravity.CENTER_VERTICAL);
            v2.setVisibility(View.VISIBLE);
            LinearLayout friendListView = (LinearLayout) findViewById(R.id.friendListViewLL);
            friendListView.addView(v2);
        }

        User u = new User("poudels@seas.upenn.edu");
        Log.d("LOGIN ACTIVITY", "Received current user details:name" + u.getName());
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


        if (id == R.id.manage_account_item) {
            manageAccountClicked();
        }

        if (id == R.id.sign_out) {
            StaticUtils.signOutClicked(this.getApplicationContext());
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchClassesClicked(View view){
        EditText courseName = (EditText)findViewById(R.id.course_search);
        String course = courseName.getText().toString();

        // search for the course in parse
        final ParseQuery<ParseObject> courseSearch = ParseQuery.getQuery("Course");
        courseSearch.whereContains("Code", course.toUpperCase());
        courseSearch.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objList,ParseException e) {
                if (e == null) {
                    // initialize the course activity using Parse's ID of the course
                    if (objList.size() > 0) {
                        initializeCourse(objList.get(0));
                    } else {
                        Toast.makeText(getApplicationContext(), "Course not found",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Course not found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initializeCourse(ParseObject po) {
        Intent i = new Intent(this, CourseActivity.class);

        i.putExtra("property", "objectId");
        i.putExtra("name", po.getObjectId());
        startActivity(i);
    }

    public void manageAccountClicked(){
        Toast.makeText(getApplicationContext(), "Manage account selected", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(this, ManageAccountActivity.class);
        startActivity(intent2);
    }
}