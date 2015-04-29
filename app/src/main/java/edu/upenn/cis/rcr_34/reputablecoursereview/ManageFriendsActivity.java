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

import java.io.File;
import java.util.ArrayList;


public class ManageFriendsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);

        //Now add all accepted friends to friends list
        final User f1 = new User(ParseUser.getCurrentUser().getEmail());
        f1.addListener(new ParseDataReceivedNotifier() {
            @Override
            public void notifyListener() {
                f1.checkIfFriendRequestIsAccepted();
            }
        });

        final LinearLayout friendList = (LinearLayout) findViewById(R.id.manage_account_friends_list);
        final LinearLayout pendingList = (LinearLayout) findViewById(R.id.manage_account_pending_list);


        loadAllFriends(friendList);
        loadAllPendingRequest(pendingList);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_friends, menu);
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

    //Load the pending friend requests
    private void loadAllPendingRequest(final LinearLayout mainListLayout) {
        final User f1 = new User(ParseUser.getCurrentUser().getEmail());
        f1.addListener(new ParseDataReceivedNotifier() {
            @Override
            public void notifyListener() {

                // Remove loading label first
                mainListLayout.removeAllViews();

                ArrayList<String> pendingList = f1.getPendingRequests();
                if (pendingList == null) {
                    return;
                }

                for (int i = 0; i < pendingList.size(); i++){
                    final User f = new User(f1.getPendingRequests().get(i));
                    f.addListener(new ParseDataReceivedNotifier(){
                        public void notifyListener() {
                            populatePendingRequest(mainListLayout, f);
                        }
                    });
                }
            }
        });
    }

    //Display pending friend requests
    private void populatePendingRequest(final LinearLayout llIn, final User u) {
        final RelativeLayout ll = new RelativeLayout(this);
        ll.setId(Utils.getUniqueID());
        ll.setPadding(0, 0, 0, 20);

        RelativeLayout.LayoutParams lpForImage = new RelativeLayout.LayoutParams(200, 250);
        final ImageView profilePic = new ImageView(this);
        profilePic.setVisibility(View.VISIBLE);
        profilePic.setId(Utils.getUniqueID());
        profilePic.setLayoutParams(lpForImage);
        profilePic.setBackgroundColor(Color.BLACK);
        profilePic.setImageBitmap(u.getProfileImage());

        ll.addView(profilePic);


        //Set name
        RelativeLayout.LayoutParams lpForName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForName.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
        TextView name = new TextView(this);
        name.setText(u.getName());
        name.setPadding(20, 0, 0, 0);
        name.setId(Utils.getUniqueID());
        name.setLayoutParams(lpForName);
        ll.addView(name);

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
        ll.addView(year);

        //Set accept button
        RelativeLayout.LayoutParams lpForAccept = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForAccept.addRule(RelativeLayout.BELOW, year.getId());
        lpForAccept.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
        Button accept = new Button(this);
        accept.setText("Accept");
        accept.setTextSize(10);
        accept.setId(Utils.getUniqueID());
        accept.setLayoutParams(lpForAccept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User me = new User(ParseUser.getCurrentUser().getEmail());
                me.addListener(new ParseDataReceivedNotifier() {
                    @Override
                    public void notifyListener() {
                        me.acceptRequest(u.getEmail(), new ParseDataReceivedNotifier() {
                            @Override
                            public void notifyListener() {
                                llIn.removeView((findViewById(ll.getId())));
                            }
                        });
                    }
                });
            }
        });
        ll.addView(accept);



        //set decline button
        RelativeLayout.LayoutParams lpForDecline = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForDecline.addRule(RelativeLayout.RIGHT_OF, accept.getId());
        lpForDecline.addRule(RelativeLayout.BELOW, year.getId());
        Button decline = new Button(this);
        decline.setText("Decline");
        decline.setTextSize(10);
        decline.setId(Utils.getUniqueID());
        decline.setLayoutParams(lpForDecline);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User me = new User(ParseUser.getCurrentUser().getEmail());
                me.addListener(new ParseDataReceivedNotifier() {
                    @Override
                    public void notifyListener() {
                        me.rejectRequest(u.getEmail());
                    }
                });
            }
        });
        ll.addView(decline);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFriendsProfile(u.getEmail());
            }
        });

        llIn.addView(ll);
    }

    //Load user's friends
    private void loadAllFriends(final LinearLayout mainListLayout) {
        final User f1 = new User(ParseUser.getCurrentUser().getEmail());
        f1.addListener(new ParseDataReceivedNotifier() {
            @Override
            public void notifyListener() {
                // Remove loading label first
                mainListLayout.removeAllViews();

                ArrayList<String> friends = f1.getFriends();
                if (friends == null) {
                    return;
                }
                for (int i = 0; i < friends.size(); i++){
                    final User f = new User(f1.getFriends().get(i));
                    f.addListener(new ParseDataReceivedNotifier() {
                        @Override
                        public void notifyListener() {
                            populateFriend(mainListLayout, f);
                        }
                    });
                }
            }
        });
    }

    //Display user's friends
    private void populateFriend(LinearLayout llIn, final User u){
        RelativeLayout rL = new RelativeLayout(this);
        rL.setPadding(5, 10, 5, 10);
        rL.setId(Utils.getUniqueID());
        if(rL.getId() % 2 == 0){
            rL.setBackgroundColor(Color.GRAY);
        }

        //Set profile pic
        RelativeLayout.LayoutParams lpForImage = new RelativeLayout.LayoutParams(200, 250);
        ImageView profilePic = new ImageView(this);
        profilePic.setVisibility(View.VISIBLE);
        profilePic.setBackgroundColor(Color.BLACK);
        profilePic.setId(Utils.getUniqueID());
        profilePic.setLayoutParams(lpForImage);
        profilePic.setImageBitmap(u.getProfileImage());
        rL.addView(profilePic);


        //Set name
        RelativeLayout.LayoutParams lpForName = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpForName.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
        TextView name = new TextView(this);
        name.setText(u.getName());
        name.setPadding(20, 0, 0, 0);
        name.setId(Utils.getUniqueID());
        name.setLayoutParams(lpForName);
        rL.addView(name);

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
        rL.addView(year);

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
        rL.addView(major);
        Intent intent = new Intent(this, FriendsDetailActivity.class);

        rL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFriendsProfile(u.getEmail());
            }
        });




        llIn.addView(rL);
    }

    //Go to a friend's profile
    protected void goToFriendsProfile(String email){
        Intent intent = new Intent(this, FriendsDetailActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
