package edu.upenn.cis.rcr_34.reputablecoursereview.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.parse.ParseUser;

import edu.upenn.cis.rcr_34.reputablecoursereview.LoginActivity;

/**
 * Created by Dan on 4/6/2015.
 *
 * If methods are common to any combination of classes, then put the method in here instead
 */
public class StaticUtils {

    // multiple methods should be able to sign out of the entire app at any time
    public static void signOutClicked(Context ctx){
        ParseUser.logOut();

        Toast.makeText(ctx.getApplicationContext(), "Signing out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ctx.getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}
