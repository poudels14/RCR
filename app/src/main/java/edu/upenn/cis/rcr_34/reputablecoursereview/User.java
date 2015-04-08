package edu.upenn.cis.rcr_34.reputablecoursereview;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sagar on 4/1/15.
 */
public class User {
    private static HashMap<String, User> allUsers;
    private ParseUser me;
    private String email;
    private String firstName;
    private String lastName;
    private String picLink;
    private String year;
    private String major;
    private ArrayList<String> friendsEmail;
    private ArrayList<String> pendingRequestEmail;
    private boolean isReady;
    private ArrayList<ParseDataReceivedNotifier> liteners;
    private boolean shouldNotify;

//    public User(final String email, final ParseDataReceivedNotifier listener){
//        this.email = email;
//        this.isReady = false;
//        this.liteners = new ArrayList<ParseDataReceivedNotifier>();
//        this.shouldNotify = false;
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereEqualTo("email", email);
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List list, com.parse.ParseException e) {
//                if (e == null) {
//                    if (list.size() > 0) {
//                        ParseUser user = (ParseUser) list.get(0);
//                        isReady = true;
//                        me = user;
//                        year = (String) user.get("year");
//                        firstName = (String) user.get("firstName");
//                        lastName = (String) user.get("lastName");
//                        major = (String) user.get("major");
//                        picLink = (String) user.get("profilePic");
//                        friendsEmail = (ArrayList) user.getList("friends");
//                        pendingRequestEmail = (ArrayList) user.getList("pendingRequest");
//                        Log.d("USER.JAVA", "User data received: " + firstName);
//                        shouldNotify = true;
////                        listener.notifyListener();
//                        notifyListeners();
//                    } else {
//
//                        Log.d("USER.JAVA", "User doesn't exist: " + email);
//                    }
//                }
//            }
//        });
//    }

    public User(final String email){
        this.email = email;
        this.isReady = false;
        this.liteners = new ArrayList<ParseDataReceivedNotifier>();
        this.shouldNotify = false;

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ParseUser user = (ParseUser) list.get(0);
                        isReady = true;
                        me = user;
                        year = (String) user.get("year");
                        firstName = (String) user.get("firstName");
                        lastName = (String) user.get("lastName");
                        major = (String) user.get("major");
                        picLink = (String) user.get("profilePic");
                        friendsEmail = (ArrayList) user.getList("friends");
                        pendingRequestEmail = (ArrayList) user.getList("pendingRequest");
                        Log.d("USER.JAVA", "User data received: " + firstName);
                        shouldNotify = true;
                        notifyListeners();
                    } else {
                        Log.d("USER.JAVA", "User doesn't exist: " + email);
                    }
                }
            }
        });
    }

    public static User getUser(String email){
        if (allUsers == null){
            allUsers = new HashMap<String, User>();
        }
        if (allUsers.containsKey(email)){
            return allUsers.get(email);
        }
        else{
            allUsers.put(email, new User(email));
            return allUsers.get(email);
        }
    }

    public String getName(){
        return firstName;
    }

    public String getEmail(){
        return email;
    }

    public String getYear(){
        return year;
    }

    public String getMajor(){
        return major;
    }

    public String getProfilePic(){
        return picLink;
    }

    public ArrayList<User> getFriends(){
        ArrayList<User> friends = new ArrayList<User>();
            for (int i = 0; i < friendsEmail.size(); i++){
                User u = new User(friendsEmail.get(0));
                friends.add(u);
            }
        return friends;
    }

    public void addFriend(final String email){
        if (friendsEmail == null){
            friendsEmail = new ArrayList<String>();
        }

        this.me.put("friends", friendsEmail);
        this.me.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("PARSE", "Friends saved properly");
                } else {
                    Log.d("USER.JAVA", "Friends couldn't be saved properly: " + email);
                }
            }
        });
    }
    public boolean isReady(){
        return isReady;
    }

    public void notifyListeners(){
        if (shouldNotify){
            for (ParseDataReceivedNotifier l : this.liteners){
                l.notifyListener();
            }
        }
    }

    public void addListener(ParseDataReceivedNotifier l){
        this.liteners.add(l);
        if (shouldNotify){
            l.notifyListener();
        }
    }
}