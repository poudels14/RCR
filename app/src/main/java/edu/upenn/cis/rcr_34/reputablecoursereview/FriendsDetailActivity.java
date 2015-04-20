package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.parse.ParseUser;

import java.util.ArrayList;


public class FriendsDetailActivity extends ActionBarActivity {
    private String email; // this email belongs to the friend
    private User userObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);

        Bundle extra = getIntent().getExtras();
        email = extra.getString("email");

        final LinearLayout profileDetail = (LinearLayout) findViewById(R.id.friends_detail_main_view);
        final User u = new User(email);
        userObject = u;
        u.addListener(new ParseDataReceivedNotifier() {
            @Override
            public void notifyListener() {
                populateFriend(profileDetail, u);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_detail, menu);
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

    private void populateFriend(LinearLayout llIn, final User u){
        RelativeLayout personalDetail = new RelativeLayout(this);
        RelativeLayout.LayoutParams lpforPersonalDetail = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        personalDetail.setLayoutParams(lpforPersonalDetail);

        personalDetail.setId(Utils.getUniqueID());
        personalDetail.setPadding(5, 20, 0, 20);

        //Set profile pic
        RelativeLayout.LayoutParams lpForImage = new RelativeLayout.LayoutParams(320, 400);
        ImageView profilePic = new ImageView(this);
        profilePic.setVisibility(View.VISIBLE);
        profilePic.setBackgroundColor(Color.BLACK);
        profilePic.setId(Utils.getUniqueID());
        profilePic.setLayoutParams(lpForImage);
        LoadImage lm = new LoadImage(this, profilePic);
        lm.execute(u.getProfilePic()); //icon = address of image to be loaded
        personalDetail.addView(profilePic);

        //Set name
        RelativeLayout.LayoutParams lpForName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForName.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
        TextView name = new TextView(this);
        name.setText(u.getName());
        name.setPadding(20, 0, 0, 0);
        name.setId(Utils.getUniqueID());
        name.setLayoutParams(lpForName);
        personalDetail.addView(name);

        //Set year
        RelativeLayout.LayoutParams lpForYear = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForYear.addRule(RelativeLayout.BELOW, name.getId());
        lpForYear.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

        TextView year = new TextView(this);
        year.setText("Year: " + u.getYear());
        year.setPadding(20, 0, 0, 0);
        year.setLayoutParams(lpForYear);
        year.setId(Utils.getUniqueID());
        personalDetail.addView(year);

        //Set major
        RelativeLayout.LayoutParams lpForMajor = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForMajor.addRule(RelativeLayout.BELOW, year.getId());
        lpForMajor.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

        TextView major = new TextView(this);
        major.setText("Major: " + u.getMajor());
        major.setPadding(20, 0, 0, 0);
        major.setLayoutParams(lpForMajor);
        major.setId(Utils.getUniqueID());
        personalDetail.addView(major);


        //Set major
        RelativeLayout.LayoutParams lpForEmail = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForEmail.addRule(RelativeLayout.BELOW, major.getId());
        lpForEmail.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

        TextView email = new TextView(this);
        email.setText("Email: " + u.getEmail());
        email.setPadding(20, 0, 0, 0);
        email.setLayoutParams(lpForEmail);
        email.setId(Utils.getUniqueID());
        personalDetail.addView(email);

        llIn.addView(personalDetail);

        //set decline button
        RelativeLayout.LayoutParams lpForRemove = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForRemove.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
        lpForRemove.addRule(RelativeLayout.BELOW, email.getId());
        final Button unfriend = new Button(this);
        unfriend.setText("Unfriend");
        unfriend.setTextSize(10);
        unfriend.setId(Utils.getUniqueID());
        unfriend.setLayoutParams(lpForRemove);
        unfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User me = new User(ParseUser.getCurrentUser().getEmail());
                me.addListener(new ParseDataReceivedNotifier() {
                    @Override
                    public void notifyListener() {
                        if (unfriend.getText() == "Unfriend"){
                            unfriend.setText("Add Friend");
                            me.removeFriend(u.getEmail());
                        }
                        else{
                            unfriend.setText("Unfriend");
                            me.sendRequest(u.getEmail());
                        }
                    }
                });
            }
        });
        personalDetail.addView(unfriend);

        // End of personal details

        //Add class details
        LinearLayout classesTakenDetails = new LinearLayout(this);
        classesTakenDetails.setOrientation(LinearLayout.VERTICAL);
        classesTakenDetails.setId(Utils.getUniqueID());
        classesTakenDetails.setPadding(5, 20, 0, 20);

        // Add title for courses taken
