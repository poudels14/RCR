package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.net.URL;
import java.util.ArrayList;


public class ManageFriendsActivity extends ActionBarActivity {
    private int viewsID = 1001;
    //http://icons.iconarchive.com/icons/ilovecolors/easter-bunny-egg/256/easter-Bunny-icon.png
    private String icon = "http://icons.iconarchive.com/icons/yellowicon/game-stars/256/Mario-icon.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
        final LinearLayout l = (LinearLayout) findViewById(R.id.manage_account_friends_list);
        loadAllFriends(l);
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

    private int getUniqueID() {
        return viewsID++;
    }

    private void loadPendingRequest() {
        String[] pendingFriends = {"Andrew Remec", "Daniel McCann", "Tahmid Shahriar", "Sagar Poudel"};
        LinearLayout pendingList = (LinearLayout) findViewById(R.id.manage_account_pending_list);

        for (int i = 0; i < pendingFriends.length; i++) {
            RelativeLayout ll = new RelativeLayout(this);
            ll.setPadding(0, 0, 0, 20);

            RelativeLayout.LayoutParams lpForImage = new RelativeLayout.LayoutParams(200, 250);
            final ImageView profilePic = new ImageView(this);
            profilePic.setVisibility(View.VISIBLE);
            profilePic.setId(this.getUniqueID());
            profilePic.setLayoutParams(lpForImage);
            profilePic.setBackgroundColor(Color.BLACK);
            LoadImage lm = new LoadImage(this, profilePic);
            lm.execute(icon); //icon = address of image to be loaded

            ll.addView(profilePic);

            //Set name
            RelativeLayout.LayoutParams lpForName = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpForName.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
            TextView name = new TextView(this);
            name.setText(pendingFriends[i]);
            name.setPadding(20, 0, 0, 0);
            name.setId(this.getUniqueID());
            name.setLayoutParams(lpForName);
            ll.addView(name);

            //Set year
            RelativeLayout.LayoutParams lpForYear = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpForYear.addRule(RelativeLayout.BELOW, name.getId());
            lpForYear.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());

            TextView year = new TextView(this);
            year.setText("Year: 2017");
            year.setPadding(20, 0, 0, 0);
            year.setLayoutParams(lpForYear);
            year.setId(this.getUniqueID());
            ll.addView(year);

            //Set accept button
            RelativeLayout.LayoutParams lpForAccept = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpForAccept.addRule(RelativeLayout.BELOW, year.getId());
            lpForAccept.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
            Button accept = new Button(this);
            accept.setText("Accept");
            accept.setTextSize(10);
            accept.setId(this.getUniqueID());
            accept.setLayoutParams(lpForAccept);
            ll.addView(accept);


            //set decline button
            RelativeLayout.LayoutParams lpForDecline = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpForDecline.addRule(RelativeLayout.RIGHT_OF, accept.getId());
            lpForDecline.addRule(RelativeLayout.BELOW, year.getId());
            Button decline = new Button(this);
            decline.setText("Decline");
            decline.setTextSize(10);
            decline.setId(this.getUniqueID());
            decline.setLayoutParams(lpForDecline);
            ll.addView(decline);

            pendingList.addView(ll);
        }
    }

    private void loadAllFriends(final LinearLayout mainListLayout) {
        mainListLayout.removeAllViews();
        final User f1 = new User(ParseUser.getCurrentUser().getEmail());
        f1.addListener(new ParseDataReceivedNotifier() {
                           @Override
                           public void notifyListener() {
                               populateFriend(mainListLayout, f1);
                           }
                       });
    }

    private void populateFriend(LinearLayout llIn, User u){
            RelativeLayout rL = new RelativeLayout(this);
            rL.setPadding(0, 0, 0, 20);

            //Set profile pic
            RelativeLayout.LayoutParams lpForImage = new RelativeLayout.LayoutParams(200, 250);
            ImageView profilePic = new ImageView(this);
            profilePic.setVisibility(View.VISIBLE);
            profilePic.setBackgroundColor(Color.BLACK);
            profilePic.setId(this.getUniqueID());
            profilePic.setLayoutParams(lpForImage);
            LoadImage lm = new LoadImage(this, profilePic);
            lm.execute(u.getProfilePic()); //icon = address of image to be loaded
            rL.addView(profilePic);

            //Set name
            RelativeLayout.LayoutParams lpForName = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpForName.addRule(RelativeLayout.RIGHT_OF, profilePic.getId());
            TextView name = new TextView(this);
            name.setText(u.getName());
            name.setPadding(20, 0, 0, 0);
            name.setId(this.getUniqueID());
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
            year.setId(this.getUniqueID());
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
            major.setId(this.getUniqueID());
            rL.addView(major);

            llIn.addView(rL);
    }
}
