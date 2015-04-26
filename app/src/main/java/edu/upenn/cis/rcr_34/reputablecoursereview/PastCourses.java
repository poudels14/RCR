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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class PastCourses extends ActionBarActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_courses);

        String email = ParseUser.getCurrentUser().getEmail();
        user = new User(email);

        // set the header of this view
        TextView t = (TextView)findViewById(R.id.header_text);

        t.setText("Past Courses: " +
                (String) ParseUser.getCurrentUser().get("firstName") +
                " " + (String) ParseUser.getCurrentUser().get("lastName"));
        //Display all courses taken
        showCourses(email);
    }

    @Override
    protected void onResume(){
        super.onResume();
        String email = user.getEmail();
        showCourses(email);
    }

    private void showCourses(String email){
        LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL);
        courseListView.removeAllViews();
        ParseQuery<ParseObject> cT = ParseQuery.getQuery("coursesTaken");
        cT.whereContains("userEmail", email);
        cT.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> courses, ParseException e) {
                if (e == null) {
                    if (courses.size() > 0) {
                        for (ParseObject ob : courses) {
                            showPastCourse(ob);
                        }
                    }
                }
            }
        });
    }

    private void showPastCourse(ParseObject course){
        //Get course information
        LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL);
        int widthEdit = 250;
        int widthCourse = 750;
        Button classButton = new Button(this);
        //Create button to access course
        classButton.setWidth(widthCourse);
        final String code = (String) course.get("courseName");
        final String id = course.getObjectId();
        String semester = (String) course.get("semester");
        String year = (String) course.get("year");
        classButton.setText(code + ", " + semester + " " + year);
        classButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                initializeCourse(code);
            }
        });
        classButton.setGravity(Gravity.CENTER_VERTICAL);
        classButton.setVisibility(View.VISIBLE);
        Button editButton = new Button(this);
        //Create button to edit course
        editButton.setText("Edit");
        editButton.setWidth(widthEdit);
        editButton.setGravity(Gravity.CENTER_VERTICAL);
        editButton.setVisibility(View.VISIBLE);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                addClass("objectId",id);
            }
        });
        //Add the buttons to a LinearLayout to be shown
        LinearLayout courseButton = new LinearLayout(this);
        courseButton.setOrientation(LinearLayout.HORIZONTAL);
        courseButton.addView(classButton);
        courseButton.addView(editButton);
        courseListView.addView(courseButton);
    }

    private void initializeCourse(String courseCode) {
        Intent i = new Intent(this, CourseActivity.class);
        i.putExtra("property", "Code");
        i.putExtra("value", courseCode);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.past_courses_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.return_to_manage) {
            returnToManageClicked();
        }

        return super.onOptionsItemSelected(item);
    }

    //User clicked add class
    public void addClassClicked(View view){
        addClass("new", "");
    }

    public void addClass( String property, String value){
        Intent i = new Intent(this, CourseAdditionActivity.class);
        i.putExtra("property", property);
        i.putExtra("value", value);
        startActivity(i);
    }

    //User clicked return to manage
    public void returnToManageClicked(){
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}