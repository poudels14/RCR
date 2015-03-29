package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.parse.Parse;

/**
 * Created by sagar on 3/29/15.
 */
public class CreateAccountActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "W5JnCKDZoSYR2AHncCRPZ7TZ94e3x9RJcAQjoc0a", "9vWs3BE45BCEtigsvl9ezo14wAg2ECoPxADTxtoC");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
