package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.parse.ParseUser;
import java.util.ArrayList;

public class PastCourses extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_courses);

        // set the header of this view
        TextView t = (TextView)findViewById(R.id.header_text);

        t.setText("Past Courses: " +
                (String) ParseUser.getCurrentUser().get("firstName") +
                " " + (String) ParseUser.getCurrentUser().get("lastName"));
        //Display all courses taken
        ArrayList<String> courses = (ArrayList) ParseUser.getCurrentUser().get("coursesTaken");
        if(courses != null) {
            LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL);
            int widthEdit = 250;
            int widthCourse = 750;
            for (String s : courses) {
                //Get course information
                Course c = new Course(s);
                Button classButton = new Button(this);
                //Create button to access course
                classButton.setWidth(widthCourse);
                String code = c.getCourseCode();
                String semester = c.getSemesterTaken();
                classButton.setText(code + ", " + semester);
                classButton.setGravity(Gravity.CENTER_VERTICAL);
                classButton.setVisibility(View.VISIBLE);
                Button editButton = new Button(this);
                //Create button to edit course
                editButton.setText("Edit");
                editButton.setWidth(widthEdit);
                editButton.setGravity(Gravity.CENTER_VERTICAL);
                editButton.setVisibility(View.VISIBLE);
                //Add the buttons to a LinearLayout to be shown
                LinearLayout courseButton = new LinearLayout(this);
                courseButton.setOrientation(LinearLayout.HORIZONTAL);
                courseButton.addView(classButton);
                courseButton.addView(editButton);
                courseListView.addView(courseButton);
            }
        }
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
        Intent i = new Intent(this, CourseAdditionActivity.class);
        startActivity(i);
    }

    //User clicked return to manage
    public void returnToManageClicked(){
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}