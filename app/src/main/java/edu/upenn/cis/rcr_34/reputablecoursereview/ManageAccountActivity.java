package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import com.parse.ParseUser;

/**
 * Created by Andrew on 3/28/2015.
 */
public class ManageAccountActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        // set the header of this view
        TextView t = (TextView)findViewById(R.id.header_text);

        t.setText("Manage Account: " +
                (String) ParseUser.getCurrentUser().get("firstName") +
                " " + (String) ParseUser.getCurrentUser().get("lastName"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    //User clicked return to main button
    public void returnToMainClicked(View view){
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    //User clicked manage friends button
    public void manageFriendsClicked(View view) {
        Intent i = new Intent(this, ManageFriendsActivity.class);
        startActivity(i);
    }

    //User clicked manage future courses button
    public void manageFutureCoursesClicked(View view) {
        Intent i = new Intent(this, FutureCourses.class);
        startActivity(i);
    }

    //User clicked manage past courses button
    public void managePastCoursesClicked(View view) {
        Intent i = new Intent(this, PastCourses.class);
        startActivity(i);
    }
}
