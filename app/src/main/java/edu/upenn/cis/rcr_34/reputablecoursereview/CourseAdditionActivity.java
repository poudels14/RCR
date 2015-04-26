package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CourseAdditionActivity extends ActionBarActivity {
    private User user;
    private boolean newCourse;
    private String property;
    private String valueOfProperty;
    private ParseObject oldCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        String email = ParseUser.getCurrentUser().getEmail();
        user = new User(email);
        setContentView(R.layout.activity_course_addition);
        property = (String) getIntent().getStringExtra("property");
        valueOfProperty = (String) getIntent().getStringExtra("value");
        newCourse = true;
        if(!property.equals("new")){
            newCourse = false;
            ParseQuery<ParseObject> cT = ParseQuery.getQuery("coursesTaken");
            cT.whereContains(property, valueOfProperty);
            cT.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> courses, ParseException e) {
                    if (e == null) {
                        if (courses.size() > 0) {
                            oldCourse = courses.get(0);
                            EditText courseName = (EditText)findViewById(R.id.add_course_name);
                            courseName.setText((String)oldCourse.get("courseName"));
                            EditText semesterName = (EditText)findViewById(R.id.add_semester);
                            semesterName.setText((String)oldCourse.get("semester"));
                            EditText yearTaken = (EditText)findViewById(R.id.add_year);
                            yearTaken.setText((String)oldCourse.get("year"));
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    //Save the changes to the course
    public void saveCourseClicked(View view){
        //Get the name and semester
        EditText courseName = (EditText)findViewById(R.id.add_course_name);
        final String course = courseName.getText().toString().toUpperCase();
        EditText semesterName = (EditText)findViewById(R.id.add_semester);
        final String semester = semesterName.getText().toString().toUpperCase();
        EditText yearTaken = (EditText)findViewById(R.id.add_year);
        final String year = yearTaken.getText().toString();
        //Add the new course to the database
        // search for the course in parse
        final ParseQuery<ParseObject> courseSearch = ParseQuery.getQuery("Course");
        courseSearch.whereContains("Code", course);
        courseSearch.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objList, ParseException e) {
                if (e == null) {
                    // initialize the course activity using Parse's ID of the course
                    if (objList.size() > 0) {
                        if (newCourse) {
                            user.addCourse(course, semester, year);
                        }
                        else{
                            oldCourse.put("courseName", course);
                            oldCourse.put("semester", semester);
                            oldCourse.put("year", year);
                            oldCourse.saveInBackground();
                        }
                        //Return
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Course not found",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Course not found",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    //Remove the course from the database
    public void deleteCourseClicked(View view){
        if(!newCourse){
            oldCourse.deleteInBackground();
        }
        //Return, the course hasn't been saved and will never be added to the database
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}