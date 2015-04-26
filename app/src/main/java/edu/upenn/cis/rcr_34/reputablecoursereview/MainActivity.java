package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.parse.ParseUser;
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
        LinearLayout friendListView = (LinearLayout) findViewById(R.id.friendListViewLL);
        friendListView.removeAllViews();
        EditText searchName = (EditText)findViewById(R.id.course_search);
        final String toSearch = searchName.getText().toString().toUpperCase();
        if(toSearch.length() > 0) {
            // search for the course in parse
            final ParseQuery<ParseObject> courseSearch = ParseQuery.getQuery("Course");
            courseSearch.whereStartsWith("Code", toSearch);
            courseSearch.whereEndsWith("Code", toSearch);
            courseSearch.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objList, ParseException e) {
                    if (e == null) {
                        // initialize the course activity using Parse's ID of the course
                        if (objList.size() > 0) {
                            initializeCourse(objList.get(0));
                        }
                        else {
                            // no course found, check to see if it's a person
                            final ParseQuery<ParseUser> personSearch = ParseUser.getQuery();
                            personSearch.whereContains("userName", toSearch);
                            personSearch.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List list, com.parse.ParseException e) {
                                    if (e == null) {
                                        if (list.size() > 0) {
                                            LinearLayout friendListView = (LinearLayout) findViewById(R.id.friendListViewLL);
                                            friendListView.removeAllViews();
                                            for (Object o : list) {
                                                ParseUser user = (ParseUser) o;
                                                addUserButton(user);
                                            }
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Nothing found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    protected void addUserButton(ParseUser user){
        Button userButton = new Button(this);
        userButton.setText(user.get("firstName") + " " + user.get("lastName"));
        userButton.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        userButton.setGravity(Gravity.CENTER_VERTICAL);
        userButton.setVisibility(View.VISIBLE);
        final String email = user.getEmail();
        userButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToPersonsProfile(email);
            }
        });
        LinearLayout friendListView = (LinearLayout) findViewById(R.id.friendListViewLL);
        friendListView.addView(userButton);
    }

    //Go to a friend's profile
    protected void goToPersonsProfile(String email){
        Intent intent = new Intent(this, FriendsDetailActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void initializeCourse(ParseObject po) {
        Intent i = new Intent(this, CourseActivity.class);
        i.putExtra("property", "objectId");
        i.putExtra("value", po.getObjectId());
        startActivity(i);
    }

    public void manageAccountClicked(){
        Intent intent2 = new Intent(this, ManageAccountActivity.class);
        startActivity(intent2);
    }
}