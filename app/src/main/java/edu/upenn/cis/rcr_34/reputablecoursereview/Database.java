package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sagar on 3/29/15.
 */
public class Database {
//    private static Database database;
//    private static HashMap<String, Object> waitingFunctions;
//    private Database(android.content.Context ctx){
//        Parse.enableLocalDatastore(ctx);
//        Parse.initialize(ctx, "W5JnCKDZoSYR2AHncCRPZ7TZ94e3x9RJcAQjoc0a", "9vWs3BE45BCEtigsvl9ezo14wAg2ECoPxADTxtoC");
//
//    }
//
//    public static Database getInstance(android.content.Context ctx){
//        if (database == null){
//            database = new Database(ctx);
//            waitingFunctions = new HashMap<String, Object>();
//        }
//        return database;
//    }
//
//    public void checkLoginDetails(final String username, final String password, final LoginActivity loginActivity){
//        ParseQuery query = ParseQuery.getQuery("userDetails");
//        query.whereEqualTo("username", username);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List list, ParseException e) {
//                if (e == null) {
//                    if (list.size() > 0){
//                        ParseObject po = (ParseObject) list.get(0);
//                        String cloudUsername = (String) po.get("username");
//                        String cloudPassword = (String) po.get("password");
//                        if (cloudPassword.equals(password) && cloudUsername.equals(username)){
//                            loginActivity.logIn();
//                            Log.d("Database", "Logged in");
//                        }
//                        else {
//                            Toast.makeText(loginActivity.getApplicationContext(), "Invalid username or wrong password", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else{
//                        Toast.makeText(loginActivity.getApplicationContext(), "Invalid username or wrong password", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Log.d("Database", "Error: " + e.getMessage());
//                }
//            }
//        });
//    }
//
//    public void createNewUser(final String username, final String password, final LoginActivity loginActivity){
//        final ParseObject newObject = new ParseObject("userDetails");
//        newObject.put("username", username);
//        newObject.put("password", password);
//
//        ParseQuery query = ParseQuery.getQuery("userDetails");
//        query.whereEqualTo("username", username);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List list, ParseException e) {
//                if (e == null) {
//                    if (list.size() > 0){
//                        Toast.makeText(loginActivity.getApplicationContext(), "User Already Exists!", Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        newObject.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                Toast.makeText(loginActivity.getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
//                                loginActivity.logIn();
//                            }
//                        });
//                    }
//                }
//            }
//        });
//     }
}
