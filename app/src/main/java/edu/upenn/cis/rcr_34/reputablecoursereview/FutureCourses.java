package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
        Log.d("Future", "0");
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
        LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL_FC);
        int width3 = 250;
        int width = 700;
        Log.d("Future", "1");
        if(courses != null) {
            Log.d("Future", "2");
            for(String s : courses) {
                Log.d("Future", "2.1");
                final Course course = new Course(s);
                TextView v1 = new TextView(this);
                v1.setWidth(width);
                Log.d("Future", "2.2");
                v1.setText(course.getCourseCode());
                v1.setGravity(Gravity.CENTER_VERTICAL);
                v1.setVisibility(View.VISIBLE);
                Button v3 = new Button(this);
                v3.setText("Remove");
                v3.setWidth(width3);
                Log.d("Future", "2.3");
                v3.setGravity(Gravity.CENTER_VERTICAL);
                v3.setVisibility(View.VISIBLE);
                v3.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL_FC);
                        View parent = (View) v.getParent();
                        courseListView.removeView(parent);
                        user.unplanCourse(course.toString());
                    }
                });
                LinearLayout courseButton = new LinearLayout(this);
                courseButton.setOrientation(LinearLayout.HORIZONTAL);
                courseButton.addView(v1);
                courseButton.addView(v3);
                courseListView.addView(courseButton);
            }
            Log.d("Future", "3");
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

    public void addClassClicked(View view){
        EditText courseName = (EditText)findViewById(R.id.add_course_name_FC);
        final String courseCode = courseName.getText().toString();


        // search for the course in parse
        final ParseQuery<ParseObject> courseSearch = ParseQuery.getQuery("Course");
        courseSearch.whereContains("Code", courseCode.toUpperCase());
        courseSearch.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objList,ParseException e) {
                if (e == null) {
                    // initialize the course activity using Parse's ID of the course
                    if (objList.size() > 0) {
                        addToList(courseCode);
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

    public void addToList (String courseCode) {
        final Course course = new Course(courseCode, "");
        user.planCourse(course.toString());
        Toast.makeText(getApplicationContext(), "Added course: " + course.getCourseCode(), Toast.LENGTH_SHORT).show();
        LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL_FC);
        int width3 = 250;
        int width = 700;
        TextView v1 = new TextView(this);
        v1.setWidth(width);
        v1.setText(course.getCourseCode());
        v1.setGravity(Gravity.CENTER_VERTICAL);
        v1.setVisibility(View.VISIBLE);
        Button v3 = new Button(this);
        v3.setText("Remove");
        v3.setWidth(width3);
        v3.setGravity(Gravity.CENTER_VERTICAL);
        v3.setVisibility(View.VISIBLE);
        v3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL_FC);
                View parent = (View) v.getParent();
                courseListView.removeView(parent);
                user.unplanCourse(course.toString());
            }
        });
        LinearLayout courseButton = new LinearLayout(this);
        courseButton.setOrientation(LinearLayout.HORIZONTAL);
        courseButton.addView(v1);
        courseButton.addView(v3);
        courseListView.addView(courseButton);
    }

    public void returnToManageClicked(){
        Toast.makeText(getApplicationContext(), "Return selected", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}