package edu.upenn.cis.rcr_34.reputablecoursereview;

import edu.upenn.cis.rcr_34.reputablecoursereview.util.StaticUtils;
import edu.upenn.cis.rcr_34.reputablecoursereview.util.SystemUiHider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends ActionBarActivity {

    private String valueOfProperty;
    private String property; // this is the property of parse object
    private String parseCourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize parse
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        //initialize facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        //to find course
        property = (String) getIntent().getStringExtra("property");
        valueOfProperty = (String) getIntent().getStringExtra("value");

        // find the course using parsecourseid
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Course");
        query.whereEqualTo(property, valueOfProperty);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects.size() > 0) {
                    ParseObject parseObject = parseObjects.get(0);
                    parseCourseID = parseObject.getObjectId();
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


        /*query.whereEqualTo(property, valueOfProperty);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseObject parseObject = (ParseObject) list.get(0);
                        // pass the object's info into the interface
                        if (parseObject == null) {
                            Toast.makeText(getApplicationContext(),
                                    "Error Retrieving Course", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("COURSE_ACTIVITY", parseObject.getString("Name"));
                            parseCourseID = parseObject.getObjectId();
                            populateUI(parseObject.getDouble("Rating"),
                                    parseObject.getString("Name"),
                                    parseObject.getString("Code"));
                        }

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
        });*/
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
        // manage account
        if (id == R.id.manage_account_item) {
            manageAccountClicked();
        }
        // sign out
        if (id == R.id.sign_out) {
            StaticUtils.signOutClicked(getApplicationContext());
        }
        return super.onOptionsItemSelected(item);
    }

    public void manageAccountClicked() {
        // manage account clicked
        Toast.makeText(getApplicationContext(), "Manage account selected", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(this, ManageAccountActivity.class);
        startActivity(intent2);
    }

    public void onReviewClicked(View v) {
        // review clicked
        Intent i = new Intent(this, ReviewActivity.class);
        i.putExtra("parseID", parseCourseID);
        startActivity(i);
    }

    protected void populateUI(Double rating, String name, String code) {
        // set the header of this view
        TextView t = (TextView) findViewById(R.id.course_code);
        t.setText(code);
        t = (TextView) findViewById(R.id.course_avg_rating);
        t.setText(rating.toString());
        t = (TextView) findViewById(R.id.course_name);
        t.setText(name);
        populateReview();
    }

    public void populateReview() {
        // fill the reviews after finding them in parse
        String parseCourseID = getIntent().getStringExtra("value");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("courseID", parseCourseID);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    final LinearLayout reviewList = (LinearLayout) findViewById(R.id.course_reviewLL);
                    for (int i = 0; i < objects.size(); i++) {
                        boolean colorHelp = ((i % 2) == 1);
                        addReview(reviewList, objects.get(i), colorHelp);
                    }
                }
            }
        });
    }

    // adds each review. uses relative layout and adds textview one at a time
    public void addReview(LinearLayout ll, final ParseObject review, boolean color) {
        int rate = (Integer) review.get("reviewRating");
        if (rate < -4) {
            try {
                review.delete();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        } else {

            Log.d("test1", "Made a review");
            // this is the uppermost linear layout
            LinearLayout parentLayout = new LinearLayout(this);

            parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            parentLayout.setOrientation(LinearLayout.HORIZONTAL);
            //parentLayout.setId(Utils.getUniqueID());

            if (color) {
                parentLayout.setBackgroundColor(Color.parseColor("#CCCCFF"));
            } else {
                parentLayout.setBackgroundColor(Color.parseColor("#9999FF"));
            }

            // create linear layout for the review rating -----------------------------------------

            LinearLayout reviewScoreLayout = new LinearLayout(this);
            reviewScoreLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            reviewScoreLayout.setOrientation(LinearLayout.VERTICAL);
            reviewScoreLayout.setGravity(Gravity.CENTER);
            parentLayout.addView(reviewScoreLayout);


            TextView rstext = new TextView(this);
            rstext.setText("Review \nScore:");
            rstext.setPadding(20, 0, 0, 0);
            rstext.setId(Utils.getUniqueID());
            rstext.setTextSize(10);
            reviewScoreLayout.addView(rstext);

            TextView rsval = new TextView(this);
            rsval.setText(review.get("reviewRating").toString());
            rsval.setGravity(Gravity.CENTER);
            rsval.setId(Utils.getUniqueID());
            rsval.setTextSize(18);
            reviewScoreLayout.addView(rsval);

            // create layout for radio buttons ----------------------------------------------------


            //////////////////////// Buttons

            LinearLayout buttonsLayout = new LinearLayout(this);
            buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            buttonsLayout.setOrientation(LinearLayout.VERTICAL);
            buttonsLayout.setGravity(Gravity.CENTER);
            parentLayout.addView(buttonsLayout);

            ArrayList<String> upv = (ArrayList<String>) review.get("upvoted");
            ArrayList<String> downv = (ArrayList<String>) review.get("downvoted");
            boolean hasUp = false;
            boolean hasDown = false;
            if (upv.contains(ParseUser.getCurrentUser().getObjectId())) {
                hasUp = true;
            }

            if (downv.contains(ParseUser.getCurrentUser().getObjectId())) {
                hasDown = true;
            }

            final RadioButton down = new RadioButton(this);
            final RadioButton up = new RadioButton(this);
            up.setText("+");
            down.setText("-");
            buttonsLayout.addView(up);
            buttonsLayout.addView(down);

            if (hasUp) {
                up.setChecked(true);
                down.setChecked(false);
            }

            if (hasDown) {
                down.setChecked(true);
                up.setChecked(false);
            }

            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<String> upv = (ArrayList<String>) review.get("upvoted");
                    ArrayList<String> downv = (ArrayList<String>) review.get("downvoted");

                    if (down.isChecked()) {
                        up.setChecked(true);
                        down.setChecked(false);
                        downv.remove(ParseUser.getCurrentUser().getObjectId());
                        upv.add(ParseUser.getCurrentUser().getObjectId());
                        review.put("upvoted", upv);
                        review.put("downvoted", downv);

                        int rate;
                        rate = (Integer) review.get("reviewRating") + 2;
                        review.put("reviewRating", rate);
                        review.saveInBackground();
                        final LinearLayout ll = (LinearLayout) findViewById(R.id.course_reviewLL);
                        ll.removeAllViews();
                        populateReview();
                    } else {
                        if (!up.isChecked()) {
                            up.setChecked(true);
                            down.setChecked(false);
                            int rate;
                            upv.add(ParseUser.getCurrentUser().getObjectId());
                            review.put("upvoted", upv);
                            rate = (Integer) review.get("reviewRating") + 1;
                            review.put("reviewRating", rate);
                            review.saveInBackground();
                            final LinearLayout ll = (LinearLayout) findViewById(R.id.course_reviewLL);
                            ll.removeAllViews();
                            populateReview();
                        }
                    }
                }
            });

            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> upv = (ArrayList<String>) review.get("upvoted");
                    ArrayList<String> downv = (ArrayList<String>) review.get("downvoted");

                    if (up.isChecked()) {
                        down.setChecked(true);
                        up.setChecked(false);
                        int rate;

                        upv.remove(ParseUser.getCurrentUser().getObjectId());
                        downv.add(ParseUser.getCurrentUser().getObjectId());
                        review.put("upvoted", upv);
                        review.put("downvoted", downv);

                        rate = (Integer) review.get("reviewRating") - 2;
                        review.put("reviewRating", rate);
                        review.saveInBackground();
                        final LinearLayout ll = (LinearLayout) findViewById(R.id.course_reviewLL);
                        ll.removeAllViews();
                        populateReview();
                    } else {
                        if (!down.isChecked()) {
                            down.setChecked(true);
                            up.setChecked(false);
                            int rate;
                            downv.add(ParseUser.getCurrentUser().getObjectId());
                            review.put("downvoted", downv);
                            rate = (Integer) review.get("reviewRating") - 1;
                            review.put("reviewRating", rate);
                            review.saveInBackground();
                            final LinearLayout ll = (LinearLayout) findViewById(R.id.course_reviewLL);
                            ll.removeAllViews();
                            populateReview();
                        }
                    }
                }
            });

            // create linear layout for the review content text -----------------------------------
            LinearLayout reviewTextLayout = new LinearLayout(this);
            reviewTextLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            reviewTextLayout.setOrientation(LinearLayout.VERTICAL);
            parentLayout.addView(reviewTextLayout);

            TextView question1 = new TextView(this);
            question1.setPadding(20, 0, 0, 0);
            question1.setText("Required for major?");
            question1.setId(Utils.getUniqueID());
            question1.setSingleLine(false);
            question1.setTextSize(10);
            reviewTextLayout.addView(question1);

            if ((boolean) review.get("required") == true) {
                question1.setText(question1.getText() + " YES");
            } else {
                question1.setText(question1.getText() + " NO");
            }

            TextView question2 = new TextView(this);
            question2.setText("Would you recommend to non-major?");
            question2.setPadding(20, 0, 0, 0);
            question2.setId(Utils.getUniqueID());
            question2.setSingleLine(false);
            question2.setTextSize(10);
            reviewTextLayout.addView(question2);

            TextView answer2 = new TextView(this);
            if ((boolean) review.get("recommend") == true) {
                question2.setText(question2.getText() + " YES");
            } else {
                question2.setText(question2.getText() + " NO");
            }

            TextView question3 = new TextView(this);
            question3.setText("Is this useful to your major?");
            question3.setPadding(20, 0, 0, 0);
            question3.setId(Utils.getUniqueID());
            question3.setTextSize(10);
            reviewTextLayout.addView(question3);

            if ((boolean) review.get("recommend") == true) {
                question3.setText(question3.getText() + " YES");
            } else {
                question3.setText(question3.getText() + " NO");
            }


            TextView question4 = new TextView(this);
            question4.setText("Is this course a serious time commitment?");
            question4.setPadding(20, 0, 0, 0);
            question4.setId(Utils.getUniqueID());
            question4.setTextSize(10);
            reviewTextLayout.addView(question4);

            if ((boolean) review.get("commitment") == true) {
                question4.setText(question4.getText() + " YES");
            } else {
                question4.setText(question4.getText() + " NO");
            }


            TextView question5 = new TextView(this);
            question5.setText("Professor Name: " + (String) review.get("professor"));
            question5.setPadding(20, 0, 0, 0);
            question5.setId(Utils.getUniqueID());
            question5.setTextSize(10);
            reviewTextLayout.addView(question5);

            TextView question8 = new TextView(this);
            question8.setText("Full Review:");
            question8.setSingleLine(false);
            question8.setPadding(20, 0, 0, 0);
            question8.setId(Utils.getUniqueID());
            question8.setTextSize(10);
            reviewTextLayout.addView(question8);

            if ((String) review.get("textReview") != null &&
                    !((String) review.get("textReview")).equals("")) {
                question8.setText(question8.getText() +  review.get("textReview").toString());
            } else {
                question8.setText(question8.getText() + "No review given");
            }

            // create linear layout for the review's course rating --------------------------------
            LinearLayout courseScoreLayout = new LinearLayout(this);
            courseScoreLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            courseScoreLayout.setOrientation(LinearLayout.VERTICAL);
            courseScoreLayout.setGravity(Gravity.CENTER_VERTICAL);
            parentLayout.addView(courseScoreLayout);

            TextView scoreText = new TextView(this);
            scoreText.setText("Course\nRating:");
            scoreText.setPadding(20, 0, 0, 0);
            scoreText.setId(Utils.getUniqueID());
            scoreText.setTextSize(10);
            courseScoreLayout.addView(scoreText);


            TextView score = new TextView(this);
            score.setText(review.get("rating").toString());
            score.setGravity(Gravity.CENTER);
            score.setId(Utils.getUniqueID());
            score.setTextSize(18);
            courseScoreLayout.addView(score);


            // add the whole window to the scrolling linear layout
            ll.addView(parentLayout);
        }
    }
}
