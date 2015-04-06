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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

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

    public void addClassClicked(View view){
        EditText courseName = (EditText)findViewById(R.id.add_course_name);
        String course = courseName.getText().toString();
        Toast.makeText(getApplicationContext(), "Added course: " + course, Toast.LENGTH_SHORT).show();
        LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL);
        int width3 = 250;
        int width = courseListView.getWidth() - width3;
        Button v1 = new Button(this);
        v1.setWidth(width);
        v1.setText(course);
        v1.setGravity(Gravity.CENTER_VERTICAL);
        v1.setVisibility(View.VISIBLE);
        v1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Button thisButton = (Button)v;
                String course = (String)thisButton.getText();
                Toast.makeText(getApplicationContext(), "Go to: " + course, Toast.LENGTH_SHORT).show();
            }
        });
        Button v3 = new Button(this);
        v3.setText("Remove");
        v3.setWidth(width3);
        v3.setGravity(Gravity.CENTER_VERTICAL);
        v3.setVisibility(View.VISIBLE);
        v3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                LinearLayout courseListView = (LinearLayout) findViewById(R.id.courseListViewLL);
                View parent = (View) v.getParent();
                courseListView.removeView(parent);
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