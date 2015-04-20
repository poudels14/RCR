package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import com.parse.ParseUser;

public class CourseAdditionActivity extends ActionBarActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        user = new User(ParseUser.getCurrentUser().getEmail());
        setContentView(R.layout.activity_course_addition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    //Add a review to the course
    public void addReviewClicked(View view){
        Toast.makeText(getApplicationContext(), "Add review", Toast.LENGTH_SHORT).show();
    }

    //Save the changes to the course
    public void saveCourseClicked(View view){
        //Get the name and semester
        EditText courseName = (EditText)findViewById(R.id.add_course_name);
        String course = courseName.getText().toString();
        EditText semesterName = (EditText)findViewById(R.id.add_semester);
        String semester = semesterName.getText().toString();
        //Construct the course
        Course newCourse = new Course(course, semester);
        //Add the new course to the database
        user.addCourse(course, semester, "2014");
        //Return
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    //Remove the course from the database
    public void deleteCourseClicked(View view){
        //Return, the course hasn't been saved and will never be added to the database
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}