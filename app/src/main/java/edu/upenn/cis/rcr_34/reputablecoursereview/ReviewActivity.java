package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;


public class ReviewActivity extends ActionBarActivity {


    private int rating = 1;
    private boolean required = false;
    private boolean recommended = false;
    private boolean commitment = false;
    private boolean useful = false;
    private boolean hasRated = false;
    private String userID;
    private String courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        FacebookSdk.sdkInitialize(getApplicationContext());

        // get the information of the current user and the course being reviewed
        // these IDs will be stored in the review and allow it to be found from the user and course
        // through the parse backend
        userID = ParseUser.getCurrentUser().getObjectId();
        courseID = getIntent().getStringExtra("parseID");

        // check if the user has already reviewed this course
        ArrayList<String> allReviews = (ArrayList<String>)
                ParseUser.getCurrentUser().get("reviewedCourses");

        if (allReviews == null) {
            allReviews = new ArrayList<String>();
        }
        if (allReviews.contains(courseID)) {
            Toast.makeText(getApplicationContext(),
                    "You have already reviewed this course.", Toast.LENGTH_LONG).show();

            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }

        // drop down menu
        Spinner spinner;

        // options for ratings
        String[] spinnerOptions = {"1 (worst)", "2 (below average)", "3 (average)", "4 (above average)", "5 (best)"};

        // translate options into something that can populate spinner
        ArrayAdapter<String> spinAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, spinnerOptions);

        // initialize the spinner
        spinner = (Spinner) findViewById(R.id.review_rating);
        spinner.setAdapter(spinAdapter);
        SpinListener sl = new SpinListener();
        spinner.setOnItemSelectedListener(sl);



        // make sure that the scroll view starts at the top of the screen and not midway
        // (why is this not done by default?)
        ScrollView v = (ScrollView) findViewById(R.id.review_scroll);

        v.requestFocus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // record responses to the checkbox interaction on the review
    public void onCheckboxClicked(View v) {
        boolean checked = ((CheckBox) v).isChecked();
        // check which checkbox was clicked
        switch(v.getId()) {
            case R.id.checkbox_commitment:
                commitment = checked;
                break;
            case R.id.checkbox_required:
                required = checked;
                break;
            case R.id.checkbox_recommended:
                recommended = checked;
                break;
            case R.id.checkbox_useful:
                useful = checked;
                break;
        }
    }

    // make a new object in parse using the information, if sufficient changes were made
    public void submitReview(View v) {
        String professor = ((EditText)findViewById(R.id.review_professor)).getText().toString();

        if (professor.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Please record which professor taught this course.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasRated) {
            Toast.makeText(getApplicationContext(),
                    "Please assign a rating to this course.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // there is sufficient information to create the review
            ParseObject review = new ParseObject("Review");

            review.put("required", required);
            review.put("recommend", recommended);
            review.put("useful", useful);
            review.put("commitment", commitment);
            review.put("userID", userID);
            review.put("courseID", courseID);
            review.put("textReview", ((EditText)findViewById(R.id.review_text)).
                    getText().toString());
            review.put("professor", professor);
            review.put("rating", rating);
            review.put("reviewRating", 0);// default rating of review, to be changed by voting
            ArrayList<String> upvAndDownv = new ArrayList<String>();
            review.put("upvoted", upvAndDownv);
            review.put("downvoted", upvAndDownv);
            review.saveInBackground();

            // add this to the user's list of courses reviewed
            // this will be used to prohibit the user from submitting a duplicate review and
            // ensure that the user can only see reviews after submitting a certain amount

            ArrayList<String> allReviews = (ArrayList<String>)
                    ParseUser.getCurrentUser().get("reviewedCourses");
            if (allReviews == null) {
                allReviews = new ArrayList<String>();
            }
            allReviews.add(courseID);
            ParseUser.getCurrentUser().put("reviewedCourses", allReviews);
            ParseUser.getCurrentUser().saveInBackground();

            Toast.makeText(getApplicationContext(),
                    "Review Submitted.", Toast.LENGTH_LONG).show();

            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        }
    }

    // end this activity and return
    public void cancelReview(View v) {
        Toast.makeText(getApplicationContext(),
                "Cancelling review...", Toast.LENGTH_SHORT).show();

        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    // creates a listener that allows the spinner to change data
    public class SpinListener extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            rating = Integer.parseInt(((String) parent.getItemAtPosition(pos)).split(" ")[0]);
            hasRated = true;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
