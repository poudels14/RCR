package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.parse.Parse;

/**
 * Created by Andrew on 3/28/2015.
 */
public class ManageAccountActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseAPI.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void returnToMainClicked(View view){
        Toast.makeText(getApplicationContext(), "Returning to main", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    public void manageFriendsClicked(View view) {
        Toast.makeText(getApplicationContext(), "Manage friends", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, ManageFriendsActivity.class);
        startActivity(i);
    }

    public void manageFutureCoursesClicked(View view) {
        Toast.makeText(getApplicationContext(), "Manage future courses", Toast.LENGTH_SHORT).show();
    }

    public void managePastCoursesClicked(View view) {
        Toast.makeText(getApplicationContext(), "Manage past courses", Toast.LENGTH_SHORT).show();
    }
}
