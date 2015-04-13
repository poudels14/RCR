package edu.upenn.cis.rcr_34.reputablecoursereview;

import edu.upenn.cis.rcr_34.reputablecoursereview.util.StaticUtils;
import edu.upenn.cis.rcr_34.reputablecoursereview.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.List;


public class CourseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        FacebookSdk.sdkInitialize(getApplicationContext());



        String parseCourseID = getIntent().getStringExtra("parseID");

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

    protected void populateUI(Double rating, String name, String code) {
        // set the header of this view
        TextView t = (TextView)findViewById(R.id.course_code);
        t.setText(code);
        t = (TextView)findViewById(R.id.course_avg_rating);
        t.setText(rating.toString());
        t = (TextView)findViewById(R.id.course_name);
        t.setText(name);
        populateReview(code);
    }

    public void populateReview(String code) {

        Toast.makeText(getApplicationContext(),
                "step 1", Toast.LENGTH_SHORT).show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("courseID", code);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    final LinearLayout reviewList = (LinearLayout) findViewById(R.id.course_reviewLL);
                    for (int i = 0; i < objects.size(); i++) {
                        boolean colorHelp = true;
                        if ((i % 2) == 0) {
                            colorHelp = false;
                        }
                        addReview(reviewList, objects.get(i), colorHelp);
                    }
                } else {
                }
            }
        });
    }

    public void addReview (LinearLayout ll, ParseObject review, boolean color) {

        RelativeLayout myRL = new RelativeLayout(this);

        if (color) {
            myRL.setBackgroundColor(Color.parseColor("#CCCCFF"));
        } else {
            myRL.setBackgroundColor(Color.parseColor("#9999FF"));
        }
        myRL.setPadding(20, 0, 0, 20);
        // First question
        TextView name = new TextView(this);
        name.setPadding(20, 0, 0, 0);
        name.setText("Required for major");
        name.setId(Utils.getUniqueID());

        myRL.addView(name);

        RelativeLayout.LayoutParams firstAnswer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        firstAnswer.addRule(RelativeLayout.RIGHT_OF, name.getId());

        // first answer
        TextView answer1 = new TextView(this);
        if ((boolean) review.get("required") == true) {
            answer1.setText("Yes");
        } else {
            answer1.setText("No");
        }

        answer1.setPadding(20, 0, 0, 0);
        answer1.setLayoutParams(firstAnswer);
        answer1.setId(Utils.getUniqueID());
        myRL.addView(answer1);


        // second question
        RelativeLayout.LayoutParams secondQuestion = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        secondQuestion.addRule(RelativeLayout.BELOW, answer1.getId());

        TextView question2 = new TextView(this);
        question2.setText("Would you recommend to non-major?");

        question2.setPadding(20, 0, 0, 0);
        question2.setLayoutParams(secondQuestion);
        question2.setId(Utils.getUniqueID());
        myRL.addView(question2);

        // second answer
        RelativeLayout.LayoutParams secondAnswer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        secondAnswer.addRule(RelativeLayout.BELOW, answer1.getId());
        secondAnswer.addRule(RelativeLayout.RIGHT_OF, question2.getId());

        TextView answer2 = new TextView(this);
        if ((boolean) review.get("recommend") == true) {
            answer2.setText("Yes");
        } else {
            answer2.setText("No");
        }

        answer2.setPadding(20, 0, 0, 0);
        answer2.setLayoutParams(secondAnswer);
        answer2.setId(Utils.getUniqueID());
        myRL.addView(answer2);




        // third question
        RelativeLayout.LayoutParams thirdQuestion = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        thirdQuestion.addRule(RelativeLayout.BELOW, question2.getId());

        TextView question3 = new TextView(this);
        question3.setText("Is this useful to your major?");

        question3.setPadding(20, 0, 0, 0);
        question3.setLayoutParams(thirdQuestion);
        question3.setId(Utils.getUniqueID());
        myRL.addView(question3);

        // third answer
        RelativeLayout.LayoutParams thirdAnswer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        thirdAnswer.addRule(RelativeLayout.BELOW, question2.getId());
        thirdAnswer.addRule(RelativeLayout.RIGHT_OF, question3.getId());
        TextView answer3 = new TextView(this);
        if ((boolean) review.get("recommend") == true) {
            answer3.setText("Yes");
        } else {
            answer3.setText("No");
        }

        answer3.setPadding(20, 0, 0, 0);
        answer3.setLayoutParams(thirdAnswer);
        answer3.setId(Utils.getUniqueID());
        myRL.addView(answer3);




        // fourth question
        RelativeLayout.LayoutParams fourthQuestion = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        fourthQuestion.addRule(RelativeLayout.BELOW, question3.getId());

        TextView question4 = new TextView(this);
        question4.setText("Is this course a serious time commitment");

        question4.setPadding(20, 0, 0, 0);
        question4.setLayoutParams(fourthQuestion);
        question4.setId(Utils.getUniqueID());
        myRL.addView(question4);

        // fourth answer
        RelativeLayout.LayoutParams fourthAnswer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        fourthAnswer.addRule(RelativeLayout.BELOW, question3.getId());
        fourthAnswer.addRule(RelativeLayout.RIGHT_OF, question4.getId());
        TextView answer4 = new TextView(this);
        if ((boolean) review.get("commitment") == true) {
            answer4.setText("Yes");
        } else {
            answer4.setText("No");
        }

        answer4.setPadding(20, 0, 0, 0);
        answer4.setLayoutParams(fourthAnswer);
        answer4.setId(Utils.getUniqueID());
        myRL.addView(answer4);

        // fifth question
        RelativeLayout.LayoutParams fifthQuestion = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        fifthQuestion.addRule(RelativeLayout.BELOW, question4.getId());

        TextView question5 = new TextView(this);
        question5.setText("Professor Name:");

        question5.setPadding(20, 0, 0, 0);
        question5.setLayoutParams(fifthQuestion);
        question5.setId(Utils.getUniqueID());
        myRL.addView(question5);

        // fifth answer
        RelativeLayout.LayoutParams fifthAnswer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        fifthAnswer.addRule(RelativeLayout.BELOW, question4.getId());
        fifthAnswer.addRule(RelativeLayout.RIGHT_OF, question5.getId());
        TextView answer5 = new TextView(this);
        answer5.setText((String) review.get("professor"));
        answer5.setPadding(20, 0, 0, 0);
        answer5.setLayoutParams(fifthAnswer);
        answer5.setId(Utils.getUniqueID());
        myRL.addView(answer5);



        // sixth question
        RelativeLayout.LayoutParams sixthQuestion = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sixthQuestion.addRule(RelativeLayout.BELOW, question5.getId());

        TextView question6 = new TextView(this);
        question6.setText("Rating for Course:");

        question6.setPadding(20, 0, 0, 0);
        question6.setLayoutParams(sixthQuestion);
        question6.setId(Utils.getUniqueID());
        myRL.addView(question6);

        // sixth answer
        RelativeLayout.LayoutParams sixthAnswer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sixthAnswer.addRule(RelativeLayout.BELOW, question5.getId());
        sixthAnswer.addRule(RelativeLayout.RIGHT_OF, question6.getId());
        TextView answer6 = new TextView(this);
        answer6.setText(review.get("rating").toString());
        answer6.setPadding(20, 0, 0, 0);
        answer6.setLayoutParams(sixthAnswer);
        answer6.setId(Utils.getUniqueID());
        myRL.addView(answer6);



        // seventh question
        RelativeLayout.LayoutParams seventhQuestion = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        seventhQuestion.addRule(RelativeLayout.BELOW, question6.getId());

        TextView question7 = new TextView(this);
        question7.setText("Rating for Review:");

        question7.setPadding(20, 0, 0, 0);
        question7.setLayoutParams(seventhQuestion);
        question7.setId(Utils.getUniqueID());
        myRL.addView(question7);

        // seventh answer
        RelativeLayout.LayoutParams seventhAnswer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        seventhAnswer.addRule(RelativeLayout.BELOW, question6.getId());
        seventhAnswer.addRule(RelativeLayout.RIGHT_OF, question7.getId());
        TextView answer7 = new TextView(this);
        answer7.setText(review.get("reviewRating").toString());
        answer7.setPadding(20, 0, 0, 0);
        answer7.setLayoutParams(seventhAnswer);
        answer7.setId(Utils.getUniqueID());
        myRL.addView(answer7);



        if ((String) review.get("textReview") != null && !((String) review.get("textReview")).equals("")) {
            // eighth question
            RelativeLayout.LayoutParams eighthQuestion = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            eighthQuestion.addRule(RelativeLayout.BELOW, question7.getId());

            TextView question8 = new TextView(this);
            question8.setText("Full Review:");

            question8.setPadding(20, 0, 0, 0);
            question8.setLayoutParams(eighthQuestion);
            question8.setId(Utils.getUniqueID());
            myRL.addView(question8);

            // eighth answer
            RelativeLayout.LayoutParams eighthAnswer = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            eighthAnswer.addRule(RelativeLayout.BELOW, question8.getId());
            TextView answer8 = new TextView(this);
            answer8.setText(review.get("textReview").toString());
            answer8.setPadding(20, 0, 0, 0);
            answer8.setLayoutParams(eighthAnswer);
            answer8.setId(Utils.getUniqueID());
            myRL.addView(answer8);
        }

        ll.addView(myRL);
    }
}
