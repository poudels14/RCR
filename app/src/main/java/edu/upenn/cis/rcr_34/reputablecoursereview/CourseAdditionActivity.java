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

public class CourseAdditionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_addition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void addReviewClicked(View view){
        Toast.makeText(getApplicationContext(), "Add selected", Toast.LENGTH_SHORT).show();
    }

    public void saveCourseClicked(View view){
        Toast.makeText(getApplicationContext(), "Save selected", Toast.LENGTH_SHORT).show();
    }

    public void deleteCourseClicked(View view){
        Toast.makeText(getApplicationContext(), "Delete selected", Toast.LENGTH_SHORT).show();
    }
}