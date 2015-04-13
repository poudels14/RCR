package edu.upenn.cis.rcr_34.reputablecoursereview;

import edu.upenn.cis.rcr_34.reputablecoursereview.util.StaticUtils;
import edu.upenn.cis.rcr_34.reputablecoursereview.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;


public class CourseActivity extends ActionBarActivity {

    private String parseCourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        FacebookSdk.sdkInitialize(getApplicationContext());

        parseCourseID = getIntent().getStringExtra("parseID");

        Toast.makeText(getApplicationContext(),
                parseCourseID, Toast.LENGTH_SHORT).show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.getInBackground(parseCourseID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, com.parse.ParseException e) {
                if (e == null) {
                    // pass the object's info into the interface
                    if (parseObject == null) {
                        Toast.makeText(getApplicationContext(),
                                "Error Retrieving Course", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("COURSE_ACTIVITY", parseObject.getString("Name"));
                        populateUI(parseObject.getDouble("Rating"),
                                parseObject.getString("Name"),
                                parseObject.getString("Code"));
                    }


                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error Retrieving Course", Toast.LENGTH_SHORT).show();
                    // quit this activity
                    Intent i = new Intent();
                    setResult(RESULT_CANCELED, i);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.manage_account_item) {
            manageAccountClicked();
        }

        if (id == R.id.sign_out) {
            StaticUtils.signOutClicked(getApplicationContext());
        }
        return super.onOptionsItemSelected(item);
    }

    public void manageAccountClicked(){
        Toast.makeText(getApplicationContext(), "Manage account selected", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(this, ManageAccountActivity.class);
        startActivity(intent2);
    }

    public void onReviewClicked(View v) {
        Intent i = new Intent(this, ReviewActivity.class);
        i.putExtra("parseID", parseCourseID);
        startActivity(i);
    }

    protected void populateUI(Double rating, String name, String code) {
        // set the header of this view
        TextView t = (TextView)findViewById(R.id.course_code);
        t.setText(code);
        t = (TextView)findViewById(R.id.course_avg_rating);
        t.setText(rating.toString());
        t = (TextView)findViewById(R.id.course_name);
        t.setText(name);
    }

}
