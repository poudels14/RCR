package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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

    public void addReviewClicked(View view){
        Toast.makeText(getApplicationContext(), "Add review", Toast.LENGTH_SHORT).show();
    }

    public void saveCourseClicked(View view){
        Toast.makeText(getApplicationContext(), "Save selected", Toast.LENGTH_SHORT).show();
        EditText courseName = (EditText)findViewById(R.id.add_course_name);
        String course = courseName.getText().toString();
        EditText semesterName = (EditText)findViewById(R.id.add_semester);
        String semester = semesterName.getText().toString();
        Course newCourse = new Course(course, semester);
        user.addCourse(newCourse.toString());
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    public void deleteCourseClicked(View view){
        Toast.makeText(getApplicationContext(), "Delete selected", Toast.LENGTH_SHORT).show();
    }
}