//        LinearLayout allCourseTitle = new LinearLayout(this);
//        allCourseTitle.setLayoutParams(new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
//        allCourseTitle.setOrientation(LinearLayout.HORIZONTAL);
//        allCourseTitle.setId(Utils.getUniqueID());
//        allCourseTitle.setBackgroundColor(Color.LTGRAY);

        TextView classesTakenLabel = new TextView(this);
        RelativeLayout.LayoutParams lpForclassesTakenLabel = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        classesTakenLabel.setText("--------- Classes Taken ---------");
        classesTakenLabel.setPadding(5, 10, 0, 0);
        classesTakenLabel.setLayoutParams(lpForclassesTakenLabel);
        classesTakenLabel.setId(Utils.getUniqueID());
        classesTakenDetails.addView(classesTakenLabel);

        //Linear layout for all holding all classes taken
        LinearLayout allClassesTaken = new LinearLayout(this);
        allClassesTaken.setOrientation(LinearLayout.VERTICAL);
        allClassesTaken.setId(Utils.getUniqueID());

        ArrayList<CoursesTaken> co = userObject.getCoursesTaken();
        if (co != null){
            for (CoursesTaken s:co){
                Log.d("COURSES TAKEN", s.getCourseCode());
            }
        }


        for (int i = 0; i < 19; i++){
            //Set course
            LinearLayout course = new LinearLayout(this);
            course.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, 200));
            course.setOrientation(LinearLayout.HORIZONTAL);
            course.setId(Utils.getUniqueID());
            if (i % 2 == 1){
                course.setBackgroundColor(Color.LTGRAY);
            }
            final FriendsDetailActivity self = this;
            course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(self, CourseActivity.class);
                    i.putExtra("property", "code");
                    i.putExtra("name", "ABC 123");
                    startActivity(i);
                }
            });

            //Set course code
            TextView className = new TextView(this);
            RelativeLayout.LayoutParams lpForClassName = new RelativeLayout.LayoutParams(
                    400, RelativeLayout.LayoutParams.WRAP_CONTENT);
            className.setText("abc 123");
            className.setPadding(20, 0, 0, 0);
            className.setId(Utils.getUniqueID());
            className.setLayoutParams(lpForClassName);
            course.addView(className);

            //Set course rating
            TextView classRating = new TextView(this);
            RelativeLayout.LayoutParams lpForClassRating = new RelativeLayout.LayoutParams(
                    200, RelativeLayout.LayoutParams.WRAP_CONTENT);
            classRating.setText("3.5");
            classRating.setPadding(20, 0, 0, 0);
            classRating.setId(Utils.getUniqueID());
            classRating.setLayoutParams(lpForClassRating);
            course.addView(classRating);


            //Set semester taken
            TextView semester = new TextView(this);
            RelativeLayout.LayoutParams lpForSemester = new RelativeLayout.LayoutParams(
                    800, RelativeLayout.LayoutParams.WRAP_CONTENT);
            semester.setText("Spring 2012");
            semester.setPadding(20, 0, 0, 0);
            semester.setId(Utils.getUniqueID());
            semester.setLayoutParams(lpForSemester);
            course.addView(semester);

            allClassesTaken.addView(course);
        }

        classesTakenDetails.addView(allClassesTaken);

        llIn.addView(classesTakenDetails);
    }
}