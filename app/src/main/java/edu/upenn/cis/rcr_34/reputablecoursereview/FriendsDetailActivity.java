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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;


public class FriendsDetailActivity extends ActionBarActivity {
    private String email; // this email belongs to the friend
    private boolean isFriend;
    private boolean isCurrentUser;
    private LinearLayout allClassesTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);

        Bundle extra = getIntent().getExtras();
        email = extra.getString("email");
        if (email.equals(ParseUser.getCurrentUser().getEmail())){
            isCurrentUser = true;
        }
        isFriend = false;
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<String> myFriends = (ArrayList<String>)currentUser.get("friends");

        // used this if statement because it returned null exception;
        if (myFriends != null){
            for(String s : myFriends){
                if(email.equals(s)){
                    isFriend = true;
                }
            }
        }


        final LinearLayout profileDetail = (LinearLayout) findViewById(R.id.friends_detail_main_view);
        final User u = new User(email);

        u.addListener(new ParseDataReceivedNotifier() {
            @Override
            public void notifyListener() {
                populateFriend(profileDetail, u);
                u.retrieveCoursesTaken(new ParseDataReceivedNotifier() {
                    @Override
                    public void notifyListener() {
                        populateCoursesTaken(profileDetail, u.getCoursesTaken());
                    }
                });
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
        profilePic.setImageBitmap(u.getProfileImage());
        personalDetail.addView(profilePic);

        //Set name
        RelativeLayout.LayoutParams lpForName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForName.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

        TextView name = newTextView(u.getName(), lpForName);
        name.setLayoutParams(lpForName);
        personalDetail.addView(name);

        //Set year
        RelativeLayout.LayoutParams lpForYear = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForYear.addRule(RelativeLayout.BELOW, name.getId());
        lpForYear.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

        TextView year = newTextView("Year: " + u.getYear(), lpForYear);
        personalDetail.addView(year);

        //Set major
        RelativeLayout.LayoutParams lpForMajor = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForMajor.addRule(RelativeLayout.BELOW, year.getId());
        lpForMajor.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

        TextView major = newTextView("Major: " + u.getMajor(), lpForMajor);
        personalDetail.addView(major);


        //Set email
        RelativeLayout.LayoutParams lpForEmail = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForEmail.addRule(RelativeLayout.BELOW, major.getId());
        lpForEmail.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

        TextView email;
        if(isFriend || isCurrentUser) {
            email = newTextView("Email: " + u.getEmail(), lpForEmail);
        }
        else{
            email = newTextView("Email: Only friends can see this", lpForEmail);
        }
        personalDetail.addView(email);

        llIn.addView(personalDetail);

        if(isFriend) {
            //set unfriend button
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
                            if (unfriend.getText() == "Unfriend") {
                                unfriend.setText("Add Friend");
                                me.removeFriend(u.getEmail());
                            } else {
                                unfriend.setText("Unfriend");
                                me.sendRequest(u.getEmail());
                            }
                        }
                    });
                }
            });
            personalDetail.addView(unfriend);
        }
        else if (!isCurrentUser) {
            //set friend request button
            RelativeLayout.LayoutParams lpForAdd = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpForAdd.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
            lpForAdd.addRule(RelativeLayout.BELOW, email.getId());
            final Button addFriend = new Button(this);
            addFriend.setText("Add friend");
            addFriend.setTextSize(10);
            addFriend.setId(Utils.getUniqueID());
            addFriend.setLayoutParams(lpForAdd);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final User me = new User(ParseUser.getCurrentUser().getEmail());
                    me.addListener(new ParseDataReceivedNotifier() {
                        @Override
                        public void notifyListener() {
                            if (addFriend.getText() == "Add friend") {
                                addFriend.setText("Request Sent");
                                me.sendRequest(u.getEmail());
                            }
                        }
                    });
                }
            });
            personalDetail.addView(addFriend);
        }
        // End of personal details
    }

    //Add class details
    private void populateCoursesTaken(LinearLayout llIn, ArrayList<CoursesTaken> coursesTaken){
        if(isFriend || isCurrentUser) {
            LinearLayout classesTakenDetails = new LinearLayout(this);
            classesTakenDetails.setOrientation(LinearLayout.VERTICAL);
            classesTakenDetails.setId(Utils.getUniqueID());
            classesTakenDetails.setPadding(5, 20, 0, 20);

            TextView classesTakenLabel = new TextView(this);
            RelativeLayout.LayoutParams lpForclassesTakenLabel = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            classesTakenLabel.setText("--------- Classes Taken ---------");
            classesTakenLabel.setPadding(5, 10, 0, 0);
            classesTakenLabel.setLayoutParams(lpForclassesTakenLabel);
            classesTakenLabel.setId(Utils.getUniqueID());
            classesTakenDetails.addView(classesTakenLabel);

            //Linear layout for all holding all classes taken
            allClassesTaken = new LinearLayout(this);
            allClassesTaken.setOrientation(LinearLayout.VERTICAL);
            allClassesTaken.setId(Utils.getUniqueID());

//        ArrayList<CoursesTaken> co = userObject.getCoursesTaken();
            Log.d("COURSE TAKEN", Integer.toString(coursesTaken.size()));
            if (coursesTaken != null) {
                for (CoursesTaken s : coursesTaken) {
                    Log.d("COURSES TAKEN", s.getCourseCode());
                }
            }

            ParseQuery<ParseObject> cT = ParseQuery.getQuery("coursesTaken");
            cT.whereContains("userEmail", email);
            cT.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> courses, ParseException e) {
                    if (e == null) {
                        if (courses.size() > 0) {
                            int index = 0;
                            for (ParseObject ob : courses) {
                                addCourseTaken(ob, index);
                                index++;
                            }
                        }
                    }
                }
            });
            classesTakenDetails.addView(allClassesTaken);

            llIn.addView(classesTakenDetails);
        }
    }

    private void addCourseTaken(ParseObject courseTaken, int i){
        final String courseName = (String) courseTaken.get("courseName");
        String semseterTaken = (String) courseTaken.get("semester");
        String yearTaken = (String) courseTaken.get("year");
        int r = (int) courseTaken.get("rating");
        String rating = "N/A";
        if(r != -1){
            rating = "" + r;
        }
        // rating is -1 if the user hasn't rated it yet

        //Set course
        LinearLayout course = new LinearLayout(this);
        course.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 200));
        course.setOrientation(LinearLayout.HORIZONTAL);
        course.setId(Utils.getUniqueID());
        if (i % 2 == 1) {
            course.setBackgroundColor(Color.LTGRAY);
        }
        final FriendsDetailActivity self = this;
        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> courseSearch = ParseQuery.getQuery("Course");
                courseSearch.whereContains("Code", courseName);
                courseSearch.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> courses, ParseException e) {
                        if (e == null) {
                            if (courses.size() > 0) {
                                String id = courses.get(0).getObjectId();
                                Intent i = new Intent(self, CourseActivity.class);
                                i.putExtra("property", "objectId");
                                i.putExtra("value", id);
                                startActivity(i);
                            }
                        }
                    }
                });
            }
        });

        //Set course code
        RelativeLayout.LayoutParams lpForClassName = new RelativeLayout.LayoutParams(
                400, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView className = newTextView(courseName, lpForClassName);
        course.addView(className);

        //Set course rating
        RelativeLayout.LayoutParams lpForClassRating = new RelativeLayout.LayoutParams(
                200, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView classRating = newTextView(rating, lpForClassRating);
        course.addView(classRating);

        //Set semester taken
        RelativeLayout.LayoutParams lpForSemester = new RelativeLayout.LayoutParams(
                800, RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView semester = newTextView(semseterTaken + " " + yearTaken, lpForSemester);
        course.addView(semester);

        allClassesTaken.addView(course);
    }


    /**
     * This method will be used to create a textview with similar properties
     *
     * */
    private TextView newTextView(String text, RelativeLayout.LayoutParams lp){
        TextView newView = new TextView(this);
        newView.setText(text);
        newView.setPadding(20, 0, 0, 0);
        newView.setLayoutParams(lp);
        newView.setId(Utils.getUniqueID());
        return newView;
    }
}