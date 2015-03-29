package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void searchClassesClicked(View view){
        EditText courseName = (EditText)findViewById(R.id.course_search);
        String course = courseName.getText().toString();
        Toast.makeText(getApplicationContext(), "Searching for course: " + course, Toast.LENGTH_SHORT).show();
    }

    public void manageAccountClicked(View view){
        Toast.makeText(getApplicationContext(), "Manage account selected", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(this, ManageAccountActivity.class);
        startActivity(intent2);
    }

    public void signOutClicked(View view){
        Toast.makeText(getApplicationContext(), "Signing out", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}