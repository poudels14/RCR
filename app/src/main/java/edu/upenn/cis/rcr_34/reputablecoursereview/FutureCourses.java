package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class FutureCourses extends ActionBarActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_courses);

        user = new User(ParseUser.getCurrentUser().getEmail());

        // set the header of this view
        TextView t = (TextView)findViewById(R.id.header_text_FC);

        t.setText("Future Courses: " +
                (String) ParseUser.getCurrentUser().get("firstName") +
                " " + (String) ParseUser.getCurrentUser().get("lastName"));
        ArrayList<String> courses = (ArrayList) ParseUser.getCurrentUser().get("plannedCourses");
        //Display previously added courses
        if(courses != null) {
            for(String s : courses) {
                final ParseQuery<ParseObject> courseSearch = ParseQuery.getQuery("Course");
                courseSearch.whereContains("objectId", s);
                courseSearch.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objList,ParseException e) {
                        if (e == null) {
                            if (objList.size() > 0) {
                                ParseObject obj = objList.get(0);
                                addToList(obj);
                            } else {
                                Toast.makeText(getApplicationContext(), "No future courses",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error populating courses",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.future_courses_menu, menu);
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

    //Add a course
    public void addClassClicked(View view){
        EditText courseName = (EditText)findViewById(R.id.add_course_name_FC);
        final String courseCode = courseName.getText().toString().toUpperCase();


        // search for the course in parse
        final ParseQuery<ParseObject> courseSearch = ParseQuery.getQuery("Course");
        courseSearch.whereContains("Code", courseCode);
        courseSearch.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objList,ParseException e) {
                if (e == null) {
                    // initialize the course activity using Parse's ID of the course
                    if (objList.size() > 0) {
                        ParseObject obj = objList.get(0);
                        if(!user.hasPlannedCourse(obj)) {
                            addToList(obj);
                            user.planCourse(obj.getObjectId());
                        }
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

    //Add a planned course to the list
    public void addToList (ParseObject course) {
        //Get course information
        LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL_FC);
        int widthRemove = 250;
        int widthCourse = 700;
        //Set TextView with course information
        TextView nameView = new TextView(this);
        String courseCode = (String) course.get("Code");
        final String id = course.getObjectId();
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeCourse(id);
            }
        });
        nameView.setWidth(widthCourse);
        nameView.setText(courseCode);
        nameView.setGravity(Gravity.CENTER_VERTICAL);
        nameView.setVisibility(View.VISIBLE);
        //Make button to remove course
        Button removeButton = new Button(this);
        removeButton.setText("Remove");
        removeButton.setWidth(widthRemove);
        removeButton.setGravity(Gravity.CENTER_VERTICAL);
        removeButton.setVisibility(View.VISIBLE);
        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL_FC);
                View parent = (View) v.getParent();
                courseListView.removeView(parent);
                user.unplanCourse(id);
            }
        });
        //Add TextView and button to a LinearLayout to be added to be shown
        LinearLayout courseButton = new LinearLayout(this);
        courseButton.setOrientation(LinearLayout.HORIZONTAL);
        courseButton.addView(nameView);
        courseButton.addView(removeButton);
        courseListView.addView(courseButton);
    }

    private void initializeCourse(String courseId) {
        Intent i = new Intent(this, CourseActivity.class);
        i.putExtra("property", "objectId");
        i.putExtra("value", courseId);
        startActivity(i);
    }

    //Return
    public void returnToManageClicked(){
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}